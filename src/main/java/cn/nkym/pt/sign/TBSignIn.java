package cn.nkym.pt.sign;

import cn.nkym.pt.pojo.TieBa;
import cn.nkym.pt.utils.TBUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Slf4j
public class TBSignIn {

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
            properties.setProperty("tb.lastsign", time);
            FileOutputStream oFile = new FileOutputStream("signTime.properties");//true表示追加打开
            properties.store(oFile, "signTime setting");
            oFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("设置贴吧最后批量签到时间出错", e);
        }
    }

    /**
     * 读取配置文件，判断是否进行贴吧签到
     */
    public void sign(){
        try {
            Properties properties = new Properties();
            FileInputStream inputStream = new FileInputStream("tb.properties");
            properties.load(inputStream);
            String signFlag = properties.getProperty("tb.sign");
            if ("1".equals(signFlag)){

                FileInputStream signTimeInputStream = new FileInputStream("signTime.properties");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Properties signTimeProperties = new Properties();
                signTimeProperties.load(signTimeInputStream);
                String lastsign = signTimeProperties.getProperty("tb.lastsign");
                Date time = null;
                try {
                    time = simpleDateFormat.parse(lastsign);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar lastTime = Calendar.getInstance();
                lastTime.setTime(time);
                Calendar today = Calendar.getInstance();
                today.setTime(new Date());
                if (!((lastTime.get(Calendar.YEAR) == today.get(Calendar.YEAR) && lastTime.get(Calendar.MONTH) == today.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) > lastTime.get(Calendar.DAY_OF_MONTH))) || (new Date().getTime() - time.getTime()) / (60 * 60 * 24 * 1000) < 1) {
                    System.out.println("今天已经进行过贴吧签到了，跳过");
                    return;
                }

                String cookie = properties.getProperty("bd.cookie");

                FileInputStream tbsFileInputStream = new FileInputStream("tbs.properties");
                Properties tbsProperties = new Properties();
                tbsProperties.load(tbsFileInputStream);
                if (StringUtils.isNotBlank(tbsProperties.getProperty("tbName1"))){
                    TBUtils.goBaiDuTaskByProperties(cookie);
                    setTime();
                    return;
                }


                if (StringUtils.isNotBlank(cookie)){
                    List<TieBa> tieBas = TBUtils.getMyLike(cookie);
                    TBUtils.getTbsAndGo(tieBas, cookie);
                    if (tieBas.size() > 0){
                        setTime();
                    }
                } else {
                    System.out.println("未设置百度贴吧cookie，跳过贴吧批量签到");
                }
            } else{
                System.out.println("未开启贴吧批量签到,跳过");
            }
        } catch (IOException e) {
            log.error("载入tb.properties出现异常", e);
            e.printStackTrace();
        }
    }
}
