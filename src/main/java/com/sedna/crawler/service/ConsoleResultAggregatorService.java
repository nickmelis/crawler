package com.sedna.crawler.service;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedna.crawler.model.PageResults;

@Service
public class ConsoleResultAggregatorService implements ResultAggregatorService {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private Logger logger;

  @Override
  public void aggregate(List<PageResults> results) {
    try {
      logger.debug(objectMapper.writeValueAsString(results));
    } catch (JsonProcessingException e) {
      logger.error("Error while transforming page results to JSON: {}", e.getMessage());
    }
  }
}
