package ZhiJianHu.Homework2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:
 */
public class evil {
    private ArrayList<once> list=new ArrayList<>();//
    Random rd=new Random();


    //第一次编号
    public ArrayList<once> create(){
        for (int i = 1; i <= 100; i++) {
            int idea=rd.nextInt(200)+1;
            if(is(list,idea)){
                i--;
                continue;
            }
            list.add(new once(idea,i));
        }
        return list;
    }
    public  boolean is(List<once> l,int idea){
        for (once a:l){
            if(a.getIdea()==idea){
                return true;
            }
        }
        return false;
    }

}
