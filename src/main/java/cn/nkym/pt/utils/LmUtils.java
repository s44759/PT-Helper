package cn.nkym.pt.utils;

import cn.nkym.pt.pojo.LmPage;
import cn.nkym.pt.pojo.LmTorr;
import cn.nkym.pt.window.WindowInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LmUtils {

    /**
     * 查询返回柠檬种子页面数据
     *
     * @return
     */
    public static LmPage getLmPage(String lmUri, int socketTimeout, int connectTimeout, String cookie) {

        LmPage lmPage = new LmPage();

        //需要爬取数据的网址 包含页数以及筛选条件
        HttpGet httpGet = new HttpGet(lmUri);

        //设置连接超时时间和数据交互超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        httpGet.setConfig(requestConfig);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //向请求中添加头信息
        httpGet.addHeader("Host", "leaguehd.com");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("Cache-Control", "max-age=0");
        httpGet.addHeader("Upgrade-Insecure-Requests", "1");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
        httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpGet.addHeader("Sec-Fetch-Site", "none");
        httpGet.addHeader(" Sec-Fetch-Mode", "navigate");
        httpGet.addHeader("Sec-Fetch-User", "?1");
        httpGet.addHeader("Sec-Fetch-Dest", "document");
//        httpGet.addHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.7,und;q=0.6");
        httpGet.addHeader("Cookie", cookie);
        CloseableHttpResponse execute = null;

        try {
            execute = httpClient.execute(httpGet);

            //如果请求发送，成功返回数据
            if (execute.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = execute.getEntity();
                String result = EntityUtils.toString(entity);

                Document document = Jsoup.parse(result);

                //获取页面包含所有种子的元素
                Elements torrenttrs = document.getElementsByClass("torrents").first().getElementsByTag("tr");

                //页面内的种子总数
                int totalNum = torrenttrs.size() / 2;
                lmPage.setTotalNum(totalNum);

                //1级置顶的种子数量
                int topNum = document.getElementsByClass("sticky_1").size() / 2;
                lmPage.setTopNum(topNum);

                //2级置顶的种子数量
                int middleNum = document.getElementsByClass("sticky_2").size() / 2;
                lmPage.setMiddleNum(middleNum);

                //普通的种子数量
                int emptyNum = totalNum - topNum - middleNum;
                lmPage.setEmptyNum(emptyNum);

                Element element1 = torrenttrs.get(200);

                List<LmTorr> lmTorrs = new ArrayList<>();
                //遍历取出每个种子,得到种子列表
                for(int i = 1; i < torrenttrs.size(); i += 2){
                    lmTorrs.add(getLmTorr(torrenttrs.get(i)));
                }

                lmPage.setLmTorrs(lmTorrs);

                return lmPage;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("柠檬查询种子页面出错", e);
        }
        return null;
    }


    /**
     * 根据种子元素提取出所需信息,并包装为种子对象返回
     *
     * @param lmTorr
     * @return
     */
    public static LmTorr getLmTorr(Element lmTorr) {
        LmTorr lm = new LmTorr();

        //提取种子的发布时间
        String timeStr = lmTorr.getElementsByClass("rowfollow nowrap").get(1).getElementsByTag("span").get(0).attr("title");

        //格式化发布时间
        try {
            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = simpleDateFormat.parse(timeStr);
            lm.setTime(time);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("格式化时间出错", e);
        }

        StringBuffer activity = new StringBuffer();

        //判断该种子是否为一级置顶
        if (lmTorr.getElementsByClass("sticky_1").size() > 0) {
            activity.append("[TopTop]");
        }

        //判断该种子是否为二级置顶
        if (lmTorr.getElementsByClass("sticky_2").size() > 0) {
            activity.append("[Top]");
        }

        //判断该种子是否为50%下载
        if (lmTorr.getElementsByClass("pro_50pctdown").size() > 0) {
            activity.append("[50%]");
        }

        //判断该种子是否为免费下载
        if (lmTorr.getElementsByClass("pro_free").size() > 0) {
            activity.append("[free]");
        }

        //判断该种子是否为30%下载
        if (lmTorr.getElementsByClass("pro_free6up").size() > 0) {
            activity.append("[6xFree]");
        }

        //判断该种子是否为官方发布
        if (lmTorr.getElementsByClass("tag tag_gf").size() > 0) {
            activity.append("[官方]");
        }

        //判断该种子是否为官方发布
        if (lmTorr.getElementsByClass("hot").size() > 0) {
            activity.append("[热门]");
        }

        //将提取出的所有优惠、官方、置顶等标识设置到对象中
        lm.setActivity(activity.toString());

        /*//提取种子标题
        String title = lmTorr.getElementsByClass("torrentname").first().getElementsByTag("a").first().attr("title");
        lm.setTitle(title);

        //提取种子副标题
        String titleContext = lmTorr.getElementsByClass("embedded").get(0).text();
        lm.setTitle(titleContext);*/

        //提取种子完整标题
        String titleContext = lmTorr.getElementsByClass("embedded").get(0).text();
        lm.setTitle(titleContext);

        //提取包含种子id的链接
        String href = lmTorr.getElementsByClass("torrentname").first().getElementsByTag("a").first().attr("href");

        String pattern = "id=(\\d+)&";

        //创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        //创建 matcher 对象
        Matcher m = r.matcher(href);
        if (m.find()) {

            //通过正则表达式匹配得出种子id
            lm.setId(Long.valueOf(m.group(1)));
        } else {
            System.out.println("正则表达式没有匹配到想要的种子id");
        }

        //提取种子包含的文件总大小
        String size = lmTorr.getElementsByClass("rowfollow").get(4).text();
        lm.setSize(size);

        return lm;
    }

    /**
     * 对比新旧页面种子数据,获取新发布的种子信息，打印到控制台和发布到win10通知,最后返回本次对比发现的新种子数量
     * @param num
     * @param bef
     * @param aft
     * @param sleepTime
     * @param simpleDateFormat
     * @return
     */
    public static int findNewTorrent(int num, LmPage bef, LmPage aft, int sleepTime, DateFormat simpleDateFormat) {
        int findNum = 0;
        for (int index = 0, count = 0; index < aft.getTotalNum(); index++) {
            if ((!bef.getLmTorrs().get(index - count).getId().equals(aft.getLmTorrs().get(index).getId())) && ((new Date().getTime() - aft.getLmTorrs().get(index).getTime().getTime()) < (sleepTime * 1.5 * 1000))) {
                System.out.println("[柠檬]" + "[" + simpleDateFormat.format(new Date()) + "]" + "第" + num + "次查询,新种\t" + (StringUtils.isBlank(aft.getLmTorrs().get(index).getActivity()) ? "" : aft.getLmTorrs().get(index).getActivity()) + aft.getLmTorrs().get(index).getTitle() + "\t" + aft.getLmTorrs().get(index).getSize() + "\t" + "https://leaguehd.com/details.php?id=" + aft.getLmTorrs().get(index).getId() + "&hit=1\t" + "https://leaguehd.com/download.php?https=1&id=" + aft.getLmTorrs().get(index).getId());
                WindowInfo.showInfo("[柠檬]" + (StringUtils.isBlank(aft.getLmTorrs().get(index).getActivity()) ? "" : aft.getLmTorrs().get(index).getActivity()) + aft.getLmTorrs().get(index).getTitle(), aft.getLmTorrs().get(index).getSize());
                count++;
                findNum++;
            }
        }
        return findNum;
    }

    public static int findNewTorrentWithOut(int num, LmPage bef, LmPage aft, int sleepTime, DateFormat simpleDateFormat) {
        int findNum = 0;
        for (int index = 0, count = 0; index < aft.getTotalNum(); index++) {
            if ((!bef.getLmTorrs().get(index - count).getId().equals(aft.getLmTorrs().get(index).getId())) && ((new Date().getTime() - aft.getLmTorrs().get(index).getTime().getTime()) < (sleepTime * 1.5 * 1000))) {
                System.out.println("[柠檬]" + "[" + simpleDateFormat.format(new Date()) + "]" + "第" + num + "次查询,新种\t" + (StringUtils.isBlank(aft.getLmTorrs().get(index).getActivity()) ? "" : aft.getLmTorrs().get(index).getActivity()) + aft.getLmTorrs().get(index).getTitle() + "\t" + aft.getLmTorrs().get(index).getSize() + "\t" + "https://leaguehd.com/details.php?id=" + aft.getLmTorrs().get(index).getId() + "&hit=1\t" + "https://leaguehd.com/download.php?https=1&id=" + aft.getLmTorrs().get(index).getId());
                count++;
                findNum++;
            }
        }
        return findNum;
    }

    public static int findNewTorrentAndDownAndInfo(int num, LmPage bef, LmPage aft, int sleepTime, DateFormat simpleDateFormat, String port, String savepath, String lmCookie, String qbCookie) {
        int findNum = 0;
        for (int index = 0, count = 0; index < aft.getTotalNum(); index++) {
            if ((!bef.getLmTorrs().get(index - count).getId().equals(aft.getLmTorrs().get(index).getId())) && ((new Date().getTime() - aft.getLmTorrs().get(index).getTime().getTime()) < (sleepTime * 1.5 * 1000))) {
                System.out.println("[柠檬]" + "[" + simpleDateFormat.format(new Date()) + "]" + "第" + num + "次查询,新种\t" + (StringUtils.isBlank(aft.getLmTorrs().get(index).getActivity()) ? "" : aft.getLmTorrs().get(index).getActivity()) + aft.getLmTorrs().get(index).getTitle() + "\t" + aft.getLmTorrs().get(index).getSize() + "\t" + "https://leaguehd.com/details.php?id=" + aft.getLmTorrs().get(index).getId() + "&hit=1\t" + "https://leaguehd.com/download.php?https=1&id=" + aft.getLmTorrs().get(index).getId());
                DownUtils.addTor(port, "https://leaguehd.com/download.php?https=1&id=" + aft.getLmTorrs().get(index).getId(), qbCookie, savepath, lmCookie);
                WindowInfo.showInfo("[柠檬]" + (StringUtils.isBlank(aft.getLmTorrs().get(index).getActivity()) ? "" : aft.getLmTorrs().get(index).getActivity()) + aft.getLmTorrs().get(index).getTitle(), aft.getLmTorrs().get(index).getSize());
                System.out.println(port);
                System.out.println("https://leaguehd.com/download.php?https=1&id=" + aft.getLmTorrs().get(index).getId());
                System.out.println(qbCookie);
                System.out.println(savepath);
                System.out.println(lmCookie);
                count++;
                findNum++;
            }
        }
        return findNum;
    }

    public static int findNewTorrentAndDown(int num, LmPage bef, LmPage aft, int sleepTime, DateFormat simpleDateFormat, String port, String savepath, String lmCookie, String qbCookie) {
        int findNum = 0;
        for (int index = 0, count = 0; index < aft.getTotalNum(); index++) {
            if ((!bef.getLmTorrs().get(index - count).getId().equals(aft.getLmTorrs().get(index).getId())) && ((new Date().getTime() - aft.getLmTorrs().get(index).getTime().getTime()) < (sleepTime * 1.5 * 1000))) {
                System.out.println("[柠檬]" + "[" + simpleDateFormat.format(new Date()) + "]" + "第" + num + "次查询,新种\t" + (StringUtils.isBlank(aft.getLmTorrs().get(index).getActivity()) ? "" : aft.getLmTorrs().get(index).getActivity()) + aft.getLmTorrs().get(index).getTitle() + "\t" + aft.getLmTorrs().get(index).getSize() + "\t" + "https://leaguehd.com/details.php?id=" + aft.getLmTorrs().get(index).getId() + "&hit=1\t" + "https://leaguehd.com/download.php?https=1&id=" + aft.getLmTorrs().get(index).getId());
                DownUtils.addTor(port, "https://leaguehd.com/download.php?https=1&id=" + aft.getLmTorrs().get(index).getId(), qbCookie, savepath, lmCookie);
                System.out.println(port);
                System.out.println("https://leaguehd.com/download.php?https=1&id=" + aft.getLmTorrs().get(index).getId());
                System.out.println(qbCookie);
                System.out.println(savepath);
                System.out.println(lmCookie);
                count++;
                findNum++;
            }
        }
        return findNum;
    }

}
