package ZhiJianHu.Homework3;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:
 */
public class Test {
     public static void main(String[] args) {
          String info = "10001,张无忌,男,2023-07-22 11:11:12,东湖-黄鹤楼#10002,赵敏,女,2023-07-22 09:11:21,黄鹤楼-归元禅寺#10003,周芷若,女,2023-07-22 04:11:21,东湖#10004,小昭,女,2023-07-22 08:11:21,东湖#10005,灭绝,女,2023-07-22 17:11:21,归元禅寺" ;
          String[] person = info.split("#");
          ArrayList<Student> list = new ArrayList<>();

          for (String p:person){
               String[] split = p.split(",");
                    int s = Integer.parseInt(split[0]);
                    String s1 = split[1];
                    String s2 = split[2];
                    LocalDateTime s3 = LocalDateTime.parse(split[3].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String s4 = split[4];
                    list.add(new Student(s,s1,s2,s3,s4));

          }
          System.out.println(list);
          /*
          - 遍历上面获取的List<Student> 集合，
          统计里面每个景点选择的次数，并输出 景点名称和选择的次数。

          **业务三：**

          * 请用程序计算出哪个景点想去的人数最多，以及哪些人没有选择这个最多人想去的景点。
                     */
          Map<String ,Integer> is=new HashMap<>();
          for (Student s:list){
               String place = s.getPlace();
               is.merge(place,1,Integer::sum);
          }
          System.out.println(is);
          //业务三，找到最多人去的地方,并且看看是谁没有选择
          int a=0;
          String pl=null;
          for (Map.Entry<String,Integer> s:is.entrySet()){
               String key = s.getKey();
               int value = s.getValue();
               if (value>a){
                    a=value;
                    pl=key;
               }
               System.out.println(key+"想去的人有"+value+"人");
          }
          System.out.println(a+"   "+pl);
          for (Student s:list){
               String place = s.getPlace();
               if (a!=is.get(place)){
                    System.out.println(s);
               }
          }
     }
}
