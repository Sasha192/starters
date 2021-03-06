package org.wpstarters.jwtauthprovider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.wpstarters.jwtauthprovider.config.context.RequestFingerprintUtil;
import org.wpstarters.jwtauthprovider.model.CustomUserDetails;
import org.wpstarters.jwtauthprovider.model.RefreshToken;
import org.wpstarters.jwtauthprovider.model.RefreshTokenStatus;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.repository.IRefreshTokenRepository;
import org.wpstarters.jwtauthprovider.service.IEncryptionKeys;
import org.wpstarters.jwtauthprovider.service.ITokenService;

import javax.servlet.http.Cookie;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class TokenService implements ITokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN_COOKIE";


    private final IEncryptionKeys keyPairSupplier;
    private final String issuer;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;

    public TokenService(String issuer,
                        IEncryptionKeys keyPairSupplier,
                        IRefreshTokenRepository refreshTokenRepository,
                        UserDetailsService userDetailsService) {
        this.keyPairSupplier = keyPairSupplier;
        this.issuer = issuer;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String generateJwtToken(CustomUserDetails userDetails)
            throws JsonProcessingException {

        // generate jwt token
        String jwtId = UUID.randomUUID().toString();
        String token = buildJwt(userDetails, jwtId);

        // generate refresh token
        UUID refreshTokenId = UUID.randomUUID();
        RefreshToken refreshToken = buildRefreshToken(refreshTokenId, userDetails, jwtId);

        // set refresh token to cookies
        refreshTokenRepository.save(refreshToken);
        setRefreshCookie(refreshToken);

        // save token history



        // return jwt token
        return token;
    }

    @Override
    public String refreshToken(String jwtToken) throws JsonProcessingException {

        Claims claims = extractClaims(jwtToken);

        if (!validateClaims(claims)) {
            throw new ExtendedAuthenticationException("Invalid token", ExceptionState.INVALID_TOKEN);
        }

        RefreshToken refreshToken = retrieveRefreshToken();

        if (refreshToken != null && refreshToken.isValid()) {

            if (validateRefreshTokenAndJwt(claims.getSubject(), claims.getId(), refreshToken)) {

                refreshTokenRepository.deleteById(refreshToken.getId());
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(claims.getSubject());

                if (userDetails != null) {
                    return generateJwtToken(userDetails);
                }
                else {
                    throw new ExtendedAuthenticationException("No such user", ExceptionState.NO_SUCH_USER);
                }

            }

        }

        throw new ExtendedAuthenticationException("Invalid token", ExceptionState.INVALID_REFRESH_TOKEN);

    }

    @Override
    public String getUserNameFromJwtToken(String token) {

        Claims claims = extractClaims(token);

        if (validateClaims(claims)) {
            return claims.getSubject();
        }
        throw new ExtendedAuthenticationException("Expired token", ExceptionState.EXPIRED_TOKEN);
    }

    @Override
    public boolean validateJwtToken(String authToken) throws ExtendedAuthenticationException {
        String errorMessage;
        ExceptionState exceptionState = ExceptionState.INVALID_TOKEN;

        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(keyPairSupplier.keyPair().getPrivate())
                    .parseClaimsJws(authToken);
            Claims claims = claimsJws.getBody();
            return validateClaims(claims);
        } catch (MalformedJwtException e) {
            errorMessage = "Invalid Token";
            logger.error(errorMessage + " {} ", e.getMessage());
        } catch (ExpiredJwtException e) {
            errorMessage = "Token is expired";
            exceptionState = ExceptionState.EXPIRED_TOKEN;
            logger.error(errorMessage + ": {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            errorMessage = "Token is unsupported";
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            errorMessage = "Claims string is empty";
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            errorMessage = "Something went wrong";
            exceptionState = ExceptionState.INVALID_TOKEN;
            logger.error("Exception occurred while validating:", e);
        }
        throw new ExtendedAuthenticationException(errorMessage, exceptionState);
    }

    // ----- PRIVATE METHODS -----

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(keyPairSupplier.keyPair().getPrivate())
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean validateClaims(Claims claims) {
        return new Date(System.currentTimeMillis()).before(claims.getExpiration());
    }

    private void setRefreshCookie(RefreshToken refreshToken) {
        Cookie cookieRefreshToken = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken.getId().toString());
        CookieUtils.addSecureCookie(cookieRefreshToken, true);
    }

    private RefreshToken retrieveRefreshToken() {

        Cookie cookieRefreshToken = CookieUtils.retrieveCookie(REFRESH_TOKEN_COOKIE);

        if (cookieRefreshToken != null) {

            if (cookieRefreshToken.isHttpOnly() && cookieRefreshToken.getSecure()) {

                UUID refreshTokenId = UUID.fromString(cookieRefreshToken.getValue());
                RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenId).orElse(null);

                if (refreshToken != null) {
                    if ((!refreshToken.isValid())) {
                        refreshTokenRepository.deleteById(refreshTokenId);
                        return null;
                    } else {
                        return refreshToken;
                    }
                }

            }
        }

        throw new ExtendedAuthenticationException("Can not retrieve refresh token", ExceptionState.INVALID_REFRESH_TOKEN);
    }

    private boolean validateRefreshTokenAndJwt(String username, String tokenId, RefreshToken refreshToken) {
        return refreshToken.getTokenId().equals(tokenId) &&
                refreshToken.getUsername().equals(username);
    }

    private RefreshToken buildRefreshToken(UUID refreshTokenId, CustomUserDetails userDetails, String tokenId) {

        long twoWeeks = ChronoUnit.WEEKS.getDuration().toMillis() * 2;

        return new RefreshToken.Builder()
                .id(refreshTokenId)
                .tokenId(tokenId)
                .ipAddress(RequestFingerprintUtil.requestIpAddress())
                .userAgent(RequestFingerprintUtil.requestUserAgent())
                .unixExpired(System.currentTimeMillis() + twoWeeks)
                .unixCreated(System.currentTimeMillis())
                .status(RefreshTokenStatus.VALID)
                .username(userDetails.getUsername())
                .build();
    }

    private String buildJwt(CustomUserDetails userDetails, String tokenId) {
        Map<String, Object> extraDetails = userDetails.getPublicDetails();
        extraDetails.put("authorities", userDetails.getAuthorities());
        extraDetails.put("provider", userDetails.getProviderType());

        long jwtExpirationTimeInMs = Duration.ofMinutes(10).toMillis();

        String requestIpAddress = RequestFingerprintUtil.requestIpAddress();
        String requestUserAgent = RequestFingerprintUtil.requestUserAgent();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTimeInMs))
                .setIssuer(issuer)
                .claim("owner_ip", requestIpAddress)
                .claim("owner_user-agent", requestUserAgent)
                .addClaims(extraDetails)
                .setId(tokenId)
                .signWith(SignatureAlgorithm.RS256, keyPairSupplier.keyPair().getPrivate())
                .compact();
    }


}
