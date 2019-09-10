package com.doodl6.springmvc.service.mq.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doodl6.springmvc.dao.api.UserMapper;
import com.doodl6.springmvc.dao.entity.User;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 清除用户事务监听
 */
@Component
public class ClearUserTransactionListener implements TransactionListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UserMapper userMapper;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String message = new String(msg.getBody());
        logger.info("收到清除用户执行本地事务消息 | {} | {}", message, arg);
        Long userId = (Long) arg;
        userMapper.deleteById(userId);
        logger.info("删除用户信息完成 | {}", userId);

        //如果返回LocalTransactionState.UNKNOWN，则会定时轮训下面的检查本地事务状态方法
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String message = new String(msg.getBody());
        logger.info("收到检查清除用户本地事务状态消息 | {}", message);
        JSONObject messageJSON = JSON.parseObject(message);
        Long userId = messageJSON.getLong("userId");

        User user = userMapper.getById(userId);
        return user == null ? LocalTransactionState.COMMIT_MESSAGE : LocalTransactionState.ROLLBACK_MESSAGE;
    }
}
