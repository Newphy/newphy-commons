package cn.newphy.commons.security;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;

/**
 * 请求计算签名/验证签名接口
 * @author liuhui18
 */
public class SignUtils {
    
    private static final Charset utf8 = Charset.forName("UTF-8");
    
    /**
     * 对bean进行RSA签名
     * @param privateKey
     * @param bean
     * @param ignoredProperties
     * @return
     * @throws Exception
     */
    public static String sign(String privateKey, Object bean, String... ignoredProperties) throws GeneralSecurityException {
        Assert.notNull(bean, "签名对象不能为null");
        return doSign(privateKey, getDigestContent(bean, ignoredProperties));
        
    }
    
    /**
     * 对Map进行RSA签名
     * @param privateKey
     * @param map
     * @return
     * @throws GeneralSecurityException
     */
    public static String sign(String privateKey, Map<String, Object> map) throws GeneralSecurityException {
        return doSign(privateKey, getDigestContent(map));
    }
    
    /**
     * 对bean进行RSA验证签名
     * @param publicKey
     * @param sign
     * @param bean
     * @param ignoredProperties
     * @return
     * @throws Exception
     */
    public static boolean verifySign(String publicKey, String sign, Object bean, String... ignoredProperties) throws GeneralSecurityException {
        return doVerifySign(publicKey, sign, getDigestContent(bean, ignoredProperties));
    }
    
    /**
     * 对Map进行RSA验证签名
     * @param publicKey
     * @param sign
     * @param map
     * @return
     * @throws Exception
     */
    public static boolean verifySign(String publicKey, String sign, Map<String, Object> map) throws GeneralSecurityException {
        return doVerifySign(publicKey, sign, getDigestContent(map));
    }
    
    /**
     * 对bean生成摘要
     * @param bean
     * @param secretKey
     * @return
     * @throws Exception
     */
    public static String digest(String secretKey, Object bean, String... ignoredProperties) {
        String digestData = getDigestContent(bean, ignoredProperties);
        return doDigest(secretKey, digestData);
    }
    
    /**
     * 对Map生成摘要
     * @param secretKey
     * @param map
     * @return
     */
    public static String digest(String secretKey, Map<String, Object> map) {
        String digestData = getDigestContent(map);
        return doDigest(secretKey, digestData);
    }
    
    /**
     * 对bean进行验证摘要
     * @param digest
     * @param bean
     * @param secretKey
     * @return
     * @throws Exception
     */
    public static boolean verifyDigest(String secretKey, String digest, Object bean, String... ignoredProperties) {
        return doVerifyDigest(secretKey, digest, getDigestContent(bean, ignoredProperties));
    }
    
    /**
     * 对map进行验证摘要
     * @param secretKey
     * @param digest
     * @param map
     * @return
     */
    public static boolean verifyDigest(String secretKey, String digest, Map<String, Object> map) {
        return doVerifyDigest(secretKey, digest, getDigestContent(map));
    }
    
    private static String doSign(String privateKey, String signData) throws GeneralSecurityException {
        if (signData == null || signData.length() == 0) {
            return "";
        }
        return RSA.signWithMD5(privateKey, signData.getBytes(utf8));
    }
    
    private static boolean doVerifySign(String publicKey, String sign, String signData) throws GeneralSecurityException {
        if (sign == null || signData == null) {
            return false;
        }
        return RSA.verifyWithMD5(publicKey, signData.getBytes(utf8), sign);
    }
    
    private static String doDigest(String secretKey, String digestData) {
        if (digestData == null || digestData.length() == 0) {
            return "";
        }
        digestData += "&secretKey=" + secretKey;
        return DigestUtils.md5Hex(digestData.getBytes(utf8));
    }
    
    private static boolean doVerifyDigest(String secretKey, String digest, String digestData) {
        if (digest == null || digestData.length() == 0 || digest == null || digest.length() == 0) {
            return false;
        }
        String digestVerify = doDigest(secretKey, digestData);
        return digest.equals(digestVerify);
    }
    
    private static String getDigestContent(Object bean, String... ignoreProperties) {
        TreeMap<String, String> treeMap = bean2TreeMap(bean, ignoreProperties);
        return join(treeMap);
    }
    
    private static String getDigestContent(Map<String, Object> map) {
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                treeMap.put(key, map.get(key).toString());
            }
        }
        return join(treeMap);
    }
    
    private static TreeMap<String, String> bean2TreeMap(Object bean, String... ignoredProperties) {
        Set<String> ignoredSet = new HashSet<String>();
        ignoredSet.add("class");
        if (ignoredProperties != null) {
            ignoredSet.addAll(Arrays.asList(ignoredProperties));
        }
        
        try {
            TreeMap<String, String> treeMap = new TreeMap<String, String>();
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean);
            for (PropertyDescriptor propertyDescriptor : descriptors) {
                String propertyName = propertyDescriptor.getName();
                if (ignoredSet.contains(propertyName)) {
                    continue;
                }
                Object value = PropertyUtils.getNestedProperty(bean, propertyName);
                if (value != null)
                    treeMap.put(propertyName, value.toString());
            }
            return treeMap;
        } catch (Exception e) {
            throw new IllegalStateException("bean2TreeMap error", e);
        }
    }
    
    private static String join(TreeMap<String, String> treeMap) {
        StringBuilder sb = new StringBuilder();
        for (String key : treeMap.keySet()) {
            if (treeMap.get(key) != null && treeMap.get(key).length() > 0) {
                sb.append(sb.length() == 0 ? "" : "&").append(key).append("=").append(treeMap.get(key));
            }
        }
        return sb.toString();
    }
    
}
