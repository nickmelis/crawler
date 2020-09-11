package com.sedna.crawler.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sedna.crawler.exception.PageProcessingException;
import com.sedna.crawler.model.PageResults;

@Service
public class CrawlerService {

  @Autowired
  private WebPageProcessorService webPageProcessorService;
  @Autowired
  private Logger logger;

  /**
   * Maximum number of pages to process. Default value is 100, and this can be overridden by an environment variable, as well as
   * program or JVM argument
   */
  @Value ("${maxPagesToSearch:100}")
  private int maxPagesToSearch;

  /**
   * Entry point for crawling a website. The url parameter is the main website URL we want to crawl. The function will recursively
   * loop through all links found and, for each page found, return a result
   *
   * @param url the base URL for the website we want to crawl
   * @return a {@link List} of {@link PageResults} with the information about each page linked to the url passed in
   * @throws MalformedURLException when the argument passed in is not a well formed URL
   */
  public List<PageResults> search(String url) throws MalformedURLException {

    // Auxiliary structures for this iteration
    Set<URL> pagesVisited = new HashSet<>();
    List<URL> pagesToVisit = new LinkedList<>();
    List<PageResults> results = new LinkedList<>();

    // Start with URL provided
    URL urlToVisit = new URL(url);
    pagesToVisit.add(urlToVisit);

    // Loop through all links until there's no more to process, or we reach the max number of pages limit
    while (!pagesToVisit.isEmpty() && pagesVisited.size() < maxPagesToSearch) {
      processPage(pagesVisited, pagesToVisit, results);
    }

    logger.debug("Visited {} web page(s)", results.size());

    // Sort results based on page URL and return
    results.sort((r1, r2) -> r1.getPageUrl().toString().compareTo(r2.getPageUrl().toString()));
    return results;
  }

  private void processPage(final Set<URL> pagesVisited,
      final List<URL> pagesToVisit, final List<PageResults> results) {
    // Fetch the first URL in the queue
    URL currentUrl = pagesToVisit.remove(0);
    // If we already visited this URL, skip
    if (pagesVisited.contains(currentUrl)) {
      logger.debug("Page at URL {} already visited. Skipping", currentUrl);
      return;
    }
    // Process page
    try {
      PageResults pageResult = webPageProcessorService.process(currentUrl);
      pagesToVisit.addAll(pageResult.getLinks());
      results.add(pageResult);
    } catch (PageProcessingException e) {
      // Something went wrong when processing the current page. Log an error and move on.
      logger.error("Error while processing page at URL {}: {}", currentUrl, e.getMessage());
    }
    // Add page to list of visited URLs
    pagesVisited.add(currentUrl);
  }
}
