package liang.common.test;

import liang.common.file.ListenerFileUpload;

import java.io.File;
import java.util.List;
import java.util.Map;

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

    public static void main(String[] args) {
        System.out.println("hello");
//        Listener listener = new Listener(new File("/Users/liangzhiyan/Downloads/Workbook1.xlsx"),"");
        Listener listener = new Listener(new File("/Users/liangzhiyan/Downloads/test.txt"));
    }
}
