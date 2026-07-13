package ru.practicum.main.user.service;

import ru.practicum.main.user.dto.NewUserRequestDto;
import ru.practicum.main.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto addUser(NewUserRequestDto newUserRequestDto);

    void deleteUser(Long id);
}
