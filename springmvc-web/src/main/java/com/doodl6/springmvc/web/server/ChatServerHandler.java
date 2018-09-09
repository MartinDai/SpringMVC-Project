package com.doodl6.springmvc.web.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map;

@Sharable
public class ChatServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static Map<Channel, String> userChannelMap = Maps.newHashMap();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        Channel currentChannel = ctx.channel();
        JSONObject messageJSON = JSON.parseObject(msg.text());
        String userName = messageJSON.getString("userName");
        String content = messageJSON.getString("content");
        Channel userChannel;
        for (Map.Entry<Channel, String> entry : userChannelMap.entrySet()) {
            userChannel = entry.getKey();
            if (userChannel == currentChannel) {
                userChannel.writeAndFlush(new TextWebSocketFrame("当前用户：" + content));
            } else {
                userChannel.writeAndFlush(new TextWebSocketFrame("用户[" + userName + "]：" + content));
            }
        }

        userChannelMap.put(currentChannel, userName);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel currentChannel = ctx.channel();
        userChannelMap.put(currentChannel, "用户[" + currentChannel.remoteAddress() + "]");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel currentChannel = ctx.channel();
        String userName = null;
        for (Map.Entry<Channel, String> entry : userChannelMap.entrySet()) {
            if (entry.getKey() == currentChannel) {
                userName = entry.getValue();
                userChannelMap.remove(currentChannel);
                break;
            }
        }

        if (userName != null) {
            for (Channel channel : userChannelMap.keySet()) {
                channel.writeAndFlush(new TextWebSocketFrame("用户[" + userName + "] 离开聊天室"));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
