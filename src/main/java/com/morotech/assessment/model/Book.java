package com.morotech.assessment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Book {
    @JsonProperty("id")
    private int id;
    @JsonProperty("download_count")
    private int downloadCount;
    @JsonProperty("title")
    private String title;
    @JsonProperty("media_type")
    private String mediaType;
    @JsonProperty("subjects")
    private List<String> subjects=new ArrayList<>();
    @JsonProperty("bookshelves")
    private List<String>  bookshelves=new ArrayList<>();
    @JsonProperty("languages")
    private List<String> languages=new ArrayList<>();
    @JsonProperty("authors")
    private List<Person> authors=new ArrayList<>();
    @JsonProperty("translators")
    private  List<Person>translators=new ArrayList<>();
    @JsonProperty("copyright")
    private boolean copyright;
    @JsonProperty("formats")
    private Map<String,String> formats=new HashMap<>();



}
