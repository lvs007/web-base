package com.liang.tcp.peer;

import java.util.HashSet;
import java.util.Set;

public class PeerChannelGroup {

  private String groupId;
  private Set<PeerChannel> peerChannelSet = new HashSet<>();

  public void addPeerChannel(PeerChannel peerChannel) {
    peerChannelSet.add(peerChannel);
  }

  public void removePeerChannel(PeerChannel peerChannel) {
    peerChannelSet.remove(peerChannel);
  }

  public String getGroupId() {
    return groupId;
  }

  public PeerChannelGroup setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  public Set<PeerChannel> getPeerChannelSet() {
    return peerChannelSet;
  }

  public PeerChannelGroup setPeerChannelSet(
      Set<PeerChannel> peerChannelSet) {
    this.peerChannelSet = peerChannelSet;
    return this;
  }
}
