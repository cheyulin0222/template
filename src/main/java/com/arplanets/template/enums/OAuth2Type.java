package com.arplanets.template.enums;

import lombok.Getter;

@Getter
public enum OAuth2Type {

    GOOGLE("googleOAuth2Provider"),
    FACEBOOK("facebookOAuth2Provider"),
    LINE("lineOAuth2Provider");

    private final String providerBeanName;

    private OAuth2Type (String providerBeanName) {
        this.providerBeanName = providerBeanName;
    }

}
