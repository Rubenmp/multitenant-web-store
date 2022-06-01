package com.mws.back_end.account.service.security;

import com.mws.back_end.account.interfaces.user.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.mws.back_end.framework.utils.DateUtils.isDateBeforeNow;
import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

@Service
public class JwtCipher {
    private static final String DEV_SECRET_KEY = "Tk9RX3NlY3JldF9rZXlfdG9fZ2VuZXJhdGVfc2lnbmVkX3Rva2VuX2Zvcl9kZXY=";
    private static final Long JWT_EXPIRATION_IN_MILLISECONDS = 100000000L;

    private static final String TOKEN_CLAIM_TENANT_ID = "TENANT_ID";
    private static final String TOKEN_CLAIM_USER_ID = "USER_ID";
    private static final String TOKEN_CLAIM_USER_EMAIL = "USER_EMAIL";

    @Transactional(readOnly = true)
    public boolean isValidToken(final String jwt) {
        return jwt != null && isTokenWellFormedAndSigned(jwt) &&
                !isTokenDateExpired(jwt);
    }

    public boolean isTokenWellFormedAndSigned(final String jwt) {
        try {
            final Claims claims = getTokenClaims(jwt);
            return claims.containsKey(TOKEN_CLAIM_TENANT_ID) && claims.containsKey(TOKEN_CLAIM_USER_ID);
        } catch (RuntimeException re) {
            return false;
        }
    }

    public Optional<String> getLoginEmailFromJwt(final String token) {
        if (isTokenWellFormedAndSigned(token)) {
            final Claims claims = getTokenClaims(token);
            return Optional.of(String.valueOf(claims.get(TOKEN_CLAIM_USER_EMAIL)));
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

    public String getCurrentToken() {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public Long getCurrentUserId() {
        final String token = getCurrentToken();
        if (token == null) {
            return null;
        }

        return Long.valueOf(String.valueOf(getTokenClaims(token).get(TOKEN_CLAIM_USER_ID)));
    }


    public Long getCurrentTenantId() {
        final String token = getCurrentToken();
        if (token != null) {
            return Long.valueOf(String.valueOf(getTokenClaims(token).get(TOKEN_CLAIM_TENANT_ID)));
        }
        return null;
    }

    public String getJwtFromRequest(final HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return bearerToken;
    }

    public Key getSecretKey() {
        String base64Secret = System.getenv("MWS_SECURITY_KEY");
        byte[] keyBytes = Decoders.BASE64.decode(StringUtils.hasText(base64Secret) ? base64Secret : DEV_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenDateExpired(final String jwt) {
        final Optional<Date> expirationDateOpt = getExpirationDateFromJwt(jwt);
        return expirationDateOpt.isEmpty() || isDateBeforeNow(expirationDateOpt.get());
    }

    private Claims getTokenClaims(final String token) {
        return parser()
                .setSigningKey(getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public Map<String, Object> toClaims(final UserDto userDto) {
        final Map<String, Object> claims = new HashMap<>();

        claims.put(TOKEN_CLAIM_TENANT_ID, userDto.getTenantId());
        claims.put(TOKEN_CLAIM_USER_ID, userDto.getId());
        claims.put(TOKEN_CLAIM_USER_EMAIL, userDto.getEmail());
        return claims;
    }
    private Long getJwtExpirationInMilliseconds() {
        return JWT_EXPIRATION_IN_MILLISECONDS;
    }

    public String createToken(UserDto userModel) {
        return Jwts.builder()
                .setClaims(toClaims(userModel))
                .setIssuedAt(from(Instant.now()))
                .setExpiration(from(Instant.now().plusMillis(getJwtExpirationInMilliseconds())))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
