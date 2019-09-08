package com.doodl6.springmvc.service.mq.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doodl6.springmvc.dao.api.UserLoginLogMapper;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 清除用户消息监听
 */
@Component
public class ClearUserMessageListener implements MessageListenerConcurrently {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UserLoginLogMapper userLoginLogMapper;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageList, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : messageList) {
            String message = new String(messageExt.getBody());
            logger.info("收到清除用户消息 | {}", message);
            JSONObject messageJSON = JSON.parseObject(message);
            Long userId = messageJSON.getLong("userId");
            if (userId != null) {
                userLoginLogMapper.deleteAllByUserId(userId);
                logger.info("删除用户登录记录完成 | {}", userId);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

}
