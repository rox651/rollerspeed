package com.rollerspeed.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI rollerspeedOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Roller Speed API")
                                                .description("API para la gestión de la escuela de patinaje Roller Speed")
                                                .version("1.0"));
        }
}