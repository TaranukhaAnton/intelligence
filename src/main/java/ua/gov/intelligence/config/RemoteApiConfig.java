package ua.gov.intelligence.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RemoteApiConfig {

    //    @Bean
    //    public WebClient localApiClient() {
    //        return WebClient.create("http://localhost:8080/api/v3");
    //    }

    //    @Bean
    //    public WebClient webClient() {
    //        return WebClient.create();
    //    }
    //

    @Bean
    public RestTemplate restTemplate() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }
}
