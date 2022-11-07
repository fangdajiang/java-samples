package com.xxdai.starter.core.exception;

import lombok.ToString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * created by xiguoding on 2018/4/10 下午4:05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class XxdRequestExceptionTest {

    @Test(expected = XxdRequestException.class)
    public void xxdRequestException() throws XxdRequestException {
        throw new XxdRequestException("XxdRequestException");
    }
}
