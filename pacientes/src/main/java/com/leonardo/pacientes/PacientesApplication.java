package com.leonardo.pacientes;

import com.leonardo.commons.clients.CitaClient;
import com.leonardo.commons.configuration.FeignClientConfig;
import com.leonardo.commons.exceptions.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(clients = CitaClient.class)
@Import({GlobalExceptionHandler.class, FeignClientConfig.class})
public class PacientesApplication {
    public static void main(String[] args) {
        SpringApplication.run(PacientesApplication.class, args);
    }
}
