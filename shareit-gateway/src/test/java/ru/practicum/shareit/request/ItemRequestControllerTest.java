package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestClient itemRequestClient;
    @Autowired
    private MockMvc mvc;

    private ItemRequestCreateDto itemRequestCreateDto;

    @Test
    void shouldCreateItemRequestResponse() throws Exception {
        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("test");

        Mockito
                .when(itemRequestClient.createRequest(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllItemRequestsResponse() throws Exception {

        Mockito
                .when(itemRequestClient.getRequests(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetItemRequestByIdResponse() throws Exception {

        Mockito
                .when(itemRequestClient.getRequest(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetItemRequestsAllResponse() throws Exception {
        Mockito
                .when(itemRequestClient.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "20")
                        .param("from", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotCreateItemRequestWhenDtoIsNotValid() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("", "Ошибка с валидацией");

        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription(null);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }

    @Test
    void shouldNotGetAllItemRequestsByRequestorIdNotWhenFromIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "20")
                        .param("from", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }

    @Test
    void shouldNotGetAllItemRequestsByRequestorIdNotWhenSizeIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "-11")
                        .param("from", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }
}
