package com.morotech.assessment.repositories;

import com.morotech.assessment.model.Book;
import com.morotech.assessment.model.BookRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRatingRepository extends JpaRepository<BookRating, Integer> {

    @Query("SELECT AVG(br.rating) FROM BookRating br where br.bookId=?1")
    public Optional<Double> getAverageRatingById(Integer id);

    @Query("SELECT br.review FROM BookRating br where br.bookId=?1")
    public List<String> getReviewsById(Integer id);

    @Query(value = "SELECT  br.bookId  from BookRating br group by br.bookId order by AVG (br.rating) desc ")
    public List<Integer> getTopNOrderByAvg();



}
