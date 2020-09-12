package com.sedna.crawler.service;

import java.util.List;
import java.util.stream.Stream;

import com.sedna.crawler.model.PageResults;

/**
 * Interface to consume the results from the crawler. It is intended to be implemented by a consumer which, for example, writes
 * the data in the console, or in a text file. This way the crawling process and the consumption of the results are completely
 * decoupled.
 */
public interface ResultAggregatorService {

  /**
   * Given a list of page results from the crawler process, this function aggregates the results and makes them available to the
   * end user
   *
   * @param results the {@link List} of {@link PageResults} from the crawler process
   */
  void aggregate(Stream<PageResults> results);

}
