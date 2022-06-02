package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.user.dto.*;
import com.mws.back_end.account.model.dao.UserDao;
import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.account.model.entity.UserRole;
import com.mws.back_end.account.service.security.JwtService;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private JwtService jwtService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Long createUser(final UserCreationDto userCreationDto) throws MWSException {
        requireNotNull(userCreationDto, "User info must be provided");

        final User user = updatePreviousUserData(userCreationDto);
        checkUserToCreate(user);
        try {
            return userDao.create(user).getId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private void checkUserToCreate(final User user) throws MWSException {
        if (tenantService.getTenant(user.getTenantId()) == null) {
            throw new MWSException("Invalid tenant id.");
        }
        checkPermissionsToCreateUser(user);
    }

    private void checkPermissionsToCreateUser(final User userToCreate) throws MWSException {
        final UserRoleDto loggedUserRole = jwtService.getCurrentUserRole();
        if (UserRole.SUPER == userToCreate.getRole()) {
            if (UserRoleDto.SUPER != loggedUserRole) {
                throw new MWSException("Not allowed to create an user with super role.");
            }
        } else if (UserRole.ADMIN == userToCreate.getRole() && !(UserRoleDto.SUPER == loggedUserRole || UserRoleDto.ADMIN == loggedUserRole)) {
            throw new MWSException("Not allowed to create an user with admin role.");
        }
    }

    private User updatePreviousUserData(final UserCreationDto userCreationDto) {
        require(userCreationDto.getRole() != null, "User role must be provided");
        require(userCreationDto.getTenantId() != null, "Tenant id must be provided");

        final User user = new User();
        user.setTenantId(userCreationDto.getTenantId());
        user.setRole(UserRole.valueOf(userCreationDto.getRole().toString()));
        user.setEmail(userCreationDto.getEmail());
        user.setPassword(passwordEncoder.encode(userCreationDto.getPassword()));
        user.setFirstName(userCreationDto.getFirstName());
        user.setLastName(userCreationDto.getLastName());

        return user;
    }

    public void updateUser(final UserUpdateDto userUpdateDto) throws MWSException {
        final User newUser = checkUserToUpdate(userUpdateDto);

        try {
            userDao.update(newUser);
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private User checkUserToUpdate(final UserUpdateDto userUpdateDto) throws MWSException {
        requireNotNull(userUpdateDto.getId(), "User id is required.");
        final User newUser = toUser(userUpdateDto);
        if (newUser == null) {
            throw new MWSException("Entity not found");
        }

        final User userWithSameEmail = userDao.findByEmail(userUpdateDto.getEmail());
        if (userWithSameEmail != null && !userWithSameEmail.getId().equals(userUpdateDto.getId())) {
            throw new MWSException("Duplicated email");
        }
        return newUser;
    }

    private User toUser(final UserUpdateDto userUpdateDto) {
        requireNotNull(userUpdateDto, "User info must be provided");
        final User user = userDao.findWeak(userUpdateDto.getId());

        if (user == null) {
            return null;
        }

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

    public UserAuthenticationResponse login(final LoginRequest loginRequest) throws MWSException {
        requireNotNull(loginRequest, "Login info must be provided");
        final Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new MWSException(e.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        final String token = jwtService.generateNewToken(authenticate);
        return UserAuthenticationResponse.builder()
                .token(token)
                .build();
    }
}
