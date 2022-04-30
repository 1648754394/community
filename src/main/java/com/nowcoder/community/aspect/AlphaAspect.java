package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    //切点           所有返回值类型       包名           所有类 所有方法 所有参数
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }
    //节点之前
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }

    //节点之后
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }

    //有返回值之后
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    //抛异常时
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    //前后都植入
    //抛异常时
    @Around("pointcut()")  //参数是连接点
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        //之前
        System.out.println("around before");
        //通过参数调用目标组件的方法,目标组件可能有返回值
        Object obj = joinPoint.proceed();
        //之后
        System.out.println("around after");
        return obj;
    }
}
