package com.cms.service.movie;

import com.cms.common.exception.AppException;
import com.cms.entity.movie.Movie;
import com.cms.repository.movie.ActorRepository;
import com.cms.repository.movie.FormatRepository;
import com.cms.repository.movie.GenreRepository;
import com.cms.repository.movie.MovieRepository;
import com.cms.service.search.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private FormatRepository formatRepository;
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private SearchService searchService;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .movieId(1)
                .mName("Test Movie")
                .slug("test-movie")
                .build();
    }

    @Test
    void getById_WhenMovieExists_ShouldReturnMovieResponse() {
        // Arrange
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(modelMapper.map(any(), any())).thenReturn(new com.cms.dto.response.MovieResponse());

        // Act
        var result = movieService.getById(1);

        // Assert
        assertNotNull(result);
        verify(movieRepository).findById(1);
    }

    @Test
    void getById_WhenMovieDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        when(movieRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppException.class, () -> movieService.getById(1));
    }

    @Test
    void delete_WhenMovieExists_ShouldDeleteMovieAndSyncSearch() {
        // Arrange
        when(movieRepository.existsById(1)).thenReturn(true);

        // Act
        movieService.delete(1);

        // Assert
        verify(movieRepository).deleteById(1);
        verify(searchService).deleteMovie(1);
    }

    @Test
    void delete_WhenMovieDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        when(movieRepository.existsById(1)).thenReturn(false);

        // Act & Assert
        assertThrows(AppException.class, () -> movieService.delete(1));
        verify(movieRepository, never()).deleteById(any());
    }
}
