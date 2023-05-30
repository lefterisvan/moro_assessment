package com.morotech.assessment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookRatingDto {
    @Min(value = 0,message = "The minimum value is 0")
    private int bookId;
    @Min(value = 0,message = "The minimum value is 0")
    @Max(value = 5,message = "The maximum value is 5")
    private int rating;
    private String review;
    private Date creationDate;

}
