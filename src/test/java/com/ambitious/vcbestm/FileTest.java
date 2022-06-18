package com.ambitious.vcbestm;

import com.ambitious.vcbestm.util.RsaUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.net.URL;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ambitious
 * @date 2022/6/17 22:18
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileTest {

    @Test
    public void testRead() throws IOException {
        ClassLoader loader = FileTest.class.getClassLoader();
        InputStream is = loader.getResourceAsStream("word_data/gaokao.txt");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int cur = -1;
        while ((cur = is.read()) != -1) {
            out.write(cur);
        }
        System.out.println(Arrays.toString(out.toByteArray()));
    }

    @Test
    public void testInitRsa() throws NoSuchAlgorithmException, IOException {
        String path = "D:/学习/软件工程综合实践/code/vcb_estm_backend/src/main/resources/rsa_keys";
        String keyName = "rsa_jwt";
        String secret = "Ambitious";
        RsaUtils.generateKeys(path, keyName, secret);
    }

    @Test
    public void testReadRsa() {
        String path = "rsa_keys/rsa_jwt";
        PrivateKey privateKey = RsaUtils.readPrivateKey(path);
        System.out.println(privateKey);
    }
}
