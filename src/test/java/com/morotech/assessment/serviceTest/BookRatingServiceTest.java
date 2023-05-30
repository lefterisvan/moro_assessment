package com.morotech.assessment.serviceTest;

import com.morotech.assessment.dtos.BookDetailsDTO;
import com.morotech.assessment.dtos.BookRatingDto;
import com.morotech.assessment.exceptions.InvalidInputException;
import com.morotech.assessment.model.Book;
import com.morotech.assessment.model.BookRating;
import com.morotech.assessment.repositories.BookRatingRepository;
import com.morotech.assessment.services.BookRatingService;
import com.morotech.assessment.services.GutendexService;
import com.morotech.assessment.services.implementation.BookRatingServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookRatingServiceTest {

    @Spy
    ModelMapper modelMapper;

    @Mock
    private GutendexService gutendexService;

    @Mock
    private BookRatingRepository bookRatingRepository;

    @InjectMocks
    private BookRatingServiceImpl bookRatingService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        modelMapper=new ModelMapper();
    }



    @Test
    public void bookRatingSuccess() throws SQLException {

        BookRatingDto bookRatingDto = new BookRatingDto();
        bookRatingDto.setBookId(123);

        Book book = new Book();
        book.setId(123);

        when(gutendexService.searchBooksById(bookRatingDto.getBookId())).thenReturn(Collections.singletonList(book));
        when(bookRatingRepository.save(any(BookRating.class))).thenReturn(new BookRating());

        String result = bookRatingService.bookRating(bookRatingDto);

        Assert.assertEquals("Thank you for your rating", result);
        verify(gutendexService, times(1)).searchBooksById(bookRatingDto.getBookId());
        verify(bookRatingRepository, times(1)).save(any(BookRating.class));
    }
    @Test
    public void bookRatingNullInput() {

        BookRatingDto bookRatingDto = null;


        assertThrows(InvalidInputException.class, () -> bookRatingService.bookRating(bookRatingDto));


    }

    @Test
    public void bookRatingBookNotFound()  {

        BookRatingDto bookRatingDto = new BookRatingDto();
        bookRatingDto.setBookId(123);

        when(gutendexService.searchBooksById(bookRatingDto.getBookId())).thenReturn(Collections.emptyList());


        assertThrows(EntityNotFoundException.class, () -> bookRatingService.bookRating(bookRatingDto));
    }

    @Test
    public void bookRatingDatabaseError()  {

        BookRatingDto bookRatingDto = new BookRatingDto();
        bookRatingDto.setBookId(123);

        Book book = new Book();
        book.setId(123);

        when(gutendexService.searchBooksById(bookRatingDto.getBookId())).thenReturn(Collections.singletonList(book));
        when(bookRatingRepository.save(any(BookRating.class))).thenReturn(null);


        assertThrows(SQLException.class, () -> bookRatingService.bookRating(bookRatingDto));

    }







    @Test
    public void getBookDetails_ValidId() {
        // Arrange
        Integer bookId = 1;
        Double averageRating = 3.0;
        List<String> reviews = new ArrayList<>();
        reviews.add("Review 1");
        reviews.add("Review 2");
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Title");

        when(bookRatingRepository.getAverageRatingById(bookId)).thenReturn(Optional.of(averageRating));
        when(bookRatingRepository.getReviewsById(bookId)).thenReturn(reviews);
        when(gutendexService.searchBooksById(bookId)).thenReturn(List.of(book));

        // Act
        BookDetailsDTO result = bookRatingService.getBookDetails(bookId);

        // Assert
        assertEquals(bookId, result.getId());
        assertEquals(averageRating, result.getRating());
        assertEquals(reviews, result.getReviews());
        assertEquals("Title", result.getTitle());
    }

    @Test
    public void getBookDetails_NullId() {
        // Arrange
        Integer bookId = null;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            bookRatingService.getBookDetails(bookId);
        });
    }

    @Test
    public void getBookDetails_InvalidId() {
        // Arrange
        Integer bookId = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            bookRatingService.getBookDetails(bookId);
        });
    }

    @Test
    public void getBookDetails_NoRating() {
        // Arrange
        Integer bookId = 1;

        when(bookRatingRepository.getAverageRatingById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            bookRatingService.getBookDetails(bookId);
        });
    }

    @Test
    public void getBookDetails_BookNotFoundInGutendex() {
        // Arrange
        Integer bookId = 1;

        when(bookRatingRepository.getAverageRatingById(bookId)).thenReturn(Optional.of(4.5));
        when(gutendexService.searchBooksById(bookId)).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            bookRatingService.getBookDetails(bookId);
        });
    }






    //------------------------------------------//
    @Test
    public void getTopNOrderByAvg_ValidN() {
        // Arrange
        Integer N = 5;
        List<Integer> bookIds = Arrays.asList(1, 2, 3, 4, 5);
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        books.add(new Book());
        books.add(new Book());
        books.add(new Book());
        books.add(new Book());

        when(bookRatingRepository.getTopNOrderByAvg()).thenReturn(bookIds);
        when(gutendexService.searchBooksByListIds(bookIds)).thenReturn(books);
        when(bookRatingRepository.getAverageRatingById(anyInt())).thenReturn(Optional.of(4.5));
        when(bookRatingRepository.getReviewsById(anyInt())).thenReturn(new ArrayList<>());

        // Act
        List<BookDetailsDTO> result = bookRatingService.getTopNOrderByAvg(N);

        // Assert
        assertNotNull(result);
        assertEquals(N, result.size());
    }

    @Test
    public void getTopNOrderByAvg_NullN() {

        assertThrows(InvalidInputException.class, () -> {
            bookRatingService.getTopNOrderByAvg(null);
        });
    }

    @Test
    public void getTopNOrderByAvg_InvalidN() {

        assertThrows(InvalidInputException.class, () -> {
            bookRatingService.getTopNOrderByAvg(0);
        });
    }

    @Test
    public void getTopNOrderByAvg_NoRatings() {
        // Arrange
        Integer N = 5;
        List<Integer> bookIds = Arrays.asList(1, 2, 3, 4, 5);
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        books.add(new Book());
        books.add(new Book());
        books.add(new Book());
        books.add(new Book());

        when(bookRatingRepository.getTopNOrderByAvg()).thenReturn(bookIds);
        when(gutendexService.searchBooksByListIds(bookIds)).thenReturn(books);
        when(bookRatingRepository.getAverageRatingById(Mockito.anyInt())).thenReturn(Optional.empty());

        // Assert
        assertThrows(InvalidInputException.class, () -> {
            bookRatingService.getTopNOrderByAvg(N);
        });
    }
}
