package com.doodl6.springmvc.common.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * http访问工具类
 */
public final class HttpUtil {

    private HttpUtil() {
    }

    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

    public static String doGet(String url) {
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = null;

        try {
            response = HTTP_CLIENT.execute(httpget);
            HttpEntity httpEntity = response.getEntity();
            String bodyAsString = EntityUtils.toString(httpEntity);
            EntityUtils.consume(httpEntity);

            return bodyAsString;
        } catch (IOException ignored) {
        } finally {
            IOUtils.closeQuietly(response);
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(doGet("http://www.baidu.com"));
    }

}