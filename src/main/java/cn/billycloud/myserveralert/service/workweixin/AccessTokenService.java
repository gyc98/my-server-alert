package cn.billycloud.myserveralert.service.workweixin;

import cn.billycloud.myserveralert.service.http.GetHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AccessTokenService {
    @Value("${workweixin.corpid}")
    private String corpid;
    @Value("${workweixin.corpsecret}")
    private String corpsecret;

    private final String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    //请求accessToken
    public AccessTokenInfo requestAccessToken(){
        try {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("corpid", corpid);
            requestMap.put("corpsecret", corpsecret);
            JSONObject jsonObject = GetHelper.send(url, requestMap);
            int errCode = jsonObject.getInteger("errcode");
            if(errCode == 0){
                String accessToken = jsonObject.getString("access_token");
                int expiresIn = jsonObject.getInteger("expires_in");
                System.out.println(accessToken);
                System.out.println(expiresIn);
                return new AccessTokenInfo(accessToken, expiresIn);
            }else{
                String errMsg = jsonObject.getString("errmsg");
                log.error("获取accessToken异常：" + errCode + "  " + errMsg);
            }

        }catch (Exception e){
            log.error("获取accessToken异常", e);
        }
        return null;
    }

    class AccessTokenInfo{
        String accessToken;
        int expireSec;

        public AccessTokenInfo(String accessToken, int expireSec){
            this.accessToken = accessToken;
            this.expireSec = expireSec;
        }
    }
}
