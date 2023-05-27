package com.morotech.assessment.serviceTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morotech.assessment.AssessmentApplicationTests;
import com.morotech.assessment.dtos.BookDto;
import com.morotech.assessment.dtos.PersonDto;
import com.morotech.assessment.dtos.ResponseBook;
import com.morotech.assessment.model.Book;
import com.morotech.assessment.model.Person;
import com.morotech.assessment.services.implementation.GutendexServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class GutendexServiceTest {


    @Mock
    private RestTemplate restTemplate;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    GutendexServiceImpl gutendexService;
    private HttpHeaders headers;
    private static final String DOMAIN="https://gutendex.com/";

    @BeforeEach
    void initUseCase() {

        modelMapper = new ModelMapper();
        headers = new HttpHeaders();
        List<MediaType> media= Arrays.asList(MediaType.APPLICATION_JSON);
        headers.setAccept(media);


    }


    @Test
    public void successCallForSearchByTitle() throws URISyntaxException {
        String title="dickens%20great";
        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN+"books?search="+title;
        ResponseEntity<String> response = new ResponseEntity<>(getSearchSuccessResponse(), HttpStatus.OK);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

    Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenReturn(response);
        ResponseBook results=gutendexService.searchBooksByTitle(title);
        ResponseBook expected=new ResponseBook();
        expected.setBooks(getResults());
        assertEquals(expected, results);
    }
    @Test
    public void notFound_CallForSearchByTitle() throws URISyntaxException {
        String title="vandoros";

        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN+"books?search="+title;
        ResponseEntity<String> response = new ResponseEntity<>(getNotFoundResponse(), HttpStatus.OK);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenReturn(response);
        ResponseBook results=gutendexService.searchBooksByTitle(title);
        ResponseBook expected=new ResponseBook();


        assertEquals(expected, results);
    }
    @Test
    public void connectionError_CallForSearchByTitle() throws URISyntaxException {
        String title="dickens%20great";

        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN+"books?search="+title;

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Client Error"));


        try{
             gutendexService.searchBooksByTitle(title);


        }catch (HttpClientErrorException e)
        {
            assertEquals("400 Client Error", e.getMessage());
        }
    }
    @Test
    public void serverUnavailable_CallForSearchByTitle() throws URISyntaxException {
        String title="dickens%20great";

        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN+"books?search="+title;

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "server is down"));


        try{
            gutendexService.searchBooksByTitle(title);

        }catch (HttpServerErrorException e)
        {
            assertEquals("500 server is down", e.getMessage());
        }
    }


    private List<BookDto> getResults()
    {
        List<BookDto> results= new ArrayList<>();
        BookDto book1=new BookDto();
        book1.setId(1400);
        book1.setTitle("Great Expectations");
        book1.setAuthors(Arrays.asList(new PersonDto("Dickens, Charles",1812,1870)));
        book1.setLanguages(Arrays.asList("en"));
        book1.setDownloadCount(17376);
        results.add(book1);


        BookDto book2=new BookDto();
        book2.setId(8608);
        book2.setTitle("Great Expectations");
        book2.setAuthors(Arrays.asList(new PersonDto("Dickens, Charles",1812,1870)));
        book2.setLanguages(Arrays.asList("en"));
        book2.setDownloadCount(37);
        results.add(book2);
        return results;
    }
    private String getNotFoundResponse()
    {
        return " \"count\": 0,\n" +
                "    \"next\": null,\n" +
                "    \"previous\": null,\n" +
                "    \"results\": []";
    }

    private String getSearchSuccessResponse()
    {
        return  "{\n" +
                "  \"count\": 2,\n" +
                "  \"next\": null,\n" +
                "  \"previous\": null,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": 1400,\n" +
                "      \"title\": \"Great Expectations\",\n" +
                "      \"authors\": [\n" +
                "        {\n" +
                "          \"name\": \"Dickens, Charles\",\n" +
                "          \"birth_year\": 1812,\n" +
                "          \"death_year\": 1870\n" +
                "        }\n" +
                "      ],\n" +
                "      \"translators\": [],\n" +
                "      \"subjects\": [\n" +
                "        \"Benefactors -- Fiction\",\n" +
                "        \"Bildungsromans\",\n" +
                "        \"England -- Fiction\",\n" +
                "        \"Ex-convicts -- Fiction\",\n" +
                "        \"Man-woman relationships -- Fiction\",\n" +
                "        \"Orphans -- Fiction\",\n" +
                "        \"Revenge -- Fiction\",\n" +
                "        \"Young men -- Fiction\"\n" +
                "      ],\n" +
                "      \"bookshelves\": [\n" +
                "        \"Best Books Ever Listings\"\n" +
                "      ],\n" +
                "      \"languages\": [\n" +
                "        \"en\"\n" +
                "      ],\n" +
                "      \"copyright\": false,\n" +
                "      \"media_type\": \"Text\",\n" +
                "      \"formats\": {\n" +
                "        \"application/x-mobipocket-ebook\": \"https://www.gutenberg.org/ebooks/1400.kf8.images\",\n" +
                "        \"application/epub+zip\": \"https://www.gutenberg.org/ebooks/1400.epub3.images\",\n" +
                "        \"image/jpeg\": \"https://www.gutenberg.org/cache/epub/1400/pg1400.cover.medium.jpg\",\n" +
                "        \"text/plain; charset=utf-8\": \"https://www.gutenberg.org/files/1400/1400-0.txt\",\n" +
                "        \"text/html; charset=utf-8\": \"https://www.gutenberg.org/files/1400/1400-h/1400-h.htm\",\n" +
                "        \"text/html\": \"https://www.gutenberg.org/ebooks/1400.html.images\",\n" +
                "        \"application/rdf+xml\": \"https://www.gutenberg.org/ebooks/1400.rdf\"\n" +
                "      },\n" +
                "      \"download_count\": 17376\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 8608,\n" +
                "      \"title\": \"Great Expectations\",\n" +
                "      \"authors\": [\n" +
                "        {\n" +
                "          \"name\": \"Dickens, Charles\",\n" +
                "          \"birth_year\": 1812,\n" +
                "          \"death_year\": 1870\n" +
                "        }\n" +
                "      ],\n" +
                "      \"translators\": [],\n" +
                "      \"subjects\": [\n" +
                "        \"Benefactors -- Fiction\",\n" +
                "        \"Bildungsromans\",\n" +
                "        \"England -- Fiction\",\n" +
                "        \"Ex-convicts -- Fiction\",\n" +
                "        \"Man-woman relationships -- Fiction\",\n" +
                "        \"Orphans -- Fiction\",\n" +
                "        \"Revenge -- Fiction\",\n" +
                "        \"Young men -- Fiction\"\n" +
                "      ],\n" +
                "      \"bookshelves\": [\n" +
                "        \"Best Books Ever Listings\"\n" +
                "      ],\n" +
                "      \"languages\": [\n" +
                "        \"en\"\n" +
                "      ],\n" +
                "      \"copyright\": true,\n" +
                "      \"media_type\": \"Sound\",\n" +
                "      \"formats\": {\n" +
                "        \"audio/mpeg\": \"http://www.gutenberg.org/files/8608/mp3/8608-094.mp3\",\n" +
                "        \"application/zip\": \"http://www.gutenberg.org/files/8608/8608-mp3.zip\",\n" +
                "        \"application/rdf+xml\": \"http://www.gutenberg.org/ebooks/8608.rdf\",\n" +
                "        \"text/plain; charset=us-ascii\": \"http://www.gutenberg.org/files/8608/8608-readme.txt\",\n" +
                "        \"text/html; charset=iso-8859-1\": \"http://www.gutenberg.org/files/8608/8608-index.htm\"\n" +
                "      },\n" +
                "      \"download_count\": 37\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

}
