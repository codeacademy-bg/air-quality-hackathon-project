package bg.startit.hackathon.airquiality.controller;

import bg.startit.hackathon.airquiality.api.AirQualityApi;
import bg.startit.hackathon.airquiality.dto.AirQualityData;
import bg.startit.hackathon.airquiality.dto.AirQualityData.PollutantEnum;
import bg.startit.hackathon.airquiality.dto.AirQualityPage;
import bg.startit.hackathon.airquiality.model.AirQuality;
import bg.startit.hackathon.airquiality.model.AirQuality.Pollutant;
import bg.startit.hackathon.airquiality.repository.AirQualityRepository;
import bg.startit.hackathon.airquiality.service.AirQualityService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping
public class AirQualityController implements AirQualityApi {

  private final AirQualityRepository airQualityRepository;
  private final AirQualityService airQualityService;

  public AirQualityController(
      AirQualityRepository airQualityRepository,
      AirQualityService airQualityService) {
    this.airQualityRepository = airQualityRepository;
    this.airQualityService = airQualityService;
  }

  @Override
  public ResponseEntity<List<String>> listCities(@Valid String city, @Valid String country) {
    return ResponseEntity.ok(airQualityRepository.findStations(city));
    //TODO implement country
  }

  private static final PollutantEnum asPollutantEnum(Pollutant pollutant) {
    if (pollutant == Pollutant.NOX_as_NO2) {
      return PollutantEnum.NOX_AS_NO2;
    }
    return PollutantEnum.valueOf(pollutant.name());
  }

  @Override
  public ResponseEntity<AirQualityPage> readAirQualityData(@Valid String city,
      @Valid String country, @Valid OffsetDateTime since, @Valid OffsetDateTime until,
      @Min(0) @Valid Integer page, @Min(0) @Max(100) @Valid Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<AirQuality> data = airQualityService.readAirQualityData(city, since, until, pageable);
    AirQualityPage airQualityPage = new AirQualityPage()
        .number(data.getNumber())
        .size(data.getSize())
        .numberOfElements(data.getNumberOfElements())
        .totalElements(data.getTotalElements())
        .totalPages(data.getTotalPages())
        .content(data.get().map(el -> {
          double dangerLimit = el.getPollutant().getMax();
          return new AirQualityData()
              .country(el.getCountry())
              .stationName(el.getStationName())
              .stationCode(el.getStationCode())
              .unit(el.getUnit())
              .pollutant(asPollutantEnum(el.getPollutant()))
              .value(el.getValue())
              .timestamp(el.getTimestamp())
              .dangerLimit(dangerLimit)
              .isDangerous(el.getValue() > dangerLimit);
        }).collect(Collectors.toList()));
    return ResponseEntity.ok(airQualityPage);
  }
}
