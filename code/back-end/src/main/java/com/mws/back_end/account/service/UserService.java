package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.user.dto.*;
import com.mws.back_end.account.model.dao.UserDao;
import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.mws.back_end.account.interfaces.user.dto.UserDto.toDto;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;


    public Long createUser(final UserCreationDto userCreationDto) throws MWSException {
        try {
            return userDao.create(toUser(userCreationDto)).getId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private User toUser(UserCreationDto userCreationDto) {
        final User user = new User();
        user.setEmail(userCreationDto.getEmail());
        user.setPassword(userCreationDto.getPassword());
        user.setFirstName(userCreationDto.getFirstName());
        user.setLastName(userCreationDto.getLastName());

        return user;
    }

    public void updateUser(final UserUpdateDto userUpdateDto) throws MWSException {
        final User user = toUser(userUpdateDto);

        if (userDao.findWeak(user.getId()) == null) {
            throw new MWSException("Entity not found");
        }

        if (userDao.findByEmail(userUpdateDto.getEmail()) != null) { // TODO: fix regression (same email, different data)
            throw new MWSException("Duplicated email");
        }

        try {
            userDao.update(user);
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private User toUser(UserUpdateDto userUpdateDto) {
        final User user = new User();
        user.setId(userUpdateDto.getId());
        user.setEmail(userUpdateDto.getEmail());
        user.setPassword(userUpdateDto.getPassword());
        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());

        return user;
    }

    public UserDto getUser(final Long id) {
        User userEntity = userDao.findWeak(id);
        if (userEntity == null) {
            return null;
        }
        return toDto(userEntity);
    }

    public UserAuthenticationResponse login(LoginRequest loginRequest) throws MWSException {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new MWSException(e.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        final String token = jwtProvider.generateNewToken(authenticate);
        return UserAuthenticationResponse.builder()
                .token(token)
                .build();
    }
}
