package com.cgv.entity.movie;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: Movie.GENRE
 */
@Entity
@Table(name = "genre")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Id
    @Column(name = "genre", length = 50)
    private String genre;
}
