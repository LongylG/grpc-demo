package com.github.longylg;

import com.github.longylg.client.HelloClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

@RunWith(SpringRunner.class)
public class SpringbootServiceTest {

    HelloClient client;

    private static String host = "localhost";

    private static int port = 6565;

    @Before
    public void init() {
        client = new HelloClient(host, port);
    }

    @Test
    public void withOutStreamGreet() {
        client.greet("liming");
    }

    @Test
    public void twoWayStream() {
        try (
            InputStream in = new FileInputStream("/home/liyulong/Desktop/test.xml");
            ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ) {
            int len = 0;
            while ((len = in.read()) != -1) {
                bos.write(len);
            }
            client.upload(Arrays.asList(bos.toByteArray(), bos.toByteArray(), bos.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 单向流，响应多个结果
     */
    @Test
    public void responseStreamGreet() {
        //message is useless,only for trigger server-side logic
        client.respStreamGreet("a", "1");
    }

    @Test
    public void requestStream() {
        client.reqStreamGreet(Arrays.asList("a", "b", "c"));
    }

    @After
    public void destory() throws InterruptedException {
        client.shutdown();
    }
}
