package com.nowcoder.community.util;

import org.apache.kafka.common.protocol.types.Field;

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 重复失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 12 * 100;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：回复
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：人
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：发布帖子
     */
    String TOPIC_PUBLISH = "publish";

    /**
     *系统用户ID
     */
    int SYSTEM_USER_ID = 1;
}
