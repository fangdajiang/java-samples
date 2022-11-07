package com.xxdai.starter.core.util.comm;

import com.xxdai.starter.core.bean.model.MailSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by fangdajiang on 16/10/11.
 */
@Slf4j
public class MailUtil {

    /**
     * The sender(host) has been defined in app-context-mail.xml.
     * @param from
     * @param to
     * @param subject
     * @param msg
     */
    public static void sendMail(String from, String to, String subject, String msg) {
        //读取 app-context-*.xml
        ApplicationContext context = new ClassPathXmlApplicationContext("app-context-*.xml");
        MailSend ms = (MailSend) context.getBean("mailSend");
        try {
            ms.sendMail(from, to, subject, msg);
        } catch (Exception e) {
            log.warn("from:" + from + ", to:" + to + ", subject:" + subject + ", msg:" + msg, e);
        }

    }

    public static void main(String[] args) {
        String from = "dajiangweb@sohu.com";
        String to = "fangdajiang@xinxindai.com";
        String subject = "JavaMailTest5";
        String msg = "Hello JavaMail";
        log.debug("from:" + from + ", to:" + to + ", subject:" + subject + ", msg:" + msg);
        log.debug("about to send mail!");
        sendMail(from, to, subject, msg);
        log.debug("mail sent.");
    }
}
