package org.wpstarters.jwtauthprovider.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

/*
*
*  {
         // These six fields are included in all Google ID Tokens.
         "iss": "https://accounts.google.com",
         "sub": "110169484474386276334",
         "azp": "1008719970978-hb24n2dstb40o45d4feuo2ukqmcc6381.apps.googleusercontent.com",
         "aud": "1008719970978-hb24n2dstb40o45d4feuo2ukqmcc6381.apps.googleusercontent.com",
         "iat": "1433978353",
         "exp": "1433981953",

         // These seven fields are only included when the user has granted the "profile" and
         // "email" OAuth scopes to the application.
         "email": "testuser@gmail.com",
         "email_verified": "true",
         "name" : "Test User",
         "picture": "https://lh4.googleusercontent.com/-kYgzyAWpZzJ/ABCDEFGHI/AAAJKLMNOP/tIXL9Ir44LE/s99-c/photo.jpg",
         "given_name": "Test",
         "family_name": "User",
         "locale": "en"
        }
*
*
* */
public class SocialAccountInfo {

    private String iss;
    private String sub;
    private String azp;
    private String aud;
    private long iat;
    private long exp;
    private String email;
    @JsonProperty(value = "email_verified")
    private boolean emailVerified;
    private String name;
    private String picture;
    @JsonProperty(value = "given_name")
    private String givenName;
    @JsonProperty(value = "family_name")
    private String family_name;
    private String locale;


    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getAzp() {
        return azp;
    }

    public void setAzp(String azp) {
        this.azp = azp;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
