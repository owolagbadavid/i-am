package me.oreos.iam.services.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.User;
import me.oreos.iam.repositories.UserRepository;
import me.oreos.iam.services.UserService;

@Component
@Transactional
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User, Integer> implements UserService {
    private final UserRepository userRepository;
    protected UserServiceImpl(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findDistinctByEmail(String email) {
        // log.debug("Finding distinct user by email: {}", email);
        return userRepository.findDistinctByEmail(email);
    }

}
