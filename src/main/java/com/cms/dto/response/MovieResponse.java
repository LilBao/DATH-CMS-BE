package com.cms.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {

    private Integer movieId;
    private String mName;
    private String descript;
    private Integer runTime;
    private Boolean isDub;
    private Boolean isSub;
    private LocalDate releaseDate;
    private LocalDate closingDate;
    private String ageRating;
    private String posterUrl;
    private String trailerUrl;
    private Set<String> genres;
    private Set<String> formats;
    private Set<String> actors;
}
