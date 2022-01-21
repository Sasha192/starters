package org.wpstarters.jwtauthprovider.service;

import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.wpstarters.jwtauthprovider.dto.SocialAccountInfo;
import org.wpstarters.jwtauthprovider.model.ProviderType;

import javax.validation.constraints.NotNull;

public abstract class AbstractSocialUserVerificationService implements IUserVerificationService {

    private static final String GOOGLE_REQUEST_ID_TOKEN = "https://oauth2.googleapis.com/tokeninfo?id_token=%s";
    private static final RestTemplate restTemplate = new RestTemplate();

    /*
    *
    *  {
             // These six fields are included in all Google ID Tokens.
             "iss": "https://accounts.google.com",
             "sub": "110169484474386276334",
             "azp": "1008719970978-hb24n2dstb40o45d4feuo2ukqmcc6381.apps.googleusercontent.com",
             "aud": "1008719970978-hb24n2dstb40o45d4feuo2ukqmcc6381.apps.googleusercontent.com",
             "iat": "1433978353",
             "exp": "1433981953",

             // These seven fields are only included when the user has granted the "profile" and
             // "email" OAuth scopes to the application.
             "email": "testuser@gmail.com",
             "email_verified": "true",
             "name" : "Test User",
             "picture": "https://lh4.googleusercontent.com/-kYgzyAWpZzJ/ABCDEFGHI/AAAJKLMNOP/tIXL9Ir44LE/s99-c/photo.jpg",
             "given_name": "Test",
             "family_name": "User",
             "locale": "en"
            }
    *
    *
    * */

    @Override
    @Nullable
    public SocialAccountInfo verifySocialAccount(@NotNull String authorizationCode,@NotNull ProviderType provider) {

        switch (provider) {
            case GOOGLE_PROVIDER: {

                String query = GOOGLE_REQUEST_ID_TOKEN.formatted(authorizationCode);
                return restTemplate.getForObject(query, SocialAccountInfo.class);

            }
        }
        return null;

    }
}
