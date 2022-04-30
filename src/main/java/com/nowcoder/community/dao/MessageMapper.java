package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表，针对每个会话只返回一条最新消息
    List<Message> selectConversations(int userId, int offset, int limit);
    //查询当前用户的会话列表数量
    int selectConversationCount(int userId);
    //查询当前会话消息列表
    List<Message> selectLetters(String conversationId, int offset, int limit);
    //查询当前会话消息列表数量
    int selectLetterCount(String conversationId);
    //查询未读私信的数量
    int selectLetterUnreadCount(int userId, String conversationId);

    //添加消息
    int insertMessage(Message message);
    //更新消息状态
    int updateStatus(List<Integer> ids, int status);
}