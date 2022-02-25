package cn.billycloud.myserveralert.service.http;

import cn.billycloud.myserveralert.util.MyException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.omg.CORBA.portable.ResponseHandler;

import javax.net.ssl.SSLSession;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PostHelper {
    public static JSONObject send(String url, JSONObject jsonObject) throws Exception {
        CloseableHttpClient httpclient = HttpClientFactory.getHttpClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");

        // 解决中文乱码问题
        StringEntity stringEntity = new StringEntity(jsonObject.toString(), StandardCharsets.UTF_8);

        httpPost.setEntity(stringEntity);

        // CloseableHttpResponse response =
        // httpclient.execute(httpPost);

        System.out.println("Executing request " + httpPost.getRequestUri());

        final HttpClientContext clientContext = HttpClientContext.create();
        try (CloseableHttpResponse response = httpclient.execute(httpPost, clientContext)){
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
