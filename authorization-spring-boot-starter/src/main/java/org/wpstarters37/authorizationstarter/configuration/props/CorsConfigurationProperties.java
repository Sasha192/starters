package org.wpstarters37.authorizationstarter.configuration.props;

import java.util.List;

public class CorsConfigurationProperties {

    private List<String> methods;
    private List<String> origins;

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public List<String> getOrigins() {
        return origins;
    }

    public void setOrigins(List<String> origins) {
        this.origins = origins;
    }

}
