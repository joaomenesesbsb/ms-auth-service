package com.meneses.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class MsAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAuthServiceApplication.class, args);

    }

}
