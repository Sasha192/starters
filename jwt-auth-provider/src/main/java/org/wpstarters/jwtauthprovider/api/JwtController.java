package org.wpstarters.jwtauthprovider.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.wpstarters.jwtauthprovider.api.state.StateMessage;
import org.wpstarters.jwtauthprovider.dto.IStateMessage;
import org.wpstarters.jwtauthprovider.dto.RefreshTokenRequest;
import org.wpstarters.jwtauthprovider.service.impl.TokenService;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.IEncryptionKeys;
import org.wpstarters.jwtauthprovider.service.INonceStrategy;

import java.util.Map;

@Controller
public class JwtController {

    private static final Logger logger = LoggerFactory.getLogger(JwtController.class);

    private final IEncryptionKeys keyPairSupplier;
    private final INonceStrategy nonceStrategy;
    private final TokenService tokenService;

    public JwtController(IEncryptionKeys keyPairSupplier,
                         TokenService tokenService,
                         INonceStrategy nonceStrategy) {
        this.keyPairSupplier = keyPairSupplier;
        this.tokenService = tokenService;
        this.nonceStrategy = nonceStrategy;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return this.keyPairSupplier.jwkSet().toJSONObject();
    }

    @PostMapping(value = "/refresh-token",
            consumes = Utf8Json.APPLICATION_JSON_VALUE,
            produces = Utf8Json.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends IStateMessage> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {

        try {
            if (nonceStrategy.validNonce(refreshTokenRequest.getNonce())) {
                
                String newJwtToken = tokenService.refreshToken(refreshTokenRequest.getJwtToken());

                return ResponseEntity.status(HttpStatus.OK)
                        .body(
                                new StateMessage(
                                        "token " + newJwtToken,
                                        true,
                                        null
                                )
                        );

            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new StateMessage("Nonce expired", false, ExceptionState.EXPIRED_NONCE));


        } catch (ExtendedAuthenticationException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new StateMessage(e.getMessage(), false, e.getExceptionState()));

        } catch (Exception e) {

            logger.error("Exception occurred:", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            new StateMessage(
                                    "Internal Server Error",
                                    false,
                                    ExceptionState.INTERNAL_SERVER_ERROR
                            )
                    );
        }

    }


}
