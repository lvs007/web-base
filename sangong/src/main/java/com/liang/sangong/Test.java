package com.liang.sangong;

import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.core.PeoplePlay.GameType;
import com.liang.sangong.core.Room;

public class Test {

  public static void main(String[] args) {
    Room table = new Room(GameType.ZHUANGJIA);
    PeoplePlay peoplePlay1 = new PeoplePlay(new PeopleInfo("s1", 100000));
    PeoplePlay peoplePlay2 = new PeoplePlay(new PeopleInfo("s2", 1000));
    PeoplePlay peoplePlay3 = new PeoplePlay(new PeopleInfo("s3", 1000));
    table.add(peoplePlay1);
    table.add(peoplePlay2);
    table.add(peoplePlay3);
    System.out.println(peoplePlay1.zuoZhuang());
    peoplePlay1.confirm(100);
    peoplePlay2.confirm(100);
    peoplePlay3.confirm(100);
    peoplePlay1.begin();
    table.getPeoplePlayList().forEach(peoplePlay -> {
      System.out.println(peoplePlay.getCurrentPoke());
    });
  }

}
