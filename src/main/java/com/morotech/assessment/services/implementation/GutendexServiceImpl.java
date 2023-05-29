package com.morotech.assessment.services.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morotech.assessment.dtos.BookDto;
import com.morotech.assessment.dtos.GutendexDto;
import com.morotech.assessment.dtos.ResponseBook;
import com.morotech.assessment.exceptions.InvalidInputException;
import com.morotech.assessment.model.Book;
import com.morotech.assessment.services.GutendexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * This class is an implementation of the GutendexSerice.
 * It is configured to communicate  with Gutendex in order to retrieve data for the books of the Gutendex database
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GutendexServiceImpl implements GutendexService {
    private final RestTemplate restTemplate;
    private final ModelMapper mapper;

    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();
    private static final String DOMAIN = "https://gutendex.com/";
    private List<MediaType> media = Arrays.asList(MediaType.APPLICATION_JSON);

    @PostConstruct
    public void init()
    {
        headers.setAccept(media);
    }

    /**
     * Gets as parameter the title of a book. Creates a valid HTTP request with HTTP Client API
     * and makes a GET request towards Gutendex in order to retrieve data for the book regarding the given title
     * @param title
     * @return
     */
    @Override
    public ResponseBook searchBooksByTitle(String title) {

        log.info("-- searchBooksByTitle is starting with title "+title);
        if (StringUtils.isEmpty(title))
            throw new InvalidInputException("The title can not be empty for this operation");
        List<Book> booksFromRequest = new ArrayList<>();
        title = title.toLowerCase(Locale.ROOT);
        title = title.replaceAll(" ", "%20");
        String url = DOMAIN + "books?search=" + title;

        HttpMethod method = HttpMethod.GET;
        try {



            log.info("iam going to invoke Gutendex "+url);
            RequestEntity<?> requestEntity = new RequestEntity<>(headers, method, new URI(url));

            // Send the HTTP request and receive the response
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            log.info("Response from gutendex is " + responseEntity);

            // Extract the response body
            String responseBody = responseEntity.getBody();
            // Process the JSON response
            GutendexDto gutendexDto = objectMapper.readValue(responseBody, GutendexDto.class);


            log.info("Response: " + gutendexDto.getResults());
            booksFromRequest = gutendexDto.getResults();
            log.info("After extraction the response is" + booksFromRequest);
        } catch (URISyntaxException | JsonProcessingException e) {
            log.error("Something went wrong with the Call to Gutendex " + e);

        }
        List<BookDto> booksDto = booksFromRequest.stream().map(book -> mapper.map(book, BookDto.class)).collect(Collectors.toList());
        ResponseBook responseBook = new ResponseBook();
        responseBook.setBooks(booksDto);
        log.info("-- end of searchBooksByTitle with response " + responseBook);
        return responseBook;
    }

    @Override
    public  List<Book> searchBooksById(Integer id) {
        log.info("-- searchBooksById is starting with id "+id);
        if (id<0)
            throw new InvalidInputException("The id must be bigger than 0");

        List<Book> booksFromRequest = new ArrayList<>();

        String url = DOMAIN + "books?ids=" + id;
        HttpMethod method = HttpMethod.GET;

        try {


            log.info("iam going to invoke Gutendex "+url);
            RequestEntity<?> requestEntity = new RequestEntity<>(headers, method, new URI(url));

            // Send the HTTP request and receive the response
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            log.info("Response from gutendex is " + responseEntity);
            // Extract the response body
            String responseBody = responseEntity.getBody();

            // Process the JSON response
            GutendexDto gutendexDto = objectMapper.readValue(responseBody, GutendexDto.class);

            booksFromRequest=gutendexDto.getResults();
            log.info("After extraction the response is" + booksFromRequest);

        } catch (URISyntaxException | JsonProcessingException e) {
            log.error("Something went wrong with the Call to Gutendex " + e);

        }
        log.info("-- end of searchBooksById with response" + booksFromRequest);
        return booksFromRequest;
    }
}
