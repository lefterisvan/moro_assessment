package com.morotech.assessment.serviceTest;

import com.morotech.assessment.dtos.BookDto;
import com.morotech.assessment.dtos.PersonDto;
import com.morotech.assessment.dtos.ResponseBook;
import com.morotech.assessment.exceptions.InvalidInputException;
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
import org.springframework.http.*;
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

    @InjectMocks
    GutendexServiceImpl gutendexService;
    private HttpHeaders headers;
    private static final String DOMAIN = "https://gutendex.com/";

    @BeforeEach
    public  void init() {

        headers = new HttpHeaders();
        List<MediaType> media = Arrays.asList(MediaType.APPLICATION_JSON);
        headers.setAccept(media);
        gutendexService.init();
    }

    //testing searchBooksByTitle operation
    @Test
    public void successCallForSearchByTitle() throws URISyntaxException {

        String title = "dickens%20great";
        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN + "books?search=" + title;
        ResponseEntity<String> response = new ResponseEntity<>(getSearchBookByTitleSuccessResponse(), HttpStatus.OK);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(requestEntity, String.class))
                .thenReturn(response);
        ResponseBook results = gutendexService.searchBooksByTitle(title);
        ResponseBook expected = new ResponseBook();
        expected.setBooks(getSearchByTitleResults());
        assertEquals(expected, results);
    }

    @Test
    public void notFound_CallForSearchByTitle() throws URISyntaxException {
        String title = "vandoros";

        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN + "books?search=" + title;
        ResponseEntity<String> response = new ResponseEntity<>(getNotFoundResponse(), HttpStatus.OK);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenReturn(response);
        ResponseBook results = gutendexService.searchBooksByTitle(title);
        ResponseBook expected = new ResponseBook();


        assertEquals(expected, results);
    }

    @Test
    public void connectionError_CallForSearchByTitle() throws URISyntaxException {
        String title = "dickens%20great";

        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN + "books?search=" + title;

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Client Error"));


        try {
            gutendexService.searchBooksByTitle(title);


        } catch (HttpClientErrorException e) {
            assertEquals("400 Client Error", e.getMessage());
        }
    }

    @Test
    public void serverUnavailable_CallForSearchByTitle() throws URISyntaxException {
        String title = "dickens%20great";

        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN + "books?search=" + title;

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "server is down"));


        try {
            gutendexService.searchBooksByTitle(title);

        } catch (HttpServerErrorException e) {
            assertEquals("500 server is down", e.getMessage());
        }
    }
//--------------------------------------------------------------------------------------------//
    //testing searchBooksById operation
    @Test
    public void successCall_searchById() throws URISyntaxException {
        Integer id=1;
        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN + "books?ids="+id ;
        ResponseEntity<String> response = new ResponseEntity<>(getSearchBookByIdSuccessResponse(), HttpStatus.OK);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenReturn(response);
        List<Book> results = gutendexService.searchBooksById(id);


        assertEquals(getResultsById(), results);
    }

    @Test
    public void notFound_SearchById() throws URISyntaxException {
        Integer id = 0;

        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN + "books?ids=" + id;
        ResponseEntity<String> response = new ResponseEntity<>(getNotFoundResponse(), HttpStatus.OK);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenReturn(response);
        List<Book> results = gutendexService.searchBooksById(id);

        assertEquals(new ArrayList<>(), results);
    }

    @Test
    public void connectionError_CallForSearchById() throws URISyntaxException {
        Integer id = 1;

        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN + "books?ids=" + id;

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Client Error"));


        try {
            gutendexService.searchBooksById(id);


        } catch (HttpClientErrorException e) {
            assertEquals("400 Client Error", e.getMessage());
        }
    }
    @Test
    public void serverUnavailable_CallForSearchById() throws URISyntaxException {
        Integer id = 1;

        HttpMethod method = HttpMethod.GET;
        String url = DOMAIN + "books?ids=" + id;

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, method, new URI(url));

        Mockito.when(restTemplate.exchange(eq(requestEntity), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "server is down"));


        try {
            gutendexService.searchBooksById(id);

        } catch (HttpServerErrorException e) {
            assertEquals("500 server is down", e.getMessage());
        }
    }



    private List<Book> getResultsById()
    {
        List<Book> results = new ArrayList<>();
        Book book= new Book();
        book.setId(1);
        book.setTitle("The Declaration of Independence of the United States of America");
        book.setLanguages(Arrays.asList("en"));
        book.setCopyright(false);
        book.setMediaType("Text");
        book.setDownloadCount(1548);
        book.setBookshelves(Arrays.asList("American Revolutionary War","Politics","United States Law"));
        book.setSubjects(Arrays.asList(    "United States -- History -- Revolution, 1775-1783 -- Sources", "United States. Declaration of Independence"));
        List<Person> authors=new ArrayList<>();
        authors.add(new Person("Jefferson, Thomas",1743,1826));
        book.setAuthors(authors);

        Map<String, String> format = new HashMap<>();
        format.put("application/x-mobipocket-ebook", "https://www.gutenberg.org/ebooks/1.kf8.images");
        format.put("application/epub+zip", "https://www.gutenberg.org/ebooks/1.epub3.images");
        format.put("text/html", "https://www.gutenberg.org/ebooks/1.html.images");
        format.put("image/jpeg", "https://www.gutenberg.org/cache/epub/1/pg1.cover.medium.jpg");
        format.put("text/plain; charset=us-ascii", "https://www.gutenberg.org/files/1/1-0.txt");
        format.put("text/plain", "https://www.gutenberg.org/ebooks/1.txt.utf-8");
        format.put("application/rdf+xml", "https://www.gutenberg.org/ebooks/1.rdf");

        book.setFormats(format);
        results.add(book);

        return results;
    }

    private List<BookDto> getSearchByTitleResults() {
        List<BookDto> results = new ArrayList<>();
        BookDto book1 = new BookDto();
        book1.setId(1400);
        book1.setTitle("Great Expectations");
        book1.setAuthors(Arrays.asList(new PersonDto("Dickens, Charles", 1812, 1870)));
        book1.setLanguages(Arrays.asList("en"));
        book1.setDownloadCount(17376);
        results.add(book1);


        BookDto book2 = new BookDto();
        book2.setId(8608);
        book2.setTitle("Great Expectations");
        book2.setAuthors(Arrays.asList(new PersonDto("Dickens, Charles", 1812, 1870)));
        book2.setLanguages(Arrays.asList("en"));
        book2.setDownloadCount(37);
        results.add(book2);
        return results;
    }

    private String getNotFoundResponse() {
        return " \"count\": 0,\n" +
                "    \"next\": null,\n" +
                "    \"previous\": null,\n" +
                "    \"results\": []";
    }
    private String getSearchBookByIdSuccessResponse()
    {
        return "{\n" +
                "  \"count\": 1,\n" +
                "  \"next\": null,\n" +
                "  \"previous\": null,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"title\": \"The Declaration of Independence of the United States of America\",\n" +
                "      \"authors\": [\n" +
                "        {\n" +
                "          \"name\": \"Jefferson, Thomas\",\n" +
                "          \"birth_year\": 1743,\n" +
                "          \"death_year\": 1826\n" +
                "        }\n" +
                "      ],\n" +
                "      \"translators\": [],\n" +
                "      \"subjects\": [\n" +
                "        \"United States -- History -- Revolution, 1775-1783 -- Sources\",\n" +
                "        \"United States. Declaration of Independence\"\n" +
                "      ],\n" +
                "      \"bookshelves\": [\n" +
                "        \"American Revolutionary War\",\n" +
                "        \"Politics\",\n" +
                "        \"United States Law\"\n" +
                "      ],\n" +
                "      \"languages\": [\n" +
                "        \"en\"\n" +
                "      ],\n" +
                "      \"copyright\": false,\n" +
                "      \"media_type\": \"Text\",\n" +
                "      \"formats\": {\n" +
                "        \"application/x-mobipocket-ebook\": \"https://www.gutenberg.org/ebooks/1.kf8.images\",\n" +
                "        \"application/epub+zip\": \"https://www.gutenberg.org/ebooks/1.epub3.images\",\n" +
                "        \"text/html\": \"https://www.gutenberg.org/ebooks/1.html.images\",\n" +
                "        \"image/jpeg\": \"https://www.gutenberg.org/cache/epub/1/pg1.cover.medium.jpg\",\n" +
                "        \"text/plain; charset=us-ascii\": \"https://www.gutenberg.org/files/1/1-0.txt\",\n" +
                "        \"text/plain\": \"https://www.gutenberg.org/ebooks/1.txt.utf-8\",\n" +
                "        \"application/rdf+xml\": \"https://www.gutenberg.org/ebooks/1.rdf\"\n" +
                "      },\n" +
                "      \"download_count\": 1548\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    private String getSearchBookByTitleSuccessResponse() {
        return "{\n" +
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
