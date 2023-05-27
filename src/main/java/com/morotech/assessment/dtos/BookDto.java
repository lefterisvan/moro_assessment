package com.morotech.assessment.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.morotech.assessment.model.Person;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookDto {
    private int id;
    private String title;
    private List<PersonDto> authors=new ArrayList<>();
    private int downloadCount;
    private List<String> languages=new ArrayList<>();

}
