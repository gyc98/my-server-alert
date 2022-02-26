package cn.billycloud.myserveralert.service;

import cn.billycloud.myserveralert.dao.mapper.UserPushSettingMapper;
import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserPushSettingService {
    @Autowired
    private UserPushSettingMapper userPushSettingMapper;

    public Result getUserPushSettingInfo(long userID){
        UserPushSettingInfo userPushSettingInfo = userPushSettingMapper.selectSetting(userID);
        if(userPushSettingInfo == null){
            //创建一个空的
            userPushSettingInfo = new UserPushSettingInfo();
            userPushSettingInfo.setUserID(userID);
            //先不插入数据库
        }
        return Result.success(ResultCode.SUCCESS, userPushSettingInfo);
    }

    public Result setUserPushSettingInfo(UserPushSettingInfo userPushSettingInfo){
        if(userPushSettingInfo == null){
            return Result.failure(ResultCode.DATA_IS_WRONG, "请输入有效信息");
        }
        int res = userPushSettingMapper.addSetting(userPushSettingInfo);
        if(res > 0){
            return Result.success(ResultCode.SUCCESS, "已更新");
        }else{
            return Result.failure(ResultCode.FAILURE, "更新失败");
        }
    }
}
