package com.liang.sangong.bo;

public class TransactionInfo {

  private long id;
  private String txId;
  private long userId;
  private int state;
  private long amount;
  private long createTime;
  private long updateTime;

  public enum TxState {

    init(0), success(1), fail(2);

    TxState(int code) {
      this.code = code;
    }

    public int code;
  }

  public long getId() {
    return id;
  }

  public TransactionInfo setId(long id) {
    this.id = id;
    return this;
  }

  public String getTxId() {
    return txId;
  }

  public TransactionInfo setTxId(String txId) {
    this.txId = txId;
    return this;
  }

  public long getUserId() {
    return userId;
  }

  public TransactionInfo setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public int getState() {
    return state;
  }

  public TransactionInfo setState(int state) {
    this.state = state;
    return this;
  }

  public long getAmount() {
    return amount;
  }

  public TransactionInfo setAmount(long amount) {
    this.amount = amount;
    return this;
  }

  public long getCreateTime() {
    return createTime;
  }

  public TransactionInfo setCreateTime(long createTime) {
    this.createTime = createTime;
    return this;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public TransactionInfo setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public static TransactionInfo build(String txId, long userId, long amount) {
    TransactionInfo transactionInfo = new TransactionInfo();
    return transactionInfo.setTxId(txId).setUserId(userId).setAmount(amount)
        .setState(TxState.init.code).setCreateTime(System.currentTimeMillis())
        .setUpdateTime(System.currentTimeMillis());
  }
}
