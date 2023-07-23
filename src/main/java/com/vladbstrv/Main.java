package com.vladbstrv;

import com.vladbstrv.customer.CustomerController;
import com.vladbstrv.customer.CustomerDataAccessService;
import com.vladbstrv.customer.CustomerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
