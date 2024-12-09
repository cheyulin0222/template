package com.arplanet.template.casbin;

import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CasbinService {

    private final Enforcer enforcer;

    public boolean checkPermission(String username, String resource, String action) {
        return enforcer.enforce(username, resource, action);
    }

    public List<String> getRolesForUser(String username) {
        return enforcer.getRolesForUser(username);
    }

    public void addRoleForUser(String username, String role) {
        enforcer.addRoleForUser(username, role);
    }
}
