package com.liang.common.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageAction<M extends Message> implements MessageAction<M> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  public AbstractMessageAction() {
    MessageFactory.register(this);
  }
}
