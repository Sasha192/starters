package org.wpstarters37.authorizationstarter.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wpstarters37.authorizationstarter.Encoding;
import org.wpstarters37.authorizationstarter.domain.services.UserVerificationService;;

import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = RegistrationController.REGISTRATION_CONTROLLER)
public class RegistrationController {

    public static final String REGISTRATION_CONTROLLER = "/clients/";

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final ClientRegistrationService clientRegistrationService;
    private final ClientDetailsService clientDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationService userVerificationService;
    private final TokenStore tokenStore;

    public RegistrationController(ClientDetailsService clientDetailsService,
                          ClientRegistrationService clientRegistrationService,
                          PasswordEncoder passwordEncoder,
                          TokenStore tokenStore,
                          UserVerificationService verificationService) {
        this.clientDetailsService = clientDetailsService;
        this.clientRegistrationService = clientRegistrationService;
        this.passwordEncoder = passwordEncoder;
        this.tokenStore = tokenStore;
        this.userVerificationService = verificationService;
    }

    @PostMapping(value = "send-client-verification",
            consumes = Encoding.JSON_UTF_8,
            produces = Encoding.JSON_UTF_8)
    @Operation(summary = "Receiving client details and sending verification code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ApplicationStateResponse.class
                            )
                    )
            })
    })
    public ResponseEntity<ApplicationStateResponse> sendVerification(@RequestBody VerificationRequest request) {
        ClientDto dto = request.getDto();
        try {
            try {
                ClientDetails cl = clientDetailsService.loadClientByClientId(dto.getClientId());
                if (cl != null) {
                    return new ResponseEntity<>(new ApplicationStateResponse("Client is already exist", false), HttpStatus.BAD_REQUEST);
                }
            } catch (NoSuchClientException e) {
                return new ResponseEntity<>(new ApplicationStateResponse("Client is already exist", false), HttpStatus.CONFLICT);
            }
            userVerificationService.sendVerification(dto.getClientDetails());
            return new ResponseEntity<>(new ApplicationStateResponse("Verification ticket was created", true), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Message:\n" + e.getMessage() + "\nCause:\n" + e.getCause());
            return new ResponseEntity<>(new ApplicationStateResponse("InternalServerError", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationStateResponse.class))
            })
    })
    @Operation(summary = "Receiving client verification code and comparing with corresponding one")
    @PostMapping(value = "client-registration",
            consumes = Encoding.JSON_UTF_8,
            produces = Encoding.JSON_UTF_8)
    public ResponseEntity<ApplicationStateResponse> register(@RequestBody VerificationRequest request) {
        try {
            if (userVerificationService.verify(request.getTicket())) {
                ClientDetails clientDetails = request.getDto().getClientDetails();
                clientRegistrationService.addClientDetails(clientDetails);
                return new ResponseEntity<>(new ApplicationStateResponse("Client created", true), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(
                        new ApplicationStateResponse("Invalid verification ticket", false),
                        HttpStatus.BAD_REQUEST
                );
            }
        }  catch (ClientAlreadyExistsException e) {
            log.error("ClientAlreadyExistsException:\n" + e.getMessage() +
                    "\nCause:\n" + e.getCause() +
                    "Data:\n" + request.getDto()
            );
            return new ResponseEntity<>(new ApplicationStateResponse("Client already exists", false), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception:\n" + e.getMessage() + "\nCause:\n" + e.getCause());
            return new ResponseEntity<>(new ApplicationStateResponse("InternalServerError", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Validated
    public static final class ClientDto {

        private final String clientId;
        private final String clientSecret;
        private Map<String, Object> additionalInformation = new HashMap<>();

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public ClientDto(@JsonProperty(value = "clientId", required = true)
                         @Pattern(regexp = "[a-zA-Z0-9_]+")
                         @Length(min = 1, max = 32) String clientId,
                         @JsonProperty("clientSecret")
                         @Length(min = 8, max = 64) String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public String getResourceIds() {
            return "bff";
        }

        public String getScopes() {
            return "read,write";
        }

        public String getGrantTypes() {
            return "authorization_code," +
                    "check_token," +
                    "refresh_token," +
                    "password," +
                    "client_credentials";
        }

        public String getAuthorities() {
            return "ROLE_USER";
        }

        public int getAccessTokenValidity() {
            //seconds
            return 60 * 60 * 24 * 14;
        }

        public int getRefreshTokenValidity() {
            return 60 * 60 * 24 * 14 * 2;
        }

        @Override
        public String toString() {
            return "ClientDto{" +
                        "clientId='" + clientId + '\'' +
                    '}';
        }

        private Map<String, Object> getAdditionalInformation() {
            return additionalInformation;
        }

        @JsonSetter(value = "additionalInformation")
        public void setAdditionalInformation(Map<String, Object> additionalInformation) {
            this.additionalInformation = additionalInformation;
        }

        private BaseClientDetails clientDetails() {
            BaseClientDetails clientDetails = new BaseClientDetails(
                    getClientId(), getResourceIds(),
                    getScopes(), getGrantTypes(), getAuthorities()
            );
            clientDetails.setAccessTokenValiditySeconds(getAccessTokenValidity());
            clientDetails.setRefreshTokenValiditySeconds(getRefreshTokenValidity());
            clientDetails.setAdditionalInformation(getAdditionalInformation());
            return clientDetails;
        }

        private ClientDetails getClientDetails() {
            BaseClientDetails clientDetails = clientDetails();
            clientDetails.setClientSecret(getClientSecret());
            return clientDetails;
        }
    }

    public static class VerificationRequest {

        @JsonProperty(required = true)
        private ClientDto dto;
        @JsonProperty(value = "ticket")
        private String ticket;
        @JsonProperty(value = "verificationMethod")
        private String verificationMethod;


        public ClientDto getDto() {
            return dto;
        }

        public void setDto(ClientDto dto) {
            this.dto = dto;
        }

        public String getTicket() {
            return ticket;
        }

        public void setTicket(String ticket) {
            this.ticket = ticket;
        }

        public void setVerificationMethod(String verificationMethod) {
            this.verificationMethod = verificationMethod;
        }

        public String getVerificationMethod() {
            return verificationMethod;
        }
    }
}
