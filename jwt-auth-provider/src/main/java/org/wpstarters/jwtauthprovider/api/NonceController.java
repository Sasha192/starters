package org.wpstarters.jwtauthprovider.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wpstarters.jwtauthprovider.api.state.StateMessage;
import org.wpstarters.jwtauthprovider.dto.IStateMessage;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.INonceStrategy;

@RestController
public class NonceController {

    private final INonceStrategy nonceStrategy;

    public NonceController(INonceStrategy nonceStrategy) {
        this.nonceStrategy = nonceStrategy;
    }

    @GetMapping("/nonce")
    public ResponseEntity<? extends IStateMessage> nonce() {

        String nonce = nonceStrategy.generateNonce();

        try {
            if (nonce != null && nonceStrategy.saveNonce(nonce)) {
                return ResponseEntity.ok().body(new StateMessage("nonce " + nonce, true, null));
            }

            return ResponseEntity.internalServerError().body(new StateMessage("Can not generate nonce for you", false, ExceptionState.INTERNAL_SERVER_ERROR));
        } catch (ExtendedAuthenticationException e) {
            return ResponseEntity.internalServerError().body(new StateMessage(e.getMessage(), false, e.getExceptionState()));
        }

    }

    @GetMapping("/nonce/validate")
    public ResponseEntity<? extends IStateMessage> validate(@RequestParam(value = "nonce", required = false) String encodedNonce) {

        if (StringUtils.isNotBlank(encodedNonce)) {

            try {

                boolean valid = nonceStrategy.validNonce(encodedNonce);
                return ResponseEntity.ok().body(new StateMessage("valid " + valid, true, null));

            } catch (ExtendedAuthenticationException e) {
                return ResponseEntity.badRequest().body(new StateMessage(e.getMessage(), false, e.getExceptionState()));
            }

        }

        return ResponseEntity.badRequest().body(new StateMessage("Your nonce is blank", false, ExceptionState.INVALID_NONCE));

    }



}
