package ZhiJianHu.Homwork4;

import java.util.ArrayList;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:
 */
public class Test {
    private static ArrayList<goods> gs=new ArrayList<>();
    public static void main(String[] args) {
        gs.add(new goods("水",		       4 ,     	   24    ));
        gs.add(new goods("牛奶"  		  ,8,      	  160       ));
        gs.add(new goods("五粮液	",	 2,    	     4000    ));
        gs.add(new goods("可乐"		  ,6     	  ,108    ));
        gs.add(new goods("茅台"	,	  1,    	      4000    ));

        //按照价格的高低排序
        gs.sort((o1, o2) -> Double.compare(o2.getPrice(),o1.getPrice()));
        System.out.println(gs);
        //先获得价值最高的，然后其次
        is();
    }
    public static void is(){
        int total = 10;
        double allMoney = 0.0;
        for (goods g:gs){
            int kg = g.getKg();
            if(total<=0){
                break;
            }
            if(kg>=total){
                System.out.println(g.getName()+"总共拿了"+total+",总价值"+g.getPrice()*total);
                allMoney+=g.getPrice()*total;
                total-=kg;
            }else{
                System.out.println(g.getName()+"总共拿了"+kg+",总价值"+g.getPrice()*kg);
                allMoney+=g.getPrice()*kg;
                total-=kg;
            }
        }
        System.out.println("总价值"+allMoney);



    }

}
