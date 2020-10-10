package cn.nkym.pt.notice;


import cn.nkym.pt.pojo.MtPage;
import cn.nkym.pt.utils.DownUtils;
import cn.nkym.pt.utils.LmUtils;
import cn.nkym.pt.utils.MtUtils;
import cn.nkym.pt.window.WindowInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Slf4j
public class MtNotice {

    private static final String FORMATPATTERN = "yyyy-MM-dd HH:mm:ss";

    public static List<String> RULE = null;

    public void mtnotice() {

        //初始化properties对象
        Properties userProperties = new Properties();
        Properties ptProperties = new Properties();
        Properties downProperties = new Properties();

        /*//读取配置文件
        File ptFile = null;
        FileInputStream ptInputStream = null;
        try {
            ptFile = new File("pt.properties");
            ptInputStream = new FileInputStream(ptFile);
        } catch (FileNotFoundException e) {
            System.out.println("配置文件未找到,请查看对应的目录下是否包含配置文件:\t" + ptFile.getAbsolutePath());
        }

        try {
            properties.load(ptInputStream);
        } catch (IOException e) {
            System.out.println("配置文件载入出现异常");
        }*/

//        InputStream inputStream = MtNotice.class.getClassLoader().getResourceAsStream("pt.properties");
        FileInputStream ptInputStream = null;
        FileInputStream userInputStream = null;
        FileInputStream downInputStream = null;
        try {
            ptInputStream = new FileInputStream("pt.properties");
            userInputStream = new FileInputStream("user.properties");
            downInputStream = new FileInputStream("autoDown.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("读取配置文件异常", e);
        }

        try {
            userProperties.load(userInputStream);
            ptProperties.load(ptInputStream);
            downProperties.load(downInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("载入配置文件异常", e);
        }

        if (!"1".equals(ptProperties.getProperty("mtsignFlag"))){
            return;
        }

        //是否开启win10通知
        String winInfo = downProperties.getProperty("mt.winInfo");

        //是否开启自动下载免费种
        String down = downProperties.getProperty("mt.down");

        //自动下载种子的规则
        String rule = downProperties.getProperty("mt.down.rule");

        //qb的端口
        String port = downProperties.getProperty("port");

        //qb的用户名
        String username = downProperties.getProperty("username");

        //qb的密码
        String password = downProperties.getProperty("password");

        //读取要爬取的网址链接
        String uri = (String) userProperties.get("mt.uri");

        //种子的保存路径
        String savePath = downProperties.getProperty("savePath");

        //qb的cookie
        String qbCookie = "";

        if (StringUtils.isBlank(qbCookie)) {
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                System.out.println("种子下载所需要配置的qBittorrent用户名或密码为空，关闭自动下载免费种");
                down = "0";
            } else if (StringUtils.isBlank(port)) {
                System.out.println("种子下载所需要配置的qBittorrent端口为空，关闭自动下载免费种");
                down = "0";
            } else if (StringUtils.isBlank(savePath)) {
                System.out.println("种子下载所需要配置的下载目录为空，关闭自动下载免费种");
                down = "0";
            } else if ("1".equals(down)) {
                qbCookie = DownUtils.getCookie(port, username, password);
                if (StringUtils.isBlank(qbCookie)) {
                    System.out.println("请检查qBittorrent的用户名和密码,无法获取cookie，关闭自动下载免费种");
                    down = "0";
                } else {
                    DownUtils.COOKIE = qbCookie;
                }
            }
        }

        if (StringUtils.isNotBlank(rule)){
            RULE = Arrays.asList(rule.split("\\*"));
        }

        //如果网址为空,则设置为默认值
        if (StringUtils.isBlank(uri)){
            uri = "https://pt.m-team.cc/torrents.php?sort=4&type=desc";
        }

        //设置cookie,如果读取的数据为空,则打印提醒并退出程序
        String cookie = (String) userProperties.get("mt.cookie");
        if (StringUtils.isBlank(cookie)){
            System.out.println("馒头新种发布监控,读取配置文件中cookie为空,请检查配置文件");
            return;
        }

        //设置数据交互超时时间,如果读取的数据为空,则设置默认值
        Object socketTimeoutObj = ptProperties.get("notice.socketTimeout");
        int socketTimeout = (socketTimeoutObj == null || StringUtils.isBlank(String.valueOf(socketTimeoutObj))) ? 2000 : Integer.valueOf(String.valueOf(socketTimeoutObj));

        //设置连接超时时间,如果读取的数据为空,则设置默认值
        Object connectTimeoutObj = ptProperties.get("notice.connectTimeout");
        int connectTimeout = (connectTimeoutObj == null || StringUtils.isBlank(String.valueOf(connectTimeoutObj))) ? 2000 : Integer.valueOf(String.valueOf(connectTimeoutObj));

        //设置休眠间隔时间,如果读取的数据为空,则设置默认值
        Object sleepTimeObj = ptProperties.get("notice.sleepTime");
        int sleepTime = (sleepTimeObj == null || StringUtils.isBlank(String.valueOf(sleepTimeObj))) ? 45 : Integer.valueOf(String.valueOf(sleepTimeObj));

        //创建刷新前的页面对象
        MtPage mtPageBef = new MtPage();

        //创建刷新后的页面对象
        MtPage mtPageAft = new MtPage();

        DateFormat simpleDateFormat = new SimpleDateFormat(FORMATPATTERN);
        System.out.println("[馒头]" + "[" + simpleDateFormat.format(new Date()) + "]" + "开始初始化初始列表");

        //读取配置文件,使用配置信息通过工具类获取页面信息
        mtPageBef = MtUtils.getMtPage(uri, socketTimeout, connectTimeout, cookie);

        //如果获取的页面信息为空,打印并发送通知
        if (mtPageBef == null) {
            WindowInfo.showInfo("[馒头]初始化失败", "查询得到的初始列表为空");
            System.out.println("[馒头]" + "[" + simpleDateFormat.format(new Date()) + "]" + "初始化初始列表失败");
//            System.exit(0);
            return;
        } else {
            System.out.println("[馒头]" + "[" + simpleDateFormat.format(new Date()) + "]" + "初始化初始列表完成");
        }

        //爬取页面的次数
        int num = 1;

        //运行程序开始找到的新种数量
        int totalFindNum = 0;

        while (true) {

            //本次爬取页面找到的新种数量
            int findNum = 0;

            //使用配置信息通过工具类获取页面信息
            MtPage newPage = MtUtils.getMtPage(uri, socketTimeout, connectTimeout, cookie);

            //如果获取的页面信息为空,打印信息
            if (newPage == null) {
//                WindowInfo.showInfo("出现异常", "查询到的列表ptListAft为空");
                System.out.println("[馒头]" + "[" + simpleDateFormat.format(new Date()) + "]" + "第" + num + "次查询\t" + "出现异常\t查询到的列表ptListAft为空");
            } else {
                //获取的页面信息不为空,将返回值赋值给mtPageAft
                mtPageAft = newPage;

                //获取本次爬取页面找到的新种子数量
//                findNum = MtUtils.findNewTorrent(num, mtPageBef, mtPageAft, sleepTime, simpleDateFormat);

                //获取本次爬取页面找到的新种子数量
                if ("1".equals(winInfo) && !"1".equals(down)) {
                    findNum = MtUtils.findNewTorrent(num, mtPageBef, mtPageAft, sleepTime, simpleDateFormat);
                } else if ("1".equals(winInfo) && "1".equals(down)) {
                    findNum = MtUtils.findNewTorrentAndDownAndInfo(num, mtPageBef, mtPageAft, sleepTime, simpleDateFormat, port, savePath, cookie);
                } else if (!"1".equals(winInfo) && "1".equals(down)) {
                    findNum = MtUtils.findNewTorrentAndDown(num, mtPageBef, mtPageAft, sleepTime, simpleDateFormat, port, savePath, cookie);
                } else {
                    findNum = MtUtils.findNewTorrentWithOut(num, mtPageBef, mtPageAft, sleepTime, simpleDateFormat);
                }
            }

            //如果本次爬取页面没有找到新种子,打印信息到控制台
            if (findNum == 0) {
                System.out.println("[馒头]" + "[" + simpleDateFormat.format(new Date()) + "]" + "第" + num + "次查询,无新种\t已查询到新种" + totalFindNum + "个");
            } else {

                //本次爬取页面找到新种子,累加找到的新种子数量,并将后一次爬取的页面数据赋值给前一次
                totalFindNum += findNum;
                mtPageBef = mtPageAft;
            }

            //爬取次数+1
            num++;

            //打印休眠信息并进入休眠
            System.out.println("[馒头]" + "[" + simpleDateFormat.format(new Date()) + "]" + "休眠" + sleepTime + "秒");
            try {
                Thread.sleep(1000 * sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("线程休眠异常", e);
            }
        }
    }
}
