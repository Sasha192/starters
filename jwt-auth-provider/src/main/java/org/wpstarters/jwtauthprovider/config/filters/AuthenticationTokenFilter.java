package org.wpstarters.jwtauthprovider.config.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.wpstarters.jwtauthprovider.api.state.StateMessage;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.IUserDetailsService;
import org.wpstarters.jwtauthprovider.service.impl.TokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationTokenFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";
    private final ObjectMapper objectMapper;

    private final TokenService tokenService;
    private final IUserDetailsService userDetailsService;

    public AuthenticationTokenFilter(TokenService tokenService,
                                     IUserDetailsService userDetailsService,
                                     ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && tokenService.validateJwtToken(jwt)) {
                String username = tokenService.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebRequestDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("SecurityContext for user: " + username + " is set");
            }

            filterChain.doFilter(request, response);

        } catch (ExtendedAuthenticationException e) {

            StateMessage stateMessage = new StateMessage(e.getMessage(), false, e.getExceptionState());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(objectMapper.writeValueAsString(stateMessage));

        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.isNotBlank(headerAuth) && headerAuth.startsWith(BEARER)) {
            return headerAuth.substring(BEARER.length()).strip();
        }

        return null;
    }

    public static class WebRequestDetails {

        private final String remoteAddress;
        private final int remotePort;
        private final String remoteHost;

        public WebRequestDetails(HttpServletRequest request) {
            this.remoteAddress = request.getRemoteAddr();
            this.remotePort = request.getRemotePort();
            this.remoteHost = request.getRemoteHost();
        }

        public String getRemoteAddress() {
            return remoteAddress;
        }

        public int getRemotePort() {
            return remotePort;
        }

        public String getRemoteHost() {
            return remoteHost;
        }
    }
}
