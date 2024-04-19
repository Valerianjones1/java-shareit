package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository repository;
    @InjectMocks
    private UserServiceImpl mockUserService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@email.ru");
        user.setName("test name");
    }

    @Test
    void shouldCreateUser() {
        Mockito
                .when(repository.save(any(User.class)))
                .thenReturn(user);

        UserDto userDto = mockUserService.create(UserMapper.mapToUserDto(user));

        assertEquals(userDto, UserMapper.mapToUserDto(user));

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldUpdateUser() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        User updatedUser = user;
        updatedUser.setName("updateName");

        Mockito
                .when(repository.save(any(User.class)))
                .thenReturn(updatedUser);

        UserDto userDto = mockUserService.update(new UserUpdateDto(updatedUser.getId(),
                updatedUser.getName(), updatedUser.getEmail()));

        assertEquals(userDto, UserMapper.mapToUserDto(updatedUser));

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldNotUpdateUserWhenUserNotFound() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockUserService.update(new UserUpdateDto(user.getId(),
                        user.getName(), user.getEmail())));

        assertEquals(String.format("Пользователь для обновления с идентификатором %s не найден", user.getId()), exception.getMessage());

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldGetUser() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto userDto = mockUserService.get(user.getId());

        assertEquals(userDto, UserMapper.mapToUserDto(user));

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldNotGetUserWhenUserNotFound() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockUserService.get(user.getId()));

        assertEquals(String.format("Пользователь с идентификатором %s не найден", user.getId()), exception.getMessage());

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteUserWhenUser() {
        mockUserService.remove(user.getId());

        verify(repository, times(1)).deleteById(user.getId());

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldGetAllUser() {
        Mockito
                .when(repository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> users = mockUserService.getAll();

        assertEquals(UserMapper.mapToUserDto(user), users.get(0));

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldNotGetAllUserWhenEmptyUsers() {
        Mockito
                .when(repository.findAll())
                .thenReturn(Collections.emptyList());

        List<UserDto> users = mockUserService.getAll();

        assertTrue(users.isEmpty());
        assertEquals(Collections.emptyList(), users);

        Mockito.verifyNoMoreInteractions(repository);
    }

}
