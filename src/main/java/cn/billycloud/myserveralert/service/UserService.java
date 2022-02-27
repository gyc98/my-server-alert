package cn.billycloud.myserveralert.service;

import cn.billycloud.myserveralert.dao.mapper.UserMapper;
import cn.billycloud.myserveralert.entity.CookieInfo;
import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.redis.UserCookieRedisCache;
import cn.billycloud.myserveralert.util.MyException;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserCookieRedisCache userCookieRedisCache;

    public Result addNewUser(String userName, String passwordSet) {
        if(userName == null || passwordSet == null){
            return Result.failure(ResultCode.DATA_IS_WRONG, "缺少参数");
        }
        //用户名和密码都不能为空
        if(userName.isEmpty()){
            return Result.failure(ResultCode.DATA_IS_WRONG, "用户名不能为空");
        }
        if(!userName.equals(stringFilter(userName))){
            return Result.failure(ResultCode.DATA_IS_WRONG, "用户名只允许字母和数字");
        }
        if(passwordSet.isEmpty()){
            return Result.failure(ResultCode.DATA_IS_WRONG, "密码不能为空");
        }
        if(passwordSet.length() < 8){
            return Result.failure(ResultCode.DATA_IS_WRONG, "密码需至少8位");
        }
        if(!passwordSet.equals(stringFilter(passwordSet))){
            return Result.failure(ResultCode.DATA_IS_WRONG, "密码只允许字母和数字");
        }

        //校验通过 开始保存
        try {
            //先处理密码盐化
            //先生成盐值
            SecureRandom random = new SecureRandom();
            byte bytes[] = new byte[64];
            random.nextBytes(bytes);
            String salt = toHexString(bytes);
            String passwordHash = getSHA256StrJava(salt + passwordSet);
            UserInfo userInfo = new UserInfo(userName, salt, passwordHash, new Date(), new Date());

            //保存到数据库
            int res = userMapper.insert(userInfo);
            if(res > 0){
                //获取用户账号
                long userID = userInfo.getUserID();
                return Result.success(ResultCode.SUCCESS, String.valueOf(userID));
            }else{
                return Result.failure(ResultCode.FAILURE, "用户信息添加到数据库失败");
            }
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException ex){
            log.error("创建用户异常", ex);
            return Result.failure(ResultCode.FAILURE, ex.getMessage());
        }
    }

    //登录
    public Result userLogin(String userIDStr, String passwordInput){
        long userID;
        try {
            userID = Long.valueOf(userIDStr);
        }catch (NumberFormatException e){
            log.error("转换userID出错", e);
            return Result.failure(ResultCode.FAILURE, "userID需为数字");
        }

        UserInfo userInfo = userMapper.selectByUserID(userID);
        if(userInfo == null){
            return Result.failure(ResultCode.FAILURE, "用户不存在");
        }
        //进行校验
        try {
            String passwordHash = getSHA256StrJava(userInfo.getSalt() + passwordInput);
            if(passwordHash.equals(userInfo.getPasswordHash())){
                //登录成功之后 为用户生成一个cookie信息
                CookieInfo cookieInfo = CookieInfo.generate(userInfo);

                //密码一致
                //更新登录时间
//                int res = userMapper.updateLastLoginTimeAndCookie(userID, new Date(), cookieInfo.getCookieVal());

                //保存cookie
                userInfo.setCookie(cookieInfo.getCookieVal());
                userInfo.setLastLoginTime(new Date());
                Result result = userCookieRedisCache.setCookie(userInfo);

                if(ResultCode.SUCCESS.code() == result.getCode()){
                    //成功保存到数据库和缓存
                    return Result.success(ResultCode.SUCCESS, cookieInfo);
                }else{
                    return Result.failure(ResultCode.FAILURE, "无法登录");
                }

//                if(res > 0){
//                    return Result.success(ResultCode.SUCCESS, cookieInfo);
//                }else{
//                    //密码不对
//                    return Result.failure(ResultCode.FAILURE, "无法登录");
//                }
            }else{
                //密码不对
                return Result.failure(ResultCode.DATA_IS_WRONG, "密码错误");
            }
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | MyException e) {
            log.error("校验密码出错", e);
            return Result.failure(ResultCode.FAILURE, e.getMessage());
        }
    }

    //检查cookie
    public UserInfo checkCookie(CookieInfo cookieInfo) {
        try {
            Result result = userCookieRedisCache.getCookie(cookieInfo.getCookieVal());
            UserInfo userInfo = null;
            if(ResultCode.SUCCESS.code() == result.getCode()){
                userInfo = (UserInfo)result.getData();
            }
            if(userInfo == null){
                return null;
            }
            if(userInfo.getUserID() == cookieInfo.getUserID() && userInfo.getUserName().equals(cookieInfo.getUserName())){
                return userInfo;
            }else{
                return null;
            }
        }catch (Exception e){
            log.info("cookie检查异常", e);
            return null;
        }
    }


    //辅助方法

    //用于检测用户名特殊字符
    public static String stringFilter(String str)throws PatternSyntaxException {
        // 只允许字母和数字
        String regEx  =  "[^a-zA-Z0-9]";
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(str);
        return   m.replaceAll("").trim();
    }

    public static String getSHA256StrJava(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest;
        messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(str.getBytes("UTF-8"));
        String encodeStr = toHexString(messageDigest.digest());
        return encodeStr;
    }

    /**
     * byte[] to Hex string.
     *
     * @param byteArray the byte array
     * @return the string
     */

    public static String toHexString(byte[] byteArray) {
        final StringBuilder hexString = new StringBuilder("");
        if (byteArray == null || byteArray.length <= 0)
            return null;
        for (int i = 0; i < byteArray.length; i++) {
            int v = byteArray[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hexString.append(0);
            }
            hexString.append(hv);
        }
        return hexString.toString().toLowerCase();
    }


}
