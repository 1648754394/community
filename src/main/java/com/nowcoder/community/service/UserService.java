package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String communityPath;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //返回信息
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        //用户名空
        if(StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        //密码空
        if(StringUtils.isBlank(user.getPassword()) || user.getPassword().length() < 6) {
            map.put("passwordMsg", "密码长度不能小于6!");
            return map;
        }
        //邮箱空
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        //账号验证
        User u = userMapper.selectByName(user.getUsername());
        if(u != null) {
            map.put("usernameMsg", "该用户已存在");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if(u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }
        //信息设置
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setStatus(0);
        user.setType(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //发送
        userMapper.insertUser(user);
        //激活邮件
        //信息
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + communityPath + "/activation" + "/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        //模板整合
        String content = templateEngine.process("/mail/activation", context);

        //发送邮件
        mailClient.sendMail(user.getEmail(), "激活邮件", content);

        return map;
    }

    public User findUserById(int id) {
        //return userMapper.selectById(id);
        //先从redis中查
        User user = getCacheUser(id);
        if(user == null) {
            //查不到就初始化缓存
            user = initCacheUser(id);
        }
        return user;
    }

    //返回激活状态
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            //清除缓存
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    //登陆验证
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        HashMap<String, Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }

        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        User user = userMapper.selectByName(username);
        //验证账号
        if(user == null) {
            map.put("usernameMsg", "该用户不存在！");
            return map;
        }

        //是否激活
        if(user.getStatus() == 0) {
            map.put("usernameMsg", "该用户未激活！");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码输入有误！");
            return map;
        }

        //验证通过，生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        //loginTicketMapper.insertLoginTicket(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    //退出登录
    public void logout(String ticket) {

        //loginTicketMapper.updateStatus(1, ticket);

        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

    }

    //通过ticket获取LoginTicket
    public LoginTicket findLoginTicketByTicket(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);

        //return loginTicketMapper.selectByTicket(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    //更新头像
    public int updateHeader(int userId, String headerUrl) {
        //return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        //清除缓存
        clearCache(userId);
        return rows;
    }

    //更改密码
    public Map<String, Object> updatePassword(User user, String oldPassword, String newPassword, String verifyPassword) {
        HashMap<String, Object> map = new HashMap<>();

        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());


        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "原先密码输入有误！");
            return map;
        }

        if(StringUtils.isBlank(newPassword) || newPassword.length() < 6) {
            map.put("newPasswordMsg", "新密码长度不等小于6！");
            return map;
        }

        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        verifyPassword = CommunityUtil.md5(verifyPassword + user.getSalt());

        if(Objects.equals(newPassword, oldPassword)) {
            map.put("newPasswordMsg", "新密码不能和旧密码一致！");
            return map;
        }


        if(!Objects.equals(verifyPassword, newPassword)) {
            map.put("verifyPasswordMsg", "两次密码输入不一致");
            return map;
        }


        userMapper.updatePassword(user.getId(), newPassword);
        return map;
    }

    public User findUserByName(String name) {
        return userMapper.selectByName(name);
    }

    //查用户信息时，优先从redis缓存中获取
    private User getCacheUser(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    //取不到时初始化缓存数据
    private User initCacheUser(int userId) {
        //先从MySQL中取出
        User user = userMapper.selectById(userId);
        //再存进Redis中
        String userKey = RedisKeyUtil.getUserKey(userId);
        //只存1小时
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    //用户数据更新时，从Redis中将数据删除
    private void clearCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
