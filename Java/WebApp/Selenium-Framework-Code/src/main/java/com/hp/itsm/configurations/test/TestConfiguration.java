package com.hp.itsm.configurations.test;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Getter
@Configuration
@ComponentScan(basePackages = { "com.hp" })
public class TestConfiguration {

    @Configuration
    @PropertySource("classpath:properties/dev.properties")
    @Profile({ "dev" })
    static class Dev {
    }

    @Configuration
    @PropertySource("classpath:properties/test.properties")
    @Profile({ "test" })
    static class Test {
    }

    @Value("${serviceNow.baseUri}")
    private String baseUri;

    @Value("${serviceNow.userName}")
    private String userName;

    @Value("${serviceNow.password}")
    private String password;


    public String getUserName() {
        String value = System.getProperty("userName");
        if (value == null || value.isEmpty()) {
            return this.userName;
        }
        return value;
    }

    public String getPassword() {
        String value = System.getProperty("password");
        if (value == null || value.isEmpty()) {
            return this.password;
        }
        return value;
    }

}
