package com.morotech.assessment.dtos;

import com.morotech.assessment.model.Book;
import lombok.Data;

import java.util.List;

@Data
public class GutendexDto {
    Integer count;
    String next,previous;
    List <Book> results;
}
