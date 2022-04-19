package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    //模板引擎TemplateEngine被Spring Boot管理
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        //发送
        mailClient.sendMail("sl_7139@163.com", "TEST", "Welcome.");
    }

    @Test
    public void testHtmlMail() {
        //利用org.thymeleaf.context向模板传参
        Context context = new Context();
        //变量存入该对象
        context.setVariable("username","sunday");

        //调用模板引擎生成动态网页
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        //发送
        mailClient.sendMail("sl_7139@163.com", "HTML", content);
    }

}
