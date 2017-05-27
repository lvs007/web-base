package liang.common.test;

import liang.common.MyTools;
import liang.common.file.FileFactory;
import liang.common.file.ListenerFileUpload;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by liangzhiyan on 2017/5/11.
 */
public class FileTest {

    private static class Listener extends ListenerFileUpload {

        public Listener(File file) {
            super(file);
        }

        @Override
        public void deal(int offset, List<Map<String, Object>> lines) {
            System.out.println("nihao");
            for (Map map : lines) {
                System.out.println(offset + " | " + map);
            }
        }
    }

    public static void test() {
        File file = new File("/Users/liangzhiyan/Downloads/test.txt");
        File file_filter = new File("/Users/liangzhiyan/Downloads/test-filter.txt");
        File file_filter222 = new File("/Users/liangzhiyan/Downloads/test-filter222.txt");
        try {
            List<String> list = FileUtils.readLines(file, "utf-8");
            List<String> list_filter = FileUtils.readLines(file_filter, "utf-8");
            List<String> list_filter222 = FileUtils.readLines(file_filter222, "utf-8");

            Set<String> set = MyTools.splitToSet(list.get(0), ",");
            Set<String> set_filter = MyTools.splitToSet(list_filter.get(0), ",");
            Set<String> set_filter222 = new HashSet<>(list_filter222);

            System.out.println("set size = " + set.size() + " | filter size = " + set_filter.size());
            Collection collection = CollectionUtils.intersection(set, set_filter);
            System.out.println("colletion size :"+collection.size());
            int i = 0;
            List<String> unSame = new ArrayList<>();
            for (String userId : set){
                if (set_filter.contains(userId)){
                    ++i;
                }else {
                    unSame.add(userId);
                }
            }
            System.out.println("contains = "+i);
            System.out.println("same size = "+CollectionUtils.intersection(set, set_filter222).size());
            System.out.println(unSame);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test2(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017,5,23,0,0);
//        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (minute > 0) {
            hour = hour + 1;
            if (hour > 23) {
                calendar.add(Calendar.HOUR_OF_DAY,1);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String newReason = "";
        newReason += year + "年" + month + "月" + day + "日 " + (hour>9?hour:("0"+hour)) + ":00可正常接单";
        System.out.println("year："+year+",month:"+month+",day:"+day+",hour:"+hour+",minute:"+minute);
        System.out.println(""+newReason);
    }

    public static void main(String[] args) {
        System.out.println("hello");
//        Listener listener = new Listener(new File("/Users/liangzhiyan/Downloads/Workbook1.xlsx"),"");
//        Listener listener = new Listener(new File("/Users/liangzhiyan/Downloads/test.txt"));
//        test();
        test2();
    }
}
