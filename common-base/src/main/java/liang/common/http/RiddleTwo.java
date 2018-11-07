package liang.common.http;

import liang.common.util.Digests;
import liang.common.util.Encodes;
import liang.common.util.MiscUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * 第二代签名、加密和解密。
 */
public class RiddleTwo extends Riddle {

    private static final int MAGIC = 42;
    private static final int AES_KEY_LEN_LIMIT = 255;

    private final Random random = new Random();

    @Override
    public boolean signEquals(String actual, String expected) {
        try {
            return actual.length() == 34 && expected.length() == 34
                    && actual.substring(0, 32).equalsIgnoreCase(expected.substring(0, 32));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String sign(String toSign, String key) {
        try {
            byte[] md5edKey = Digests.md5(key.getBytes("utf-8"));
            byte[] sha1edToSign = Digests.sha1(toSign.getBytes("utf-8"));
            byte[] concated = new byte[md5edKey.length + sha1edToSign.length];
            System.arraycopy(md5edKey, 0, concated, 0, md5edKey.length);
            System.arraycopy(sha1edToSign, 0, concated, md5edKey.length,
                    sha1edToSign.length);
            for (int i = 0; i < concated.length; i += 2) {
                concated[i] = (byte) (concated[i] & 0xff ^ MAGIC);
            }
            byte[] hexed = Digests.md5(concated);
            byte[] signed = new byte[hexed.length + 1];
            System.arraycopy(hexed, 0, signed, 0, hexed.length);
            signed[signed.length - 1] = (byte) random.nextInt();
            return Encodes.encodeHex(signed);
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public byte[] encrypt(byte[] toEncrypt, String key) {
        MiscUtils.guardNull(key, "key");
        MiscUtils.guardNull(toEncrypt, "toEncrypt");
        if (key.length() > AES_KEY_LEN_LIMIT) {
            throw new RuntimeException("rawKey的长度超过了" + AES_KEY_LEN_LIMIT);
        }

        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            Cipher cipher = createFromSecretKeyAES(keyBytes, Cipher.ENCRYPT_MODE);
            MiscUtils.guardNull(cipher, "cipher");
            byte[] encrypted = cipher.doFinal(toEncrypt);

            CryptoData data = new CryptoData();
            data.content = encrypted;
            data.rawKey = keyBytes;
            return combine(data);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] toDecrypt, String ignoredKey) {
        MiscUtils.guardNull(toDecrypt, "toDecrypt");
        CryptoData sliced = slice(toDecrypt);
        try {
            Cipher cipher = createFromSecretKeyAES(sliced.rawKey, Cipher.DECRYPT_MODE);
            return cipher.doFinal(sliced.content);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 该组合类包含加密解密所需的数据：
     * - 对于加密，包含裸秘钥和加密原文；
     * - 对于解密，包含裸秘钥和加密结果。
     */
    static class CryptoData {
        public byte[] rawKey;
        public byte[] content;
    }

    /**
     * 产生用于AES/CBC/PKCS5Padding加密解密的Cipher。
     */
    private Cipher createFromSecretKeyAES(byte[] rawKey, int mode) throws Exception {
        MiscUtils.guardNull(rawKey, "rawKey");
        // 产生真的key。
        byte[] realKey = Digests.md5(rawKey);
        reverse(realKey);
        xor(realKey);
        // 由于每次使用的key都不一样，所以使用倒置realKey作为初始向量iv没有问题。
        byte[] iv = realKey.clone();
        reverse(iv);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        MiscUtils.guardNull(cipher, "cipher");
        cipher.init(mode, new SecretKeySpec(realKey, "AES"), new IvParameterSpec(iv));

        return cipher;
    }

    /**
     * 拼接裸秘钥和加密结果。返回的结果第1位表示裸秘钥的长度（不能超过255），接下来是裸秘钥和加密结果。
     */
    static byte[] combine(CryptoData data) {
        MiscUtils.guardNull(data.content, "data.content");
        MiscUtils.guardNull(data.rawKey, "data.rawKey");

        if (data.rawKey.length > AES_KEY_LEN_LIMIT) {
            throw new RuntimeException("rawKey的长度超过了" + AES_KEY_LEN_LIMIT);
        }
        byte[] combined = new byte[data.rawKey.length + data.content.length + 1];
        combined[0] = (byte) data.rawKey.length;
        System.arraycopy(data.rawKey, 0, combined, 1, data.rawKey.length);
        System.arraycopy(data.content, 0, combined, data.rawKey.length + 1, data.content.length);
        return combined;
    }

    /**
     * slice是combine的逆向操作。
     */
    static CryptoData slice(byte[] combined) {
        MiscUtils.guardNull(combined, "combined");

        int rawKeyLen = combined[0] & 0xff;
        int encryptedLen = combined.length - rawKeyLen - 1;
        if (encryptedLen < 0) {
            throw new RuntimeException("密文总长度小于0");
        }

        CryptoData sliced = new CryptoData();
        sliced.content = new byte[encryptedLen];
        sliced.rawKey = new byte[rawKeyLen];
        System.arraycopy(combined, rawKeyLen + 1, sliced.content, 0, encryptedLen);
        System.arraycopy(combined, 1, sliced.rawKey, 0, rawKeyLen);

        return sliced;
    }

    /**
     * 偶数位与MAGIC异或。
     */
    static void xor(byte[] bytes) {
        MiscUtils.guardNull(bytes, "bytes");
        for (int i = 0; i < bytes.length; i += 2) {
            bytes[i] = (byte) (bytes[i] ^ MAGIC);
        }
    }

    /**
     * 倒置byte数组。
     */
    static void reverse(byte[] bytes) {
        MiscUtils.guardNull(bytes, "bytes");
        for (int l = 0, r = bytes.length - 1; l < r; ++l, --r) {
            byte tmp = bytes[l];
            bytes[l] = bytes[r];
            bytes[r] = tmp;
        }
    }
}
