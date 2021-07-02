package com.liang.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liang.common.http.api.ApiResponse;
import com.liang.common.http.api.BaseApi;
import com.liang.common.http.api.exception.ApiException;
import com.liang.common.http.api.exception.HttpException;
import com.liang.common.http.api.exception.InternalException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import org.joda.time.DateTime;

/**
 * Created by liangzhiyan on 2017/3/7.
 */
public class TokenTest {

  /**
   * 6      * 时间戳转换成日期格式字符串 7      * @param seconds 精确到秒的字符串 8      * @param formatStr 9      *
   *
   * @return 10
   */
  public static String timeStamp2Date(long time) {
    String format = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(new Date(time));
  }

  public static class Entity {

    private String address;
    private long date_created;
    private String name;
    private String time;

    public String getAddress() {
      return address;
    }

    public Entity setAddress(String address) {
      this.address = address;
      return this;
    }

    public long getDate_created() {
      return date_created;
    }

    public Entity setDate_created(long date_created) {
      this.date_created = date_created;
      return this;
    }

    public String getName() {
      return name;
    }

    public Entity setName(String name) {
      this.name = name;
      return this;
    }

    @Override
    public String toString() {
      return "Entity{" +
          "address='" + address + '\'' +
          ", name='" + name + '\'' +
          ", time='" + timeStamp2Date(date_created) + '\'' +
          '}';
    }
  }

  public static class HttpTest extends BaseApi {

    public void getList()
        throws InternalException, ApiException, HttpException, IOException, URISyntaxException {
      HttpClient httpClient = getHttpClient();
//            httpClient.setCookie("attempt.do","JSESSIONID","B8FE03DE4D46BE6385E8D445BB73E371")
//                    .setCookie("attempt.do","SID","10q8ksgel568fjbqgh924ua6l3")
//                    .setCookie("attempt.do","ssoid","3bb7f8618f*844feae85774222923942")
//                    .setCookie("attempt.do","ssoid.sig","PPuuNrWzOYoI-bteZbFYlrg86TM")
//                    .setCookie("attempt.do","dacs.sid","5FUgEXpusJRUYt-o74isV1XlndBx3vj9")
//                    .setCookie("attempt.do","dacs.sid.sig","K2W5I6z3jUDkEvsD4qr2AcNvg3Q")
//                    .setCookie("attempt.do","userId","2069920")
//                    .setCookie("attempt.do","u","1833463")
//                    .setCookie("attempt.do","userId.sig","NWIoO0L531xO-QPcwiOj0ernO3A")
//                    .setCookie("attempt.do","statDs","BANMA_MESSAGE_DB_CONNECT_URL");
      List<NameValuePair> nameValuePairList = new ArrayList<>();
      NameValuePair nameValuePair = new NameValuePair("count", "true");
      NameValuePair nameValuePair2 = new NameValuePair("limit", "20");
      NameValuePair nameValuePair3 = new NameValuePair("confirm", "0");
      NameValuePair nameValuePair4 = new NameValuePair("start", "0");
      NameValuePair nameValuePair5 = new NameValuePair("verified-only", "true");
      NameValuePair nameValuePair6 = new NameValuePair("open-source-only", "false");
      NameValuePair nameValuePair7 = new NameValuePair("sort", "-date_created");
      nameValuePairList.add(nameValuePair);
      nameValuePairList.add(nameValuePair2);
      nameValuePairList.add(nameValuePair3);
      nameValuePairList.add(nameValuePair4);
      nameValuePairList.add(nameValuePair5);
      nameValuePairList.add(nameValuePair6);
      nameValuePairList.add(nameValuePair7);
      int limit = 40;
      int start = 0;
      for (int i = 0; i < 10000; i++) {
        String result = httpClient.httpGet(
            "https://apilist.tronscan.org/api/contracts?count=true&limit=40&confirm=0&start="
                + start + "&verified-only=true&open-source-only=false&sort=-date_created");
        JSONObject jsonObject = JSON.parseObject(result);

        ApiResponse response = new ApiResponse(jsonObject);
        List<Entity> entityList = response.getDataArray("data", Entity.class);
        for (Entity entity : entityList) {
          if (entity.date_created < DateTime.parse("2021-02-20").getMillis()) {
            return;
          }
          if (entity.name.toLowerCase().contains("pool") &&
              entity.date_created > DateTime.parse("2021-02-20").getMillis()) {
            //time>2020-11-06
            System.out.println(entity);
          }
        }
        start += limit;
      }
//      String result = httpClient.httpGet(
//          "https://apilist.tronscan.org/api/contracts?count=true&limit=40&confirm=0&start=0&verified-only=true&open-source-only=false&sort=-date_created");
//      JSONObject jsonObject = JSON.parseObject(result);
//
//      ApiResponse response = new ApiResponse(jsonObject);
//      List<Entity> entityList = response.getDataArray("data", Entity.class);
//      for (Entity entity : entityList) {
//        if (entity.name.contains("TrxPool")) {
//          System.out.println(entity);
//        }
//      }
//      System.out.println("返回的结果：" + response.getJsonObject());
    }

    @Override
    protected String getApiHost() {
      return "https://apilist.tronscan.org/api/contracts";
    }

    @Override
    protected String getSignKey() {
      return null;
    }
  }

  public static void main(String[] args) {
    HttpTest httpTest = new HttpTest();
    try {
//      httpTest.getList();
//      System.out.println(DateTime.parse("2020-11-06").getMillis());
//      testttttt();
//      TestSync2.test();
    } catch (Exception e) {
      e.printStackTrace();
    }
    //https://pinggo.luxe/Home/Index/save?0.5564103586662303
    //参数：
    //address: TJvJeiyB97Y7pmbwjNfdF11BW7mCaiAx2G
    //code: 3ms
    //dre: C9F4

    //https://pinggo.luxe/Home/Index/Code/?0.6850094835891924

    Node n9 = new Node(6, null);
    Node n8 = new Node(5, n9);
    Node n7 = new Node(5, n8);
    Node n6 = new Node(4, n7);
    Node n5 = new Node(3, n6);
    Node n4 = new Node(2, n5);
    Node n3 = new Node(1, n4);
    Node n2 = new Node(1, n3);
    Node n1 = new Node(1, n2);
    System.out.println(n1);
    System.out.println(removeList(n1));
  }

  public static class Node{
    private int value;
    private Node next;

    public Node(int value, Node next) {
      this.value = value;
      this.next = next;
    }

    public int getValue(){
      return value;
    }
    public void setValue(int value){
      this.value = value;
    }
    public Node getNext(){
      return next;
    }
    public void setNext(Node next){
      this.next = next;
    }

    @Override
    public String toString() {
      return "Node{" +
          "value=" + value +
          ", next=" + next +
          '}';
    }
  }

  public static Node removeList(Node node){
    if(node == null){
      return null;
    }
    int compare = node.getValue();
    Node tmp = node;
    while(tmp.getNext() != null){
      Node n = tmp.getNext();
      if(n.getValue() == compare){
        tmp.setNext(tmp.getNext().getNext());
      }else{
        compare = n.getValue();
        tmp = tmp.getNext();
      }
    }
    return node;
  }

  private static void testttttt() {
    PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>(100,
        (o1, o2) -> {
          return Long.compare(o2, o1);
//          if (o1 > o2) {
//            return -1;
//          } else if (o1 < o2) {
//            return 1;
//          } else {
//            return 0;
//          }
        });

    queue.add(100);
    queue.add(5);
    queue.add(200);
    queue.add(10);
    queue.add(300);

//    System.out.println(queue);
//    System.out.println(queue.peek());
    Iterator iterator = queue.iterator();
    while (queue.size() > 0) {
      System.out.println(queue.poll());
    }
  }

  public static class TestSync2 implements Runnable {

    int b = 100;

    synchronized void m1() throws InterruptedException {
      b = 1000;
      Thread.sleep(500); //6
      System.out.println("b=" + b);
    }

    synchronized void m2() throws InterruptedException {
      System.out.println("m2 b=" + b);
      Thread.sleep(250); //5
      b = 2000;
    }

    public static void test() throws InterruptedException {
      TestSync2 tt = new TestSync2();
      Thread t = new Thread(tt);  //1
      t.start(); //2

      tt.m2(); //3
      System.out.println("main thread b=" + tt.b); //4
    }

    @Override
    public void run() {
      try {
        m1();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
