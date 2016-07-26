package cn.newphy.commons.lang.encrypt;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class AES {
	public static Log logger = LogFactory.getLog(AES.class);

	private static Cipher init(int mode, byte[] keyData) throws GeneralSecurityException {
			KeyGenerator kgen;
            kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(keyData));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(mode, key);// 初始化
            return cipher;
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            需要加密的内容
	 * @param key
	 *            加密密码
	 * @return
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static byte[] encrypt(String key, byte[] data) throws GeneralSecurityException {
		Assert.isTrue((data != null && data.length > 0), "data cannot be empty");
		Cipher cipher = init(Cipher.ENCRYPT_MODE, key.getBytes(getCharset()));
		byte[] encryptData = cipher.doFinal(data);
		return encryptData;
	}

	/**
	 * 加密字符串
	 * 
	 * @param content
	 * @param key
	 * @return
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static byte[] encryptStr(String key, String content) throws GeneralSecurityException {
		Assert.isTrue(StringUtils.isNotEmpty(content), "content cannot be empty");
		return encrypt(key, content.getBytes(getCharset()));
	}
	
	/**
	 * 加密字符串返回Base64字符串
	 * @param content
	 * @param key
	 * @return
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static String encryptBase64(String key, String content) throws GeneralSecurityException {
		byte[] encryptData = encryptStr(key, content);
		return Base64.encodeBase64String(encryptData);
	}

	/**
	 * 解密
	 * 
	 * @param encryptData
	 *            待解密内容
	 * @param key
	 *            解密密钥
	 * @return
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static byte[] decrypt(String key, byte[] encryptData) throws GeneralSecurityException {
		Assert.isTrue(ArrayUtils.isNotEmpty(encryptData), "encryptData cannot be empty");
		Cipher cipher = init(Cipher.DECRYPT_MODE, key.getBytes(getCharset()));
		byte[] data = cipher.doFinal(encryptData);
		return data;
	}
	
	/**
	 * 解密字符串
	 * @param encryptData
	 * @param key
	 * @return
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static String decryptStr(String key, byte[] encryptData) throws GeneralSecurityException {
		byte[] data = decrypt(key, encryptData);
		return new String(data, getCharset());
	}
	
	/**
	 * 解密Base64字符串
	 * @param base64Str
	 * @param key
	 * @return
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static String decryptBase64(String key, String base64Str) throws GeneralSecurityException {
		byte[] encryptData = Base64.decodeBase64(base64Str);
		return decryptStr(key, encryptData);
	}
	
	private static Charset getCharset() {
	    return Charset.forName("UTF-8");
	}
	
	public static void main(String[] args) {
        try {
            String secretKey = RandomStringUtils.randomAlphanumeric(8);
            String content = "换";
            System.out.println("content length: " + content.length());
            long start = System.currentTimeMillis();
            String encrypt = null, decrypt = null;
            // private encrypt public decrypt
            System.out.println("====== private encrypt public decrypt");
            encrypt = AES.encryptBase64(secretKey, content);
            decrypt = AES.decryptBase64(secretKey, encrypt);
           
            System.out.println("it take " + (System.currentTimeMillis() - start) + " ms");
            System.out.println("encrypt: " + encrypt);
            System.out.println("decrypt: " + decrypt);            
        } catch (GeneralSecurityException e) {
           e.printStackTrace();
        }
    }
}
