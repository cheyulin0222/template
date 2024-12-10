package com.arplanet.template.repository;

import com.arplanet.template.domain.CasbinRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CasbinRuleRepository extends JpaRepository<CasbinRule, Long> {

    List<CasbinRule> findByPtype(String ptype);
    List<CasbinRule> findByPtypeAndV0(String ptype, String v0);
}
