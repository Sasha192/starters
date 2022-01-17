package org.wpstarters.jwtauthprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.wpstarters.jwtauthprovider.config.BeansConfiguration;
import org.wpstarters.jwtauthprovider.config.SecurityConfiguration;

@SpringBootTest(classes = {
        JwtAuthProviderApplication.class,
        WebConfiguration.class,
        SecurityConfiguration.class,
        BeansConfiguration.class
}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:/application-test.yaml")
public abstract class BaseIT {


    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @BeforeEach
    public void init() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();

        beforeEach();

    }

    protected abstract void beforeEach();

}
