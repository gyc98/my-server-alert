package cn.billycloud.myserveralert.service.base;

import cn.billycloud.myserveralert.util.Result;

public interface MessagePushService {
    //推送消息并返回结果
    Result push(long userID, String message, boolean allowRetry);
}
