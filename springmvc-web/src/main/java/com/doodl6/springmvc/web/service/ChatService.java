package com.doodl6.springmvc.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by daixiaoming on 2019/1/3.
 */
@Service
public class ChatService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 保存聊天记录
     */
    public void saveChatRecord(String userName, String content) {
        logger.info("用户聊天内容 | {} | {} ", userName, content);

        //真实项目可以在这里把聊天内容入库
    }
}
