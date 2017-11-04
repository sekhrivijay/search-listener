package com.micro.services.search.listener;

import com.micro.services.search.listener.index.IndexListenerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IndexListenerApplication.class)
public class IndexListenerApplicationTests {
    @Test
    public void contextLoads() {
    }

}
