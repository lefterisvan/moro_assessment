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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookRatingServiceImpl implements BookRatingService {

    private final BookRatingRepository bookRatingRepository;
    private final ModelMapper modelMapper;
    private final GutendexService gutendexService;

    /**
     * Receives the rating by the user, looking to Gutendex if the book exists and if is true then saves the rating and the review
     * @param bookRatingDto
     * @return
     * @throws SQLException
     */
    @Override
    @Transactional
    public String bookRating(BookRatingDto bookRatingDto) throws SQLException {
        log.info("-- bookRating is starting with input "+bookRatingDto);
        if(bookRatingDto==null) throw new InvalidInputException("The rating must not be null");
        // looking to Gutendex for the book existence
        List<Book> books=gutendexService.searchBooksById(bookRatingDto.getBookId());
        if(books.isEmpty()) throw new EntityNotFoundException("The book that you are looking for does not exist");
        log.info("the book that you are looking for is found "+books.get(0));
        //mapping the objects
        BookRating bookRating=modelMapper.map(bookRatingDto,BookRating.class);
        //saves the object
        bookRating=bookRatingRepository.save(bookRating);
        if(bookRating==null) throw new SQLException("Something went wrong in database. Please check the connection");
        log.info("the rating for the book "+books.get(0).getId()+" is saved successfully ");
        log.info("-- bookRating ends --");
        return "Thank you for your rating";
    }

    /**
     * Retrieves the details of a book. The details are the combination  of the information that the DB holds and the information by Gutendex
     * @param id
     * @return
     */
    @Override
    @Transactional
    @Cacheable(value = "getBookDetails")// here is the cache mechanism of the operation
    public BookDetailsDTO getBookDetails(Integer id) {
        log.info("-- getBookDetails is starting with input "+id);
        if (id==null) throw new InvalidInputException("The id must have a value");
        if(id<1) throw new InvalidInputException("The id must be greater than 0");
        // Fetch the average rating of a book
        Double avgRating= bookRatingRepository.getAverageRatingById(id).orElseThrow(()->new EntityNotFoundException("There is no rating for this book"));
        log.info("book with id= "+id+" has been found in database and has average rating "+avgRating);
        //gets the review of a book
        List<String> reviews=bookRatingRepository.getReviewsById(id);
        log.info("reviews for thr book with id= "+id+" are "+reviews);
        //retrieves the info from Gutendex
        List<Book> books=gutendexService.searchBooksById(id);
        if(books.isEmpty()) throw new EntityNotFoundException("The book that you are looking for does not exist in Gutendex");
        log.info("info for the book by Gutendex is "+ books.get(0));
        //Mapping the objects
        BookDetailsDTO bookDetails= modelMapper.map(books.get(0),BookDetailsDTO.class);
        bookDetails.setRating(avgRating);
        bookDetails.setReviews(reviews);
        log.info("-- getBookDetails finishes with output "+bookDetails);
        return bookDetails;
    }

    /**
     * Clears the cache from the getBookDetails every 5 minutes
     */
    @Scheduled(fixedDelay = 300000) // Runs every 5 minutes
    @CacheEvict(value = "getBookDetails", allEntries = true)
    public void evictCache() {
        log.info("Evicting getBookDetails cache");
    }

    /**
     * Calculates the average rating of the top N books. The N is been populated by the user
     * @param N
     * @return
     */
    @Override
    @Transactional
    public List<BookDetailsDTO> getTopNOrderByAvg(Integer N) {
        log.info("-- getTopNOrderByAvg is starting with input "+N);
        if (N==null) throw new InvalidInputException("The N must have a value");
        if(N<1) throw new InvalidInputException("The N must be greater than 0");
        //fetch the book ids with the average rating and then make a sublist dependent on the N
        List<Integer> ids=bookRatingRepository.getTopNOrderByAvg().subList(0,  N);
        //Retrieves the info for the book from Gutendex
        List<Book> booksByGutendex=gutendexService.searchBooksByListIds(ids);

        //mapping the objects
        List<BookDetailsDTO> topBookDetails=booksByGutendex.stream().map(book -> {
            BookDetailsDTO bookDetails=modelMapper.map(book,BookDetailsDTO.class);
            Double avgRating= bookRatingRepository.getAverageRatingById(book.getId()).get();
            bookDetails.setRating(avgRating);
            List<String> reviews=bookRatingRepository.getReviewsById(book.getId());
            bookDetails.setReviews(reviews);
            return bookDetails;
        }).collect(Collectors.toList());
        log.info("-- getTopNOrderByAvg ends with output "+topBookDetails);
        return topBookDetails;


    }


}
