package com.morotech.assessment.services;

import com.morotech.assessment.dtos.BookRatingDto;

import java.sql.SQLException;

public interface BookRatingService {
    public String bookRating(BookRatingDto bookRatingDto) throws SQLException;
}
