package com.sedna.crawler.service;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  public void aggregate(Stream<PageResults> results) {
    // The only reason why we are aggregating the whole stream into a string is to print it
    // separately from all other log lines. This is very memory inefficient and in a production
    // enviroment should be replaced with a file based implementaion where the results are
    // written to the file one by one as they pass through the stream.
    logger.info("Results: \n{}", results.map(printJson)
      .collect(Collectors.joining(",\n", "[", "]")));
  }

  Function<PageResults, String> printJson = (result) -> {
    try {
      return objectMapper.writeValueAsString(result);
    } catch (JsonProcessingException e) {
      logger.error("Error while transforming page results to JSON: {}", e.getMessage());
      return "";
    }
  };
}
