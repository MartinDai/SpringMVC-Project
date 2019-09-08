package com.doodl6.springmvc.service.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RocketMQService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "clearUserProducer")
    private TransactionMQProducer clearUserProducer;

    @Resource(name = "chatRecordProducer")
    private DefaultMQProducer chatRecordProducer;

    @Resource(name = "clearUserTopic")
    private String clearUserTopic;

    @Resource(name = "chatRecordTopic")
    private String chatRecordTopic;

    @Resource(name = "chatRecordTags")
    private String chatRecordTags;

    public void sendNewChatRecord(String userName, String content, long timestamp) {
        JSONObject messageJSON = new JSONObject();
        messageJSON.put("userName", userName);
        messageJSON.put("content", content);
        messageJSON.put("timestamp", timestamp);
        try {
            Message msg = new Message(chatRecordTopic, chatRecordTags, messageJSON.toJSONString().getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = chatRecordProducer.send(msg);
            logger.info("发送新聊天记录消息完成 | {}", JSON.toJSONString(sendResult));
        } catch (Exception e) {
            logger.error("发送新聊天记录消息异常", e);
        }
    }

    /**
     * 发送清除用户消息
     */
    public void sendClearUserMsg(long userId) {
        JSONObject messageJSON = new JSONObject();
        messageJSON.put("userId", userId);
        try {
            Message msg = new Message(clearUserTopic, null, messageJSON.toJSONString().getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = clearUserProducer.sendMessageInTransaction(msg, userId);
            logger.info("发送清除用户消息完成 | {}", JSON.toJSONString(sendResult));
        } catch (Exception e) {
            logger.error("发送清除用户消息异常", e);
        }
    }

}
