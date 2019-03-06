package file;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.liang.common.MyTools;
import com.liang.common.file.FileHandler;
import com.liang.common.file.TxtCsvFile;
import com.liang.common.file.XlsFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2017/2/14 17:51.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class FileTest {

    public static void main(String[] args) {
//        testTxt();
//        try {
//            testXls();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
        testCache();
    }

    public static void testCache() {
        Cache<String, String> cache = CacheBuilder.newBuilder().build();
        cache.put("test", "bihao");
        cache.getIfPresent("test");
    }

    private static void testTxt() {
        FileHandler txtCsvFile = new TxtCsvFile();
        File file = new File("D:\\test.csv");
        System.out.println(txtCsvFile.readFile(file, ",", Xxx.class, false));

        List<List<Object>> listList = new ArrayList<>();
        List<Object> list = new ArrayList<>();
        list.add("fadf");
        list.add(12);
        List<Object> list2 = new ArrayList<>();
        list2.add("rfafasdf");
        list2.add(3221);
        listList.add(list);
        listList.add(list2);
        txtCsvFile.writeFile(file, listList, ",");
    }

    public static void testXls() throws IllegalAccessException {
        FileHandler fileHandler = new XlsFile();
        File file = new File("D:\\员工信息.xls");
        System.out.println(fileHandler.readFile(file, ",", Yyy.class, false));
        file = new File("D:\\testttttt.xls");
        List<List<Object>> listList = new ArrayList<>();
        List<Xxx> xxxList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Xxx xxx = new Xxx("test" + i, i);
            xxxList.add(xxx);
        }
        listList = MyTools.beanListToList(xxxList);
        fileHandler.writeFile(file, listList, ",");
    }

    public static class Xxx {
        private String v1;
        private int v2;

        public Xxx() {
        }

        public Xxx(String v1, int v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public String getV1() {
            return v1;
        }

        public void setV1(String v1) {
            this.v1 = v1;
        }

        public int getV2() {
            return v2;
        }

        public void setV2(int v2) {
            this.v2 = v2;
        }

        @Override
        public String toString() {
            return "Xxx{" +
                    "v1='" + v1 + '\'' +
                    ", v2='" + v2 + '\'' +
                    '}';
        }
    }

    public static class Yyy {
        private int v1;
        private String v2;
        private String v3;
        private String v4;
        private String v5;
        private String v6;
        private String v7;

        public int getV1() {
            return v1;
        }

        public void setV1(int v1) {
            this.v1 = v1;
        }

        public String getV2() {
            return v2;
        }

        public void setV2(String v2) {
            this.v2 = v2;
        }

        public String getV3() {
            return v3;
        }

        public void setV3(String v3) {
            this.v3 = v3;
        }

        public String getV4() {
            return v4;
        }

        public void setV4(String v4) {
            this.v4 = v4;
        }

        public String getV5() {
            return v5;
        }

        public void setV5(String v5) {
            this.v5 = v5;
        }

        public String getV6() {
            return v6;
        }

        public void setV6(String v6) {
            this.v6 = v6;
        }

        public String getV7() {
            return v7;
        }

        public void setV7(String v7) {
            this.v7 = v7;
        }

        @Override
        public String toString() {
            return "Yyy{" +
                    "v1=" + v1 +
                    ", v2='" + v2 + '\'' +
                    ", v3='" + v3 + '\'' +
                    ", v4='" + v4 + '\'' +
                    ", v5='" + v5 + '\'' +
                    ", v6='" + v6 + '\'' +
                    ", v7='" + v7 + '\'' +
                    '}';
        }
    }
}
