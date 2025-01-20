package com.arplanets.template.casbin;

import com.arplanets.template.domain.CasbinRule;
import com.arplanets.template.repository.CasbinRuleRepository;
import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class JDBCAdapter implements Adapter {

    private final CasbinRuleRepository casbinRuleRepository;

    @Override
    public void loadPolicy(Model model) {
        List<CasbinRule> casbinRules = casbinRuleRepository.findAll();

        for (CasbinRule rule : casbinRules) {
            loadPolicyLine(rule, model);
        }
    }

    @Override
    public void savePolicy(Model model) {
        // 1. 獲取資料庫現有的規則
        List<CasbinRule> existingRules = casbinRuleRepository.findAll();
        Set<String> existingRuleStrings = existingRules.stream()
                .map(this::ruleToString)  // 轉換成可比較的字串
                .collect(Collectors.toSet());

        // 2. 獲取 Model 中的規則
        List<CasbinRule> newRules = new ArrayList<>();
        // 處理 p 和 g 型別的規則
        for (String section : Arrays.asList("p", "g")) {
            model.model.getOrDefault(section, Collections.emptyMap())
                    .forEach((ptype, assertion) -> {
                        for (List<String> policy : assertion.policy) {
                            newRules.add(createRule(ptype, policy));
                        }
                    });
        }

        // 3. 找出需要刪除和新增的規則
        Set<String> newRuleStrings = newRules.stream()
                .map(this::ruleToString)
                .collect(Collectors.toSet());

        // 需要刪除的
        List<CasbinRule> rulesToDelete = existingRules.stream()
                .filter(rule -> !newRuleStrings.contains(ruleToString(rule)))
                .collect(Collectors.toList());

        // 需要新增的
        List<CasbinRule> rulesToAdd = newRules.stream()
                .filter(rule -> !existingRuleStrings.contains(ruleToString(rule)))
                .collect(Collectors.toList());

        // 4. 執行差異更新
        casbinRuleRepository.deleteAll(rulesToDelete);
        casbinRuleRepository.saveAll(rulesToAdd);
    }

    private String ruleToString(CasbinRule rule) {
        return String.format("%s:%s:%s:%s",
                rule.getPtype(), rule.getV0(), rule.getV1(), rule.getV2());
    }

    private CasbinRule createRule(String ptype, List<String> policy) {
        CasbinRule rule = new CasbinRule();
        rule.setPtype(ptype);
        if (!policy.isEmpty()) rule.setV0(policy.get(0));
        if (policy.size() > 1) rule.setV1(policy.get(1));
        if (policy.size() > 2) rule.setV2(policy.get(2));
        return rule;
    }

    @Override
    public void addPolicy(String ptype, String key, List<String> policy) {
        CasbinRule rule = new CasbinRule();
        rule.setPtype(ptype);
        for (int i = 0; i < policy.size(); i++) {
            switch (i) {
                case 0: rule.setV0(policy.get(i)); break;
                case 1: rule.setV1(policy.get(i)); break;
                case 2: rule.setV2(policy.get(i)); break;
                case 3: rule.setV3(policy.get(i)); break;
                case 4: rule.setV4(policy.get(i)); break;
                case 5: rule.setV5(policy.get(i)); break;
            }
        }
        casbinRuleRepository.save(rule);
    }

    @Override
    public void removePolicy(String ptype, String key, List<String> values) {
        if (values.isEmpty()) return;

        // 根據第一個值查找並刪除匹配的規則
        List<CasbinRule> rules = casbinRuleRepository.findByPtypeAndV0(ptype, values.get(0));
        for (CasbinRule rule : rules) {
            if (matchesPolicy(rule, values)) {
                casbinRuleRepository.delete(rule);
            }
        }
    }

    @Override
    public void removeFilteredPolicy(String ptype, String key, int fieldIndex, String... fieldValues) {
        // 可以根據需要實現過濾條件
        List<CasbinRule> rules = casbinRuleRepository.findByPtype(ptype);
        for (CasbinRule rule : rules) {
            if (matchesFilter(rule, fieldIndex, fieldValues)) {
                casbinRuleRepository.delete(rule);
            }
        }
    }

    private boolean matchesPolicy(CasbinRule rule, List<String> values) {
        if (values.size() >= 1 && !values.get(0).equals(rule.getV0())) return false;
        if (values.size() >= 2 && !values.get(1).equals(rule.getV1())) return false;
        if (values.size() >= 3 && !values.get(2).equals(rule.getV2())) return false;
        if (values.size() >= 4 && !values.get(3).equals(rule.getV3())) return false;
        if (values.size() >= 5 && !values.get(4).equals(rule.getV4())) return false;
        if (values.size() >= 6 && !values.get(5).equals(rule.getV5())) return false;
        return true;
    }

    private boolean matchesFilter(CasbinRule rule, int fieldIndex, String[] fieldValues) {
        String[] values = new String[]{
                rule.getV0(), rule.getV1(), rule.getV2(),
                rule.getV3(), rule.getV4(), rule.getV5()
        };

        for (int i = 0; i < fieldValues.length; i++) {
            int targetIndex = fieldIndex + i;
            if (targetIndex >= values.length) {
                return false;
            }

            String fieldValue = fieldValues[i];
            if (fieldValue != null && !fieldValue.equals(values[targetIndex])) {
                return false;
            }
        }
        return true;
    }

    private void loadPolicyLine(CasbinRule line, Model model) {
        List<String> values = new ArrayList<>();

        if (line.getV0() != null) values.add(line.getV0());
        if (line.getV1() != null) values.add(line.getV1());
        if (line.getV2() != null) values.add(line.getV2());
        if (line.getV3() != null) values.add(line.getV3());
        if (line.getV4() != null) values.add(line.getV4());
        if (line.getV5() != null) values.add(line.getV5());

        if (!values.isEmpty()) {
            // p 型別的放在 p section，g 型別的放在 g section
            String section = "p".equals(line.getPtype()) ? "p" : "g";
            model.addPolicy(section, line.getPtype(), values);
        }
    }
}
