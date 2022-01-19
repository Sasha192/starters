package org.wpstarters.jwtauthprovider.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "token_history")
public class TokenHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    @Column(name = "unix_created")
    private long unixCreated;


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public long getUnixCreated() {
        return unixCreated;
    }


    public static final class Builder {
        private Long id;
        private String username;
        private String ipAddress;
        private String userAgent;
        private long unixCreated = Instant.now().getEpochSecond();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder unixCreated(long unixCreated) {
            this.unixCreated = unixCreated;
            return this;
        }

        public TokenHistory build() {
            TokenHistory tokenHistory = new TokenHistory();
            tokenHistory.id = this.id;
            tokenHistory.unixCreated = this.unixCreated;
            tokenHistory.userAgent = this.userAgent;
            tokenHistory.username = this.username;
            tokenHistory.ipAddress = this.ipAddress;
            return tokenHistory;
        }
    }
}
