package ZhiJianHu.Homework2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:目前有100名囚犯，每个囚犯的编号是1-200之间的随机数。
 * 现在要求依次随机生成100名囚犯的编号（要求这些囚犯的编号是不能重复的），
 * 然后让他们依次站成一排。(注：位置是从1开始计数的)，接下来，
 * 国王命令手下先干掉全部奇数位置处的人。剩下的人，又从新按位置1开始，
 * 再次干掉全部奇数位置处的人，依此类推，直到最后剩下一个人为止，
 * 剩下的这个人为幸存者。
 */
public class Test {
    public static void main(String[] args) {
        evil evil = new evil();
        List<once> list = evil.create();
        while (list.size()>1){
            List<once> is=new ArrayList<>();
            //System.out.println(list);
            System.out.println(list.get(1));
            for (int i=1;i<list.size();i+=2) {
                //因为索引是从0开始的，所以索引0==位置 1
                //这里把索引1==编号 位置2  这里把索引1给幸存者集合存储

                is.add(list.get(i));
            }
                //把剩余的人交给集合
            list=is;
            System.out.println(is);
        }
        System.out.println("最后一名"+list.get(0));


    }
}
