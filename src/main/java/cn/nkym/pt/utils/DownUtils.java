package cn.nkym.pt.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownUtils {

    public static String getCookie(String port, String username, String password) {
        String cookie = null;
        HttpPost httpPost = new HttpPost("http://localhost:" + port + "/api/v2/auth/login");
        try {
            httpPost.setEntity(new StringEntity("username=" + username + "&password=" + password));
        } catch (Exception e) {

        }
        httpPost.addHeader("Host", "localhost:" + port);
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36 Edg/81.0.416.64");
        httpPost.addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("Origin", "http://localhost:" + port);
        httpPost.addHeader("Referer", "http://localhost: " + port + "/");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            String setCookieStr = Arrays.toString(response.getHeaders("set-cookie"));
            String pattern = "SID=([^;]*)";

            // 创建 Pattern 对象
            Pattern r = Pattern.compile(pattern);

            // 现在创建 matcher 对象
            Matcher m = r.matcher(setCookieStr);
            if (m.find()) {
                cookie = m.group(0);
            }
        } catch (Exception e) {

        }
        return cookie;
    }

    public static boolean addTor(String port, String urls, String cookie, String savepath, String lmCookie) {
//        String boundary = "--------------------------" + getCharAndNumr(24);
        String boundary = "----WebKitFormBoundarymxmyBmcN2OArwOQ7";
        String boundary2 = "--" + boundary;
        //请求地址
        String url = "http://localhost:" + port + "/api/v2/torrents/add";
        String param = "";

        param += boundary2 + "\r\n";
        param += "Content-Disposition: form-data; name=\"urls\"" + "\r\n\r\n";

        param += urls + "\r\n";
        param += boundary2 + "\r\n";
        param += "Content-Disposition: form-data; name=\"autoTMM\"" + "\r\n\r\n";

        param += "false\r\n";
        param += boundary2 + "\r\n";
        param += "Content-Disposition: form-data; name=\"savepath\"\r\n\r\n";

        param += savepath + "\r\n";
        param += boundary2 + "\r\n";
        param += "Content-Disposition: form-data; name=\"cookie\"\r\n\r\n";

        param += lmCookie + "\r\n";
        param += boundary2 + "\r\n";
        param += "Content-Disposition: form-data; name=\"rename\"\r\n\r\n\r\n";

        param += boundary2 + "\r\n";
        param += "Content-Disposition: form-data; name=\"dlLimi\"\r\n\r\n";

        param += "NaN\r\n";
        param += boundary2 + "\r\n";
        param += "Content-Disposition: form-data; name=\"upLimit\"\r\n\r\n";

        param += "NaN\r\n";
        param += boundary2 + "\r\n";
        param += "Content-Disposition: form-data; name=\"category\"\r\n\r\n\r\n";

        param += boundary2 + "--\r\n";

        HttpPost httpPost = new HttpPost("http://localhost:" + port + "/api/v2/torrents/add");

        httpPost.addHeader("Cookie", cookie);
        httpPost.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

        httpPost.setEntity(new StringEntity(param, "UTF-8"));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        CloseableHttpResponse response;

        String result = "";

        try {
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e);
        }

        if (result.contains("Ok.")) {
            return true;
        }

        return false;
    }

    private static String getCharAndNumr(int length) {

        Random random = new Random();

        StringBuffer valSb = new StringBuffer();

        String charStr = "0123456789abcdefghijklmnopqrstuvwxyz";

        int charLength = charStr.length();


        for (int i = 0; i < length; i++) {

            int index = random.nextInt(charLength);

            valSb.append(charStr.charAt(index));

        }

        return valSb.toString();

    }

}