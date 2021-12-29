package org.wpstarters.jwtauthprovider.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wpstarters.jwtauthprovider.config.JwtUtils;
import org.wpstarters.jwtauthprovider.dto.CustomUserDetails;
import org.wpstarters.jwtauthprovider.dto.IAuthencationRequest;
import org.wpstarters.jwtauthprovider.dto.IStateMessage;
import org.wpstarters.jwtauthprovider.dto.LoginRequest;
import org.wpstarters.jwtauthprovider.dto.ProviderType;
import org.wpstarters.jwtauthprovider.dto.RefreshToken;
import org.wpstarters.jwtauthprovider.dto.SignUpRequest;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.IKeyPairSupplier;
import org.wpstarters.jwtauthprovider.service.IRefreshTokenService;
import org.wpstarters.jwtauthprovider.service.IUserDetailsService;
import org.wpstarters.jwtauthprovider.service.IUserEnablementPredicate;
import org.wpstarters.jwtauthprovider.service.INonceStrategy;
import org.wpstarters.jwtauthprovider.service.IUserVerificationService;

import java.util.Map;

@RestController
public class AuthenticationRestController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationRestController.class);

    private final IKeyPairSupplier keyPairSupplier;
    private final AuthenticationManager authenticationManager;
    private final IUserVerificationService IUserVerificationService;
    private final INonceStrategy nonceStrategy;
    private final ObjectMapper objectMapper;
    private final IUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final IUserEnablementPredicate userEnablementPredicate;
    private final IRefreshTokenService refreshTokenService;

    public AuthenticationRestController(IKeyPairSupplier keyPairSupplier,
                                        AuthenticationManager authenticationManager,
                                        IUserVerificationService IUserVerificationService,
                                        ObjectMapper objectMapper,
                                        IUserDetailsService userDetailsService,
                                        JwtUtils jwtUtils,
                                        @Autowired(required = false) IUserEnablementPredicate userEnablementPredicate,
                                        IRefreshTokenService refreshTokenService,
                                        INonceStrategy nonceStrategy) {
        this.keyPairSupplier = keyPairSupplier;
        this.authenticationManager = authenticationManager;
        this.IUserVerificationService = IUserVerificationService;
        this.objectMapper = objectMapper;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userEnablementPredicate = (CustomUserDetails::isEnabled);
        this.refreshTokenService = refreshTokenService;
        this.nonceStrategy = nonceStrategy;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return this.keyPairSupplier.jwkSet().toJSONObject();
    }

    @PostMapping("/signup/basic/1")
    public ResponseEntity<? extends IStateMessage> basicSignUp1(@RequestBody SignUpRequest tokenRequest) {
        return sendVerificationCode(tokenRequest);
    }

    @PostMapping("/signup/basic/2")
    public ResponseEntity<? extends IStateMessage> basicSignUp2(@RequestBody SignUpRequest tokenRequest,
                                                                @RequestParam String nonce) {

        try {

            if (nonceStrategy.validNonce(nonce)) {
                if (IUserVerificationService.verifyCodeForRequest(tokenRequest)) {

                    CustomUserDetails userDetails = retrieveBasicUserDetails(tokenRequest);
                    userDetails = registerUser(userDetails);

                    return ResponseEntity.ok(new StateMessage("Logged in", true, null));

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
                String token = registerUser(socialDetails);

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

    @PostMapping("/signin/social")
    public ResponseEntity<? extends IStateMessage> socialSignIn(@RequestParam(name = "code") String authorizationCode,
                                          @RequestParam(name = "state") String providerName) {

        StateMessage errorMessage = new StateMessage("", false, ExceptionState.INTERNAL_SERVER_ERROR);
        try {

            Map<String, Object> mapDetails = IUserVerificationService.verifySocialAccount(authorizationCode, ProviderType.valueOf(providerName));
            if (mapDetails != null) {

                CustomUserDetails socialDetails = objectMapper.convertValue(mapDetails, CustomUserDetails.class);
                String token = loginSocialUser(socialDetails);

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

    @PostMapping("/signin/basic/1")
    public ResponseEntity<? extends IStateMessage> basicSignIn1(LoginRequest loginRequest) {

        return sendVerificationCode(loginRequest);

    }

    @PostMapping("/signin/basic/2")
    public ResponseEntity<? extends IStateMessage> basicSignIn2(
            @RequestBody LoginRequest loginRequest, @RequestParam(name = "nonce") String nonce) {

        try {
            if (nonceStrategy.validNonce(nonce)) {
                if (IUserVerificationService.verifyCodeForRequest(loginRequest)) {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getId(),
                                    loginRequest.getPassword()
                            )
                    );


                    if (authentication != null) {
                        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(authentication.getName());
                        String token = jwtUtils.generateJwtToken(userDetails);
                        return ResponseEntity.ok(new StateMessage(token, true, null));
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new StateMessage("Nonce expired", false, ExceptionState.EXPIRED_NONCE));

        } catch (ExtendedAuthenticationException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new StateMessage(e.getMessage(), false, e.getExceptionState()));

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

    @PostMapping
    public ResponseEntity<? extends IStateMessage> refreshToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest,
            @RequestParam String nonce) {

        try {
            if (nonceStrategy.validNonce(nonce)) {
                RefreshToken refreshToken = refreshTokenService.findOne(refreshTokenRequest.getRefreshTokenId());

                if (refreshTokenService.isValid(refreshToken)) {

                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(refreshToken.getUsername());
                    if (userEnablementPredicate.test(customUserDetails)) {

                        String token = jwtUtils.generateJwtToken(customUserDetails);
                        return ResponseEntity.ok(new StateMessage(token, true, null));

                    }
                }

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new StateMessage("Refresh token is not valid", false, ExceptionState.INVALID_TOKEN));

            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new StateMessage("Nonce expired", false, ExceptionState.EXPIRED_NONCE));


        } catch (ExtendedAuthenticationException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new StateMessage(e.getMessage(), false, e.getExceptionState()));

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

    private ResponseEntity<? extends IStateMessage> sendVerificationCode(IAuthencationRequest authencationRequest) {

        try {

            if (IUserVerificationService.sendVerificationForRequest(authencationRequest)) {

                return ResponseEntity.accepted()
                        .body(new StateMessage("Code was sent, please enter the code", true, null));

            }

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new StateMessage("Sorry, something went wrong. Please, try later", false, ExceptionState.INTERNAL_SERVER_ERROR));

        } catch (RuntimeException exception) {

            logger.error("Exception occurred, sending verification request for {}", authencationRequest, exception);
            if (exception instanceof AuthenticationException) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new StateMessage("Sorry, cannot process your request:", false, ExceptionState.INTERNAL_SERVER_ERROR));

            } else {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new StateMessage("Internal server error, please try again later", false, ExceptionState.INTERNAL_SERVER_ERROR));

            }
        }

    }

    private String loginSocialUser(CustomUserDetails socialDetails)
            throws JsonProcessingException {

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(socialDetails.getUsername());

        if (userDetails != null) {

            return jwtUtils.generateJwtToken(userDetails);

        }

        // should not reach there;

        throw new UsernameNotFoundException("Username not found");

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


    public static class StateMessage implements IStateMessage {

        private String message;
        private boolean success;
        private ExceptionState exceptionState;

        public StateMessage(String message, boolean success, ExceptionState exceptionState) {
            this.message = message;
            this.success = success;
            this.exceptionState = exceptionState;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public ExceptionState getRequestState() {
            return exceptionState;
        }

        public void setRequestState(ExceptionState exceptionState) {
            this.exceptionState = exceptionState;
        }
    }

    public static class RefreshTokenRequest {

        private final String refreshTokenId;
        private final String nonce;

        @JsonCreator
        public RefreshTokenRequest(@JsonProperty String refreshTokenId,
                                   @JsonProperty String nonce) {
            this.refreshTokenId = refreshTokenId;
            this.nonce = nonce;
        }

        public String getRefreshTokenId() {
            return refreshTokenId;
        }

        public String getNonce() {
            return nonce;
        }
    }


}
