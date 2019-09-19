package com.ontology.utils;

import com.ontology.exception.CallbackException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HttpClientUtils {

    private static Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    private static HttpClient httpClient;

    static {
        //HttpClient4.5版本后的参数设置
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        //客户端和服务器建立连接的timeout
        requestConfigBuilder.setConnectTimeout(30000);
        //从连接池获取连接的timeout
        requestConfigBuilder.setConnectionRequestTimeout(30000);
        //连接建立后，request没有回应的timeout。
        requestConfigBuilder.setSocketTimeout(60000);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
        //连接建立后，request没有回应的timeout
        clientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(60000).build());
        clientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(30);
        httpClient = clientBuilder.setConnectionManager(cm).build();
    }

    /**
     * httpclient post请求
     *
     * @param url
     * @param reqBodyStr
     * @param headerMap
     * @return 响应数据
     * @throws Exception
     */
    public String httpClientPost(String url, String reqBodyStr, Map<String, Object> headerMap) throws Exception {

        String responseStr = "";

        StringEntity stringEntity = new StringEntity(reqBodyStr, Charset.forName("UTF-8"));
        stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(stringEntity);
        //设置请求头
        for (Map.Entry<String, Object> entry :
                headerMap.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue().toString());
        }

        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            responseStr = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            log.error("{} error...",e);
            throw new CallbackException(ErrorInfo.COMM_FAIL.code(), ErrorInfo.COMM_FAIL.descEN(), ErrorInfo.COMM_FAIL.descCN());
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            log.info("send requestbody:{} to {},response 200:{}", reqBodyStr, url, responseStr);
            return responseStr;
        } else {
            log.error("send requestbody:{} to {},response {}:{}", reqBodyStr, url, response.getStatusLine().getStatusCode(), responseStr);
            throw new CallbackException(ErrorInfo.COMM_FAIL.code(), ErrorInfo.COMM_FAIL.descEN(), ErrorInfo.COMM_FAIL.descCN());
        }
    }

    /**
     * httpclient get请求
     *
     * @param uri
     * @param paramMap
     * @param headerMap
     * @return 响应数据
     * @throws Exception
     */
    public String httpClientGet(String uri, Map<String, Object> paramMap, Map<String, Object> headerMap) throws Exception {

        String responseStr = "";

        CloseableHttpResponse response = null;
        URIBuilder uriBuilder = null;
        try {
            //拼完整的请求url
            uriBuilder = new URIBuilder(uri);
            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, Object> entry :
                    paramMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            uriBuilder.setParameters(params);

            HttpGet httpGet = new HttpGet(uriBuilder.build());
            //设置请求头
            for (Map.Entry<String, Object> entry :
                    headerMap.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue().toString());
            }
            response = (CloseableHttpResponse) httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            responseStr = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            log.error("{} error...", e);
            throw new CallbackException(ErrorInfo.COMM_FAIL.code(), ErrorInfo.COMM_FAIL.descEN(), ErrorInfo.COMM_FAIL.descCN());
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            log.info("send to {},response 200:{}", uriBuilder.toString(), responseStr);
            return responseStr;
        } else {
            log.error("send to {},response {}:{}", uriBuilder.toString(), response.getStatusLine().getStatusCode(), responseStr);
            throw new CallbackException(ErrorInfo.COMM_FAIL.code(), ErrorInfo.COMM_FAIL.descEN(), ErrorInfo.COMM_FAIL.descCN());
        }
    }
}
