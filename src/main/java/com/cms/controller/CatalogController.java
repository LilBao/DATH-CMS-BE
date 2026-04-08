package com.cms.controller;

import com.cms.common.exception.AppException;
import com.cms.common.response.ApiResponse;
import com.cms.entity.movie.Actor;
import com.cms.entity.movie.Format;
import com.cms.entity.movie.Genre;
import com.cms.repository.movie.ActorRepository;
import com.cms.repository.movie.FormatRepository;
import com.cms.repository.movie.GenreRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Các API danh mục (Thể loại, Định dạng, Diễn viên)")
public class CatalogController {

    private final GenreRepository genreRepository;
    private final FormatRepository formatRepository;
    private final ActorRepository actorRepository;

    // ── Genre ──────────────────────────────────────────────────

    @GetMapping("/genres")
    @Operation(summary = "Lấy danh sách tất cả thể loại", tags = {"Genre"})
    public ResponseEntity<ApiResponse<List<Genre>>> getAllGenres() {
        return ResponseEntity.ok(ApiResponse.ok(genreRepository.findAll()));
    }

    @PostMapping("/genres")
    @Operation(summary = "Tạo mới một thể loại", tags = {"Genre"})
    public ResponseEntity<ApiResponse<Genre>> createGenre(@RequestParam String genre) {
        if (genreRepository.existsById(genre)) {
            throw AppException.conflict("Genre already exists: " + genre);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(genreRepository.save(Genre.builder().genre(genre).build())));
    }

    @DeleteMapping("/genres/{genre}")
    @Operation(summary = "Xóa một thể loại", tags = {"Genre"})
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@PathVariable String genre) {
        if (!genreRepository.existsById(genre)) throw AppException.notFound("Genre", genre);
        genreRepository.deleteById(genre);
        return ResponseEntity.ok(ApiResponse.ok("Genre deleted", null));
    }

    @GetMapping("/formats")
    @Operation(summary = "Lấy danh sách tất cả định dạng phim", tags = {"Format"})
    public ResponseEntity<ApiResponse<List<Format>>> getAllFormats() {
        return ResponseEntity.ok(ApiResponse.ok(formatRepository.findAll()));
    }

    @PostMapping("/formats")
    @Operation(summary = "Tạo mới một định dạng phim", tags = {"Format"})
    public ResponseEntity<ApiResponse<Format>> createFormat(@RequestParam String fName) {
        if (formatRepository.existsById(fName)) {
            throw AppException.conflict("Format already exists: " + fName);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(formatRepository.save(Format.builder().fName(fName).build())));
    }

    @DeleteMapping("/formats/{fName}")
    @Operation(summary = "Xóa một định dạng phim", tags = {"Format"})
    public ResponseEntity<ApiResponse<Void>> deleteFormat(@PathVariable String fName) {
        if (!formatRepository.existsById(fName)) throw AppException.notFound("Format", fName);
        formatRepository.deleteById(fName);
        return ResponseEntity.ok(ApiResponse.ok("Format deleted", null));
    }

    @GetMapping("/actors")
    @Operation(summary = "Lấy danh sách tất cả diễn viên", tags = {"Actor"})
    public ResponseEntity<ApiResponse<List<Actor>>> getAllActors() {
        return ResponseEntity.ok(ApiResponse.ok(actorRepository.findAll()));
    }

    @PostMapping("/actors")
    @Operation(summary = "Tạo mới một diễn viên", tags = {"Actor"})
    public ResponseEntity<ApiResponse<Actor>> createActor(@RequestParam String fullName) {
        if (actorRepository.existsById(fullName)) {
            throw AppException.conflict("Actor already exists: " + fullName);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(actorRepository.save(Actor.builder().fullName(fullName).build())));
    }

    @DeleteMapping("/actors/{fullName}")
    @Operation(summary = "Xóa một diễn viên", tags = {"Actor"})
    public ResponseEntity<ApiResponse<Void>> deleteActor(@PathVariable String fullName) {
        if (!actorRepository.existsById(fullName)) throw AppException.notFound("Actor", fullName);
        actorRepository.deleteById(fullName);
        return ResponseEntity.ok(ApiResponse.ok("Actor deleted", null));
    }
}
