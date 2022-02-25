package cn.billycloud.myserveralert.service.http;

import cn.billycloud.myserveralert.util.MyException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import javax.net.ssl.SSLSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class GetHelper {
    public static JSONObject send(String url, Map<String, String> requestMap) throws Exception {
        CloseableHttpClient httpclient = HttpClientFactory.getHttpClient();

        if(requestMap.size() > 0){
            url += "?";
            boolean addFlag = false;
            for(Map.Entry<String, String> entry : requestMap.entrySet()){
//                url += URLEncoder.encode(entry.getKey(),"utf-8") + "=" + URLEncoder.encode(entry.getValue(),"utf-8");
                if(addFlag){
                    url += "&";
                }
                url += entry.getKey() + "=" + entry.getValue();
                addFlag = true;
            }
        }

        HttpGet httpGet = new HttpGet(url);

//        // 解决中文乱码问题
//        StringEntity stringEntity = new StringEntity(jsonObject.toString(), StandardCharsets.UTF_8);
//
//        httpGet.setEntity(stringEntity);

        // CloseableHttpResponse response =
        // httpclient.execute(httpPost);
//        System.out.println("entity: " + httpGet.getEntity());
        System.out.println("Executing request " + httpGet.getRequestUri());

        final HttpClientContext clientContext = HttpClientContext.create();
        try (CloseableHttpResponse response = httpclient.execute(httpGet, clientContext)){
            if(response.getCode() == 200){
                String responseStr = EntityUtils.toString(response.getEntity());
                try {
                    //转成json
                    JSONObject responseJsonObj = JSON.parseObject(responseStr);
                    return responseJsonObj;
                }catch (Exception e){
                    log.info("转换http响应异常", e);
                    throw new MyException("http响应异常 可能不是json");
                }

            }else{
                //失败
                log.error(response.getCode() + " " + response.getReasonPhrase());
                throw new MyException("http请求异常");
            }
        }
    }
}
