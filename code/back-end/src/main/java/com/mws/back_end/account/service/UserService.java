package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.user.dto.*;
import com.mws.back_end.account.model.dao.UserDao;
import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.account.model.entity.UserRole;
import com.mws.back_end.account.service.security.JwtService;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import com.mws.back_end.framework.utils.StringUtils;
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

        checkUserToCreate(userCreationDto);
        final User user = updatePreviousUserData(userCreationDto);
        try {
            return userDao.create(user).getId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private void checkUserToCreate(final UserCreationDto userCreationDto) throws MWSException {
        if (tenantService.getTenant(userCreationDto.getTenantId()) == null) {
            throw new MWSException("Invalid tenant id.");
        }
        checkUserEmail(userCreationDto.getEmail());
        checkUserPassword(userCreationDto.getPassword());
        checkUserFirstName(userCreationDto.getFirstName());
        checkUserLastName(userCreationDto.getLastName());

        checkPermissionsToCreateUser(userCreationDto);
    }

    private void checkPermissionsToCreateUser(final UserCreationDto userToCreate) throws MWSException {
        final UserRoleDto loggedUserRole = jwtService.getCurrentUserRole();
        if (UserRoleDto.SUPER == userToCreate.getRole()) {
            if (UserRoleDto.SUPER != loggedUserRole) {
                throw new MWSException("Not allowed to create an user with super role.");
            }
        } else if (UserRoleDto.ADMIN == userToCreate.getRole() && !(UserRoleDto.SUPER == loggedUserRole || UserRoleDto.ADMIN == loggedUserRole)) {
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
        checkUserEmail(userUpdateDto.getEmail());
        checkUserPassword(userUpdateDto.getPassword());
        checkUserFirstName(userUpdateDto.getFirstName());
        checkUserLastName(userUpdateDto.getLastName());

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

    private void checkUserEmail(final String email) throws MWSException {
        if (StringUtils.isEmpty(email)) {
            throw new MWSException("User email is required.");
        }
    }

    private void checkUserPassword(final String password) throws MWSException {
        if (StringUtils.isEmpty(password)) {
            throw new MWSException("User password is required.");
        } else if (password.length() < 8) {
            throw new MWSException("User password must contain at least 8 characters.");
        }
    }
    private void checkUserFirstName(final String firstName) throws MWSException {
        if (StringUtils.isEmpty(firstName)) {
            throw new MWSException("User first name cannot be blank.");
        }
    }

    private void checkUserLastName(final String lastName) throws MWSException {
        if (StringUtils.isEmpty(lastName)) {
            throw new MWSException("User last name cannot be blank.");
        }
    }

    private User toUser(final UserUpdateDto userUpdateDto) {
        requireNotNull(userUpdateDto, "User info must be provided");
        final User user = userDao.findWeak(userUpdateDto.getId());

        if (user == null) {
            return null;
        }

        user.setEmail(userUpdateDto.getEmail());
        user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
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
        final User loggedUser = userDao.findWeak(jwtService.getUserId(token));

        return UserAuthenticationResponse.builder()
                .firstName(loggedUser.getFirstName())
                .lastName(loggedUser.getLastName())
                .token(token)
                .build();
    }
}
