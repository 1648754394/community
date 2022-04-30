package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 替代session持有用户数据，线程隔离
 */

@Component
public class HostHolder {

    ThreadLocal<User> users = new ThreadLocal<>();
    //ThreadLocal以当前线程为key，进行存储，可看源码中set方法

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
