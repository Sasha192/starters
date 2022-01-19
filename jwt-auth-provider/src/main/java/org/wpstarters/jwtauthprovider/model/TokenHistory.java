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

    private String username;

    private String ip;

    @Column(name = "useragent")
    private String userAgent;

    private long unixTimeToUtc;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getUnixTimeToUtc() {
        return unixTimeToUtc;
    }

    public void setUnixTimeToUtc(long unixTimeToUtc) {
        this.unixTimeToUtc = unixTimeToUtc;
    }


    public static final class Builder {
        private Long id;
        private String username;
        private String ip;
        private String userAgent;
        private long unixTimeToUtc;

        public Builder() {
            this.unixTimeToUtc = Instant.now().toEpochMilli();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder unixTimeToUtc(long unixTimeToUtc) {
            this.unixTimeToUtc = unixTimeToUtc;
            return this;
        }

        public TokenHistory build() {
            TokenHistory tokenHistory = new TokenHistory();
            tokenHistory.setId(id);
            tokenHistory.setUsername(username);
            tokenHistory.setIp(ip);
            tokenHistory.setUserAgent(userAgent);
            tokenHistory.setUnixTimeToUtc(unixTimeToUtc);
            return tokenHistory;
        }
    }
}
