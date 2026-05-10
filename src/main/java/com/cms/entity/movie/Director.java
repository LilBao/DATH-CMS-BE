package com.cms.entity.movie;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: Movie.DIRECTOR
 */
@Entity
@Table(name = "director")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Director {

    @Id
    @Column(name = "full_name", length = 100)
    private String fullName;
}
