package com.liang.vo;

import com.liang.bo.PokersBo.Poker;
import com.liang.bo.PokersBo.PokerType;

public class PokerVo {

  private PokerType pokerType;
  private int value;
  private String uri;

  public static PokerVo build(Poker poker) {
    PokerVo pokerVo = new PokerVo();
    pokerVo.setPokerType(poker.getPokerType());
    pokerVo.setValue(poker.getValue());
    int value = 0;
    if (poker.getPokerType() == PokerType.GUI) {
      if (poker.getValue() == 100) {
        value = 2;
      } else {
        value = 1;
      }
    } else if (poker.getPokerType() == PokerType.FANGP) {
      value = 400 + poker.getValue();
    } else if (poker.getPokerType() == PokerType.MEIH) {
      value = 300 + poker.getValue();
    } else if (poker.getPokerType() == PokerType.HONGT) {
      value = 200 + poker.getValue();
    } else {
      value = 100 + poker.getValue();
    }
    pokerVo.setUri("/static/images/poker/" + value + ".jpg");
    return pokerVo;
  }

  public PokerType getPokerType() {
    return pokerType;
  }

  public PokerVo setPokerType(PokerType pokerType) {
    this.pokerType = pokerType;
    return this;
  }

  public int getValue() {
    return value;
  }

  public PokerVo setValue(int value) {
    this.value = value;
    return this;
  }

  public String getUri() {
    return uri;
  }

  public PokerVo setUri(String uri) {
    this.uri = uri;
    return this;
  }
}
