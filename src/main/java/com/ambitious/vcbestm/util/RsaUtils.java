package com.ambitious.vcbestm.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * rsa加密工具类
 *
 * @author Ambitious
 * @date 2022/3/17 22:07
 */
@Slf4j
public class RsaUtils {

    /**
     * 生成一对rsa密钥
     * @param path 密钥存放路径
     * @param keyName 密钥名称
     * @param secret 盐值
     */
    public static void generateKeys(String path, String keyName, String secret) throws NoSuchAlgorithmException, IOException {
        // 初始化
        KeyPairGenerator generator = KeyPairGenerator.getInstance("rsa");
        SecureRandom random = new SecureRandom(secret.getBytes());
        generator.initialize(2048, random);
        // 获得密钥对，并写入本地文件中
        KeyPair keyPair = generator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        byte[] encodePublicKey = Base64.getEncoder().encode(publicKey.getEncoded());
        writeBytesToFile(path + "/" + keyName + ".pub", encodePublicKey);
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] encodePrivateKey = Base64.getEncoder().encode(privateKey.getEncoded());
        writeBytesToFile(path + "/" + keyName, encodePrivateKey);
    }

    /**
     * 将字节数组写入到文件中
     * @param fileName 文件名（绝对路径）
     * @param bytes （字节数组）
     */
    private static void writeBytesToFile(String fileName, byte[] bytes) throws IOException {
        File file = new File(fileName);
        if(!file.exists()) {
            // 文件不存在，先创建
            boolean isSuccess = file.createNewFile();
            if(!isSuccess) {
                log.error("无法创建文件：{}", fileName);
                return;
            }
        }
        Files.write(file.toPath(), bytes);
    }

    /**
     * 读取本地文件中的公钥
     * @param fileName 文件名称（绝对路径）
     * @return 公钥对象
     */
    public static PublicKey readPublicKey(String fileName) {
        try {
            byte[] encodePublicKey = readFile2BytesFromResource(fileName);
            encodePublicKey = Base64.getDecoder().decode(encodePublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encodePublicKey);
            KeyFactory factory = KeyFactory.getInstance("rsa");
            return factory.generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            log.error("获取密钥工厂异常", e);
            return null;
        } catch (InvalidKeySpecException e) {
            log.error("文件无法转换成公钥", e);
            return null;
        }
    }

    /**
     * 读取本地文件中的私钥
     * @param fileName 文件名称（绝对路径）
     * @return 私钥对象
     */
    public static PrivateKey readPrivateKey(String fileName) {
        try {
            byte[] encodePrivateKey = readFile2BytesFromResource(fileName);
            encodePrivateKey = Base64.getDecoder().decode(encodePrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodePrivateKey);
            KeyFactory factory = KeyFactory.getInstance("rsa");
            return factory.generatePrivate(spec);
        } catch (NoSuchAlgorithmException e) {
            log.error("获取密钥工厂异常", e);
            return null;
        } catch (InvalidKeySpecException e) {
            log.error("文件无法转换成公钥", e);
            return null;
        }
    }

    /**
     * 从 resource 中将文件读入成字节数组
     * @param fileName 文件名称
     * @return 字节数组
     */
    private static byte[] readFile2BytesFromResource(String fileName) {
        InputStream is = RsaUtils.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            log.error("读取不到文件: {}", fileName);
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int cur = -1;
        try {
            while ((cur = is.read()) != -1) {
                out.write(cur);
            }
        } catch (IOException e) {
            log.error("读取文件异常: {}", e.getMessage());
        }
        return out.toByteArray();
    }
}
