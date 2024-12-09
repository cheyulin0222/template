package com.arplanet.template.casbin;

import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;

import javax.sql.DataSource;
import java.util.List;

@RequiredArgsConstructor
public class JDBCAdapter implements Adapter {

    private final DataSource dataSource;

    @Override
    public void loadPolicy(Model model) {

    }

    @Override
    public void savePolicy(Model model) {

    }

    @Override
    public void addPolicy(String s, String s1, List<String> list) {

    }

    @Override
    public void removePolicy(String s, String s1, List<String> list) {

    }

    @Override
    public void removeFilteredPolicy(String s, String s1, int i, String... strings) {

    }
}
