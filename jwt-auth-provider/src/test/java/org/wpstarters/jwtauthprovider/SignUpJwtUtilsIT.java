package org.wpstarters.jwtauthprovider;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import org.wpstarters.jwtauthprovider.api.Utf8Json;
import org.wpstarters.jwtauthprovider.api.state.StateMessage;
import org.wpstarters.jwtauthprovider.dto.IStateMessage;
import org.wpstarters.jwtauthprovider.dto.RefreshTokenRequest;
import org.wpstarters.jwtauthprovider.dto.SignUpRequest;
import org.wpstarters.jwtauthprovider.model.ProviderType;
import org.wpstarters.jwtauthprovider.repository.IRefreshTokenRepository;
import org.wpstarters.jwtauthprovider.repository.UserDetailsRepository;
import org.wpstarters.jwtauthprovider.service.IUserVerificationService;

import javax.servlet.http.Cookie;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;


class SignUpJwtUtilsIT extends BaseIT {

	@Autowired
	IUserVerificationService userVerificationService;

	@Autowired
	IRefreshTokenRepository refreshTokenRepository;

	@Autowired
	UserDetailsRepository userDetailsRepository;

	private String id = "ID_NAME";
	private String password = "SUPER_SECURE_PASSWORD";
	private ProviderType providerType = ProviderType.BASIC;

	@Override
	protected void beforeEach() {
		refreshTokenRepository.deleteAll();
		userDetailsRepository.deleteAll();
	}

	@AfterEach
	public void cleanup() {

		refreshTokenRepository.deleteAll();

	}

	@Test
	public void testSignUp() throws Exception {

		// basic1
		MvcResult basic1Result = basic1Result();

			// retrieve nonce from basic1
			Cookie nonceCookie = basic1Result.getResponse().getCookie("COOKIE_NONCE");
			String encryptedNonce = objectMapper.readValue(basic1Result.getResponse().getContentAsString(), StateMessage.class)
					.getMessage().substring("nonce".length()).trim();

			// user should not be inserted on this stage
			assertThat(userDetailsRepository.findById(id).orElse(null)).isNull();

		// basic2
		MvcResult basic2 = basic2Result(nonceCookie, encryptedNonce);


			// get token from basic2
			IStateMessage message = fromContent(basic2.getResponse().getContentAsString());
			String token = message.getMessage().substring("token".length()).trim();

			Cookie refreshTokenCookie = getRefreshTokenCookie(basic2);

			assertThat(refreshTokenCookie).isNotNull();

			String refreshToken1 = refreshTokenCookie.getValue();

			// user should already exist on this stage
			assertThat(userDetailsRepository.findById(id).orElse(null)).isNotNull();


		// refresh token

		MvcResult refreshTokenResult = refreshTokenResult(nonceCookie, encryptedNonce, token, refreshTokenCookie);

			// from refresh token result retrieve refresh token and check
			String refreshToken2 = getRefreshTokenCookie(refreshTokenResult).getValue();

			String newToken = fromContent(refreshTokenResult.getResponse().getContentAsString()).getMessage()
					.substring("token".length()).trim();

			assertThat(refreshToken1).isNotEqualTo(refreshToken2);
			assertThat(newToken).isNotEqualTo(token);

	}

						// ------ PRIVATE  METHODS ------

	private MvcResult refreshTokenResult(Cookie nonceCookie, String encryptedNonce, String token, Cookie refreshTokenCookie) throws Exception {
		final UriComponentsBuilder refreshTokenUriBuilder = fromUriString("/refresh-token");

		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(token, encryptedNonce);

		return mockMvc.perform(
				post(refreshTokenUriBuilder.build().encode().toUri())
						.cookie(nonceCookie, refreshTokenCookie)
						.content(objectMapper.writeValueAsString(refreshTokenRequest))
						.accept(Utf8Json.APPLICATION_JSON_VALUE)
						.contentType(Utf8Json.APPLICATION_JSON_VALUE)
		).andExpect(cookie().exists("REFRESH_TOKEN_COOKIE")).andReturn();
	}

	private MvcResult basic2Result(Cookie nonceCookie, String encryptedNonce) throws Exception {
		// basic2
		final UriComponentsBuilder validateNonce = fromUriString("/signup/basic/2");

		SignUpRequest signUpRequest2 = new SignUpRequest.Builder()
				.id(id)
				.password(password)
				.provider(providerType)
				.nonce(encryptedNonce)
				.code("ANY_CODE")
				.publicDetails(new HashMap<>())
				.build();

		return mockMvc.perform(
				post(validateNonce.build().encode().toUri())
						.cookie(nonceCookie)
						.content(objectMapper.writeValueAsString(signUpRequest2))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
		).andExpect(cookie().exists("REFRESH_TOKEN_COOKIE")).andReturn();
	}

	private MvcResult basic1Result() throws Exception {
		// basic 1
		final UriComponentsBuilder basic1 = fromUriString("/signup/basic/1");

		final SignUpRequest signUpRequest1 = new SignUpRequest.Builder()
				.id(id)
				.provider(providerType)
				.publicDetails(new HashMap<>())
				.build();

		MvcResult basic1Result = mockMvc.perform(
				post(basic1.build().encode().toUri())
				.content(objectMapper.writeValueAsString(signUpRequest1))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk()).andExpect(cookie().exists("COOKIE_NONCE")).andReturn();

		assertThat(basic1).isNotNull();
		return basic1Result;
	}

	private Cookie getRefreshTokenCookie(MvcResult result) {
		return result.getResponse().getCookie("REFRESH_TOKEN_COOKIE");
	}

	private IStateMessage fromContent(String content) throws JsonProcessingException {
		return objectMapper.readValue(content, StateMessage.class);
	}

}
