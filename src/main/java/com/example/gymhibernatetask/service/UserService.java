package com.example.gymhibernatetask.service;

import com.example.gymhibernatetask.dto.CreateRequestDto;
import com.example.gymhibernatetask.models.User;

public interface UserService {

    User createUser(CreateRequestDto createRequestDto);
}
