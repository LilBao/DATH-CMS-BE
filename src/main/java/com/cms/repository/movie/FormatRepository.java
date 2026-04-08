package com.cms.repository.movie;

import com.cms.entity.movie.Format;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormatRepository extends JpaRepository<Format, String> {
}
