package com.doodl6.springmvc.service.mq;

import com.doodl6.springmvc.service.mq.listener.ChatRecordMessageListener;
import com.doodl6.springmvc.service.mq.listener.ClearUserMessageListener;
import com.doodl6.springmvc.service.mq.listener.ClearUserTransactionListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:mq.properties")
public class RocketMQConfig implements InitializingBean, DisposableBean, ApplicationContextAware {

    @Value("${nameServerAddress}")
    private String nameServerAddress;

    @Value("${clearUser.group}")
    private String clearUserGroup;

    @Value("${clearUser.topic}")
    private String clearUserTopic;

    @Value("${chatRecord.group}")
    private String chatRecordGroup;

    @Value("${chatRecord.topic}")
    private String chatRecordTopic;

    @Value("${chatRecord.tags}")
    private String chatRecordTags;

    private ApplicationContext applicationContext;

    private TransactionMQProducer clearUserProducer;

    private DefaultMQProducer chatRecordProducer;

    private DefaultMQPushConsumer chatRecordConsumer;

    private DefaultMQPushConsumer clearUserConsumer;

    @Bean(name = "clearUserProducer")
    public TransactionMQProducer getClearUserProducer() {
        return clearUserProducer;
    }

    @Bean(name = "chatRecordProducer")
    public DefaultMQProducer getChatRecordProducer() {
        return chatRecordProducer;
    }

    @Bean(name = "clearUserTopic")
    public String getClearUserTopic() {
        return clearUserTopic;
    }

    @Bean(name = "chatRecordTopic")
    public String getChatRecordTopic() {
        return chatRecordTopic;
    }

    @Bean(name = "chatRecordTags")
    public String getChatRecordTags() {
        return chatRecordTags;
    }

    @Override
    public void destroy() {
        if (clearUserProducer != null) {
            clearUserProducer.shutdown();
        }

        if (clearUserConsumer != null) {
            clearUserConsumer.shutdown();
        }

        if (chatRecordProducer != null) {
            chatRecordProducer.shutdown();
        }

        if (chatRecordConsumer != null) {
            chatRecordConsumer.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() throws MQClientException {
        clearUserProducer = new TransactionMQProducer(clearUserGroup);
        clearUserProducer.setNamesrvAddr(nameServerAddress);
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000), r -> {
            Thread thread = new Thread(r);
            thread.setName("client-transaction-msg-check-thread");
            return thread;
        });
        clearUserProducer.setExecutorService(executorService);
        clearUserProducer.setTransactionListener(applicationContext.getAutowireCapableBeanFactory().getBean(ClearUserTransactionListener.class));
        clearUserProducer.start();

        clearUserConsumer = new DefaultMQPushConsumer(clearUserGroup);
        clearUserConsumer.setNamesrvAddr(nameServerAddress);
        clearUserConsumer.subscribe(clearUserTopic, "*");
        clearUserConsumer.registerMessageListener(applicationContext.getAutowireCapableBeanFactory().getBean(ClearUserMessageListener.class));
        clearUserConsumer.start();

        chatRecordProducer = new DefaultMQProducer(chatRecordGroup);
        chatRecordProducer.setNamesrvAddr(nameServerAddress);
        chatRecordProducer.start();

        chatRecordConsumer = new DefaultMQPushConsumer(chatRecordGroup);
        chatRecordConsumer.setNamesrvAddr(nameServerAddress);
        chatRecordConsumer.subscribe(chatRecordTopic, chatRecordTags);
        chatRecordConsumer.registerMessageListener(applicationContext.getAutowireCapableBeanFactory().getBean(ChatRecordMessageListener.class));
        chatRecordConsumer.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
