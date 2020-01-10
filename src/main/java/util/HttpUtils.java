package util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

/**
 * @Author sir
 * @Date 2020/1/9 10:18
 * @Description TODO
 **/
@Slf4j
public class HttpUtils {

    // 默认配置
    private static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
            .setConnectionRequestTimeout(1000).setSocketTimeout(5000).build();


    /**
     * 发送get请求
     *
     * @param url
     * @param headers
     * @param isHttps
     * @return
     */
    public static String get(String url, Map<String, String> headers, boolean isHttps) {
        String result = "";
        TimeCounter.start();
        try {
            HttpClient httpClient = createHttpClient(isHttps);
            HttpGet get = new HttpGet(url);
            get.setConfig(requestConfig);
            if (!MapUtils.isEmpty(headers)) {
                headers.forEach(get::setHeader);
            }
            HttpResponse response = httpClient.execute(get);
            result = parseRes(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("请求耗时：[{}ms]", TimeCounter.counts());
        }
        return result;
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param body
     * @param headers
     * @param isHttps
     * @return
     */
    public static String postJson(String url, String body, Map<String, String> headers, boolean isHttps) {
        String result = "";
        TimeCounter.start();
        try {
            HttpClient httpClient = createHttpClient(isHttps);
            HttpPost post = new HttpPost(url);
            post.setConfig(requestConfig);
            if (!MapUtils.isEmpty(headers)) {
                headers.forEach(post::setHeader);
            }
            post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
            HttpResponse response = httpClient.execute(post);
            result = parseRes(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("请求耗时：[{}ms]", TimeCounter.counts());
        }
        return result;
    }

    public static String postFile(String url, Map<String, Object> params, Map<String, String> headers, boolean isHttps) {
        String result = "";
        TimeCounter.start();
        try {
            HttpClient httpClient = createHttpClient(isHttps);
            HttpPost post = new HttpPost(url);
            post.setConfig(requestConfig);
            if (!MapUtils.isEmpty(headers)) {
                headers.forEach(post::setHeader);
            }
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() instanceof File) {
                    multipartEntityBuilder.addBinaryBody(entry.getKey(), (File) entry.getValue());
                } else {
                    multipartEntityBuilder.addTextBody(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            HttpEntity httpEntity = multipartEntityBuilder.build();
            post.setEntity(httpEntity);
            HttpResponse response = httpClient.execute(post);
            result = parseRes(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("请求耗时：[{}ms]", TimeCounter.counts());
        }
        return result;
    }

    /**
     * 创建HttpClient
     *
     * @param isHttps
     * @return
     */
    private static HttpClient createHttpClient(boolean isHttps) {
        HttpClient httpClient = null;
        if (isHttps) {
            try {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
                        null, (x509Certificates, s) -> true).build();
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
                e.printStackTrace();
            }
        } else {
            httpClient = HttpClients.createDefault();
        }
        return httpClient;
    }

    /**
     * 解析响应结果
     *
     * @param response
     * @return
     * @throws IOException
     */
    private static String parseRes(HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }
}
