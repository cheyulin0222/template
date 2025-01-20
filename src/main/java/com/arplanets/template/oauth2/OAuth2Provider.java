package com.arplanets.template.oauth2;

import com.arplanets.template.dto.req.OAuth2LoginRequest;
import com.arplanets.template.dto.res.JwtResponse;

public interface OAuth2Provider {

    JwtResponse login(OAuth2LoginRequest request);
}
