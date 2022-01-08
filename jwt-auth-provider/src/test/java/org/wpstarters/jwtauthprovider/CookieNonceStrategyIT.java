package org.wpstarters.jwtauthprovider;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import org.wpstarters.jwtauthprovider.api.state.StateMessage;
import org.wpstarters.jwtauthprovider.dto.IStateMessage;

import javax.servlet.http.Cookie;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


class CookieNonceStrategyIT extends BaseIT {

	private PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder(String.valueOf(new SecureRandom().nextLong()));
	private PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);


	@Override
	protected void beforeEach() {
		//
	}

	@Test
	public void testCookieNonceStrategy() throws Exception {

		final UriComponentsBuilder getNonce = fromUriString("/nonce/");

		MvcResult getNonceResult = mockMvc.perform(get(getNonce.build().encode().toUri()))
				.andExpect(cookie().exists("COOKIE_NONCE"))
				.andReturn();


		int fromNonce = "nonce".length();
		assertThat(getNonceResult).isNotNull();

		Cookie nonceCookie = getNonceResult.getResponse().getCookie("COOKIE_NONCE");
		String encryptedNonce = objectMapper.readValue(getNonceResult.getResponse().getContentAsString(), StateMessage.class)
				.getMessage().substring(fromNonce).trim();

		final UriComponentsBuilder validateNonce = fromUriString("/nonce/validate").queryParam("nonce", encryptedNonce);

		MvcResult validNonceResult = mockMvc.perform(
				get(validateNonce.build().encode().toUri()).cookie(nonceCookie)
		).andReturn();


		IStateMessage message = fromContent(validNonceResult.getResponse().getContentAsString());
		String isValid = message.getMessage().substring("valid".length()).trim();

		assertThat(Boolean.valueOf(isValid)).isTrue();



	}

	private IStateMessage fromContent(String content) throws JsonProcessingException {
		return objectMapper.readValue(content, StateMessage.class);
	}

	@Test
	public void testEfficiency() throws JsonProcessingException {

		for (PasswordEncoder passwordEncoder: List.of(pbkdf2PasswordEncoder, bCryptPasswordEncoder)) {
			long min = Integer.MAX_VALUE;
			long max = Integer.MIN_VALUE;
			long sum = 0;
			int counter = 0;

			for (String text : plainTexts()) {

				long start = System.currentTimeMillis();

				passwordEncoder.encode(text);

				long lasted = System.currentTimeMillis() - start;

				if (lasted > max) {
					max = lasted;
				}

				if (lasted < min) {
					min = lasted;
				}

				sum += lasted;
				++counter;

			}

			Map<String, Object> metrics = new HashMap<>();

			metrics.put("maxMs", max);
			metrics.put("minMs", min);
			metrics.put("meanMs", sum / counter);
			metrics.put("name", passwordEncoder.getClass().getSimpleName());


			System.out.println(objectMapper.writeValueAsString(metrics));
		}

	}

	private String[] plainTexts() {
		String[] array = new String[10];

		array[0] = plainText();
		array[1] = plainText();
		array[2] = plainText();
		array[3] = plainText();
		array[4] = plainText();
		array[5] = plainText();
		array[6] = plainText();
		array[7] = plainText();
		array[8] = plainText();
		array[9] = plainText();

		return array;

	}

	private String plainText() {

		return UUID.randomUUID().toString().replace("-", "");
		
	}

}
