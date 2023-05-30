package com.morotech.assessment.controllers;

import com.morotech.assessment.dtos.BookDetailsDTO;
import com.morotech.assessment.dtos.BookDto;
import com.morotech.assessment.dtos.BookRatingDto;
import com.morotech.assessment.dtos.ResponseBook;
import com.morotech.assessment.services.BookRatingService;
import com.morotech.assessment.services.GutendexService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
@Slf4j
public class BookController {

    private final GutendexService gutendexService;
    private final BookRatingService bookRatingService;
    @GetMapping("/bookSearch")
    @ApiOperation(value = "Gets as an input a title of a book, makes a request towards Gutendex and returns the results")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid input "),
            @ApiResponse(code = 500, message = "Server error/Internal error")
    })
    public ResponseBook bookSearch(@RequestParam String title)
    {
        return gutendexService.searchBooksByTitle(title);
    }


    @PostMapping("/rateABook")
    @ApiOperation(value = "Gets as an input a BookRatingDto (which contains the id of a book, rating and reviews by a reader)," +
            " makes a request towards Gutendex for retrieving the book by the id in order to check if the book exists and then saves the reviews in the database and returns a message if the process completed successfully")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "The rating must not be null "),
            @ApiResponse(code = 404, message = "The book that you are looking for does not exist"),
            @ApiResponse(code = 500, message = "Something went wrong in database. Please check the connection"),
            @ApiResponse(code = 200, message = "Thank you for your rating")
    })
    public String bookRating(@RequestBody @Valid BookRatingDto bookRatingDto) throws SQLException {return  bookRatingService.bookRating(bookRatingDto);}

    @GetMapping("/bookDetails")
    @ApiOperation(value = "Gets as an input the id of a book, searches the book in the database , retrieves the average rating and the reviews. Makes a request towards Gutendex for retrieving the book details and" +
            "returns the book details, book rating and book reviews ")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "The id must have a value "),@ApiResponse(code = 400, message = "The id must be greater than 0"),
            @ApiResponse(code = 404, message = "There is no rating for this book"),
            @ApiResponse(code = 404, message = "The book that you are looking for does not exist in Gutendex"),
            @ApiResponse(code = 200, message = "Returns the BookDetailsDto object")
    })
    public BookDetailsDTO bookDetails(@RequestParam Integer id)
    {
        return bookRatingService.getBookDetails(id);
    }

    @GetMapping("/getBookDetailsByAvgRating")
    @ApiOperation(value = "Gets as an input the number of the books that the user wants to see, searches the books in the database , retrieves the average rating and the reviews. " +
            "Makes a request towards Gutendex by ids for retrieving the details of all the books and returns a list with the books details , average books rating and books reviews ")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "The N must have a value "),@ApiResponse(code = 400, message = "The N must be greater than 0")})
    public List<BookDetailsDTO> getBokDetailsByAvgRating(@RequestParam Integer numberOfBooks)
    {
        return bookRatingService.getTopNOrderByAvg(numberOfBooks);
    }


}
