package com.cms.entity.movie;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Composite PK: (MovieID, CUserID)
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReviewId implements Serializable {

    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "c_user_id", length = 20)
    private String cUserId;
}
