package com.xxdai.starter.core.util;

import com.xxdai.pub.common.model.BaseRequestHeader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * created by xiguoding on 2018/3/15 下午2:40
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CheckerUtilTest {
    private BaseRequestHeader getHeader() {
        BaseRequestHeader header = new BaseRequestHeader();
        header.setClientTime(String.valueOf(System.currentTimeMillis()));
        return header;
    }

    @Test
    public void checkBaseRequestHeaderTest() {
        CheckerUtil checkerUtil = new CheckerUtil(true);
        checkerUtil.checkBaseRequestHeader(getHeader());
    }
}
