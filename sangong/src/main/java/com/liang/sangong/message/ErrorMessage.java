package com.liang.sangong.message;

public class ErrorMessage extends Message {

  public static final String NOT_RETURN = "NOT_RETURN";
  private String reason;

  public ErrorMessage() {
    super(MessageType.error);
  }

  public String getReason() {
    return reason;
  }

  public ErrorMessage setReason(String reason) {
    this.reason = reason;
    return this;
  }

  public static String notReturn() {
    return NOT_RETURN;
  }

  public static String build(String reason) {
    return new ErrorMessage().setReason(reason).toString();
  }
}
