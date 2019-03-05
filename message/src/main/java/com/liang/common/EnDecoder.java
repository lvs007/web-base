package com.liang.common;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class EnDecoder {

  public static <T> T decoder(byte[] bytes, Class<T> tClass)
      throws IllegalAccessException, InstantiationException {
    if (bytes == null || bytes.length <= 0) {
      return null;
    }
    Schema<T> schema = RuntimeSchema.getSchema(tClass);
    T t = tClass.newInstance();
    ProtostuffIOUtil.mergeFrom(bytes, t, schema);
    return t;
  }

  public static byte[] encoder(Object object) {
    if (object == null) {
      return null;
    }
    Schema schema = RuntimeSchema.getSchema(object.getClass());
    LinkedBuffer buffer = LinkedBuffer.allocate(4096);
    byte[] bytes = null;
    try {
      bytes = ProtostuffIOUtil.toByteArray(object, schema, buffer);
    } finally {
      buffer.clear();
    }
    return bytes;
  }
}
