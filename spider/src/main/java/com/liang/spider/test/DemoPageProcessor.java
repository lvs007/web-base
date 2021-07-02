package com.liang.spider.test;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.AtomicLongMap;
import com.liang.common.http.HttpClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class DemoPageProcessor {

  private static String imgPath = "/Users/liangzhiyan/code/img/";

  //https://pinggo.luxe/Home/Index/Code/?0.6850094835891924
  private static void downloadImg(String url, String imgName) throws IOException {

    URL url1 = new URL(url);
    URLConnection uc = url1.openConnection();
    InputStream inputStream = uc.getInputStream();
//    InputStream inputStream = HttpClient.getDefault().httpGetStream("");

    FileOutputStream out = new FileOutputStream(imgPath + imgName);
    int j = 0;
    while ((j = inputStream.read()) != -1) {
      out.write(j);
    }
    inputStream.close();

  }


  public static String executeTess4J(String imgName) {
    String ocrResult = "";
    try {
      ITesseract instance = new Tesseract();
      instance.setDatapath("/usr/local/Cellar/tesseract/4.1.1/share/tessdata");
      instance.setTessVariable("user_defined_dpi", "300");
//      instance.setLanguage("osd");
      File imgDir = new File(imgPath + imgName);
      //long startTime = System.currentTimeMillis();
      ocrResult = instance.doOCR(imgDir);

      System.out.println("ocrResult=" + ocrResult);
    } catch (TesseractException e) {
      e.printStackTrace();
    }
    return ocrResult;
  }

  private static void xxx() throws IOException {
    //http://47.75.128.222:8090/wallet/getnodeinfo
    //http://47.90.209.64:8888/getallnode
    String result = HttpClient.getDefault().httpGet("http://47.90.209.64:8888/getallnode");
    JSONObject jsonObject = JSON.parseObject(result);
    String[] array = jsonObject.getJSONArray("ip").toArray(new String[0]);
    for (int i = 0; i < array.length; i++) {
      try {
        result = HttpClient.getDefault().httpGet("http://" + array[i] + ":8090/wallet/getnodeinfo");
        jsonObject = JSON.parseObject(result);
        result = jsonObject.getJSONObject("configNodeInfo").getString("codeVersion");
        if (result.startsWith("4")) {
          System.out.println(array[i]);
        }
      } catch (Exception e) {
//        System.out.println(array[i] + " error");
      }
    }
  }

  private static class Game {

    private int cycle;
    private int big;
    private int small;
    private double rate;

    public Game(int cycle, int big, int small, double rate) {
      this.cycle = cycle;
      this.big = big;
      this.small = small;
      this.rate = rate;
    }

    @Override
    public String toString() {
      return "cycle: " + cycle + ", big=" + big + ", small=" + small + ", rate=" + rate;
    }
  }

  private static int findLatestNum(String str) {
    for (int i = str.length() - 1; i >= 0; i--) {
      char value = str.charAt(i);
      if (String.valueOf(value).matches("[0-9]")) {
        return Integer.valueOf(String.valueOf(value));
      }
    }
    return -1;
  }

  private static void game() throws IOException, InterruptedException {

    int big = 0;
    int small = 0;
    int big20 = 0;
    int small20 = 0;

    String url = "https://api.trongrid.io/wallet/getnowblock";
    int num = 0;
    int count = 0;
    Map<Integer, Game> countMap = new HashMap<>();
    AtomicLongMap<Integer> numTimesMap = AtomicLongMap.create();
    AtomicLongMap<Integer> numTimesMapAll = AtomicLongMap.create();
    int big5 = 0;
    int small5 = 0;
    int biglianxu = 0;
    int smalllianxu = 0;
    int maxbig = 0;
    int maxsmall = 0;
    while (true) {
      String result = HttpClient.getDefault().httpGet(url);
      JSONObject jsonObject = JSON.parseObject(result);
      String blockID = jsonObject.getString("blockID");
      int bnum = jsonObject.getJSONObject("block_header").getJSONObject("raw_data")
          .getIntValue("number");
      String sr = jsonObject.getJSONObject("block_header").getJSONObject("raw_data")
          .getString("witness_address");
      int winnum = findLatestNum(blockID);
      if (num != bnum && winnum != -1) {
        if (winnum >= 5) {
          ++big;
        } else {
          ++small;
        }
        if (bnum % 20 == 0) {
          if (winnum >= 5) {
            ++big20;
            ++big5;
            ++biglianxu;
            smalllianxu = 0;
          } else {
            ++small20;
            ++small5;
            ++smalllianxu;
            biglianxu = 0;
          }
          numTimesMap.incrementAndGet(winnum);
          numTimesMapAll.incrementAndGet(winnum);
        }
        num = bnum;
        ++count;

        if (biglianxu > maxbig) {
          maxbig = biglianxu;
        }
        if (smalllianxu > maxsmall) {
          maxsmall = smalllianxu;
        }

        if (count % 100 == 0) {
          int cycle = count / 100;
          countMap.put(cycle, new Game(cycle, big5, small5, (double) big5 / (big5 + small5)));
          System.out.println(countMap.get(cycle));
          System.out
              .println("【开奖】大次数：" + big20 + "，小次数：" + small20 + "，大占比" + (double) big20 / (big20
                  + small20));
          System.out.println(
              "【总】大次数：" + big + "，小次数：" + small + "，大占比" + (double) big / (big + small));
          System.out.println("【max】连续大的次数：" + maxbig + "，连续小的次数：" + maxsmall);
          big5 = 0;
          small5 = 0;
        }
        if (count % 200 == 0) {
          System.out.println(numTimesMap);
          System.out.println(numTimesMapAll);

          numTimesMap.clear();
        }
      }
      Thread.sleep(1000);
    }

  }

  private static String[] srList = {
      "TLyqzVGLV1srkB7dToTAEqgDSfPtXRJZYH", "TTjacDH5PL8hpWirqU7HQQNZDyF723PuCg",
      "TJvaAeFb8Lykt9RQcVyyTFN2iDvGMuyD4M", "TV9QitxEJ3pdiAUAfJ2QuPxLKp9qTTR3og",
      "TTW663tQYJTTCtHh6DWKAfexRhPMf2DxQ1", "TFA1qpUkQ1yBDw4pgZKx25wEZAqkjGoZo1",
      "TSzoLaVCdSNDpNxgChcFt9rSRF5wWAZiR4", "TNmas2SUNGJ2R1jq4Sivya5nHDLYN9ggpG",
      "TWkpg1ZQ4fTv7sj41zBUTMo1kuJEUWTere", "TCEo1hMAdaJrQmvnGTCcGT2LqrGU4N7Jqf",
      "TVFKwzE8qeETLaZEHMx2tjEsdnujAgAWaA", "TNaJADoq1u2atryP1ZzwvmEE4ZBELXfMqw",
      "TY65QiDt4hLTMpf3WRzcX357BnmdxT2sw9", "TUD4YXYdj2t1gP5th3A7t97mx1AUmrrQRt",
      "TJ2aDMgeipmoZRuUEru2ri8t7TGkxnm6qY", "TSNbzxac4WhxN91XvaUfPTKP2jNT18mP6T",
      "TWvncFqyDfMcKfjsx4hvoWwhJfF5yKMZcU", "TTMNxTmRpBZnjtUnohX84j25NLkTqDga7j",
      "TBsyKdNsCKNXLgvneeUJ3rbXgWSgk6paTM", "TRTC1DxDg2eWPHmyc3DTGR672rVHoZQa8h",
      "TAHg5zi2ejWeWiE6bqtDT9vfbH3zNTWrfA", "TMG95kirH4cKW5GnKoCcCye1dBqbt77yGu",
      "TTcYhypP8m4phDhN6oRexz2174zAerjEWP", "TE7hnUtWRRBz3SkFrX8JESWUmEvxxAhoPt",
      "THYPk7Z72REsPrHtZkcwp9pWJadsPxp1UP", "TKSXDA8HfE9E1y39RczVQ1ZascUEtaSToF",
      "TGzz8gjYiYRqpfmDwnLxfgPuLVNmpCswVp"};

  private static int findIndex(String value) {
    for (int i = 0; i < srList.length; i++) {
      if (srList[i].equalsIgnoreCase(value)) {
        return i;
      }
    }
    return -1;
  }

  private static void game2() throws IOException, InterruptedException {

    String url = "https://api.trongrid.io/wallet/getnowblock";
    int index = 5;
    int num = 0;
    int next = 0;
    while (true) {
      String result = HttpClient.getDefault().httpGet(url);
      JSONObject jsonObject = JSON.parseObject(result);
      int bnum = jsonObject.getJSONObject("block_header").getJSONObject("raw_data")
          .getIntValue("number");
      String sr = jsonObject.getJSONObject("block_header").getJSONObject("raw_data")
          .getString("witness_address");
      sr = Base58.encode58Check(ByteArray.fromHexString(sr));
      if (num != bnum) {
        int tmp = bnum % 20;
        int nowIndex = findIndex(sr);
        next = nowIndex + 20 - tmp;
        next = next > 27 ? next - 27 : next;

        if (next == index) {
          System.out.println("bnum：" + bnum + "，sr：" + sr + "，" + srList[next]);
        }
        num = bnum;
      }
      Thread.sleep(500);
    }

  }

  private static void lll() {
    int num = 1;
    int add = num;
    List<Integer> result = new ArrayList<>();
    int i = 0;
    while (true) {
      add = add + 20;
      if (add > 27) {
        add = add - 27;
        result.add(add);
        if (add == 1) {
          break;
        }
      } else if (add == 1) {
        break;
      } else {
        result.add(add);
      }
      if (i > 29) {
        break;
      }
      ++i;
    }
    System.out.println(result.size());
    System.out.println(result);
  }

  public static void main(String[] args) {
    try {
      //TSNbzxac4WhxN91XvaUfPTKP2jNT18mP6T
      //41b3eec71481e8864f0fc1f601b836b74c40548287
//      String imgName = "1.png";
//      downloadImg("https://pinggo.luxe/Home/Index/Code/?0.3850094835891924", imgName);
//      executeTess4J(imgName);
//      xxx();
//      game();
//      lll();
      game2();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
