package com.arplanets.template.repository;

import com.arplanets.template.domain.CasbinRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CasbinRuleRepository extends JpaRepository<CasbinRule, Long> {

    List<CasbinRule> findByPtype(String pType);
    List<CasbinRule> findByPtypeAndV0(String pType, String v0);
}
