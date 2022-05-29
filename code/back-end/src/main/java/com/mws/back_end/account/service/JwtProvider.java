package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.user.dto.UserDto;
import com.mws.back_end.framework.exception.MWSException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static com.mws.back_end.framework.utils.DateUtils.isDateBeforeNow;
import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

@Service
public class JwtProvider {

    private static final Long JWT_EXPIRATION_IN_MILLISECONDS = 100000000L;
    private static final String INVALID_TOKEN_IN_HTTP_HEADERS = "Invalid token provided in http request";
    private static final String DEV_SECRET_KEY = "Tk9RX3NlY3JldF9rZXlfdG9fZ2VuZXJhdGVfc2lnbmVkX3Rva2VuX2Zvcl9kZXY=";

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    public String generateNewToken(final Authentication authentication) throws MWSException {
        User user;
        try {
            user = (User) authentication.getPrincipal();
        } catch (Exception e) {
            throw new MWSException("Invalid authentication");
        }

        return createToken(user.getUsername());
    }

    @Transactional(readOnly = true)
    public String refreshToken() throws MWSException {
        final String currentToken = getCurrentToken();

        if (!isTokenWellFormedAndSigned(currentToken)) {
            throw new MWSException(INVALID_TOKEN_IN_HTTP_HEADERS);
        }

        final Optional<String> emailOpt = getLoginEmailFromJwt(currentToken);
        if (emailOpt.isEmpty() || !isLoginEmailValid(currentToken)) {
            throw new MWSException(INVALID_TOKEN_IN_HTTP_HEADERS);
        }

        return createToken(emailOpt.get());
    }

    @Transactional(readOnly = true)
    public boolean isValidToken(final String jwt) {
        return jwt != null && isTokenWellFormedAndSigned(jwt) &&
                !isTokenDateExpired(jwt) && isLoginEmailValid(jwt);
    }

    public boolean isTokenWellFormedAndSigned(final String jwt) {
        try {
            Key key = getSecretKey();
            parser().setSigningKey(key).parseClaimsJws(jwt);
        } catch (RuntimeException re) {
            return false;
        }

        return true;
    }

    public Optional<String> getLoginEmailFromJwt(final String token) {
        if (isTokenWellFormedAndSigned(token)) {
            final Claims claims = getTokenClaims(token);
            return Optional.of(claims.getSubject());
        }

        return Optional.empty();
    }

    public Optional<Date> getExpirationDateFromJwt(final String token) {
        if (isTokenWellFormedAndSigned(token)) {
            final Claims claims = getTokenClaims(token);
            return Optional.of(claims.getExpiration());
        }

        return Optional.empty();
    }

    public String getCurrentToken () {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public String getJwtFromRequest(final HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return bearerToken;
    }

    public Long getJwtExpirationInMilliseconds() {
        return JWT_EXPIRATION_IN_MILLISECONDS;
    }


    /*********************** Private methods ***********************/
    private String createToken(final String loginEmail) {
        final Optional<UserDto> userModelOpt = userDetailsServiceImpl.getUserByEmail(loginEmail);
        if (userModelOpt.isEmpty()) {
            return null;
        }

        Key key = getSecretKey();

        return Jwts.builder()
                .setSubject(loginEmail)
                .setIssuedAt(from(Instant.now()))
                .setExpiration(from(Instant.now().plusMillis(getJwtExpirationInMilliseconds())))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSecretKey() {
        String base64Secret = System.getenv("NOQ_SECURITY_KEY");
        byte[] keyBytes = Decoders.BASE64.decode(StringUtils.hasText(base64Secret) ? base64Secret : DEV_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenDateExpired(final String jwt) {
        final Optional<Date> expirationDateOpt = getExpirationDateFromJwt(jwt);
        return expirationDateOpt.isEmpty() || isDateBeforeNow(expirationDateOpt.get());
    }

    private boolean isLoginEmailValid(final String jwt) {
        final Optional<String> loginEmailOpt = getLoginEmailFromJwt(jwt);
        return loginEmailOpt.isPresent() && isUsernameAllowedToAuthenticate(loginEmailOpt.get());
    }

    private boolean isUsernameAllowedToAuthenticate(final String loginEmail) {
        return userDetailsServiceImpl.getUserByEmail(loginEmail).isPresent();
    }

    private Claims getTokenClaims(final String token) {
        Key key = getSecretKey();

        return parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}
