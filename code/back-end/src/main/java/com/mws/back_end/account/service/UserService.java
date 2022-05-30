package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.user.dto.*;
import com.mws.back_end.account.model.dao.UserDao;
import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.account.model.entity.UserRole;
import com.mws.back_end.account.service.security.JwtProvider;
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
import static com.mws.back_end.framework.utils.ExceptionUtils.require;
import static com.mws.back_end.framework.utils.ExceptionUtils.requireNotNull;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;


    public Long createUser(final UserCreationDto userCreationDto) throws MWSException {
        requireNotNull(userCreationDto, "User info must be provided");

        final User user = toUser(userCreationDto);
        checkPermissionsToCreateUser(user);
        try {
            return userDao.create(user).getId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private void checkPermissionsToCreateUser(final User userToCreate) throws MWSException {
        final UserDto authenticatedUser = jwtProvider.getCurrentUser();
        if (UserRole.SUPER == userToCreate.getRole()) {
            if (authenticatedUser == null || UserRoleDto.SUPER != authenticatedUser.getRole()) {
                throw new MWSException("Not allowed to create an user with super role.");
            }
        } else if (UserRole.ADMIN == userToCreate.getRole() && isAllowedToCreateAdmins(authenticatedUser)) {
            throw new MWSException("Not allowed to create an user with admin role.");
        }
    }

    private boolean isAllowedToCreateAdmins(UserDto authenticatedUser) {
        return authenticatedUser == null || !(UserRoleDto.SUPER == authenticatedUser.getRole() || UserRoleDto.ADMIN == authenticatedUser.getRole());
    }

    private User toUser(UserCreationDto userCreationDto) {
        require(userCreationDto.getRole() != null, "User role must be provided");
        final User user = new User();
        user.setRole(UserRole.valueOf(userCreationDto.getRole().toString()));
        user.setEmail(userCreationDto.getEmail());
        user.setPassword(userCreationDto.getPassword());
        user.setFirstName(userCreationDto.getFirstName());
        user.setLastName(userCreationDto.getLastName());

        return user;
    }

    public void updateUser(final UserUpdateDto userUpdateDto) throws MWSException {
        final User newUser = toUser(userUpdateDto);
        final User previousUser = userDao.findWeak(newUser.getId());
        if (previousUser == null) {
            throw new MWSException("Entity not found");
        }

        if (previousUser.getRole() != newUser.getRole()) {
            throw new MWSException("It's not possible to change user role.");
        }

        final User userWithSameEmail = userDao.findByEmail(userUpdateDto.getEmail());
        if (userWithSameEmail != null && !userWithSameEmail.getId().equals(userUpdateDto.getId())) {
            throw new MWSException("Duplicated email");
        }

        try {
            userDao.update(newUser);
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private User toUser(UserUpdateDto userUpdateDto) {
        requireNotNull(userUpdateDto, "User info must be provided");
        requireNotNull(userUpdateDto.getRole(), "User role must be provided");

        final User user = new User();
        user.setId(userUpdateDto.getId());
        user.setRole(UserRole.valueOf(userUpdateDto.getRole().toString()));
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
        requireNotNull(loginRequest, "Login info must be provided");
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
