package org.wpstarters.jwtauthprovider.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wpstarters.jwtauthprovider.api.state.StateMessage;
import org.wpstarters.jwtauthprovider.dto.SocialAccountInfo;
import org.wpstarters.jwtauthprovider.service.ITokenService;
import org.wpstarters.jwtauthprovider.model.CustomUserDetails;
import org.wpstarters.jwtauthprovider.dto.IAuthenticationRequest;
import org.wpstarters.jwtauthprovider.dto.IStateMessage;
import org.wpstarters.jwtauthprovider.dto.LoginRequest;
import org.wpstarters.jwtauthprovider.model.ProviderType;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.IUserDetailsService;
import org.wpstarters.jwtauthprovider.service.INonceStrategy;
import org.wpstarters.jwtauthprovider.service.IUserVerificationService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SignInController {

    private static final Logger logger = LoggerFactory.getLogger(SignInController.class);

    private final AuthenticationManager authenticationManager;
    private final IUserVerificationService userVerificationService;
    private final INonceStrategy nonceStrategy;
    private final IUserDetailsService userDetailsService;
    private final ITokenService tokenService;

    public SignInController(AuthenticationManager authenticationManager,
                            IUserVerificationService userVerificationService,
                            IUserDetailsService userDetailsService,
                            ITokenService tokenService,
                            INonceStrategy nonceStrategy) {
        this.authenticationManager = authenticationManager;
        this.userVerificationService = userVerificationService;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.nonceStrategy = nonceStrategy;
    }

//    @PostMapping("/signin/social")
//     nonce is optional, since we (must) use Authorization code + PKCE flow
    public ResponseEntity<? extends IStateMessage> socialSignIn(@RequestParam(name = "code") String authorizationCode,
                                          @RequestParam(name = "state") String providerName) {

        StateMessage errorMessage = new StateMessage("", false, ExceptionState.INTERNAL_SERVER_ERROR);

        try {

            ProviderType providerType = ProviderType.valueOf(providerName);
            SocialAccountInfo socialAccountInfo = userVerificationService.verifySocialAccount(authorizationCode, providerType);
            if (socialAccountInfo != null && socialAccountInfo.isEmailVerified()) {

                CustomUserDetails socialDetails = toCustomUserDetails(socialAccountInfo, providerType);
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
    // here we set the nonce
    public ResponseEntity<? extends IStateMessage> basicSignIn1(@RequestBody LoginRequest loginRequest) {

        // nonce is set and generated inside 'sendVerificationCode(String)' method
        return sendVerificationCode(loginRequest);

    }

    @PostMapping("/signin/basic/2")
    // here we check the nonce, that was set at "/signin/basic/1"
    public ResponseEntity<? extends IStateMessage> basicSignIn2(@RequestBody LoginRequest loginRequest) {

        try {
            if (nonceStrategy.validNonce(loginRequest.getNonce())) {
                if (userVerificationService.verifyCodeForRequest(loginRequest)) {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getId(),
                                    loginRequest.getPassword()
                            )
                    );


                    if (authentication != null && authentication.isAuthenticated()) {
                        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(authentication.getName());
                        String token = tokenService.generateJwtToken(userDetails);
                        return ResponseEntity.ok(new StateMessage("token " + token, true, null));
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

    private ResponseEntity<? extends IStateMessage> sendVerificationCode(IAuthenticationRequest authenticationRequest) {

        try {
                userVerificationService.sendVerificationForRequest(authenticationRequest);
                return nonceIsSentResponse();

        } catch (RuntimeException exception) {

            logger.error("Exception occurred, while sending verification request for {}", authenticationRequest, exception);
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

    private String loginSocialUser(CustomUserDetails socialDetails)
            throws JsonProcessingException {

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(socialDetails.getUsername());

        if (userDetails != null) {

            return tokenService.generateJwtToken(userDetails);

        }

        // should not reach there;

        throw new UsernameNotFoundException("Username not found");

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
