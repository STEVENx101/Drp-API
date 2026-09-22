package lk.fintrex.drpapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class DrpApiApplication
        extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application
    ) {
        return application.sources(
                DrpApiApplication.class
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(
                DrpApiApplication.class,
                args
        );
    }
}
