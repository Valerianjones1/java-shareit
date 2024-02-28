package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final ModelMapper mapper;

    @Override
    public List<UserDto> getAll() {
        return repo.getAll().stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = mapper.map(userDto, User.class);
        User savedUser = repo.create(user);
        return mapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto get(int id) {
        User user = repo.get(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", id)));
        return mapper.map(user, UserDto.class);
    }

    @Override
    public void remove(int id) {
        repo.remove(id);
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = mapper.map(userDto, User.class);
        User foundUser = repo.get(user.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь для обновления с идентификатором %s не найден",
                                user.getId())));

        String oldEmail = foundUser.getEmail();
        User updatedUser = fillUser(user, foundUser);
        repo.update(updatedUser, oldEmail);
        return mapper.map(updatedUser, UserDto.class);
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
