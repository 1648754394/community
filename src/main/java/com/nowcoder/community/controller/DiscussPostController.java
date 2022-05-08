package com.nowcoder.community.controller;

import com.nowcoder.community.entity.*;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
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

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没登陆哦~");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //触发事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());

        eventProducer.fireEvent(event);

        //出现错误后面统一处理
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    @RequestMapping(path = "/detail/{discussId}", method = RequestMethod.GET)
    public String showDiscussPost(@PathVariable("discussId") int discussId, Model model, Page page) {
        DiscussPost post = discussPostService.findDiscussById(discussId);
        model.addAttribute("post", post);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCount);
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus", likeStatus);

        page.setLimit(5);
        page.setRows(post.getCommentCount());
        page.setPath("/discuss/detail/" + discussId);

        //帖子的评论列表
        List<Comment> postComment = commentService.findCommentByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        //评论信息封装进Map, 再封装进List
        List<Map<String, Object>> commentList = new ArrayList<>();

        if (postComment != null) {
            for (Comment comment :postComment) {
                //创建，map封装评论信息
                Map<String, Object> commentMap = new HashMap<>();
                //评论装进map
                commentMap.put("comment", comment);
                //帖子评论者 信息存放进map
                commentMap.put("commentUser", userService.findUserById(comment.getUserId()));
                //点赞信息
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentMap.put("likeCount", likeCount);
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentMap.put("likeStatus", likeStatus);
                //帖子的回复贴
                List<Comment> commentReplay = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复贴装进map，再装进List
                List<Map<String, Object>> replayList = new ArrayList<>();
                if(commentReplay != null) {
                    for (Comment replay :commentReplay) {
                        //创建map,封装回复信息
                        Map<String, Object> replayMap = new HashMap<>();
                        //回复装进map
                        replayMap.put("replay", replay);
                        //回复者装进map
                        replayMap.put("replayUser", userService.findUserById(replay.getUserId()));
                        //点赞
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, replay.getId());
                        replayMap.put("likeCount", likeCount);
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, replay.getId());
                        replayMap.put("likeStatus", likeStatus);
                        //回复目标装进map
                        User replayTarget = replay.getTargetId() == 0? null : userService.findUserById(replay.getTargetId());
                        replayMap.put("replayTarget", replayTarget);
                        replayList.add(replayMap);
                    }
                }
                //回复贴集合也是评论信息
                commentMap.put("replayList", replayList);
                //评论信息还有回复者数量
                int countReplay = commentService.findCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentMap.put("countReplay", countReplay);
                //每个评论集合，放进评论列表
                commentList.add(commentMap);
            }
        }
        model.addAttribute("commentList", commentList);
        return "/site/discuss-detail";
    }
}
