package org.wpstarters.jwtauthprovider.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wpstarters.jwtauthprovider.api.state.StateMessage;
import org.wpstarters.jwtauthprovider.service.impl.TokenService;
import org.wpstarters.jwtauthprovider.model.CustomUserDetails;
import org.wpstarters.jwtauthprovider.dto.IAuthenticationRequest;
import org.wpstarters.jwtauthprovider.dto.IStateMessage;
import org.wpstarters.jwtauthprovider.model.ProviderType;
import org.wpstarters.jwtauthprovider.dto.SignUpRequest;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.INonceStrategy;
import org.wpstarters.jwtauthprovider.service.IUserDetailsService;
import org.wpstarters.jwtauthprovider.service.IUserVerificationService;

import java.util.Map;

@RestController
public class SignUpController {

    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    private final IUserVerificationService IUserVerificationService;
    private final INonceStrategy nonceStrategy;
    private final ObjectMapper objectMapper;
    private final IUserDetailsService userDetailsService;
    private final TokenService tokenService;

    public SignUpController(IUserVerificationService IUserVerificationService,
                            ObjectMapper objectMapper,
                            IUserDetailsService userDetailsService,
                            TokenService tokenService,
                            INonceStrategy nonceStrategy) {
        this.IUserVerificationService = IUserVerificationService;
        this.objectMapper = objectMapper;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.nonceStrategy = nonceStrategy;
    }

    @PostMapping("/signup/basic/1")
    // here we set the nonce
    public ResponseEntity<? extends IStateMessage> basicSignUp1(@RequestBody SignUpRequest tokenRequest) {
        return sendVerificationCode(tokenRequest);
    }

    @PostMapping("/signup/basic/2")
    // here we check the nonce, that was set at "/signup/basic/1"
    public ResponseEntity<? extends IStateMessage> basicSignUp2(@RequestBody SignUpRequest tokenRequest) {

        try {

            if (nonceStrategy.validNonce(tokenRequest.getNonce())) {
                if (IUserVerificationService.verifyCodeForRequest(tokenRequest)) {

                    CustomUserDetails userDetails = retrieveBasicUserDetails(tokenRequest);
                    userDetails = registerUser(userDetails);
                    String token = tokenService.generateJwtToken(userDetails);

                    return ResponseEntity.ok(new StateMessage("token " + token, true, null));

                }

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new StateMessage("Wrong verification code", false, ExceptionState.INVALID_VERIFICATION_CODE));

            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new StateMessage("Nonce is invalid", false, ExceptionState.INVALID_NONCE));

        }  catch (ExtendedAuthenticationException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new StateMessage(e.getMessage(), false, e.getExceptionState()));

        } catch (RuntimeException | JsonProcessingException exception) {

            StateMessage errorMessage = new StateMessage("", false, null);
            logger.error("Exception occurred, while /signup/basic/2, ", exception);
            if (exception instanceof JsonProcessingException) {
                errorMessage.setMessage("Internal server error. You passed wrong json format");
            } else {
                errorMessage.setMessage("Internal server error, please try again later");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        } catch (Exception e) {
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

    @PostMapping("/signup/social")
    public ResponseEntity<? extends IStateMessage> socialSignUp(@RequestParam(name = "code") String authorizationCode,
                                          @RequestParam(name = "state") String providerName) {

        StateMessage errorMessage = new StateMessage("", false, ExceptionState.INTERNAL_SERVER_ERROR);
        try {

            Map<String, Object> mapDetails = IUserVerificationService.verifySocialAccount(authorizationCode, ProviderType.valueOf(providerName));
            if (mapDetails != null) {

                CustomUserDetails socialDetails = objectMapper.convertValue(mapDetails, CustomUserDetails.class);
                socialDetails = registerUser(socialDetails);
                String token = tokenService.generateJwtToken(socialDetails);

                return ResponseEntity.ok(new StateMessage(token, true, null));
            }

            errorMessage.setMessage("Can not verify your account.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorMessage);

        } catch (RuntimeException | JsonProcessingException exception) {

            logger.error("Exception occurred, while /signup/social, ", exception);
            errorMessage.setMessage("Something went wrong. Please, try later");
            return ResponseEntity.internalServerError()
                    .body(errorMessage);

        }

    }

    private ResponseEntity<? extends IStateMessage> sendVerificationCode(IAuthenticationRequest authenticationRequest) {

        try {

            if (IUserVerificationService.sendVerificationForRequest(authenticationRequest)) {
                return nonceIsSentResponse();
            }

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new StateMessage("Sorry, something went wrong. Please, try later", false, ExceptionState.INTERNAL_SERVER_ERROR));

        } catch (RuntimeException exception) {

            logger.error("Exception occurred, sending verification request for {}", authenticationRequest, exception);
            if (exception instanceof AuthenticationException) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new StateMessage("Sorry, cannot process your request:", false, ExceptionState.INTERNAL_SERVER_ERROR));

            } else {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new StateMessage("Internal server error, please try again later", false, ExceptionState.INTERNAL_SERVER_ERROR));

            }
        }

    }

    private ResponseEntity<StateMessage> nonceIsSentResponse() {
        String encryptedNonce = nonceStrategy.generateNonce();

        if (nonceStrategy.saveNonce(encryptedNonce)) {
            return ResponseEntity.accepted()
                    .body(
                            new StateMessage(
                                    "Nonce:" + encryptedNonce,
                                    true,
                                    null
                            )
                    );
        } else {
            return ResponseEntity.accepted()
                    .body(
                            new StateMessage(
                                    "Can not save secret nonce for this request",
                                    false,
                                    ExceptionState.INTERNAL_SERVER_ERROR
                            )
                    );
        }
    }

    private CustomUserDetails registerUser(CustomUserDetails userDetails)
            throws JsonProcessingException {
        return userDetailsService.save(userDetails);
    }


    private CustomUserDetails retrieveBasicUserDetails(SignUpRequest tokenRequest)
            throws JsonProcessingException {

        return new CustomUserDetails.Builder()
                .basicAccount(true)
                .username(tokenRequest.getId())
                .password(tokenRequest.getPassword())
                .extraDetails(objectMapper.writeValueAsString(tokenRequest.getUserDetails()))
                .build();

    }


}
