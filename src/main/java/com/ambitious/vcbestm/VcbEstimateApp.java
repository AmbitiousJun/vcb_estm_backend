package com.ambitious.vcbestm;

import com.ambitious.vcbestm.util.ContextUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Ambitious
 * @date 2022/6/13 20:36
 */
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class VcbEstimateApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(VcbEstimateApp.class, args);
        ContextUtils.initContext(ctx);
    }
}
