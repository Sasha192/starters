package org.wpstarters.jwtauthprovider.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "persistence_nonce")
public class PersistenceNonce implements Nonce {

    @Id
    @Column(name = "nonce")
    private UUID nonce;
    @Column(name = "encrypted_request")
    private String encryptedRequest;
    private long expired;
    private boolean used;

    public UUID getNonce() {
        return nonce;
    }

    public void setNonce(UUID nonce) {
        this.nonce = nonce;
    }

    public String getEncryptedRequest() {
        return encryptedRequest;
    }

    public void setEncryptedRequest(String encryptedRequest) {
        this.encryptedRequest = encryptedRequest;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public static final class Builder {
        private UUID nonce;
        private String encryptedRequest;
        private long expired;

        public Builder() {
        }

        public Builder nonce(UUID nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder encryptedRequest(String encryptedRequest) {
            this.encryptedRequest = encryptedRequest;
            return this;
        }

        public Builder expired(long expired) {
            this.expired = expired;
            return this;
        }

        public PersistenceNonce build() {
            PersistenceNonce persistenceNonce = new PersistenceNonce();
            persistenceNonce.setNonce(nonce);
            persistenceNonce.setEncryptedRequest(encryptedRequest);
            persistenceNonce.setExpired(expired);
            return persistenceNonce;
        }
    }
}
