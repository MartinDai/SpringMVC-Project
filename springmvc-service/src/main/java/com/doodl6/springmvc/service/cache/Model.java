package com.doodl6.springmvc.service.cache;

/**
 * Created by daixiaoming on 2018/5/12.
 */
public class Model {

    private String key;

    private String value;

    public Model() {
    }

    public Model(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
