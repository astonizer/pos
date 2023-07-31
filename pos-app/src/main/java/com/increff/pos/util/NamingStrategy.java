package com.increff.pos.util;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class NamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private static final long serialVersionUID = 1L;
    private String tablePrefix;

    public NamingStrategy() {
        this.tablePrefix = "";
    }

    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return new Identifier(this.addUnderscoresWithPrefix(name.getText()), name.isQuoted());
    }

    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return new Identifier(this.addUnderscores(name.getText()), name.isQuoted());
    }

    protected String addUnderscoresWithPrefix(String name) {
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";
        String newName = name.replaceAll(regex, replacement).toLowerCase();
        return this.tablePrefix + newName;
    }

    protected String addUnderscores(String name) {
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";
        return name.replaceAll(regex, replacement).toLowerCase();
    }

}
