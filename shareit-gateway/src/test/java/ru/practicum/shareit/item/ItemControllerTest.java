package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;
    private ItemCreateDto itemCreateDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setId(1L);
        itemCreateDto.setName("test name");
        itemCreateDto.setRequestId(2L);
        itemCreateDto.setAvailable(true);
        itemCreateDto.setDescription("test desc");

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("test text");
        commentDto.setAuthorName("test author");
    }

    @Test
    void shouldNotCreateItemWhenDtoIsNotValid() throws Exception {
        itemCreateDto.setDescription("");
        ErrorResponse errorResponse = new ErrorResponse("", "Ошибка с валидацией");

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }

    @Test
    void shouldNotGetAllItemsOfOwnerWhenFromIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "20")
                        .param("from", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }

    @Test
    void shouldNotGetAllItemsOfOwnerWhenSizeIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "-1")
                        .param("from", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }

    @Test
    void shouldNotSearchItemsWhenFromIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/items/search")
                        .param("size", "20")
                        .param("from", "-1")
                        .param("text", "test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }

    @Test
    void shouldNotSearchItemsWhenSizeIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/items/search")
                        .param("size", "-1")
                        .param("from", "1")
                        .param("text", "test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }

    @Test
    void shouldNotCreateCommentWhenDtoIsNotValid() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("", "Ошибка с валидацией");

        commentDto.setText(null);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }
}
