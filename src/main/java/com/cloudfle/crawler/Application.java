package com.cloudfle.crawler;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.cloudfle.crawler.model.PageResults;
import com.cloudfle.crawler.service.CrawlerService;
import com.cloudfle.crawler.service.ResultAggregatorService;

@SpringBootApplication
public class Application implements CommandLineRunner {

  @Autowired
  private CrawlerService crawlerService;
  @Autowired
  private ResultAggregatorService resultAggregatorService;
  @Autowired
  private Logger logger;

  public static void main(String[] args) {
    new SpringApplicationBuilder(Application.class)
      .web(WebApplicationType.NONE)
      .run(args);
  }

  @Override
  public void run(String... args) throws Exception {
    if (args.length == 0) {
      logger.error("No URL provided");
      System.exit(-1);
    }
    // Get crawler results
    Stream<PageResults> results = crawlerService.search(args[0]);

    // Aggregate and consume results
    resultAggregatorService.aggregate(results);
  }
}
