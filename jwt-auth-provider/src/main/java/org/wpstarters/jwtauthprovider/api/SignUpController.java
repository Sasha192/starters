package org.wpstarters.jwtauthprovider.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.wpstarters.jwtauthprovider.api.state.StateMessage;
import org.wpstarters.jwtauthprovider.dto.SocialAccountInfo;
import org.wpstarters.jwtauthprovider.service.ITokenService;
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

import java.util.HashMap;
import java.util.Map;

@Controller
public class SignUpController {

    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    private final IUserVerificationService userVerificationService;
    private final INonceStrategy nonceStrategy;
    private final IUserDetailsService userDetailsService;
    private final ITokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public SignUpController(IUserVerificationService userVerificationService,
                            IUserDetailsService userDetailsService,
                            ITokenService tokenService,
                            INonceStrategy nonceStrategy,
                            PasswordEncoder passwordEncoder) {
        this.userVerificationService = userVerificationService;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.nonceStrategy = nonceStrategy;
        this.passwordEncoder = passwordEncoder;
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
                if (userVerificationService.verifyCodeForRequest(tokenRequest)) {

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

    //@PostMapping("/signup/social")
    public ResponseEntity<? extends IStateMessage> socialSignUp(@RequestParam(name = "code") String authorizationCode,
                                          @RequestParam(name = "state") String providerName) {

        StateMessage errorMessage = new StateMessage("", false, ExceptionState.INTERNAL_SERVER_ERROR);
        try {

            ProviderType providerType = ProviderType.valueOf(providerName);
            SocialAccountInfo socialAccountInfo = userVerificationService.verifySocialAccount(authorizationCode, providerType);
            if (socialAccountInfo != null && socialAccountInfo.isEmailVerified()) {

                CustomUserDetails socialDetails = toCustomUserDetails(socialAccountInfo, providerType);
                socialDetails = registerUser(socialDetails);
                String token = tokenService.generateJwtToken(socialDetails);

                return ResponseEntity.ok(new StateMessage(token, true, null));
            }

            errorMessage.setMessage("Can not verify your account.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorMessage);

        }
        catch (RuntimeException | JsonProcessingException exception) {

            logger.error("Exception occurred, while /signup/social, ", exception);
            errorMessage.setMessage("Something went wrong. Please, try later");
            return ResponseEntity.internalServerError()
                    .body(errorMessage);

        }

    }

    private ResponseEntity<? extends IStateMessage> sendVerificationCode(IAuthenticationRequest authenticationRequest) {

        try {

            userVerificationService.sendVerificationForRequest(authenticationRequest);
            return nonceIsSentResponse();

        }  catch (RuntimeException exception) {

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
            return ResponseEntity.ok()
                    .body(
                            new StateMessage(
                                    "nonce " + encryptedNonce,
                                    true,
                                    null
                            )
                    );
        } else {
            return ResponseEntity.internalServerError()
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
                .publicDetails(tokenRequest.getPublicDetails())
                .encoder(passwordEncoder)
                .build();

    }

    private CustomUserDetails toCustomUserDetails(SocialAccountInfo socialAccountInfo, ProviderType providerType) {

        Map<String, Object> publicDetails = new HashMap<>();
        publicDetails.put("picture", socialAccountInfo.getPicture());
        publicDetails.put("name", socialAccountInfo.getName());

        return new CustomUserDetails.Builder()
                .basicAccount(false)
                .username(socialAccountInfo.getEmail())
                .publicDetails(publicDetails)
                .providerType(providerType)
                .build();

    }

}
