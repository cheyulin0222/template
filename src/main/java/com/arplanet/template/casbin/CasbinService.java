package com.arplanet.template.casbin;

import com.arplanet.template.enums.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.arplanet.template.enums.ResultMessage.*;

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

    public ResultMessage addRoleForUser(String username, String role) {
        boolean result = enforcer.addRoleForUser(username, role);
        return result ? INSERT_SUCCESSFUL : INSERT_FAILED;
    }

    public ResultMessage createRole(String role, String resource, String action) {
        boolean result = enforcer.addPolicy(role, resource, action);
        return result ? INSERT_SUCCESSFUL : INSERT_FAILED;
    }

    public boolean isRoleExists(String role) {
        return enforcer.getAllRoles().contains(role);
    }

    public ResultMessage deleteRole(String role) {
        boolean removed = enforcer.removeFilteredPolicy(0, role);
        return removed ? DELETE_SUCCESSFUL : DELETE_FAILED;
    }


}
