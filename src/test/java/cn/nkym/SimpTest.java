package cn.nkym;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SimpTest {

    @Test
    public void test1() throws IOException {
        int count = 1;
        File file = new File("C:\\F\\java_work\\pt-helper3\\tbs.properties");
        Properties properties = new Properties();
        properties.load(new FileInputStream("C:\\F\\java_work\\pt-helper3\\tbs.properties"));
        while(StringUtils.isNotBlank(properties.getProperty("tbName" + count)) && StringUtils.isNotBlank(properties.getProperty("tbTbs" + count))){
            System.out.println(properties.getProperty("tbName" + count));
            System.out.println(properties.getProperty("tbTbs" + count));
            count++;
        }
    }
}
