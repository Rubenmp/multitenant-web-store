package com.mws.back_end.account.service.security;

import com.mws.back_end.account.interfaces.user.dto.UserDto;
import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
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
import java.util.*;

import static com.mws.back_end.framework.utils.DateUtils.isDateBeforeNow;
import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

@Service
public class JwtCipher {
    public static final boolean USE_JWT_RESTRICTIONS = true;
    private static final String DEV_SECRET_KEY = "Tk9RX3NlY3JldF9rZXlfdG9fZ2VuZXJhdGVfc2lnbmVkX3Rva2VuX2Zvcl9kZXY=";
    private static final Long JWT_EXPIRATION_IN_MILLISECONDS = 100000000L;
    private static final int TOTAL_NUMBER_OF_TOKEN_CLAIMS = 6;
    private static final String TOKEN_CLAIM_TENANT_ID = "TENANT_ID";
    private static final String TOKEN_CLAIM_USER_ID = "USER_ID";
    private static final String TOKEN_CLAIM_USER_EMAIL = "USER_EMAIL";
    private static final String TOKEN_CLAIM_USER_ROLE = "USER_ROLE";
    private static final String TOKEN_CLAIM_ISSUED_AT = "iat";
    private static final String TOKEN_CLAIM_EXPIRATION_TIME = "exp";

    public boolean jwtRestrictionsEnabled() {
        return USE_JWT_RESTRICTIONS;
    }

    @Transactional(readOnly = true)
    public boolean isValidToken(final String jwt) {
        if (!jwtRestrictionsEnabled()) {
            return true;
        }
        return jwt != null && isTokenWellFormedAndSigned(jwt) && !isTokenDateExpired(jwt);
    }

    public boolean isTokenWellFormedAndSigned(final String jwt) {
        try {
            final Claims claims = getTokenClaims(jwt);
            return claims.size() == TOTAL_NUMBER_OF_TOKEN_CLAIMS
                    && Long.valueOf(String.valueOf(claims.get(TOKEN_CLAIM_TENANT_ID))) != null
                    && Long.valueOf(String.valueOf(claims.get(TOKEN_CLAIM_USER_ID))) != null
                    && !String.valueOf(claims.get(TOKEN_CLAIM_USER_EMAIL)).isBlank()
                    && UserRoleDto.valueOf(String.valueOf(claims.get(TOKEN_CLAIM_USER_ROLE))) != null
                    && Long.valueOf(String.valueOf(claims.get(TOKEN_CLAIM_ISSUED_AT))) != null
                    && Long.valueOf(String.valueOf(claims.get(TOKEN_CLAIM_EXPIRATION_TIME))) != null;
        } catch (RuntimeException re) {
            return false;
        }
    }

    private Long getTenantId(final Claims claims) {
        return Long.valueOf(String.valueOf(claims.get(TOKEN_CLAIM_TENANT_ID)));
    }

    public Optional<String> getLoginEmail(final String token) {
        if (isTokenWellFormedAndSigned(token)) {
            final Claims claims = getTokenClaims(token);
            return Optional.of(String.valueOf(claims.get(TOKEN_CLAIM_USER_EMAIL)));
        }

        return Optional.empty();
    }

    public Long getUserId(final String token) {
        if (isTokenWellFormedAndSigned(token)) {
            final Claims claims = getTokenClaims(token);
            return Long.valueOf(String.valueOf(claims.get(TOKEN_CLAIM_USER_ID)));
        }

        return null;
    }

    public Long getTenantId(final String token) {
        if (isTokenWellFormedAndSigned(token)) {
            final Claims claims = getTokenClaims(token);
            return Long.valueOf(String.valueOf(claims.get(TOKEN_CLAIM_TENANT_ID)));
        }

        return null;
    }

    public UserRoleDto getUserRole(final String token) {
        if (isTokenWellFormedAndSigned(token)) {
            final Claims claims = getTokenClaims(token);
            return UserRoleDto.valueOf(String.valueOf(claims.get(TOKEN_CLAIM_USER_ROLE)));
        }

        return null;
    }

    protected Date getExpirationDateFromJwt(final String token) {
        if (!jwtRestrictionsEnabled()) {
            return getDateInFuture();
        }
        if (isTokenWellFormedAndSigned(token)) {
            final Claims claims = getTokenClaims(token);
            return claims.getExpiration();
        }

        return null;
    }

    private Date getDateInFuture() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 150); // Adds 150 days

        return c.getTime();
    }

    public String getCurrentToken() {
        final String token = getCurrentTokenWithoutValidation();
        return isTokenWellFormedAndSigned(token) ? token : null;
    }

    String getCurrentTokenWithoutValidation() {
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
            return getTenantId(getTokenClaims(token));
        } else if (!jwtRestrictionsEnabled()) {
            return 1L;
        }
        return null;
    }

    public UserRoleDto getCurrentUserRole() {
        if (!jwtRestrictionsEnabled()) {
            return UserRoleDto.SUPER;
        }
        final String token = getCurrentToken();
        if (token != null) {
            return UserRoleDto.valueOf(String.valueOf(getTokenClaims(token).get(TOKEN_CLAIM_USER_ROLE)));
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

    public boolean isTokenDateExpired(final String jwt) {
        if (!jwtRestrictionsEnabled()) {
            return false;
        }
        final Date expirationDate = getExpirationDateFromJwt(jwt);
        return expirationDate == null || isDateBeforeNow(expirationDate);
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
        claims.put(TOKEN_CLAIM_USER_ROLE, userDto.getRole().toString());
        return claims;
    }
    private Long getJwtExpirationInMilliseconds() {
        return JWT_EXPIRATION_IN_MILLISECONDS;
    }

    public String createToken(final UserDto userModel) {
        return Jwts.builder()
                .setClaims(toClaims(userModel))
                .setIssuedAt(from(Instant.now()))
                .setExpiration(from(Instant.now().plusMillis(getJwtExpirationInMilliseconds())))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
