package com.liang.tcp.message.action;

import com.liang.tcp.message.Message;
import com.liang.tcp.message.MessageFactory;

public abstract class AbstractMessageAction<M extends Message> implements MessageAction<M> {

  public AbstractMessageAction() {
    MessageFactory.register(this);
  }
}
