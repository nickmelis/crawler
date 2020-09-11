package com.sedna.crawler;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedna.crawler.model.PageResults;
import com.sedna.crawler.service.CrawlerService;

@SpringBootApplication
public class Application implements CommandLineRunner {

  @Autowired
  private CrawlerService crawlerService;
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
    List<PageResults> results = crawlerService.search(args[0]);
    logger.debug(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(results));

  }
}
