package com.cms.controller;

import com.cms.entity.search.MovieDocument;
import com.cms.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Elasticsearch Search APIs")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/movies")
    @Operation(summary = "Search movies by title")
    public ResponseEntity<List<MovieDocument>> searchMovies(@RequestParam String q) {
        return ResponseEntity.ok(searchService.searchMovies(q));
    }

    @GetMapping("/movies/genre")
    @Operation(summary = "Search movies by genre")
    public ResponseEntity<List<MovieDocument>> searchByGenre(@RequestParam String genre) {
        return ResponseEntity.ok(searchService.searchMoviesByGenre(genre));
    }

    @PostMapping("/sync")
    @Operation(summary = "Sync all data to Elasticsearch (Admin only)")
    public ResponseEntity<String> syncAll() {
        searchService.syncAllMovies();
        searchService.syncAllActors();
        searchService.syncAllDirectors();
        searchService.syncAllGenres();
        return ResponseEntity.ok("Synchronization started successfully");
    }
}
