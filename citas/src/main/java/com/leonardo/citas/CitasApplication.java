package com.leonardo.citas;

import com.leonardo.commons.clients.MedicoClient;
import com.leonardo.commons.clients.PacienteClient;
import com.leonardo.commons.configuration.FeignClientConfig;
import com.leonardo.commons.exceptions.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(clients = {MedicoClient.class, PacienteClient.class})
@Import({GlobalExceptionHandler.class, FeignClientConfig.class})
public class CitasApplication {
    public static void main(String[] args) {
        SpringApplication.run(CitasApplication.class, args);
    }
}
