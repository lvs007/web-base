package liang.common.test;

import liang.common.MyTools;
import liang.common.file.FileFactory;
import liang.common.file.ListenerFileUpload;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
        File file_filter = new File("/Users/liangzhiyan/Downloads/test-filter222.txt");
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

    public static void test3(){
        File file_filter222 = new File("/Users/liangzhiyan/Downloads/test-filter222.txt");
        try {
            List<String> list_filter222 = FileUtils.readLines(file_filter222, "utf-8");
            Set<String> set_filter222 = MyTools.splitToSet(list_filter222.get(0), ",");

            System.out.println("colletion size :"+set_filter222.size());

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

    public static void transfer(){
        String str = "iVBORw0KGgoAAAANSUhEUgAAARUAAAAUCAYAAACjxanzAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAASzSURBVHhe7ZrNcdwwDIVdQBpwAzn4Zu/67gpSQTpwB25hW3AN20S6SDMOHueBA1IgKUqyNzPGN4NZEgBBin+ipL0LgiAIgiAIgiAIgiAIgiAIbszj4+NvJjPPz88/n56eTiKvp9PpIj5/IDQXiP3K5CGgXiaRvp+NP+OPunD98vvWu8YRUueLV29Lr6Cf1S6/f1uSnIlnh9Bc4PlB1JacHKzNlqtlZFdJgQwju1L7tYTuCequ6FuqViPz4L2OF2yAg/DCbB5ILDB0MhYcFp/IPV0KYGPyEHRhPzw8/JB2XGfjS5kLf/OkgySjQXRX3UhgP5/Pv/QaW2U80E721aKdrGOxaSusO/U96kOsZCDsg96iu4h8SN3vVK1GyrwxWYDFiHYx64JrrdvFtiyuYQ+jeK3+QX+IfupmBHANTAZ74ATJA+AN0leC+ntCt4KRT6ucUtttXmO2Yli79ZN+xSnPLacL1244tnwtdMlwMV1h621aW8B86G0qqA/1YhOmKi9uqzsCxFwjdM94m15wQ249GJwo+eQ0i3dnG11Tbff8WzG8sjqp5dc93YktncB0c6BuVXwsXOiw8Lcc8Xsgbi00ZaBD25lNSH7TyWAE6vLGU9HNjNkM+qi3MQafTD0oyPeEbglPtxeJ96Fxa6HLgpFfS2ck16m25GTwdKDWM86qxzYsRpH0GDITH+WYPRyJj3do6fTjtanW6Sbn+e5F446E7gnpm3tsKEefmoIJ6kEZ5T+bvQtG2rs45fSuQWyLdwGefytGrbd5STffqWAzqXzdBQOhS6LOb8WLDfQEpI9o6mdksQGL4IW05rGhviK9BvE99J0c2tHq8zUc3Z5viU4GgMmBicRswtq/AtTXE7pl1vowWYCNxCwc972STlDovMcNlnXrxwS1eQUxa1+kxR9fuwr/Uf5oND7aKO1ZbPB1/bZ/8ItrQJ96ZT3gy2QGsbYIy25+dAZee4JJdDAAJoJI8VXA2m9F69nZAh+03544lFZZ+GMSwm6v3fpLOr/zELv3haeIXecbZdLjkfXVdNWOF+ThjzywZY4Gmybi89c9ZbXqr/VoNzeX5p1/ZvNRUI83xkfRur5gAu1E+c1HWMtXdzLqawldFmDyiz19DfH8PB0ms05o2Llx5c/R+LV4OlDrW34e1lfTuoFScE3FnVf9Pgv2ZbO/Z/SMtfhMi40GG4pns4h98T8f1OMJzcH/AAaEE/nqvdwaDVjvTnQUutCYLeDmkE8Y3l3MKyu6PKFre8PfrX9N2RbWV9N2UXt9OxN/K1zwenorvmC16p9pl/jml8E92I7FpsKkC8owuYm95QMBC5KDN3wWrScYys5MJg9MGjvBEK8ldGli/Ww51bWo7XUZ/cLBbIH6WqFpiPXVsjoW0rfpbo4+pktiJr4HYjO5AOPLOvUEh5fYefPtbe5722XRmxz7Iv2K4B2PZPv1wJfJTewtHxAumvz40JH6rnHBQDO7CZ0omDRUucCHySYtn1HZ2m7zSEOkncXiVnplMUFru8Xx9R4V8E4l60f95KHtUKF6gdiaX6u0LNpDVUEv7ixoh0hxzfU1OFLMzeCbIxMivZBkNgiCIAiCIAiC4HDu7v4BFnBPul1lYDYAAAAASUVORK5CYII=";
        try {
            System.out.println(new String(Base64.getDecoder().decode(str),"gb2312"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("hello");
//        Listener listener = new Listener(new File("/Users/liangzhiyan/Downloads/Workbook1.xlsx"),"");
//        Listener listener = new Listener(new File("/Users/liangzhiyan/Downloads/test.txt"));
//        test();
//        test2();
//        test3();
//        transfer();
//        test1();
        Map<String,Object> map = new HashedMap();
        map.put(null,null);
        System.out.println(map);
        Map<String,Object> tmap = new Hashtable<>();
        tmap.put("s","");
        System.out.println(tmap);
        try {
            FileTest fileTest = (FileTest) Class.forName("liang.common.test.FileTest").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static class Pppt{
        int name;
    }

    public static void test1(){
        File file = new File("/Users/liangzhiyan/Downloads/test.txt");
        try {
            List<String> list = FileUtils.readLines(file, "utf-8");
            Set<String> set = MyTools.splitToSet(list.get(0), ",");
            System.out.println("set size = " + set.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
