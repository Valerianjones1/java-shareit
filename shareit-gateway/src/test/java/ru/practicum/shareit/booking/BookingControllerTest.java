package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.error.ErrorResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;
    private BookingCreateDto bookingCreateDto;


    @BeforeEach
    void setUp() {
        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setId(1L);
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setItemId(1L);
    }

    @Test
    void shouldNotCreateBookingWhenDtoIsNotValid() throws Exception {
        bookingCreateDto.setItemId(null);

        ErrorResponse errorResponse = new ErrorResponse("", "Ошибка с валидацией");
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }

    @Test
    void shouldNotGetBookingsByUserWhenFromIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }

    @Test
    void shouldNotGetBookingsByUserWhenSizeIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "-20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }

    @Test
    void shouldNotGetBookingsByUserWhenStateIsNotSupported() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(String.format("Unknown state: %s", "MANY"),
                "Состояние не поддерживается");

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "MANY")
                        .param("from", "1")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())))
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }

    @Test
    void shouldNotGetBookingsByOwnerItemsWhenFromIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }

    @Test
    void shouldNotGetBookingsByOwnerItemsWhenSizeIsNegative() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка",
                "", "");
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())));
    }

    @Test
    void shouldNotGetBookingsByOwnerItemsWhenStateIsNotSupported() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(String.format("Unknown state: %s", "TEST"),
                "Состояние не поддерживается");

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "TEST")
                        .param("from", "1")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())))
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }
//    @Test
//    void shouldGetBookingsOfUserWhenSizeAndFromNull() throws Exception {
//        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
//        bookingDto.setStatus(BookingStatus.APPROVED);
//
//        Mockito
//                .when(bookingService.getAllByUser(anyLong(), any(BookingState.class), any(Pageable.class)))
//                .thenReturn(List.of(bookingDto));
//
//        mvc.perform(get("/bookings")
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("state", "ALL")
//                        .param("from", (String) null)
//                        .param("size", (String) null)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
//                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), ItemDto.class))
//                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
//                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class));
//    }
//@Test
//void shouldGetBookingsByOwnerItemsWhenSizeAndFromNull() throws Exception {
//    BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
//    bookingDto.setStatus(BookingStatus.APPROVED);
//
//    Mockito
//            .when(bookingService.getAllByOwnerItems(anyLong(), any(BookingState.class), any(Pageable.class)))
//            .thenReturn(List.of(bookingDto));
//
//    mvc.perform(get("/bookings/owner")
//                    .header("X-Sharer-User-Id", 1L)
//                    .param("state", "ALL")
//                    .param("from", (String) null)
//                    .param("size", (String) null)
//                    .characterEncoding(StandardCharsets.UTF_8)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
//            .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), ItemDto.class))
//            .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
//            .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class));
//}
}
