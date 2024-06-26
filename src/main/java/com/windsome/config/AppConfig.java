package com.windsome.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    String apiKey = "4013418525116234";
    String secretKey = "FSeTbffI9qwh9hU4E5VZvkcePsp3j1FzLldMm6otxjX31neaEbB8PMj9hwsR183ItUhqc5ECapBuZkpK";

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

        return modelMapper;
    }

    @Bean
    public ObjectMapper  objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, secretKey);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
