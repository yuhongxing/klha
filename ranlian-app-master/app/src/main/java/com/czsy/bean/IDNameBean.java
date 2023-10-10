package com.czsy.bean;


import java.io.Serializable;

public class IDNameBean implements Serializable {
    public long id;
    public String name; // name

    @Override
    public String toString() {
        return name;
    }
}
