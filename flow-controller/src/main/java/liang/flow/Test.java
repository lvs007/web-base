package liang.flow;

import java.util.Calendar;

/**
 * Created by liangzhiyan on 2017/3/31.
 */
public class Test {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(1491455590043L);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (minute > 0) {
            hour = hour + 1;
        }
        System.out.println(year + "年" + month + "月" + day + "日 " + hour + ":00可正常接单");
    }
}
