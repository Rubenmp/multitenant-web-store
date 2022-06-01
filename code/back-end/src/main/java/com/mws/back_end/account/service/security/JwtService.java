package com.mws.back_end.account.service.security;

import com.mws.back_end.account.interfaces.user.dto.UserDto;
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

        return createToken(user.getUsername());
    }

    @Transactional(readOnly = true)
    public String refreshToken() throws MWSException {
        final String currentToken = jwtCipher.getCurrentToken();

        if (!jwtCipher.isTokenWellFormedAndSigned(currentToken)) {
            throw new MWSException(INVALID_TOKEN_IN_HTTP_HEADERS);
        }

        final Optional<String> emailOpt = jwtCipher.getLoginEmailFromJwt(currentToken);
        if (emailOpt.isEmpty()) {
            throw new MWSException(INVALID_TOKEN_IN_HTTP_HEADERS);
        }

        return createToken(emailOpt.get());
    }

    public Long getCurrentUserId() {
        return jwtCipher.getCurrentUserId();
    }

    private String createToken(final String loginEmail) {
        final Optional<UserDto> userModelOpt = userDetailsServiceImpl.getUserByEmail(loginEmail);
        if (userModelOpt.isEmpty()) {
            return null;
        }

        return jwtCipher.createToken(userModelOpt.get());
    }

}
