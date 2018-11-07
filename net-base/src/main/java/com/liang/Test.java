package com.liang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.sun.management.OperatingSystemMXBean;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Test {

  public static class JVMInfo implements Serializable {

    private static final long serialVersionUID = 7593745554626593803L;

    /**
     * JVM 启动时间
     */
    private String jvmStartTime;
    /**
     * JVM 版本信息
     */
    private String jvmVersion;
    /**
     * jvm名称
     */
    private String jvmName;
    /**
     * 当前线程ID
     */
    private String processId;
    /**
     * 非堆内存使用情况(MB)
     */
//    private MemoryUsage nonHeapMemoryUsage;
//    /**
//     * 堆内存使用情况(MB)
//     */
//    private MemoryUsage heapMemoryUsage;
    /**
     * 操作系统名称
     */
    private String osName;
    /**
     * 操作系统版本
     */
    private String osVersion;
    /**
     * 机器总内存(MB)
     */
    private long totalPhysicalMenory;
    /**
     * 机器以使用内存(MB)
     */
    private long freePhysicalMenory;
    /**
     * 机器可用内存比例
     */
    private String freePhysicalMenoryRatio;
    /**
     * CPU内核
     */
    private int processors;

    private String systemUpTime;

    private List<String> list = new ArrayList<>();

    public List<String> getList() {
      return list;
    }

    public JVMInfo setList(List<String> list) {
      this.list = list;
      return this;
    }

    public String getJvmStartTime() {
      return jvmStartTime;
    }

    public void setJvmStartTime(String jvmStartTime) {
      this.jvmStartTime = jvmStartTime;
    }

    public String getJvmVersion() {
      return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
      this.jvmVersion = jvmVersion;
    }

    public String getJvmName() {
      return jvmName;
    }

    public void setJvmName(String jvmName) {
      this.jvmName = jvmName;
    }

    public String getProcessId() {
      return processId;
    }

    public void setProcessId(String processId) {
      this.processId = processId;
    }

//    public MemoryUsage getNonHeapMemoryUsage() {
//      return nonHeapMemoryUsage;
//    }
//
//    public JVMInfo setNonHeapMemoryUsage(MemoryUsage nonHeapMemoryUsage) {
//      this.nonHeapMemoryUsage = nonHeapMemoryUsage;
//      return this;
//    }
//
//    public MemoryUsage getHeapMemoryUsage() {
//      return heapMemoryUsage;
//    }
//
//    public JVMInfo setHeapMemoryUsage(MemoryUsage heapMemoryUsage) {
//      this.heapMemoryUsage = heapMemoryUsage;
//      return this;
//    }

    public String getOsName() {
      return osName;
    }

    public void setOsName(String osName) {
      this.osName = osName;
    }

    public String getOsVersion() {
      return osVersion;
    }

    public void setOsVersion(String osVersion) {
      this.osVersion = osVersion;
    }

    public long getTotalPhysicalMenory() {
      return totalPhysicalMenory;
    }

    public void setTotalPhysicalMenory(long totalPhysicalMenory) {
      this.totalPhysicalMenory = totalPhysicalMenory;
    }

    public long getFreePhysicalMenory() {
      return freePhysicalMenory;
    }

    public void setFreePhysicalMenory(long freePhysicalMenory) {
      this.freePhysicalMenory = freePhysicalMenory;
    }

    public int getProcessors() {
      return processors;
    }

    public void setProcessors(int processors) {
      this.processors = processors;
    }

    public String getFreePhysicalMenoryRatio() {
      return freePhysicalMenoryRatio;
    }

    public void setFreePhysicalMenoryRatio(String freePhysicalMenoryRatio) {
      this.freePhysicalMenoryRatio = freePhysicalMenoryRatio;
    }

    public String getSystemUpTime() {
      return systemUpTime;
    }

    public void setSystemUpTime(String systemUpTime) {
      this.systemUpTime = systemUpTime;
    }

    @Override
    public String toString() {
      return "JVMInfo{" +
          "jvmStartTime='" + jvmStartTime + '\'' +
          ", jvmVersion='" + jvmVersion + '\'' +
          ", jvmName='" + jvmName + '\'' +
          ", processId='" + processId + '\'' +
//          ", nonHeapMemoryUsage=" + nonHeapMemoryUsage +
//          ", heapMemoryUsage=" + heapMemoryUsage +
          ", osName='" + osName + '\'' +
          ", osVersion='" + osVersion + '\'' +
          ", totalPhysicalMenory=" + totalPhysicalMenory +
          ", freePhysicalMenory=" + freePhysicalMenory +
          ", freePhysicalMenoryRatio='" + freePhysicalMenoryRatio + '\'' +
          ", processors=" + processors +
          ", systemUpTime='" + systemUpTime + '\'' +
          '}';
    }
  }

  //获取jvm信息并封装到JVMInfo实体类中
  public JVMInfo getJVMInfo() {
    JVMInfo jvmStatus = new JVMInfo();
    try {
      //获取JVM的启动时间，版本及名称，当前进程ID
      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
//      System.out.println(JSON.toJSONString(runtimeMXBean));
      jvmStatus.setJvmVersion(runtimeMXBean.getVmVersion());
      jvmStatus.setJvmName(runtimeMXBean.getVmName());
      jvmStatus.setProcessId(runtimeMXBean.getName().split("@")[0]);

      //获取JVM内存使用状况，包括堆内存和非堆内存
      MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
//      jvmStatus.setNonHeapMemoryUsage(memoryMXBean.getNonHeapMemoryUsage());
//      jvmStatus.setHeapMemoryUsage(memoryMXBean.getHeapMemoryUsage());

      //操作系统及硬件信息：系统名称、版本，CPU内核，机器总内存、可用内存、可用内存占比
      OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory
          .getOperatingSystemMXBean();
      System.out.println(JSON.toJSONString(operatingSystemMXBean, SerializerFeature.SortField));
      jvmStatus.setOsName(operatingSystemMXBean.getName());
      jvmStatus.setOsVersion(operatingSystemMXBean.getVersion());
      jvmStatus.setProcessors(operatingSystemMXBean.getAvailableProcessors());
      jvmStatus.setTotalPhysicalMenory(operatingSystemMXBean.getTotalPhysicalMemorySize());
      jvmStatus.setFreePhysicalMenory(operatingSystemMXBean.getFreePhysicalMemorySize());
      DecimalFormat decimalFormat = new DecimalFormat("0.00%");
      if (operatingSystemMXBean.getTotalPhysicalMemorySize() > 0) {
        jvmStatus.setFreePhysicalMenoryRatio(decimalFormat.format(
            Double.valueOf(operatingSystemMXBean.getFreePhysicalMemorySize())
                / operatingSystemMXBean.getTotalPhysicalMemorySize()));
      }

    } catch (Exception e) {

    }
    return jvmStatus;
  }

  public static void main(String[] args) {
    int length = 1000000;
    Test test = new Test();
    JVMInfo jvmInfo = test.getJVMInfo();
    List<String> listStr = new ArrayList<>();
    listStr.add("xxx");
    listStr.add("ddd");
    listStr.add("aaa");
    jvmInfo.setList(listStr);
    List<byte[]> list = new ArrayList<>();
    long begin = System.nanoTime();
    int a = 0;
    for (int i = 0; i < length; i++) {
      list.add(JSON.toJSONBytes(jvmInfo));
    }
    long end = System.nanoTime();
    System.out.println("toJSONString time: " + (end - begin));
    begin = System.nanoTime();
    for (byte[] str : list) {
      JSON.parseObject(str, JVMInfo.class);
    }
    end = System.nanoTime();
    System.out.println("parseObject time: " + (end - begin));
    List<JVMInfo> jvmInfos = new ArrayList<>();
    for (int i = 0; i < length; i++) {
//      JVMInfo jvm = jvmInfo;
//      jvm.setOsName(jvm.getOsName() + i);
      jvmInfos.add(jvmInfo);
    }
    deserializeProtoStuffDataListToProductsList(serializeProtoStuffProductsList(jvmInfos));

  }

  public static List<byte[]> serializeProtoStuffProductsList(List<JVMInfo> pList) {
    if (pList == null || pList.size() <= 0) {
      return null;
    }
    long start = System.nanoTime();
    List<byte[]> bytes = new ArrayList<byte[]>();
    Schema<JVMInfo> schema = RuntimeSchema.getSchema(JVMInfo.class);
    LinkedBuffer buffer = LinkedBuffer.allocate(4096);
    byte[] protostuff = null;
    for (JVMInfo p : pList) {
      try {
        protostuff = ProtostuffIOUtil.toByteArray(p, schema, buffer);
        bytes.add(protostuff);
      } finally {
        buffer.clear();
      }
    }
    long end = System.nanoTime();
    System.out.println("Serialize ProtoStuff time: " + (end - start));
    return bytes;
  }

  public static List<JVMInfo> deserializeProtoStuffDataListToProductsList(
      List<byte[]> bytesList) {
    if (bytesList == null || bytesList.size() <= 0) {
      return null;
    }
    long start = System.nanoTime();
    Schema<JVMInfo> schema = RuntimeSchema.getSchema(JVMInfo.class);
    List<JVMInfo> list = new ArrayList<JVMInfo>();
    for (byte[] bs : bytesList) {
      JVMInfo product = null;
      try {
        product = JVMInfo.class.newInstance();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      ProtostuffIOUtil.mergeFrom(bs, product, schema);
//      list.add(product);
    }
    long end = System.nanoTime();
    System.out.println("Deserialize ProtoStuff time: " + (end - start));
    return list;
  }
}
