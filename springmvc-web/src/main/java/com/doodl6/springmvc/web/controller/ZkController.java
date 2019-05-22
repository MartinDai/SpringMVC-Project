package com.doodl6.springmvc.web.controller;

import com.doodl6.springmvc.web.response.base.BaseResponse;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 操作zookeeper的相关操作
 */
@RestController
@RequestMapping("/zk")
public class ZkController extends BaseController {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    private static final String ZK_LOCK_PATH = "/zkLock";

    private static final int LOCK_WAIT = 1;

    /**
     * 获取独占锁，然后执行业务
     */
    @RequestMapping("/doXLockBiz")
    public BaseResponse<String> doLockBiz() {

        InterProcessMutex lock = ZkLockerHolder.getXLock();
        String result;
        try {
            if (lock.acquire(LOCK_WAIT, TimeUnit.SECONDS)) {
                for (int i = 0; i < 60; i++) {
                    Thread.sleep(1000L);
                    System.out.println("独占锁做业务中:" + i);
                }
                result = "拿锁成功，执行完成";
            } else {
                result = "拿锁失败，请稍后重试";
            }
        } catch (Exception e) {
            LOGGER.error("执行zk独占锁业务异常", e);
            result = "执行异常";
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                LOGGER.error("释放zk独占锁异常", e);
            }
        }

        return BaseResponse.success(result);
    }

    /**
     * 获取写锁，然后执行业务
     */
    @RequestMapping("/doWriteLockBiz")
    public BaseResponse<String> doWriteLockBiz() {

        InterProcessMutex writeLock = ZkLockerHolder.getWriteLock();
        String result;
        try {
            if (writeLock.acquire(LOCK_WAIT, TimeUnit.SECONDS)) {
                for (int i = 0; i < 60; i++) {
                    Thread.sleep(1000L);
                    System.out.println("写锁做业务中:" + i);
                }
                result = "拿锁成功，执行完成";
            } else {
                result = "拿锁失败，请稍后重试";
            }
        } catch (Exception e) {
            LOGGER.error("执行zk写锁业务异常", e);
            result = "执行异常";
        } finally {
            try {
                writeLock.release();
            } catch (Exception e) {
                LOGGER.error("释放zk写锁异常", e);
            }
        }

        return BaseResponse.success(result);
    }

    /**
     * 获取读锁，然后执行业务
     */
    @RequestMapping("/doReadLockBiz")
    public BaseResponse<String> doReadLockBiz() {

        InterProcessMutex readLock = ZkLockerHolder.getReadLock();
        String result;
        try {
            if (readLock.acquire(LOCK_WAIT, TimeUnit.SECONDS)) {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(1000L);
                    System.out.println("读锁做业务中:" + i);
                }
                result = "拿锁成功，执行完成";
            } else {
                result = "拿锁失败，请稍后重试";
            }
        } catch (Exception e) {
            LOGGER.error("执行zk读锁业务异常", e);
            result = "执行异常";
        } finally {
            try {
                readLock.release();
            } catch (Exception e) {
                LOGGER.error("释放zk读锁异常", e);
            }
        }

        return BaseResponse.success(result);
    }

    private static class ZkLockerHolder {

        private static InterProcessMutex xLock;

        private static InterProcessReadWriteLock readWriteLock;

        static {
            CuratorFramework client = CuratorFrameworkFactory.newClient(
                    ZK_ADDRESS,
                    new RetryNTimes(10, 5000)
            );
            client.start();

            xLock = new InterProcessMutex(client, ZK_LOCK_PATH);
            readWriteLock = new InterProcessReadWriteLock(client, ZK_LOCK_PATH);
        }

        static InterProcessMutex getXLock() {
            return xLock;
        }

        static InterProcessMutex getReadLock() {
            return readWriteLock.readLock();
        }

        static InterProcessMutex getWriteLock() {
            return readWriteLock.writeLock();
        }
    }
}
