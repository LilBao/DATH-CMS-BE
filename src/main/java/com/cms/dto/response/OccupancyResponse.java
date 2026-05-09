package com.cms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyResponse {
    private Integer showtimeId;
    private String movieName;
    private String branchName;
    private Integer roomId;
    private LocalDate day;
    private LocalTime startTime;
    private Integer capacity;
    private Integer ticketsSold;
    private Double occupancyRate;

    public OccupancyResponse(Integer showtimeId, String movieName, String branchName, Integer roomId, 
                             LocalDate day, LocalTime startTime, Integer capacity, int ticketsSold) {
        this.showtimeId = showtimeId;
        this.movieName = movieName;
        this.branchName = branchName;
        this.roomId = roomId;
        this.day = day;
        this.startTime = startTime;
        this.capacity = capacity;
        this.ticketsSold = ticketsSold;
        this.occupancyRate = 0.0;
    }
}
