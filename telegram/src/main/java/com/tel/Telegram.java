package com.tel;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.meta.api.objects.ChatMember;

public class Telegram {

  public static void getMember() {
    String chainId = "-1001176237452";
    try {
      DefaultAbsSender defaultAbsSender = new DefaultAbsSender(new DefaultBotOptions()) {
        @Override
        public String getBotToken() {
          return "1962318622:AAFLFK5HwL0JegzLATY7wLYC5yVGez0S15Q";
        }
      };
      GetChatMember getChatMember = new GetChatMember();
      GetChat getChat = new GetChat();
      getChat.setChatId(chainId);
      System.out.println(defaultAbsSender.execute(getChat));
      System.out.println(defaultAbsSender.execute(new GetChatMemberCount().setChatId(chainId)));
//      for (int i = 1; i < Integer.MAX_VALUE; i++) {
//        getChatMember.setChatId("-1001176237452").setUserId(i);
//        try {
//          ChatMember chatMember = defaultAbsSender.execute(getChatMember);
//          System.out.println(chatMember);
//        } catch (Exception e) {
//        }
//      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    getMember();
  }
}
