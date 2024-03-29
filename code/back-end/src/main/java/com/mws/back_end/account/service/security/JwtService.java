package com.mws.back_end.account.service.security;

import com.mws.back_end.account.interfaces.user.dto.UserDto;
import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class JwtService {
    private static final String INVALID_TOKEN_IN_HTTP_HEADERS = "Invalid token provided in http request";

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private JwtCipher jwtCipher;

    public String generateNewToken(final Authentication authentication) throws MWSException {
        final User user;
        try {
            user = (User) authentication.getPrincipal();
        } catch (Exception e) {
            throw new MWSException("Invalid authentication");
        }

        return createToken(user.getUsername(), null);
    }

    public String generateNewToken(final Authentication authentication, final Long tenantId) throws MWSException {
        final User user;
        try {
            user = (User) authentication.getPrincipal();
        } catch (Exception e) {
            throw new MWSException("Invalid authentication");
        }

        return createToken(user.getUsername(), tenantId);
    }

    public Long getUserId(final String token) {
        return jwtCipher.getUserId(token);
    }

    @Transactional(readOnly = true)
    public String refreshToken() throws MWSException {
        final String currentToken = jwtCipher.getCurrentToken();

        if (!jwtCipher.isTokenWellFormedAndSigned(currentToken)) {
            throw new MWSException(INVALID_TOKEN_IN_HTTP_HEADERS);
        }

        final Optional<String> emailOpt = jwtCipher.getLoginEmail(currentToken);
        final Long tenantId = jwtCipher.getTenantId(currentToken);
        if (tenantId == null || emailOpt.isEmpty()) {
            throw new MWSException(INVALID_TOKEN_IN_HTTP_HEADERS);
        }

        return createToken(emailOpt.get(), tenantId);
    }

    public UserRoleDto getCurrentUserRole() {
        return jwtCipher.getCurrentUserRole();
    }

    private String createToken(final String loginEmail, final Long tenantId) {
        final Optional<UserDto> userModelOpt = userDetailsServiceImpl.getUserByEmail(loginEmail, tenantId);
        if (userModelOpt.isEmpty()) {
            return null;
        }

        return jwtCipher.createToken(userModelOpt.get());
    }

}
