package com.nft.bo;

import org.spongycastle.util.encoders.Hex;

public class NFTDesc {

  private long id;
  private long nftId;
  private long createTime;
  private String title;
  private String url;
  private String desc;
  private int type;
  private int state;

  public long getId() {
    return id;
  }

  public NFTDesc setId(long id) {
    this.id = id;
    return this;
  }

  public long getNftId() {
    return nftId;
  }

  public NFTDesc setNftId(long nftId) {
    this.nftId = nftId;
    return this;
  }

  public long getCreateTime() {
    return createTime;
  }

  public NFTDesc setCreateTime(long createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public NFTDesc setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public NFTDesc setUrl(String url) {
    this.url = url;
    return this;
  }

  public String getDesc() {
    return desc;
  }

  public NFTDesc setDesc(String desc) {
    this.desc = desc;
    return this;
  }

  public int getType() {
    return type;
  }

  public NFTDesc setType(int type) {
    this.type = type;
    return this;
  }

  public int getState() {
    return state;
  }

  public NFTDesc setState(int state) {
    this.state = state;
    return this;
  }

  public static void main(String[] args) {
    int a = 150;
    int b = 150;
    int count = Integer.bitCount(b);
    int result = a<<8|b;
    System.out.println(Integer.toBinaryString(a)+","+Integer.toBinaryString(b));
    System.out.println(Integer.toBinaryString(result));
    System.out.println(count);
    System.out.println(result);
    b = result & ((1<<8)-1);
    a = result >> 8;
    System.out.println(a);//uint32(n & ((1<<22)-1));
    System.out.println(b);
    String v = "e4bda0e5a5bde5a5bd0000000000000000000000000000000000000000000000";
    byte[] bytes = Hex.decode(v);
    System.out.printf(new String(bytes));
  }
}
