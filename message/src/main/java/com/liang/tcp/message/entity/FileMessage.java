package com.liang.tcp.message.entity;

import com.liang.common.message.Message;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class FileMessage extends Message {

  public static final byte mType = 4;

  private byte[] data;

  private String destPath;

  public FileMessage() {
    super(mType);
  }

  public FileMessage(String filePath, String destPath) throws IOException {
    this(new File(filePath), destPath);
  }

  public FileMessage(File file, String destPath) throws IOException {
    super(mType);
    data = new byte[(int) file.length()];
    this.destPath = destPath;
    FileUtils.openInputStream(file).read(data);
  }

  public byte[] getData() {
    return data;
  }

  public FileMessage setData(byte[] data) {
    this.data = data;
    return this;
  }

  public String getDestPath() {
    return destPath;
  }

  public FileMessage setDestPath(String destPath) {
    this.destPath = destPath;
    return this;
  }
}
