package com.doodl6.springmvc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackTest {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger("com.doodl6.springmvc.test.LogbackTest");
        logger.debug("Hello world.");
        logger.trace("Hello world trace.");

        Logger traceLog = LoggerFactory.getLogger("trace");
        traceLog.trace("trace test");

    }
}
