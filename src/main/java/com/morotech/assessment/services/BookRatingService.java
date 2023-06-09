package com.morotech.assessment.services;

import com.morotech.assessment.dtos.BookDetailsDTO;
import com.morotech.assessment.dtos.BookRatingDto;

import java.sql.SQLException;
import java.util.List;

public interface BookRatingService {
    public String bookRating(BookRatingDto bookRatingDto) throws SQLException;
    public BookDetailsDTO getBookDetails(Integer id);
    public List<BookDetailsDTO> getTopNOrderByAvg(Integer N);
}
