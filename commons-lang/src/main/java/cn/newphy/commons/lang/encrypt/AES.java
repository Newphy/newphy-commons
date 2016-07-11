package cn.newphy.commons.lang.encrypt;

import java.io.UnsupportedEncodingException;
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

	private static Cipher init(int mode, byte[] keyData) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(keyData));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(mode, key);// 初始化
			return cipher;
		} catch (Exception e) {
			throw new IllegalStateException("init encrypt cipher error", e);
		}
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            需要加密的内容
	 * @param key
	 *            加密密码
	 * @return
	 */
	public static byte[] encrypt(byte[] data, String key) {
		Assert.isTrue((data != null && data.length > 0), "data cannot be empty");
		try {
			Cipher cipher = init(Cipher.ENCRYPT_MODE, key.getBytes("UTF-8"));
			byte[] encryptData = cipher.doFinal(data);
			return encryptData;
		} catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
			throw new IllegalStateException("AES encrypt error", e);
		}
	}

	/**
	 * 加密字符串
	 * 
	 * @param content
	 * @param key
	 * @return
	 */
	public static byte[] encryptStr(String content, String key) {
		Assert.isTrue(StringUtils.isNotEmpty(content), "content cannot be empty");
		try {
			return encrypt(content.getBytes("UTF-8"), key);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("AES encrypt error", e);
		}
	}
	
	/**
	 * 加密字符串返回Base64字符串
	 * @param content
	 * @param key
	 * @return
	 */
	public static String encryptBase64(String content, String key) {
		byte[] encryptData = encryptStr(content, key);
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
	 */
	public static byte[] decrypt(byte[] encryptData, String key) {
		Assert.isTrue(ArrayUtils.isNotEmpty(encryptData), "encryptData cannot be empty");
		try {
			Cipher cipher = init(Cipher.DECRYPT_MODE, key.getBytes("UTF-8"));
			byte[] data = cipher.doFinal(encryptData);
			return data;
		} catch (UnsupportedEncodingException e) {
			logger.error("unsupported encoding type", e);
			return null;
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new IllegalStateException("AES decrypt error", e);
		}
	}
	
	/**
	 * 解密字符串
	 * @param encryptData
	 * @param key
	 * @return
	 */
	public static String decryptStr(byte[] encryptData, String key) {
		try {
			byte[] data = decrypt(encryptData, key);
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("AES decrypt error", e);
		}
	}
	
	/**
	 * 解密Base64字符串
	 * @param base64Str
	 * @param key
	 * @return
	 */
	public static String decryptBase64(String base64Str, String key) {
		byte[] encryptData = Base64.decodeBase64(base64Str);
		return decryptStr(encryptData, key);
	}

	public static void main(String[] args) {
		String content = "abcd我什么的谁";
		String password = RandomStringUtils.randomAlphanumeric(32);
		System.out.println("content=" + content + "; password=" + password);
		String encryptBase64Str = AES.encryptBase64(content, password);
		System.out.println("encrypt after: " + encryptBase64Str);
		String data = AES.decryptBase64(encryptBase64Str, password);
		System.out.println(data);
	}
}
