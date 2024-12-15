package ZhiJianHu.Homwork4;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:货物
 */
@Data
public class goods {
     private String name;
     private int kg;
     private int money;

     public goods(String name, int kg, int money) {
          this.name = name;
          this.kg = kg;
          this.money = money;
     }
     //获得单价
     public double getPrice(){
          BigDecimal kg=BigDecimal.valueOf(this.kg);
          BigDecimal money=BigDecimal.valueOf(this.money);
          return money.divide(kg,2,BigDecimal.ROUND_HALF_UP).doubleValue();
     }

}
