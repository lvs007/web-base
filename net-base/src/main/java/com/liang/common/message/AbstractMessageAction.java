package com.liang.common.message;

public abstract class AbstractMessageAction<M extends Message> implements MessageAction<M> {

  public AbstractMessageAction() {
    MessageFactory.register(this);
  }
}
