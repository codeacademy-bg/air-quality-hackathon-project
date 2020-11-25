package bg.startit.hackathon.airquiality.service;

import bg.startit.hackathon.airquiality.model.AirQuality;
import bg.startit.hackathon.airquiality.repository.AirQualityRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AirQualityService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AirQualityService.class);

  // how often to download and process the files (30 minutes)
  private static final long DOWNLOAD_PERIOD = 30L/*min*/ * 60L /*s*/ * 1000L /*ms*/;
  private static final long CLEANUP_PERIOD_DAYS = 7;
  // the list containing all files
  private static final String FILES_LIST_URL = "https://discomap.eea.europa.eu/map/fme/latest/files.txt";

  private final RestTemplate http = new RestTemplate();


  @Autowired
  private AirQualityRepository airQualityRepository;

  @Scheduled(fixedDelay = DOWNLOAD_PERIOD)
  public void downloadData() {
    // retrieve list of files
    String[] lines = http.getForObject(FILES_LIST_URL, String.class).split("\n");

    // now retrieve every line
    for (String line : lines) {
      if (line.contains("/BG_")) {
        try {
          downloadCsvFile(line.trim());
        } catch (Exception e) { // Jackson may throw Runtime Exception
          LOGGER.warn("Failed to load data from {}.", line/*, e*/);
        }
      }
    }

    // now cleanup the data, older by 7 days
    LOGGER.info("Removing data older than {} days", CLEANUP_PERIOD_DAYS);
    airQualityRepository.deleteByTimestampBefore(
        OffsetDateTime.now()
            .minus(CLEANUP_PERIOD_DAYS, ChronoUnit.DAYS));
  }


  private void downloadCsvFile(String url) {
    final ObjectMapper mapper = new CsvMapper();
    // support weird time serialization in the CSV files
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addDeserializer(OffsetDateTime.class, new CustomOffsetDateTimeDeserializer());
    mapper.registerModule(javaTimeModule);

    List<AirQuality> entries = http.execute(url, HttpMethod.GET, null, clientHttpResponse -> {
      LOGGER.info("Loading Data File {}", url);

      CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
      MappingIterator<AirQualityCsvEntry> it = mapper.readerFor(AirQualityCsvEntry.class)
          .with(bootstrapSchema)
          .readValues(clientHttpResponse.getBody());

      // process every entry of the CSV file
      List<AirQuality> ret = new ArrayList<>();
      while (it.hasNextValue()) {
        addCsvData(it.next()).ifPresent(e -> {
          ret.add(e);
        });
      }
      return ret;
    });

    airQualityRepository.saveAll(entries);
  }

  private static AirQuality.Pollutant parse(String polutant) {
    polutant = polutant.replace('-', '_')
        .replace('.', '_')
        .replace(' ', '_');
    return AirQuality.Pollutant.valueOf(polutant);
  }

  protected Optional<AirQuality> addCsvData(AirQualityCsvEntry pojo) {
    // skipping invalid entries
    if (pojo.value_validity < 0) {
      return Optional.empty();
    }
    // checking if data is already stored
    if (airQualityRepository.countByStationCodeAndTimestamp(
        pojo.station_code, pojo.value_datetime_inserted) > 0) {
      return Optional.empty();
    }

    AirQuality airQuality = new AirQuality();

    airQuality.setCountry(pojo.network_countrycode);
    airQuality.setStationName(pojo.station_name);
    airQuality.setStationCode(pojo.station_code);
    airQuality.setUnit(pojo.value_unit);
    airQuality.setValue(pojo.value_numeric);
    airQuality.setPollutant(parse(pojo.pollutant));
    airQuality.setTimestamp(pojo.value_datetime_inserted);

    return Optional.of(airQuality);

  }

  @Data
  static class AirQualityCsvEntry {

    public String network_countrycode;
    public String network_localid;
    public String network_name;
    public String network_namespace;
    public String network_timezone;
    public String pollutant;
    public String samplingpoint_localid;
    public String samplingpoint_namespace;
    public double samplingpoint_x;
    public double samplingpoint_y;
    public String coordsys;
    public String station_code;
    public String station_localid;
    public String station_name;
    public String station_namespace;
    public String value_datetime_begin;
    public String value_datetime_end;
    public OffsetDateTime value_datetime_inserted;
    public String value_datetime_updated;
    public double value_numeric;
    public double value_validity;
    public double value_verification;
    public double station_altitude;
    public String value_unit;
  }

}


