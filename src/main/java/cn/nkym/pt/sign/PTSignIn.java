package cn.nkym.pt.sign;

import cn.nkym.pt.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

@Slf4j
public class PTSignIn {

    /**
     * 修改signTime.properties内的最后批量签到时间为当前时间
     */
    public void setTime() {
        File file = new File("signTime.properties");
        Properties properties = new Properties();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            properties.load(new FileInputStream(file));
            String time = simpleDateFormat.format(new Date());
            properties.setProperty("pt.lastsign", time);
            FileOutputStream oFile = new FileOutputStream("signTime.properties");//true表示追加打开
            properties.store(oFile, "SignTime setting");
            oFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("设置PT站点最后批量签到时间出错", e);
        }
    }

    /**
     * 判断是否进行批量签到
     */
    public void goSign() {
        try {
//            InputStream inputStream = PTSignIn.class.getClassLoader().getResourceAsStream("pt.properties");
            FileInputStream inputStream = new FileInputStream("pt.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String signFlag = properties.getProperty("ptSignFlag");
            if ("1".equals(signFlag)) {
                sign();
                setTime();
            } else {
                System.out.println("未开启自动签到功能,跳过批量签到");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("批量签到出错", e);
        }
    }

    /**
     * 读取配置文件，进行批量签到
     */
    public void sign() {
        InputStream userInputStream = null;
        InputStream ptInputStream = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            ptInputStream = PTSignIn.class.getClassLoader().getResourceAsStream("pt.properties");
//            ptInputStream = new FileInputStream("pt.properties");
            ptInputStream = new FileInputStream("signTime.properties");
            Properties ptProperties = new Properties();
            ptProperties.load(ptInputStream);
            String timeStr = ptProperties.getProperty("pt.lastsign");
            Date time = simpleDateFormat.parse(timeStr);
            Calendar lastTime = Calendar.getInstance();
            lastTime.setTime(time);
            Calendar today = Calendar.getInstance();
            today.setTime(new Date());
            if ((lastTime.get(Calendar.YEAR) == today.get(Calendar.YEAR) && lastTime.get(Calendar.MONTH) == today.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) > lastTime.get(Calendar.DAY_OF_MONTH)) || (new Date().getTime() - time.getTime()) / (60 * 60 * 24 * 1000) > 1) {
                String signCookie;
                String signUri;

                userInputStream = new FileInputStream("user.properties");
                Properties userProperties = new Properties();
                userProperties.load(userInputStream);

                signCookie = userProperties.getProperty("btschool.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("btschool.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "BTSCHOOL");
                }

                signCookie = userProperties.getProperty("pthome.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("pthome.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "PTHOME");
                }

                signCookie = userProperties.getProperty("hddolby.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("hddolby.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "HDDOLBY");
                }

                signCookie = userProperties.getProperty("leaguehd.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("leaguehd.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "LEAGUEHD");
                }

                signCookie = userProperties.getProperty("hdcity.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("hdcity.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "HDCITY");
                }

                signCookie = userProperties.getProperty("ptsbao.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("ptsbao.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "PTSBAO");
                }

                signCookie = userProperties.getProperty("soulvoice.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("soulvoice.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "SOULVOICE");
                }

                signCookie = userProperties.getProperty("haidan.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("haidan.sign.uri1");
                    SignUtils.signIn(signCookie, signUri, "HAIDAN");
                    signUri = userProperties.getProperty("haidan.sign.uri2");
                    SignUtils.signIn(signCookie, signUri, "HAIDAN");
                }

                signCookie = userProperties.getProperty("hdatmos.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("hdatmos.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "HDATMOS");
                }

                signCookie = userProperties.getProperty("hdfans.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("hdfans.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "HDFANS");
                }

                signCookie = userProperties.getProperty("pttime.cookie");
                if (StringUtils.isNotBlank(signCookie)) {
                    signUri = userProperties.getProperty("pttime.sign.uri");
                    SignUtils.signIn(signCookie, signUri, "PTTIME");
                }

                //cust.sign.uri1
                //cust.sign.cookie1
                int custIndex = 1;
                while(StringUtils.isNotBlank(signCookie = userProperties.getProperty("cust.sign.cookie" + custIndex))){
                    if (StringUtils.isNotBlank(signCookie)) {
                        signUri = userProperties.getProperty("cust.sign.uri" + custIndex);
                        SignUtils.signIn(signCookie, signUri, "用户自定义签到站点" + custIndex + "\t" + userProperties.getProperty("cust.sign.name" + custIndex));
                        custIndex++;
                    }
                }
                System.out.println("PT站点批量签到完成");

            } else {
                System.out.println("今天已经进行过PT签到了，跳过");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("批量签到出错", e);
        } finally {
            try {
                if (userInputStream != null) {
                    userInputStream.close();
                }
                if (ptInputStream != null) {
                    ptInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("批量签到出错", e);
            }
        }
    }
}
