package ZhiJianHu.Homework2;

import lombok.Data;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:
 */
@Data
public class once {
    private int idea;//编号
    private int place;//站的位置

    public once(int idea, int place) {
        this.idea = idea;
        this.place = place;
    }
}
