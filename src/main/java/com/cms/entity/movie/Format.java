package com.cms.entity.movie;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: Movie.FORMATS
 * Ví dụ: 2D, 3D, IMAX, 4DX
 */
@Entity
@Table(name = "formats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Format {

    @Id
    @Column(name = "f_name", length = 50)
    private String fName;
}
