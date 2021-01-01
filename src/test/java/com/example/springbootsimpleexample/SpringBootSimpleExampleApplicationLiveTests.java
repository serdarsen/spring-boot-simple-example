package com.example.springbootsimpleexample;

import com.example.springbootsimpleexample.dto.BookDTO;
import com.example.springbootsimpleexample.model.Book;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SpringBootSimpleExampleApplicationLiveTests {
    private static final String API_ROOT = "http://localhost:8081/api/books/";
    private static final String API_CREATE = API_ROOT + "create/";
    private static final String API_UPDATE = API_ROOT + "update/";
    private static final String API_PATCH = API_ROOT + "patch/";
    private static final String API_DELETE = API_ROOT + "delete/";
    private static final String API_TITLE = API_ROOT + "title/";
    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void whenGetAllBooks_thenOK() {
        final Response response = RestAssured.get(API_ROOT);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    public void whenGetBooksByTitle_thenOK() {
        final BookDTO bookDTO = createRandomBookDTO();
        final Response createResponse = request(bookDTO, API_CREATE).post();

        final String createdBookTitle = createResponse.jsonPath().get("title");
        final String findByTitleUri = API_TITLE + createdBookTitle;
        final Response response = RestAssured.get(findByTitleUri);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertTrue(response.as(List.class).size() > 0);
    }

    @Test
    public void whenGetCreatedBookById_thenOK() {
        final BookDTO bookDTO = createRandomBookDTO();
        final Response createResponse = request(bookDTO, API_CREATE).post();

        final Integer createdBookId = createResponse.jsonPath().get("id");
        final String findByIdUri = API_ROOT + createdBookId.longValue();
        final Response response = RestAssured.get(findByIdUri);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(bookDTO.getTitle(), response.jsonPath().get("title"));
    }

    @Test
    public void whenGetNotExistBookById_thenNotFound() {
        final Response response = RestAssured.get(API_ROOT + randomNumeric(4));
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    public void whenCreate_thenCreated() {
        final BookDTO bookDTO = createRandomBookDTO();
        final Response response = request(bookDTO, API_CREATE).post();
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
    }

    @Test
    public void whenInvalidBook_thenError() {
        final BookDTO bookDTO = createRandomBookDTO();
        bookDTO.setAuthor(null);
        final Response response = request(bookDTO, API_CREATE).post();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    public void whenUpdate_thenOK() {
        final BookDTO bookDTO = createRandomBookDTO();
        final Response createResponse = request(bookDTO, API_CREATE).post();

        final BookDTO newBookDTO = createRandomBookDTO();
        final Integer createdBookId = createResponse.jsonPath().get("id");
        bookDTO.setId(createdBookId.longValue());
        bookDTO.setTitle(newBookDTO.getTitle());
        bookDTO.setAuthor(newBookDTO.getAuthor());

        final String updateUri = API_UPDATE + bookDTO.getId();
        final Response updateResponse = request(bookDTO, updateUri).put();

        assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode());
        final Integer updatedBookId = updateResponse.jsonPath().get("id");
        assertEquals(bookDTO.getId(), updatedBookId.longValue());
        assertEquals(bookDTO.getTitle(), updateResponse.jsonPath().get("title"));
        assertEquals(bookDTO.getAuthor(), updateResponse.jsonPath().get("author"));
    }

    @Test
    public void whenPatch_thenOK() {
        final BookDTO bookDTO = createRandomBookDTO();

        final Map<String, String> updates = new HashMap<>();
        updates.put("title", "title" + randomNumeric(9));
        updates.put("author", "author" + randomNumeric(9));

        final Response createResponse = request(bookDTO, API_CREATE).post();

        Integer createdBookId = createResponse.jsonPath().get("id");

        final String updateUri = API_PATCH + createdBookId.longValue();
        final Response updateResponse = request(updates, updateUri).patch();

        assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode());

        final Integer updatedBookId = updateResponse.jsonPath().get("id");
        assertEquals(createdBookId.longValue(), updatedBookId.longValue());

        assertEquals(updates.get("title"), updateResponse.jsonPath().get("title"));
        assertEquals(updates.get("author"), updateResponse.jsonPath().get("author"));
    }

    @Test
    public void whenDeleteCreatedBook_thenOk() {
        final BookDTO bookDTO = createRandomBookDTO();
        final Response createResponse = request(bookDTO, API_CREATE).post();

        final Integer createdBookId = createResponse.jsonPath().get("id");
        final String deleteUri = API_DELETE + createdBookId.longValue();
        Response response = RestAssured.delete(deleteUri);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        response = RestAssured.delete(API_DELETE + 789652);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());

        response = RestAssured.delete(API_DELETE + "bad_id");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    public void whenConvertBookEntityToBookDTO_thenCorrect() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle(randomAlphabetic(6));
        book.setAuthor(randomAlphabetic(6));

        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        assertEquals(book.getId(), bookDTO.getId());
        assertEquals(book.getTitle(), bookDTO.getTitle());
        assertEquals(book.getAuthor(), bookDTO.getAuthor());
    }

    @Test
    public void whenConvertBookDTOToBookEntity_thenCorrect() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setTitle(randomAlphabetic(6));
        bookDTO.setAuthor(randomAlphabetic(6));

        Book book = modelMapper.map(bookDTO, Book.class);
        assertEquals(bookDTO.getId(), book.getId());
        assertEquals(bookDTO.getTitle(), book.getTitle());
        assertEquals(bookDTO.getAuthor(), book.getAuthor());
    }

    // ===============================

    private BookDTO createRandomBookDTO() {
        final BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(randomAlphabetic(10));
        bookDTO.setAuthor(randomAlphabetic(15));
        return bookDTO;
    }

    private RequestSpecification request(Object body, String baseUri) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .baseUri(baseUri)
                .body(body);
    }
}
