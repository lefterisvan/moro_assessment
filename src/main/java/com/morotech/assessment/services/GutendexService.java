package com.morotech.assessment.services;

import com.morotech.assessment.dtos.BookDto;
import com.morotech.assessment.dtos.ResponseBook;
import com.morotech.assessment.model.Book;

import java.util.List;

public interface GutendexService {
        public ResponseBook searchBooksByTitle(String title);
        public  List<Book> searchBooksById(Integer id);

}
