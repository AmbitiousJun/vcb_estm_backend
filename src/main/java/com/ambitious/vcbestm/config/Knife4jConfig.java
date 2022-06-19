package com.ambitious.vcbestm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author Ambitious
 * @date 2022/6/18 21:15
 */
@Configuration
@Profile({"dev", "test"})
@EnableSwagger2WebMvc
public class Knife4jConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder()
                .title("Vocabulary Estimation App Backend")
                .description("document of restful apis")
                .version("1.0")
                .build())
            .useDefaultResponseMessages(false)
            .select()
            //这里指定Controller扫描包路径
            .apis(RequestHandlerSelectors.basePackage("com.ambitious.vcbestm.controller"))
            .paths(PathSelectors.any())
            .build();
    }
}
