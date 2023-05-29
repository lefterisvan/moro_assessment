package com.morotech.assessment.services.implementation;

import com.morotech.assessment.dtos.BookDetailsDTO;
import com.morotech.assessment.dtos.BookRatingDto;
import com.morotech.assessment.exceptions.InvalidInputException;
import com.morotech.assessment.model.Book;
import com.morotech.assessment.model.BookRating;
import com.morotech.assessment.repositories.BookRatingRepository;
import com.morotech.assessment.services.BookRatingService;
import com.morotech.assessment.services.GutendexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookRatingServiceImpl implements BookRatingService {

    private final BookRatingRepository bookRatingRepository;
    private final ModelMapper modelMapper;
    private final GutendexService gutendexService;
    @Override
    @Transactional
    public String bookRating(BookRatingDto bookRatingDto) throws SQLException {
        log.info("-- bookRating is starting with input "+bookRatingDto);
        if(bookRatingDto==null) throw new InvalidInputException("The rating must not be null");
        List<Book> books=gutendexService.searchBooksById(bookRatingDto.getBookId());
        if(books.isEmpty()) throw new EntityNotFoundException("The book that you are looking for does not exist");
        log.info("the book that you are looking for is found "+books.get(0));
        BookRating bookRating=modelMapper.map(bookRatingDto,BookRating.class);
        bookRating=bookRatingRepository.save(bookRating);
        if(bookRating==null) throw new SQLException("Something went wrong in database. Please check the connection");
        log.info("the rating for the book "+books.get(0).getId()+" is saved successfully ");
        log.info("-- bookRating ends --");
        return "Thank you for your rating";
    }

    @Override
    @Transactional
    public BookDetailsDTO getBookDetails(Integer id) {
        log.info("-- getBookDetails is starting with input "+id);
        if (id==null) throw new InvalidInputException("The id must have a value");
        if(id<1) throw new InvalidInputException("The id must be greater than 0");
        Double avgRating= bookRatingRepository.getAverageRatingById(id).orElseThrow(()->new EntityNotFoundException("There is no rating for this book"));
        log.info("book with id= "+id+" has been found in database and has average rating "+avgRating);
        List<String> reviews=bookRatingRepository.getReviewsById(id);
        log.info("reviews for thr book with id= "+id+" are "+reviews);
        List<Book> books=gutendexService.searchBooksById(id);
        if(books.isEmpty()) throw new EntityNotFoundException("The book that you are looking for does not exist in Gutendex");
        log.info("info for the book by Gutendex is "+ books.get(0));
        BookDetailsDTO bookDetails= modelMapper.map(books.get(0),BookDetailsDTO.class);
        bookDetails.setRating(avgRating);
        bookDetails.setReviews(reviews);
        log.info("-- getBookDetails finishes with output "+bookDetails);
        return bookDetails;
    }
}
