package com.morotech.assessment.services;

import com.morotech.assessment.model.Book;

import java.util.List;

public interface GutendexService {
        public List<Book> searchBooksByTitle(String title);

}
