package com.liang.sangong.core;

import com.liang.sangong.bo.PokesBo.Poke;
import java.util.Collections;
import java.util.List;

public class Rule {

  public enum ResultType {
    ZHIZHUN(1), SANTIAO(2), SANGONG(3), DIANSHU(4);

    ResultType(int code) {
      this.code = code;
    }

    public int code;
  }

  public static boolean sameValue(List<Poke> pokeList) {
    Poke targe = pokeList.get(0);
    for (Poke poke : pokeList) {
      if (targe.getValue() != poke.getValue()) {
        return false;
      }
    }
    return true;
  }

  public static boolean sangong(List<Poke> pokeList) {
    for (Poke poke : pokeList) {
      if (poke.getValue() <= 10) {
        return false;
      }
    }
    return true;
  }

  public static int dianshu(List<Poke> pokeList) {
    int number = 0;
    for (Poke poke : pokeList) {
      if (poke.getValue() < 10) {
        number += poke.getValue();
      }
    }
    return number % 10;
  }

  public static ResultType getPokeResultType(List<Poke> pokeList) {
    Poke targe = pokeList.get(0);
    if (sameValue(pokeList) && targe.getValue() == 3) {
      return ResultType.ZHIZHUN;
    }
    if (sameValue(pokeList)) {
      return ResultType.SANTIAO;
    }
    if (sangong(pokeList)) {
      return ResultType.SANGONG;
    }
    return ResultType.DIANSHU;
  }

  public static PeoplePlay compareRetrunWinner(PeoplePlay peoplePlay1, PeoplePlay peoplePlay2) {
    List<Poke> pokeList1 = peoplePlay1.getCurrentPoke();
    List<Poke> pokeList2 = peoplePlay2.getCurrentPoke();
    Collections.sort(pokeList1);
    Collections.sort(pokeList2);
    ResultType resultType1 = getPokeResultType(pokeList1);
    ResultType resultType2 = getPokeResultType(pokeList2);
    peoplePlay1.setResultType(resultType1);
    peoplePlay2.setResultType(resultType2);
    if (resultType1 == ResultType.ZHIZHUN) {
      return peoplePlay1;
    }
    if (resultType2 == ResultType.ZHIZHUN) {
      return peoplePlay2;
    }
    if (resultType1 == resultType2 && resultType1 == ResultType.SANTIAO) {
      if (pokeList1.get(0).getValue() > pokeList2.get(0).getValue()) {
        return peoplePlay1;
      } else {
        return peoplePlay2;
      }
    }
    if (resultType1 == resultType2 && resultType1 == ResultType.SANGONG) {
      if (pokeList1.get(0).compareTo(pokeList2.get(0)) > 0) {
        return peoplePlay1;
      } else {
        return peoplePlay2;
      }
    }
    if (resultType1 == resultType2 && resultType1 == ResultType.DIANSHU) {
      int dianshu1 = dianshu(pokeList1);
      int dianshu2 = dianshu(pokeList2);
      peoplePlay1.setDianshu(dianshu1);
      peoplePlay2.setDianshu(dianshu2);
      if (dianshu1 > dianshu2) {
        return peoplePlay1;
      } else if (dianshu1 < dianshu2) {
        return peoplePlay2;
      } else if (pokeList1.get(0).compareTo(pokeList2.get(0)) > 0) {
        return peoplePlay1;
      } else {
        return peoplePlay2;
      }
    }
    if (resultType1.code < resultType2.code) {
      return peoplePlay1;
    } else {
      return peoplePlay2;
    }
  }
}
