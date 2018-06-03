package com.doodl6.springmvc.dao.api;

import com.doodl6.springmvc.dao.entity.UserLoginLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {

    List<UserLoginLog> queryLastLoginLog(long userId);
}
