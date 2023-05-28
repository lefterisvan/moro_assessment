package com.morotech.assessment.model;

import lombok.*;

import javax.persistence.*;

@Entity(name="book_rating")
@Table(name = "book_rating")
@Data
@RequiredArgsConstructor
@ToString
public class BookRating {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "rating")
    private Integer rating;
    @Column(name = "review")
    private String review;




}
