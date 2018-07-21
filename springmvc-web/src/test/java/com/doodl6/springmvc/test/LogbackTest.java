package com.doodl6.springmvc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackTest {

    public static void main(String[] args) {

//        Logger logger = LoggerFactory.getLogger("com.doodl6.springmvc.web.controller");
        Logger logger = LoggerFactory.getLogger(LogbackTest.class);
        logger.trace("Hello world trace.");
        logger.debug("Hello world debug.");
        logger.info("Hello world info.");
        logger.warn("Hello world warn.");
        logger.error("Hello world error.");

    }
}
