package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//全局配置类，括号中缩小范围，只管理有Controller注解的Bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    //记日志实例化日志组件
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    // 大括号中Exception所有异常的父类，即处理所有异常
    @ExceptionHandler({Exception.class})
    //三个参数传过来的异常e、request处理请求、response处理响应
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //只是记录概括
        logger.error("服务器发生异常：" + e.getMessage());
        //记录详细错误信息，每个element记录一个错误信息
        for (StackTraceElement element :e.getStackTrace()) {
            logger.error(element.toString());
        }

        //普通请求返回页面，异步请求返回JSON
        //如何判断普通请求还是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        //只有异步请求才期望返回XML
        if("XMLHttpRequest".equals(xRequestedWith)) {
            //这是一个异步请求向浏览器返回普通字符串手动转为JSON，设置utf-8格式
            response.setContentType("application/plain;charset=utf-8");
            //获取输出流，向外输出字符串
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常"));
        } else {
            //普通请求，重定向到错误页面 项目访问路径+错误页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
