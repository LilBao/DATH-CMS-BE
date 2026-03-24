package com.cgv.entity.movie;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: Movie.ACTOR
 */
@Entity
@Table(name = "actor")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Actor {

    @Id
    @Column(name = "full_name", length = 100)
    private String fullName;
}
