package com.doodl6.springmvc.web.controller;

import com.doodl6.springmvc.web.response.base.BaseResponse;
import com.doodl6.springmvc.web.vo.MessageVo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 聊天控制类，基于异步请求实现
 * Created by daixiaoming on 2018-12-10.
 */
@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController {

    /**
     * 用户ID生成器
     */
    private static final AtomicInteger USER_ID_GENERATOR = new AtomicInteger();

    private static final Map<Integer, String> USER_MAP = Maps.newHashMap();

    /**
     * 为每个用户维护一份聊天记录队列
     */
    private static final Map<Integer, LinkedBlockingDeque<MessageVo>> MESSAGE_QUEUE_MAP = Maps.newHashMap();

    /**
     * 拉取数据，如果没有数据，会hold一段时间
     */
    @RequestMapping("/pullData")
    public DeferredResult<BaseResponse<List<MessageVo>>> pullData(Integer userId) {

        Preconditions.checkArgument(userId != null, "用户ID不能为空");

        Preconditions.checkArgument(USER_MAP.containsKey(userId), "用户不在聊天室内");

        //超时5秒钟
        DeferredResult<BaseResponse<List<MessageVo>>> deferredResult = new DeferredResult<>(5000L, new BaseResponse<>());

        PullDataThread pullDataThread = new PullDataThread(userId, deferredResult, 4000);
        ForkJoinPool.commonPool().submit(pullDataThread);

        return deferredResult;
    }

    /**
     * 进入聊天室
     */
    @RequestMapping("/intoChatRoom")
    public BaseResponse<Integer> intoChatRoom(String userName) {

        Preconditions.checkArgument(StringUtils.isNotEmpty(userName), "用户名不能为空");

        int userId = USER_ID_GENERATOR.incrementAndGet();

        MESSAGE_QUEUE_MAP.put(userId, Queues.newLinkedBlockingDeque());

        USER_MAP.put(userId, userName + "[" + userId + "]");

        BaseResponse<Integer> response = new BaseResponse<>();
        response.setData(userId);
        return response;
    }

    /**
     * 发送聊天信息
     */
    @RequestMapping("/sendMessage")
    public BaseResponse<Void> sendMessage(Integer userId, String content) {
        Preconditions.checkArgument(userId != null, "用户ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(content), "消息内容不能为空");
        Preconditions.checkArgument(USER_MAP.containsKey(userId), "用户不存在");

        String userName = USER_MAP.get(userId);

        MessageVo messageVo = new MessageVo();
        messageVo.setUserId(userId);
        messageVo.setUserName(userName);
        messageVo.setSendTime(new Date());
        messageVo.setContent(content);

        for (Integer id : USER_MAP.keySet()) {
            MESSAGE_QUEUE_MAP.get(id).add(messageVo);
        }

        return BaseResponse.success();
    }

    static class PullDataThread implements Runnable {

        /**
         * 用户ID
         */
        private final int userId;

        private final DeferredResult<BaseResponse<List<MessageVo>>> deferredResult;

        /**
         * 超时时间(单位：毫秒)
         */
        private final int timeout;

        PullDataThread(int userId, DeferredResult<BaseResponse<List<MessageVo>>> deferredResult, int timeout) {
            this.userId = userId;
            this.deferredResult = deferredResult;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            LinkedBlockingDeque<MessageVo> messageQueue = MESSAGE_QUEUE_MAP.get(userId);
            BaseResponse<List<MessageVo>> response = new BaseResponse<>();
            List<MessageVo> list = Lists.newArrayList();

            MessageVo vo;
            try {
                if ((vo = messageQueue.poll(timeout, TimeUnit.MILLISECONDS)) != null) {
                    list.add(vo);
                    //一次最多取10条信息
                    for (int i = 0; i < 9; i++) {
                        vo = messageQueue.poll();
                        if (vo == null) {
                            break;
                        }
                        list.add(vo);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            response.setData(list);
            deferredResult.setResult(response);
        }
    }

}

