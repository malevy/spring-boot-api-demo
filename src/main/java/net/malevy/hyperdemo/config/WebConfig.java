package net.malevy.hyperdemo.config;

import net.malevy.hyperdemo.messageconverters.HalWstlHttpMessageConverter;
import net.malevy.hyperdemo.messageconverters.SirenWstlHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@Profile("!unittest")
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        // insert the converters at the front of the list so that our requests aren't
        // hijacked by the jackson converter. the jackson converter registers support
        // for the mediatype 'application/*+json'
        converters.add(0, new SirenWstlHttpMessageConverter());
        converters.add(0, new HalWstlHttpMessageConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .ignoreAcceptHeader(false)
                .favorParameter(false);;
    }

}
