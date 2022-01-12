package org.wpstarters.jwtauthprovider.props;


public class JksConfigurationProperties {

    private String privateKeyPath;
    private String publicKeyPath;
    private String keyId;
    private String password;

    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getPassword() {
        return this.password;
    }
}
