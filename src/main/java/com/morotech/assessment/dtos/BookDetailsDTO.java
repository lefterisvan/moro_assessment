package com.morotech.assessment.dtos;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookDetailsDTO {
    private int id;
    private String title;
    private List<PersonDto> authors=new ArrayList<>();
    private int downloadCount;
    private List<String> languages=new ArrayList<>();
    private double rating;
    private List<String> reviews=new ArrayList<>();
}
