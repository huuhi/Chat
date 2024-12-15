package ZhiJianHu.Homework1;

import java.util.*;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:生成双色球
 */
public class Ball {
     private int[] colors;
     private Set<Integer> red=new HashSet<>();
     private int blue;
     Random rd=new Random();

     //生成red
     public void create(){
          while(red.size()<6){
               red.add(rd.nextInt(35)+1);
          }
          ArrayList<Integer> a=new ArrayList<>(red);
          Collections.sort(a);
          blue=rd.nextInt(15)+1;

          a.add(blue);
          System.out.println(a);
     }

     //生成蓝色

}
