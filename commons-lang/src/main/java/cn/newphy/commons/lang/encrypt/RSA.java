package cn.newphy.commons.lang.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class RSA {
    
    /**
     * 加密算法RSA
     */
    public static final String ALGORITHM = "RSA";
    
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM_SHA256 = "SHA256withRSA";
    
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    
    /**
     * 获取公钥的key
     */
    public static final String PUBLIC_KEY = "RSAPublicKey";
    
    /**
     * 获取私钥的key
     */
    public static final String PRIVATE_KEY = "RSAPrivateKey";
    
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
    
    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     * @return
     * @throws NoSuchAlgorithmException
     * @throws Exception
     */
    public static Map<String, Key> generateRSAKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Key> keyMap = new HashMap<String, Key>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }
    
    /**
     * 使用SHA256生成签名
     * @param data 需要签名的数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws GeneralSecurityException
     */
    public static String signWithSHA256(String privateKey, byte[] data) throws GeneralSecurityException {
        return doSign(privateKey, data, SIGNATURE_ALGORITHM_SHA256);
    }
    
    /**
     * 使用MD5生成签名
     * @param data
     * @param privateKey
     * @return
     * @throws GeneralSecurityException
     */
    public static String signWithMD5(String privateKey, byte[] data) throws GeneralSecurityException {
        return doSign(privateKey, data, SIGNATURE_ALGORITHM_MD5);
    }
    
    /**
     * 使用SHA256校验数字签名
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     * @return
     * @throws GeneralSecurityException
     * @throws Exception
     */
    public static boolean verifyWithSHA256(String publicKey, byte[] data, String sign) throws GeneralSecurityException {
        return doVerify(publicKey, data, sign, SIGNATURE_ALGORITHM_SHA256);
    }
    
    /**
     * 使用MD5校验数字签名
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     * @return
     * @throws GeneralSecurityException
     * @throws Exception
     */
    public static boolean verifyWithMD5(String publicKey, byte[] data, String sign) throws GeneralSecurityException {
        return doVerify(publicKey, data, sign, SIGNATURE_ALGORITHM_MD5);
    }
    
    /**
     * 私钥解密
     * @param encryptedData
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws GeneralSecurityException
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws GeneralSecurityException {
        return doDecrypt(getPrivateKey(privateKey), encryptedData);
    }
    
    /**
     * 公钥解密
     * @param encryptedData
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws GeneralSecurityException
     */
    public static byte[] decryptByPublicKey(String publicKey, byte[] encryptedData) throws GeneralSecurityException {
        return doDecrypt(getPublicKey(publicKey), encryptedData);
    }
    
    /**
     * 公钥加密
     * @param data
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws GeneralSecurityException
     */
    public static byte[] encryptByPublicKey(String publicKey, byte[] data) throws GeneralSecurityException {
        return doEncrypt(getPublicKey(publicKey), data);
    }
    
    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(String privateKey, byte[] data) throws GeneralSecurityException {
        return doEncrypt(getPrivateKey(privateKey), data);
    }
    
    private static String doSign(String privateKey, byte[] data, String algorithm) throws GeneralSecurityException {
        PrivateKey privateK = getPrivateKey(privateKey);
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateK);
        signature.update(data);
        return encodeBase64(signature.sign());
    }
    
    private static boolean doVerify(String publicKey, byte[] data, String sign, String algorithm) throws GeneralSecurityException {
        PublicKey publicK = getPublicKey(publicKey);
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(decodeBase64(sign));
    }
    
    private static byte[] doEncrypt(Key key, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        int dataSize = data.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int offset = 0, left = 0;
        byte[] buffer = null;
        // 对数据分段解密
        try {
            while ((left = dataSize - offset) > 0) {
                buffer = cipher.doFinal(data, offset, Math.min(MAX_ENCRYPT_BLOCK, left));
                baos.write(buffer);
                offset += MAX_ENCRYPT_BLOCK;
            }
        } catch (IOException e) {
            throw new GeneralSecurityException("encrypt error", e);
        }
        return baos.toByteArray();
    }
    
    private static byte[] doDecrypt(Key key, byte[] encryptedData) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        int encryptSize = encryptedData.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int offset = 0, left = 0;
        byte[] buffer = null;
        // 对数据分段解密
        try {
            while ((left = encryptSize - offset) > 0) {
                buffer = cipher.doFinal(encryptedData, offset, Math.min(MAX_DECRYPT_BLOCK, left));
                baos.write(buffer);
                offset += MAX_DECRYPT_BLOCK;
            }
        } catch (IOException e) {
            throw new GeneralSecurityException("decrypt  error", e);
        }
        return baos.toByteArray();
        
    }
    
    private static PrivateKey getPrivateKey(String privateKey) throws InvalidKeySpecException {
        byte[] keyBytes = decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey pKey = getKeyFactory().generatePrivate(pkcs8KeySpec);
        return pKey;
    }
    
    private static PublicKey getPublicKey(String publicKey) throws InvalidKeySpecException {
        byte[] keyBytes = decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        PublicKey publicK = getKeyFactory().generatePublic(x509KeySpec);
        return publicK;
    }
    
    private static KeyFactory getKeyFactory() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("no RSA algorithm");
        }
    }
    
    private static String encodeBase64(byte[] data) {
        return Base64.encodeBase64String(data);
    }
    
    private static byte[] decodeBase64(String base64String) {
        return Base64.decodeBase64(base64String);
    }
    
    
    public static void main(String[] args) {
        try {
            Map<String, Key> rsaKeys = generateRSAKey();
            PublicKey pubKey = (PublicKey)rsaKeys.get(PUBLIC_KEY);
            PrivateKey priKey = (PrivateKey)rsaKeys.get(PRIVATE_KEY);           
            
            String publicKey = encodeBase64(pubKey.getEncoded());
            String privateKey = encodeBase64(priKey.getEncoded());
            System.out.println("privateKey: " + privateKey);
            System.out.println("publicKey: " + publicKey);
            String content = "换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实换句话说，我没什么天赋，不算太努力，家里没什么背景，接受的教育也就是凑合，也没什么高层次的追求。你觉得我是值得被鄙视的？可一个社会中，最多的就是我这样的人。我人生的目标就是能做个普普通通的人，过普普通通的日子，而这一切，只需要普普通通的努力程度与普普通通的天赋就足够了，这样的人占了总人数的差不多百分之七十，你凭什么觉得这些人都没有权利过上平常但有尊严的生活？实际上我们理应过着平常而有尊严的生活，而且我们最应该过着平常而有尊严的生活——而那些很努力很努力的人，应该过上比我们好得多的生活。如果我们“很努力很努力”只为了过上平常的生活，那社会结构一定出了大问题。社会结构整个的畸形导致了普遍的中产阶层焦虑，而知乎（典型的中产阶层网站）就是这一焦虑的投影。这并非错觉，这是事实";
            System.out.println("content length: " + content.length());
            long start = System.currentTimeMillis();
            String encrypt = null, decrypt = null;
            // private encrypt public decrypt
            System.out.println("====== private encrypt public decrypt");
            encrypt = encodeBase64(encryptByPrivateKey(privateKey, content.getBytes()));
            decrypt = new String(decryptByPublicKey(publicKey, decodeBase64(encrypt)));
            System.out.println("encrypt: " + encrypt);
            System.out.println("decrypt: " + decrypt);
            
            // public encrypt private decrypt
            System.out.println("====== public encrypt private decrypt");
            encrypt = encodeBase64(encryptByPublicKey(publicKey, content.getBytes()));
            decrypt = new String(decryptByPrivateKey(decodeBase64(encrypt), privateKey));
            System.out.println("encrypt: " + encrypt);
            System.out.println("decrypt: " + decrypt);
            
            String sign = null;
            boolean verify = false;
            // sign with MD5
            System.out.println("====== sign with MD5");
            sign = signWithMD5(privateKey, content.getBytes());
            verify = verifyWithMD5(publicKey, content.getBytes(), sign);
            System.out.println("sign: " + sign);
            System.out.println("verify: " + verify);
            
            // sign with SHA256
            System.out.println("====== sign with SHA256");
            sign = signWithSHA256(privateKey, content.getBytes());
            verify = verifyWithSHA256(publicKey, content.getBytes(), sign);
            System.out.println("sign: " + sign);
            System.out.println("verify: " + verify);
            System.out.println("it take " + (System.currentTimeMillis() - start) + " ms");
        } catch (GeneralSecurityException e) {
           e.printStackTrace();
        }
    }
}
