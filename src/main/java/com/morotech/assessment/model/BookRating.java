package com.morotech.assessment.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "book_rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @CreationTimestamp
    private Date creationDate;




}
