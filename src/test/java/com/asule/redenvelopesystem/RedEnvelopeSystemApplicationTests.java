package com.asule.redenvelopesystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
class RedEnvelopeSystemApplicationTests {

    @Test
    void contextLoads() throws ParseException {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,24);
        Date after24h = calendar.getTime();
        System.out.println("当前时间：" + now);
        System.out.println("24小时后的时间：" + after24h);

    }

}
