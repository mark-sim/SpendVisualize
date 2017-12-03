package com.mark.app;

public class CompanyType {
    
    private String prefixName;
    private String type;

    public CompanyType(String prefixName, String type) {
        this.prefixName = prefixName;
        this.type = type;
    }

    public String getPrefixName() {
        return this.prefixName;
    }

    public String getType() {
        return this.type;
    }
}
