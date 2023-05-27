package com.morotech.assessment.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseBook {
    List<BookDto> books=new ArrayList<>();
}
