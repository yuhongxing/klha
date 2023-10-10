package com.czsy.bean;

public class TypeWithName {
    final public String name;
    final public int type;

    public TypeWithName(String name, int type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}
