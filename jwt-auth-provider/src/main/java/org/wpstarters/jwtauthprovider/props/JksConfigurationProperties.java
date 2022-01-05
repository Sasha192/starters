package org.wpstarters.jwtauthprovider.props;


public class JksConfigurationProperties {

    private String jksFilePath;
    private String keyPass;
    private String keyStore;
    private String storePass;
    private String alias;
    private String keyId;

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getJksFilePath() {
        return jksFilePath;
    }

    public void setJksFilePath(String jksFilePath) {
        this.jksFilePath = jksFilePath;
    }

    public String getKeyPass() {
        return keyPass;
    }

    public void setKeyPass(String keyPass) {
        this.keyPass = keyPass;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getStorePass() {
        return storePass;
    }

    public void setStorePass(String storePass) {
        this.storePass = storePass;
    }
}
