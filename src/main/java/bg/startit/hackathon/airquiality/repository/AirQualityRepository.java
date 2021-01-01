package bg.startit.hackathon.airquiality.repository;

import bg.startit.hackathon.airquiality.model.AirQuality;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AirQualityRepository extends JpaRepository<AirQuality, Long> {

  @Query("SELECT DISTINCT stationName FROM AirQuality WHERE UPPER(stationName) LIKE CONCAT('%',UPPER(:stationName),'%')")
  List<String> findStations(@Param("stationName") String stationName);

  long countByStationCodeAndTimestamp(String stationCode, OffsetDateTime timeStamp);

  void deleteByTimestampBefore(OffsetDateTime timestamp);

  Page<AirQuality> findByStationNameAndTimestampAfterAndTimestampBeforeOrderByTimestampDesc(
      String stationName, OffsetDateTime since, OffsetDateTime until, Pageable pageable);

  Page<AirQuality> findByStationNameOrderByTimestamp(
      String stationName, Pageable pageable);
}
