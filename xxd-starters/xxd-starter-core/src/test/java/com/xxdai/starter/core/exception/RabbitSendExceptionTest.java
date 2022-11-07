package com.xxdai.starter.core.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * created by xiguoding on 2018/4/10 下午4:01
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitSendExceptionTest {

    @Test(expected = RabbitSendException.class)
    public void rabbitSendException() throws RabbitSendException {
        throw new RabbitSendException("RabbitSendException");
    }
}
