package com.liang.sangong.bo;

import com.liang.sangong.bo.PokesBo.Poke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OnePoke {

  private List<Poke> allPoke = new ArrayList<>();

  public OnePoke() {
    PokesBo pokersBo = new PokesBo();
    allPoke.addAll(pokersBo.getPokerList());
    //打乱顺序
    Collections.shuffle(allPoke);
  }

  public List<Poke> getAllPoke() {
    return allPoke;
  }

  public Poke pop() {
    if (allPoke.size() > 0) {
      return allPoke.remove(0);
    }
    return null;
  }

  public int currentPokerNumber() {
    return allPoke.size();
  }

}
