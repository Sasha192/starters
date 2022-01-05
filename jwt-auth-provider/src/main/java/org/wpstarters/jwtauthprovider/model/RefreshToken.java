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

    @Column(nullable = false)
    private long unixCreated;

    @Column(nullable = false)
    private long unixExpired;

    @Column(nullable = false)
    private String tokenId;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RefreshTokenStatus status;

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

    public static final class Builder {
        private UUID id;
        private String username;
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
            refreshToken.unixExpired = this.unixExpired;
            refreshToken.id = this.id;
            refreshToken.unixCreated = this.unixCreated;
            refreshToken.status = this.status;
            refreshToken.tokenId = this.tokenId;
            return refreshToken;
        }
    }
}
