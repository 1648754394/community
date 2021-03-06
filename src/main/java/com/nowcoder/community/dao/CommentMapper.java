package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);
    int selectCountByEntity(int entityType, int entityId);

    //增加评论
    int insertComment(Comment comment);

    //根据id查找评论
    Comment selectCommentById(int entityId);
}


