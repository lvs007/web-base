package com.nft.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Mint {

  public String mint(ModelMap modelMap) {
    return "mint";
  }

  public String mintDetail(@RequestParam(name = "type", required = true) String type,
      ModelMap modelMap) {

    if (type.equals("1")) {
      return "mintdetail";
    } else {
      return "mintnftdetail";
    }
  }

  public static String compress(String data) {
    if (data == null || data.length() == 0) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    GZIPOutputStream gzip;
    try {
      gzip = new GZIPOutputStream(out);
      gzip.write(data.getBytes("utf-8"));
      gzip.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(out.size());
    return new String(out.toByteArray());
  }

  public static void main(String[] args) {
    String str = "QmWgV2YkgUqfVUgcQVbjH8cPtTfy4Ng4oY4Ccv6SHTqZa4";
    System.out.println(str);
    System.out.println(compress(str));
  }

}
