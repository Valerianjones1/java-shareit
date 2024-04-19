package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserClient userClient;
    @Autowired
    private MockMvc mvc;

    @Test
    void shouldNotCreateUserWhenDtoIsNotValid() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("", "Ошибка с валидацией");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName(null);
        userDto.setEmail(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }

    @Test
    void shouldNotUpdateUserWhenDtoIsNotValid() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("", "Ошибка с валидацией");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("valera");
        userDto.setEmail("testmail");

        mvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }
}
