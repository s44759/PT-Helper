package cn.nkym.pt.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@Slf4j
public class SignUtils {

    /**
     * 根据传递的参数，签到对应的PT站点
     * @param cookie
     * @param signUri
     * @param name
     */
    public static void signIn(String cookie, String signUri, String name) {
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
            HttpGet httpGet = new HttpGet(signUri);
            httpGet.setConfig(requestConfig);
            httpGet.addHeader(":method", "GET");
            httpGet.addHeader(":path", "/index.php");
            httpGet.addHeader(":scheme", "https");
            httpGet.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            httpGet.addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.7,und;q=0.6");
            httpGet.addHeader("cache-control", "max-age=0");
            httpGet.addHeader("cookie", cookie);
            httpGet.addHeader("sec-fetch-dest", "document");
            httpGet.addHeader("sec-fetch-mode", "navigate");
            httpGet.addHeader("sec-fetch-site", "none");
            httpGet.addHeader("sec-fetch-user", "?1");
            httpGet.addHeader("upgrade-insecure-requests", "1");
            httpGet.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response;
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println(name + " 签到成功");
            } else {
                System.out.println(name + "\t签到出现异常 未签到成功\t" + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(name + " 签到出现异常 未签到成功");
            log.error("签到出现异常", e);
        }
    }
}
