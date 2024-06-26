package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(repository.save(user));
    }

    @Override
    public UserDto get(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", id)));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void remove(long id) {
        repository.deleteById(id);
    }

    @Override
    public UserDto update(UserUpdateDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User foundUser = repository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь для обновления с идентификатором %s не найден", user.getId())));

        User updatedUser = fillUser(user, foundUser);
        return UserMapper.mapToUserDto(repository.save(updatedUser));
    }

    private User fillUser(User newUser, User oldUser) {
        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
        return newUser;
    }
}
