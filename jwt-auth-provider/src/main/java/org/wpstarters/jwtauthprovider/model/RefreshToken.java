package org.wpstarters.jwtauthprovider.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private UUID id;

    @Column(nullable = false)
    @Length(max = 512, min = 1, message = "Please, specify username no more than 512 characters and no less than 1 character")
    private String username;

    @Column(name = "ip_address", nullable = false)
    @Length(max = 64, min = 16, message = "Please, specify ipAddress no more than 64 characters and no less than 16 characters")
    private String ipAddress;

    @Column(name = "user_agent", nullable = false)
    @Length(max = 512, message = "Please, specify userAgent no more than 512 characters")
    private String userAgent;

    @Column(nullable = false)
    private long unixCreated;

    @Column(nullable = false)
    private long unixExpired;

    @Column(nullable = false)
    private String tokenId;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RefreshTokenStatus status;

    public RefreshToken() {
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public long getUnixCreated() {
        return unixCreated;
    }

    public long getUnixExpired() {
        return unixExpired;
    }

    public RefreshTokenStatus getStatus() {
        return status;
    }

    public String getTokenId() {
        return tokenId;
    }

    public boolean isValid() {

        return status.equals(RefreshTokenStatus.VALID) && System.currentTimeMillis() <= getUnixExpired();

    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public static final class Builder {
        private UUID id;
        private String username;
        private String ipAddress = "";
        private String userAgent = "";
        private long unixCreated;
        private long unixExpired;
        private String tokenId;
        private RefreshTokenStatus status = RefreshTokenStatus.VALID;

        public Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder tokenId(String tokenId) {
            this.tokenId = tokenId;
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

        public Builder unixExpired(long unixExpired) {
            this.unixExpired = unixExpired;
            return this;
        }

        public Builder status(RefreshTokenStatus status) {
            this.status = status;
            return this;
        }

        public RefreshToken build() {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.username = this.username;
            refreshToken.ipAddress = this.ipAddress;
            refreshToken.userAgent = this.userAgent;
            refreshToken.unixExpired = this.unixExpired;
            refreshToken.id = this.id;
            refreshToken.unixCreated = this.unixCreated;
            refreshToken.status = this.status;
            refreshToken.tokenId = this.tokenId;
            return refreshToken;
        }
    }
}
