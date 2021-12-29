package org.wpstarters.jwtauthprovider.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.wpstarters.jwtauthprovider.dto.CustomUserDetails;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.IKeyPairSupplier;
import org.wpstarters.jwtauthprovider.service.IPublicKeyService;
import org.wpstarters.jwtauthprovider.service.IRefreshTokenService;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private final IKeyPairSupplier keyPairSupplier;
    private final long jwtExpirationTimeInMs;
    private final String issuer;
    private final ObjectMapper objectMapper;

    public JwtUtils(long jwtExpirationTimeInMs,
                    String issuer,
                    IPublicKeyService publicKeyService,
                    IKeyPairSupplier keyPairSupplier,
                    IRefreshTokenService refreshTokenService,
                    ObjectMapper objectMapper) {
        this.jwtExpirationTimeInMs = jwtExpirationTimeInMs;
        this.keyPairSupplier = keyPairSupplier;
        this.issuer = issuer;
        this.objectMapper = objectMapper;
    }

    public String generateJwtToken(CustomUserDetails userDetails)
            throws JsonProcessingException {

        Map<String, Object> extraDetails = objectMapper.readValue(userDetails.getExtraDetails(), Map.class);
        extraDetails.put("authorities", userDetails.getAuthorities());
        extraDetails.put("provider", userDetails.getProviderType());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationTimeInMs))
                .setIssuer(issuer)
                .addClaims(extraDetails)
                .signWith(SignatureAlgorithm.RS256, keyPairSupplier.keyPair().getPrivate())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(keyPairSupplier.keyPair().getPrivate())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) throws ExtendedAuthenticationException {
        String errorMessage;
        ExceptionState exceptionState = ExceptionState.INVALID_TOKEN;

        try {
            Jwts.parser().setSigningKey(keyPairSupplier.keyPair().getPrivate()).parseClaimsJws(authToken);
            return true;
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
        }

        throw new ExtendedAuthenticationException(errorMessage, exceptionState);
    }


}
