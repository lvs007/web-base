package com.liang.tcp.message.action;

import com.liang.common.message.AbstractMessageAction;
import com.liang.tcp.message.entity.FileMessage;
import com.liang.tcp.peer.PeerChannel;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class FileMessageAction extends AbstractMessageAction<FileMessage> {

  @Override
  public byte getMessageType() {
    return FileMessage.mType;
  }

  @Override
  public Class<FileMessage> getMessageClass() {
    return FileMessage.class;
  }

  @Override
  public void action(PeerChannel peerChannel, FileMessage message) {
    try {
      FileUtils.writeByteArrayToFile(new File(message.getDestPath()), message.getData());
    } catch (IOException e) {
      logger.error("", e);
    }
  }
}
