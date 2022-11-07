package com.xxdai.starter.core.util.comm;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * created by xiguoding on 2018/3/15 上午10:50
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MailUtilTest {

    @Test @Ignore
    public void sendMailTest() {
        String from = "dajiangweb@sohu.com";
        String to = "fangdajiang@xinxindai.com";
        String subject = "JavaMailTest5";
        String msg = "Hello JavaMail";
        log.debug("from:" + from + ", to:" + to + ", subject:" + subject + ", msg:" + msg);
        log.debug("about to send mail!");
        MailUtil.sendMail(from, to, subject, msg);
        log.debug("mail sent.");
    }
}
