package com.doodl6.springmvc.service.leaf.segment.dao;

import com.doodl6.springmvc.service.leaf.segment.model.LeafAlloc;

import java.util.List;

public interface IDAllocDao {

    List<LeafAlloc> getAllLeafAllocs();

    LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);

    LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);

    List<String> getAllTags();
}