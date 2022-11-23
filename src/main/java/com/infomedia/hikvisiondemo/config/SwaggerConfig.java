package com.infomedia.hikvisiondemo.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@SecurityScheme(
        name = "X-API-KEY",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    @Value("${info.build.name}")
    private String projectName;

    @Value("${info.build.version}")
    private String projectVersion;

    @Value("${info.build.description}")
    private String projectDescription;

    @Bean
    public OpenApiCustomiser customizer() {

        return openApi -> openApi
                .info(new Info()
                        .title(projectName)
                        .version(projectVersion)
                        .description(projectDescription)
                        .contact(new Contact()
                                .name("Infomedia")
                                .url("https://www.infomediaservice.com/")));
    }
}
