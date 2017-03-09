package liang.common.http;

import liang.common.util.Digests;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.nio.charset.Charset;

/**
 * 第一代签名、加密和解密。
 */
public class RiddleOne extends Riddle {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public boolean signEquals(String actual, String expected) {
        try {
            return actual.length() == 32 && expected.length() == 32 && actual.equalsIgnoreCase(expected);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String sign(String toSign, String key) {
        if (toSign == null || key == null) {
            return null;
        }
        return Digests.md5(toSign + key);
    }

    @Override
    public byte[] encrypt(byte[] toEncrypt, String key) {
        if (toEncrypt == null || key == null) {
            return null;
        }
        try {
            Cipher cipher = createFromSecretKey(key, Cipher.ENCRYPT_MODE);
            return cipher.doFinal(toEncrypt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] toDecrypt, String key) {
        if (toDecrypt == null || key == null) {
            return null;
        }
        try {
            Cipher cipher = createFromSecretKey(key, Cipher.DECRYPT_MODE);
            return cipher.doFinal(toDecrypt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 产生用于DESede/ECB/PKCS5Padding加密解密的Cipher。
     */
    private Cipher createFromSecretKey(String secretKey, int mode) throws Exception {
        if (secretKey == null) {
            return null;
        }
        byte[] data1 = Digests.md5(secretKey.getBytes(UTF_8));
        byte[] data2 = Digests.sha1(secretKey.getBytes(UTF_8));
        byte[] keyData = new byte[24];
        System.arraycopy(data1, 0, keyData, 0, 16);
        System.arraycopy(data2, 0, keyData, 16, 8);
        DESedeKeySpec dks = new DESedeKeySpec(keyData);

        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");

        // 用密匙初始化Cipher对象
        cipher.init(mode, key);
        return cipher;
    }
}
