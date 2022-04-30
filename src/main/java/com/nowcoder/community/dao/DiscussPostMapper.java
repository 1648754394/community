package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit); //userId传入某人的帖子，首页为0，<if>动态调用
                                                                             //offset：当前页帖子号 limit：每页最多贴子数
    //@Param注解用于给参数别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId); //查询帖子总数，userId同样<if>里使用

    int insertDiscussPost(DiscussPost discussPost);

    //根据id查询帖子
    DiscussPost selectDisPostById(int id);

    //更新帖子评论数量
    int updateCommentCount(int commentCount, int id);
}
