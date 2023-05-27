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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GutendexServiceImpl implements GutendexService {
   // private  RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
   private final RestTemplate restTemplate;
   private final ModelMapper mapper;

    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpHeaders headers;
    private static final String DOMAIN="https://gutendex.com/";
    @Override
    public ResponseBook searchBooksByTitle(String title) {
        if (StringUtils.isEmpty(title)) throw new InvalidInputException("The title can not be empty for this operation");
        List <Book> results=new ArrayList<>();
        title=title.toLowerCase(Locale.ROOT);
        title=title.replaceAll(" ","%20");
        String url = DOMAIN+"books?search="+title;

        HttpMethod method = HttpMethod.GET;
        try {
            headers = new HttpHeaders();
            List<MediaType> media= Arrays.asList(MediaType.APPLICATION_JSON);
            headers.setAccept(media);
            RequestEntity<?> requestEntity = new RequestEntity<>(headers, method, new URI(url));

            // Send the HTTP request and receive the response
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            // Extract the response body
            String responseBody = responseEntity.getBody();

            // Process the JSON response
            GutendexDto gutendexDto=objectMapper.readValue(responseBody,GutendexDto.class);


            log.info("Response: " + gutendexDto.getResults());
            results=gutendexDto.getResults();
        } catch (URISyntaxException | JsonProcessingException e) {
            log.error( "Something went wrong with the Call to Gutendex "+e);

        }
        List<BookDto> books=results.stream().map(result->mapper.map(result,BookDto.class)).collect(Collectors.toList());
        ResponseBook responseBook=new ResponseBook();
        responseBook.setBooks(books);
        return responseBook;
    }
}
