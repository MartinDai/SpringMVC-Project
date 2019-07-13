package com.doodl6.springmvc.service.leaf;


import com.doodl6.springmvc.service.leaf.common.Result;

public interface IDGen {

    Result get(String key);

    boolean init();
}