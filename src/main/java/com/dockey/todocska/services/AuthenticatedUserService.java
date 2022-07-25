package com.dockey.todocska.services;

import com.dockey.todocska.entities.User;
import com.dockey.todocska.entities.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticatedUserService {

    @Autowired
    private UserRepository userRepository;

    public boolean hasId(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("---Authenticated user: " + username);
        User user = userRepository.findByUsername(username);
        log.info("---Authenticated userId: " + user.getId());
        return user.getId().equals(id);

    }

}