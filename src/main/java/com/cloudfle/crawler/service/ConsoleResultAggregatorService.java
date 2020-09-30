package com.cloudfle.crawler.service;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.cloudfle.crawler.model.PageResults;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Primary
public class ConsoleResultAggregatorService implements ResultAggregatorService {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private Logger logger;

  /**
   * This console based aggregator will print all the results in the console, in JSON format
   */
  @Override
  public void aggregate(Stream<PageResults> results) {
    // The only reason why we are aggregating the whole stream into a string is to print it
    // separately from all other log lines. This is very memory inefficient and in a production
    // enviroment should be replaced with a file based implementaion, which is included in this project

    // To use the file based implementation, simply mark the class FileResultAggregatorService as @Primary
    // instead of this one.
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
