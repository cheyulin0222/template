package com.arplanets.template.oauth2;

import com.arplanets.template.req.OAuth2LoginRequest;
import com.arplanets.template.res.JwtResponse;

public interface OAuth2Provider {

    JwtResponse login(OAuth2LoginRequest request);
}
