package bg.startit.hackathon.airquiality.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasks {

  private final AirQualityService airQualityService;

  private static final long DOWNLOAD_PERIOD = 30L/*min*/ * 60L /*s*/ * 1000L /*ms*/;
  private static final int CLEANUP_PERIOD_DAYS = 3;

  public ScheduledTasks(AirQualityService airQualityService) {
    this.airQualityService = airQualityService;
  }

  @Scheduled(fixedDelay = DOWNLOAD_PERIOD)
  public void downloadData() {
    airQualityService.downloadData();
    airQualityService.cleanupOlderEntries(CLEANUP_PERIOD_DAYS);
  }

}
