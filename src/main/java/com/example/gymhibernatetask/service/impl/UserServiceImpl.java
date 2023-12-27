package com.example.gymhibernatetask.service.impl;

import com.example.gymhibernatetask.dto.CreateRequestDto;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.service.UserService;
import com.example.gymhibernatetask.util.UtilService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final UtilService utilService;

    public UserServiceImpl(UserRepository userRepository, UtilService utilService) {
        this.userRepository = userRepository;
        this.utilService = utilService;
    }

    @Transactional
    @Override
    public User createUser(CreateRequestDto createRequestDto) {
        User user = new User();
        String firstName = createRequestDto.getFirstName();
        String lastName = createRequestDto.getLastName();
        user.setActive(true);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(utilService.generateUsername(firstName, lastName, userRepository.findAll()));
        user.setPassword(utilService.generateRandomPassword(10));
        logger.info("User created successfully.");

        return userRepository.save(user);
    }
}
