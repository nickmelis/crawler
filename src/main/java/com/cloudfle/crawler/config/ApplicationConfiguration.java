package com.cloudfle.crawler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class ApplicationConfiguration {

  @Bean
  @Scope (ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public Logger logger(InjectionPoint injectionPoint) {
    Class<?> clazz = injectionPoint.getMember().getDeclaringClass();
    return LoggerFactory.getLogger(clazz);
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);
  }

  @Bean
  public JsonFactory jsonFactory() {
    return new JsonFactory();
  }
}
