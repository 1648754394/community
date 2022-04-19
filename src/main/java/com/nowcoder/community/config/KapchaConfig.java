package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

//配置类非普通类
@Configuration
public class KapchaConfig {
    //加入Spring Boot管理 Producer是个接口有两个方法
    @Bean
    public Producer kaptchaProducer() {
        //kaptcha配置文件，key难写，直接在这里配置
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        //取数范围
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789QWERTYUIOPASDFGHJKLZXCVBNM");
        //长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        //噪声，不设置（自带）
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        //实例化其实现类
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        //向kaptcha中传入参数，封装参数到config对象中，需要传入properties
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        //返回实现类
        return kaptcha;
    }
}
