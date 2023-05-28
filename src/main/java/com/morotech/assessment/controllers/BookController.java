package com.morotech.assessment.controllers;

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
    public String bookRating(@RequestBody @Valid BookRatingDto bookRatingDto) throws SQLException {

        return  bookRatingService.bookRating(bookRatingDto);
    }


}
