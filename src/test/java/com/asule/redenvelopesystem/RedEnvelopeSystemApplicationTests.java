package com.asule.redenvelopesystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootTest
class RedEnvelopeSystemApplicationTests {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() throws ParseException {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,24);
        Date after24h = calendar.getTime();
        System.out.println("当前时间：" + now);
        System.out.println("24小时后的时间：" + after24h);

    }

    @Test
    public void FileTest() throws IOException {
        String path = System.getProperty("user.dir") + "/script/test.txt";
        File file = new File(path);
        FileWriter fw = new FileWriter(file);
        fw.write("666");
        fw.close();
    }

    @Test
    public void addList(){
        Integer sum = 0;
        List<Integer> list = redisTemplate.opsForList().range("redPocket:u-666666:list",0,-1);
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        System.out.println(sum);
    }

}
