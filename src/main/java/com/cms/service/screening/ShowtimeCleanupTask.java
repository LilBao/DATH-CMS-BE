package com.cms.service.screening;

import com.cms.repository.screening.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShowtimeCleanupTask {

    private final ShowtimeRepository showtimeRepository;

    /**
     * Tự động xóa các suất chiếu đã qua ngày diễn ra.
     * Chạy vào lúc 2:00 AM hàng ngày.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    @CacheEvict(value = {"showtimes", "showtimes_movie_day", "showtimes_branch_day", "showtimes_movie_slug"}, allEntries = true)
    public void cleanupOldShowtimes() {
        log.info("Starting scheduled cleanup of old showtimes...");
        
        LocalDate today = LocalDate.now();
        showtimeRepository.deleteByDayBefore(today);

        log.info("Finished scheduled cleanup of old showtimes.");
    }
}
