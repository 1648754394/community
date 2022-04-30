package com.nowcoder.community;

import com.nowcoder.community.dao.*;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private MessageMapper messageMapper;


    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("abc@bb.com");
        user.setType(1);
        user.setStatus(1);
        user.setActivationCode(null);
        user.setHeaderUrl("www.abc.com");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    
    @Test
    public void testUpdateUser() {
        int rows = userMapper.updateStatus(150, 2);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "www.abc1.com");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "654321");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testFindPosts() {
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostService.findDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setId(001);
        loginTicket.setUserId(001);
        loginTicket.setStatus(0);
        loginTicket.setTicket("abc");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 60 * 1000 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectByTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus(1, "abc");
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testInsertDiscussPost() {
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(180);
        discussPost.setTitle("xxc");
        discussPost.setContent("ceshi");
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
    }

    @Test
    public void testSelectDiscussPostById() {
        DiscussPost discussById = discussPostService.findDiscussById(281);
        System.out.println(discussById.getTitle());
        System.out.println(discussById.getContent());
    }

    @Test
    public void testSelectCommentByEntity() {
        List<Comment> comments = commentMapper.selectCommentByEntity(1, 228, 0, 4);
        for (Comment comment :comments) {
            System.out.println(comment);
        }
    }

    @Test
    public void testSelectCountByEntity() {
        int count = commentMapper.selectCountByEntity(1, 228);
        System.out.println(count);
    }

    @Test
    public void testInsertComment(){
        Comment comment = new Comment();
        comment.setUserId(1);
        comment.setContent("1");
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setEntityId(1);
        comment.setEntityType(1);
        commentMapper.insertComment(comment);
    }

    @Test
    public void testSelectLetters() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message :messages) {
            System.out.println(message);
        }

        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);
        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 20);
        for (Message message :messages1) {
            System.out.println(message);
        }
        int i1 = messageMapper.selectLetterCount("111_112");
        System.out.println(i1);
        int i2 = messageMapper.selectLetterUnreadCount(111, null);
        System.out.println(i2);
        int i3 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(i3);


    }
}
