package com.myalley.member.options;

public enum Gender implements EnumMapperType {
    M("MAN"),
    W("WOMAN");

    public String value;


    Gender(String value){
        this.value=value;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }

}

