package com.asule.redenvelopesystem;

import com.asule.redenvelopesystem.domain.User;
import com.asule.redenvelopesystem.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/22 13:28
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@SpringBootTest
@Slf4j
public class HighConcurrencyTest {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://rm-cn-uqm36mm7r0002jmo.rwlb.rds.aliyuncs.com:3306/redpacket";
    static final String USER = "root";
    static final String PASSWORD = "Leilei@1";

    /**
     * 批量添加数据库用户
     */
    @Test
    public void addUser(){
        Connection conn = null;
        PreparedStatement ps = null;
        FileWriter fw1 = null;
        try {
            // 1.注册JDBC驱动
            Class.forName(JDBC_DRIVER);
            // 2.打开连接
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            // 3.关闭自动提交
            conn.setAutoCommit(false);
            // 4.构建SQL语句
            String sql = "INSERT INTO user(phone, password) VALUES(?, ?)";
            // 5.创建 PreparedStatement 对象
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            // 6.设置参数
            String path = System.getProperty("user.dir") + "/script/redPacketUser.txt";
            File file = new File(path);
            fw1 = new FileWriter(file);
            for (int i = 0; i < 100; i++) {
                BigInteger start = new BigInteger("15000000000");
                BigInteger phone = start.add(new BigInteger(String.valueOf(i)));
                ps.setString(1, String.valueOf(phone));
                ps.setString(2, "123456");
                // 7.添加到批量操作
                //输出插入的文本文件
                fw1.write(phone + ",");
                fw1.write("123456\n");
                ps.addBatch();
            }
            // 8.执行批量操作
            ps.executeBatch();
            // 10.提交事务
            conn.commit();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 11.关闭连接
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (fw1 != null)
                    fw1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private RestTemplate restTemplate = new RestTemplate();

    //2.用户登陆
    private static final String URL = "http://localhost:8888/user/doLogin";


    /**
     * 抢红包自动测试编写
     */
    @Test
    public void BFSTest() {
        FileWriter fw1 = null;
        FileWriter fw2 = null;
        try {
            String path = System.getProperty("user.dir") + "/script/redPacketUser.txt";
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            List<String> strList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                strList.add(line);
            }
            String[] strArray = strList.stream().toArray(String[]::new);


            //File file1 = new File(System.getProperty("user.dir") + "/script/result.txt");
            //fw1 = new FileWriter(file1);
            File file2 = new File(System.getProperty("user.dir") + "/script/grabThread.txt");
            fw2 = new FileWriter(file2);
            //1.读取用户信息
            for (int i = 0; i < 100; i++) {
                String[] splitStr = strArray[i].split(",");
                User user = new User();
                user.setPhone(splitStr[0]);
                user.setPassword(splitStr[1]);


                // 请求头设置,x-www-form-urlencoded格式的数据
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                //提交参数设置
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("phone", user.getPhone());
                map.add("password", user.getPassword());
                // 组装请求体
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

                // 发送post请求，并打印结果，以String类型接收响应结果JSON字符串
                //第一种方法
                CommonResult result = restTemplate.postForObject(URL, request, CommonResult.class);
                System.out.println(result);

                //第二种方法
                //ResponseEntity<CommonResult> responseEntity = restTemplate.postForEntity(URL, request , CommonResult.class);
                //System.out.println(responseEntity);


                //2.获取返回结果
                String userTicket = result.getData().toString();
                log.info(user.getPhone() + "," + user.getPassword() + "," + userTicket);

                //3.写入登陆过的用户信息
                //fw1.write(user.getPhone() + ",");
                //fw1.write(user.getPassword() + ",");
                //fw1.write(userTicket + "\n");
                //4.写入将要并发抢红包的线程
                fw2.write("r-5555,");
                fw2.write(userTicket);
                fw2.write(",2" + "\n");
            }
            log.info("***********************登陆成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw1 != null)
                    fw1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fw2 != null)
                    fw2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 抢代金券红包自动测试编写
     */
    @Test
    public void BFSTest2() {
        FileWriter fw1 = null;
        FileWriter fw2 = null;
        try {
            String path = System.getProperty("user.dir") + "/script/redPacketUser.txt";
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            List<String> strList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                strList.add(line);
            }
            String[] strArray = strList.stream().toArray(String[]::new);


            //File file1 = new File(System.getProperty("user.dir") + "/script/result.txt");
            //fw1 = new FileWriter(file1);
            File file2 = new File(System.getProperty("user.dir") + "/script/grabCouponThread.txt");
            fw2 = new FileWriter(file2);
            //1.读取用户信息
            for (int i = 0; i < 100; i++) {
                String[] splitStr = strArray[i].split(",");
                User user = new User();
                user.setPhone(splitStr[0]);
                user.setPassword(splitStr[1]);


                // 请求头设置,x-www-form-urlencoded格式的数据
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                //提交参数设置
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("phone", user.getPhone());
                map.add("password", user.getPassword());
                // 组装请求体
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

                // 发送post请求，并打印结果，以String类型接收响应结果JSON字符串
                //第一种方法
                CommonResult result = restTemplate.postForObject(URL, request, CommonResult.class);
                System.out.println(result);

                //第二种方法
                //ResponseEntity<CommonResult> responseEntity = restTemplate.postForEntity(URL, request , CommonResult.class);
                //System.out.println(responseEntity);


                //2.获取返回结果
                String userTicket = result.getData().toString();
                log.info(user.getPhone() + "," + user.getPassword() + "," + userTicket);

                //3.写入登陆过的用户信息
                //fw1.write(user.getPhone() + ",");
                //fw1.write(user.getPassword() + ",");
                //fw1.write(userTicket + "\n");
                //4.写入将要并发抢红包的线程
                fw2.write("j-3333,");
                fw2.write(userTicket+"\n");
            }
            log.info("***********************登陆成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw1 != null)
                    fw1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fw2 != null)
                    fw2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
