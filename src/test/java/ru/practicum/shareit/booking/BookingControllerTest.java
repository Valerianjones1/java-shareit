package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.CommentDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingCreateDto bookingCreateDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setId(1L);
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setItemId(1L);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("test text");
        commentDto.setAuthorName("test author");
    }

    @Test
    void shouldCreateBooking() throws Exception {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(BookingMapper.mapToBooking(bookingCreateDto));
        bookingDto.setStatus(BookingStatus.WAITING);

        Mockito
                .when(bookingService.create(any(BookingCreateDto.class), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker())));
    }

    @Test
    void shouldUpdateBookingStatus() throws Exception {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(BookingMapper.mapToBooking(bookingCreateDto));
        bookingDto.setStatus(BookingStatus.APPROVED);

        Mockito
                .when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingCreateDto.getId())
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker())));
    }

    @Test
    void shouldGetBookingById() throws Exception {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(BookingMapper.mapToBooking(bookingCreateDto));
        bookingDto.setStatus(BookingStatus.APPROVED);

        Mockito
                .when(bookingService.get(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingCreateDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker())));
    }

    @Test
    void shouldGetBookingsOfOwner() throws Exception {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(BookingMapper.mapToBooking(bookingCreateDto));
        bookingDto.setStatus(BookingStatus.APPROVED);

        Mockito
                .when(bookingService.getAllByUser(anyLong(), any(BookingState.class), any(Pageable.class)))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker())));
    }

    @Test
    void shouldGetBookingsByUser() throws Exception {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(BookingMapper.mapToBooking(bookingCreateDto));
        bookingDto.setStatus(BookingStatus.APPROVED);

        Mockito
                .when(bookingService.getAllByOwnerItems(anyLong(), any(BookingState.class), any(Pageable.class)))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker())));
    }

}
