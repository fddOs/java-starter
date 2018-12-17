package cn.ehai.web;

import cn.ehai.common.utils.AESUtils;
import cn.ehai.common.utils.LoggerUtils;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class JavaWebApplicationTests {

    @Test
    public void contextLoads() throws NoSuchPaddingException, UnsupportedEncodingException,
        InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException,
        BadPaddingException, InvalidKeyException {

        LoggerUtils.error(JavaWebApplicationTests.class,AESUtils.aesEncryptString("{\"errorCode\":15000403,\"message\":\"验证码接口错误，请稍后再试。\",\"result\":null}"));
        //LoggerUtils.error(JavaWebApplicationTests.class,AESUtils.aesDecryptString("cGi553yfbAxVGKtOLtsQ57WsKKMfPyjz6izQVo7Jw2QzJpgNZsbUmyp+kMXv+bJsLx3CT4BCSKO7wehI/e"));
    }

}
