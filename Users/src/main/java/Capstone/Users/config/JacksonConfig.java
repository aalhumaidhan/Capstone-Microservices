package Capstone.Users.config;

import Capstone.Users.bo.Register.Request.RegisterRequest;
import Capstone.Users.bo.Register.Request.RegisterRequestDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(RegisterRequest.class, new RegisterRequestDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}