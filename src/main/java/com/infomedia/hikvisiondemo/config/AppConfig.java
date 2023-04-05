package com.infomedia.hikvisiondemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Locale;

@Configuration
public class AppConfig {

    static {
        Locale.setDefault(Locale.forLanguageTag("es"));
    }

    @Bean
    public Locale locale(){
        return new Locale("es","419");
    }
}
