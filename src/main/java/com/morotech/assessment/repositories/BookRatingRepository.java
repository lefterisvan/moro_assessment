package com.morotech.assessment.repositories;

import com.morotech.assessment.model.BookRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRatingRepository extends JpaRepository<BookRating,Integer> {

}
