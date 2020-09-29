package cn.nkym.pt.utils;

import cn.nkym.pt.pojo.TieBa;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TBUtils {

    private static int againNum = 0;
    private static int successNum = 0;
    private static int wrongNum = 0;
    private static int num = 1;

    /**
     * 根据cookie查询获取当前cookie对应账号关注的贴吧列表
     * @param cookie
     * @return
     */
    public static List<TieBa> getMyLike(String cookie) {
        ArrayList<TieBa> tieBas = new ArrayList<>();
        int pageNum = 1;
        while (true) {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            URIBuilder builder = null;//http://tieba.baidu.com/f/like/mylike?pn=2
            try {
                builder = new URIBuilder("http://tieba.baidu.com/f/like/mylike");
            } catch (URISyntaxException e) {
                log.error("URISyntaxException", e);
                e.printStackTrace();
            }
            builder.setParameter("pn", pageNum + "");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
            HttpGet httpGet = new HttpGet(String.valueOf(builder));
            httpGet.setConfig(requestConfig);
            httpGet.addHeader("Cookie", cookie);
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String entityStr = EntityUtils.toString(response.getEntity());
                    Document document = Jsoup.parse(entityStr);
                    Elements elements = document.getElementsByTag("table").first().getElementsByTag("tr");
                    if (elements.size() > 1) {
                        for (int i = 1; i < elements.size(); i++) {
                            TieBa tieBa = new TieBa();
                            Element ele = elements.get(i).getElementsByTag("td").first();
                            String title = ele.getElementsByTag("a").attr("title");
                            String href = ele.getElementsByTag("a").attr("href");
                            String kw = URLEncoder.encode(title, "UTF-8");
                            tieBa.setTitle(title);
                            tieBa.setHref(href);
                            tieBa.setKw(kw);
                            tieBas.add(tieBa);
                        }
                    } else {
                        System.out.println("获取贴吧列表完成,共关注" + "" + tieBas.size() + "个贴吧\r\n");
                        return tieBas;
                    }
                } else {
                    System.out.println("获取贴吧关注列表出错,网络错误\t" + response.getStatusLine().getStatusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("获取贴吧关注列表出错", e);
            } finally {
                if (response != null)
                    try {
                        response.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
            pageNum++;
        }
    }

    /**
     * 根据查询获得的贴吧列表，遍历每一个贴吧对象，获取其签到所需的Tbs，并调用goBaiDuTask进行签到
     * @param tieBas
     * @param cookie
     */
    public static void getTbsAndGo(List<TieBa> tieBas, String cookie) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        int count = 1;
        saveTbs("totalNum", String.valueOf(tieBas.size()));
        for (TieBa tieBa : tieBas) {
            try {
                String href = tieBa.getHref();
                URIBuilder builder = new URIBuilder("https://tieba.baidu.com" + href);//https://tieba.baidu.com/f?kw=c%D3%EF%D1%D4
                HttpPost httpPost = new HttpPost(String.valueOf(builder));
                httpPost.addHeader("Cookie", cookie);
                httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
                CloseableHttpResponse response = httpClient.execute(httpPost);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String entityStr = EntityUtils.toString(response.getEntity());
                    String pattern = "_\\.Module\\.use\\('bawu/widget/bawuAddSection',([^)])*";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(entityStr);
                    if (m.find()) {
                        String findRes = m.group(0);
                        int index = findRes.indexOf("{");
                        findRes = findRes.substring(index);
                        ObjectMapper mapper = new ObjectMapper();
                        Map resMap = mapper.readValue(findRes, Map.class);
                        String tbs = String.valueOf(resMap.get("tbs"));
                        saveTbs("tbName" + count, tieBa.getTitle());
                        saveTbs("tbTbs" + count, tbs);
                        saveTbs("tbKw" + count, tieBa.getKw());
                        saveTbs("tbHref" + count, tieBa.getHref());
                        count++;
                        tieBa.setTbs(tbs);
                        goBaiDuTask(tieBa, cookie, tieBas.size(), num);
                        num++;
                    }
                }
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("\r\n");
        System.out.println("签到成功: " + successNum);
        System.out.println("重复签到: " + againNum);
        System.out.println("签到出错: " + wrongNum);
        System.out.println("\r\n");
        System.out.println("\r\n");
    }


    /**
     * 签到对应的贴吧
     * @param tieBa
     * @param cookie
     * @param totalNum
     * @param num
     */
    public static void goBaiDuTask(TieBa tieBa, String cookie, int totalNum, int num) {
        Map<String, Integer> resultmap = new HashMap<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        StringBuffer resultStr = new StringBuffer();
        resultStr.append("[" + num + "/" + totalNum + "]");
        String kw = tieBa.getKw();
        String tbs = tieBa.getTbs();
        URIBuilder builder = null;
        try {
            builder = new URIBuilder("https://tieba.baidu.com/sign/add");
        } catch (URISyntaxException e) {
            log.error("URISyntaxException", e);
            e.printStackTrace();
        }
        HttpPost httpPost = new HttpPost(String.valueOf(builder));
        httpPost.addHeader("Cookie", cookie);
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
        httpPost.setEntity(new StringEntity("ie=utf-8" + "&kw=" + kw + "&tbs=" + tbs, "gbk")); //ie=utf-8&kw=%E5%A4%A7%E5%AD%A6&tbs=d9ea83924514cae81594038389
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                String entityStr = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                Map map = mapper.readValue(entityStr, Map.class);
                String no = String.valueOf(map.get("no"));
                if ("1101".equals(no)) {
                    resultStr.append("\t\t重复签到\t\t");
                    againNum++;
                } else if ("0".equals(no)) {
                    resultStr.append("\t\t签到成功\t\t");
                    successNum++;
                } else {
                    resultStr.append("\t\t未知错误\t\t");
                    wrongNum++;
                }
                resultStr.append(tieBa.getTitle());
                System.out.println(resultStr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                try {
                    response.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        resultmap.put("againNum", againNum);
        resultmap.put("successNum", successNum);
        resultmap.put("wrongNum", wrongNum);
    }

    /**
     * 将参数保存到tbs.properties中，不用每次都拉取关注的贴吧列表
     * @param key
     * @param val
     */
    public static void saveTbs(String key, String val) {
        File file = new File("tbs.properties");
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(file));
            properties.setProperty(key, val);
            FileOutputStream oFile = new FileOutputStream("tbs.properties");//true表示追加打开
            properties.store(oFile, "Tbs Setting");
            oFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("设置贴吧TBS出错", e);
        }
    }

    public static void goBaiDuTaskByProperties(String cookie) throws IOException {
        int count = 1;
        Properties properties = new Properties();
        properties.load(new FileInputStream("tbs.properties"));
        int totalNum = Integer.valueOf(properties.getProperty("totalNum"));
        TieBa tieBa;
        while (StringUtils.isNotBlank(properties.getProperty("tbName" + count))){
            tieBa = new TieBa(properties.getProperty("tbName" + count), properties.getProperty("tbHref" + count), properties.getProperty("tbKw" + count), properties.getProperty("tbTbs" + count));
            goBaiDuTask(tieBa, cookie, totalNum, count);
            count++;
        }
    }

}
