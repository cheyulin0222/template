package com.arplanet.template.oauth2;

import com.arplanet.template.req.OAuth2LoginRequest;
import com.arplanet.template.res.JwtResponse;

public interface OAuth2Provider {

    JwtResponse login(OAuth2LoginRequest request);
}
