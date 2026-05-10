package com.cms.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {

    @NotBlank(message = "Movie name is required")
    @Size(max = 255)
    private String mName;

    private String descript;

    @NotNull(message = "Runtime is required")
    @Min(value = 1, message = "Runtime must be at least 1 minute")
    private Integer runTime;

    @Builder.Default
    private Boolean isDub = false;

    @Builder.Default
    private Boolean isSub = true;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @NotNull(message = "Closing date is required")
    private LocalDate closingDate;

    @NotBlank(message = "Age rating is required")
    @Pattern(regexp = "^(K|T13|T16|T18)$", message = "Age rating must be K, T13, T16, or T18")
    private String ageRating;

    private String posterUrl;
    private String trailerUrl;

    private Set<String> genreIds;
    private Set<String> formatIds;
    private Set<String> actorIds;
}
