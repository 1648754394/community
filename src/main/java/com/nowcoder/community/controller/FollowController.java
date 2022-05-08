package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    private EventProducer eventProducer;

    //关注
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        //触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setUserId(user.getId())
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "关注成功！");
    }

    //取消关注
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unFollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "取消成功！");
    }

    //关注列表
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFolloweeList(@PathVariable("userId") int userId, Model model, Page page) {

        User user = userService.findUserById(userId);
        if(user == null) {
            throw new RuntimeException("该用户不存在");
        }

        model.addAttribute("user", user);
        page.setLimit(5);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        page.setPath("/followees" + userId);

        List<Map<String, Object>> followeeCounts = followService.findFolloweeCounts(
                userId, page.getOffset(), page.getLimit());

        for (Map<String, Object> map :followeeCounts) {
            User u = (User)map.get("user");
            map.put("hasFollowed", hasFollowed(u.getId()));
        }
        model.addAttribute("users", followeeCounts);

        return "/site/followee";
    }

    //粉丝列表
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowerList(@PathVariable("userId") int userId, Model model, Page page) {

        //将显示用户传入
        User user = userService.findUserById(userId);
        if(user == null) {
            throw new RuntimeException("该用户不存在");
        }

        model.addAttribute("user", user);

        //设置分页
        page.setLimit(5);
        page.setRows((int) followService.findFollowerCount(userId, ENTITY_TYPE_USER));
        page.setPath("/followers" + userId);

        //查询显示用户的粉丝列表
        List<Map<String, Object>> followerCounts = followService.findFollowerCounts(
                userId, page.getOffset(), page.getLimit());

        //循环粉丝列表，判断登陆者是否与列表中的人互相关注
        for (Map<String, Object> map :followerCounts) {
            User u = (User)map.get("user");
            map.put("hasFollowed", hasFollowed(u.getId()));
        }
        model.addAttribute("users", followerCounts);

        return "/site/follower";
    }

    private boolean hasFollowed(int userId) {
        if(hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
