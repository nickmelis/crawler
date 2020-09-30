package com.cloudfle.crawler.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudfle.crawler.exception.MaxVisitedPagesException;
import com.cloudfle.crawler.exception.NoPagesLeftToVisitException;
import com.cloudfle.crawler.exception.PageProcessingException;
import com.cloudfle.crawler.model.PageResults;

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
   * loop through all links found and, for each page found, return a result The results will be made available through a stream
   *
   * @param url the base URL for the website we want to crawl
   * @return a {@link Stream} of {@link PageResults} with the information about each page linked to the url passed in
   * @throws MalformedURLException when the argument passed in is not a well formed URL
   */
  public Stream<PageResults> search(String url) throws MalformedURLException {

    // Auxiliary structures for this iteration
    // Note: if we want the stream generated later on to be a parallel stream,
    // we need the following two structures to be thread safe
    Set<URL> pagesVisited = Collections.synchronizedSet(new HashSet<>());
    List<URL> pagesToVisit = Collections.synchronizedList(new LinkedList<>());

    // Start with URL provided
    pagesToVisit.add(new URL(url));

    // Create stream of results - The resulting stream will be a parallel stream, so that
    // crawling operations don't have to go sequentially. This will result in better performances
    return StreamSupport.stream(new GeneratingSpliterator(pagesToVisit, pagesVisited), true);
  }

  private PageResults processPage(
      final Set<URL> pagesVisited, final List<URL> pagesToVisit)
      throws NoPagesLeftToVisitException, MaxVisitedPagesException {

    // Check that we are not over the max limit of pages to visit set in the config
    if (pagesVisited.size() >= maxPagesToSearch) {
      throw new MaxVisitedPagesException();
    }

    // Check that there are still pages we need to visit, if not, exit
    if (pagesToVisit.isEmpty()) {
      throw new NoPagesLeftToVisitException();
    }

    // Fetch the first URL in the queue
    URL currentUrl = pagesToVisit.remove(0);
    // If we already visited this URL, move to the next element
    if (pagesVisited.contains(currentUrl)) {
      logger.debug("Page at URL {} already visited. Skipping", currentUrl);
      return processPage(pagesVisited, pagesToVisit);
    }

    // Start processing
    try {
      // Process page
      PageResults pageResult = webPageProcessorService.process(currentUrl);
      // Mark all links found as pages to visit
      pagesToVisit.addAll(pageResult.getLinks());
      // Make sure we mark the URL we just visited
      pagesVisited.add(currentUrl);
      return pageResult;
    } catch (PageProcessingException e) {
      // Something went wrong when processing the current page. Log an error and move to the next element.
      logger.error("Error while processing page at URL {}: {}", currentUrl, e.getMessage());
      return processPage(pagesVisited, pagesToVisit);
    }
  }

  public class GeneratingSpliterator implements Spliterator<PageResults> {
    final List<URL> pagesToVisit;
    final Set<URL> pagesVisited;

    public GeneratingSpliterator(
        final List<URL> pagesToVisit,
        final Set<URL> pagesVisited) {
      this.pagesToVisit = pagesToVisit;
      this.pagesVisited = pagesVisited;
    }

    @Override
    public int characteristics() {
      return 0;
    }

    @Override
    public long estimateSize() {
      return Long.MAX_VALUE;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super PageResults> action) {
      // No more pages to visit, or reached maximum number of pages we want to visit
      if (pagesToVisit.isEmpty() || pagesVisited.size() >= maxPagesToSearch) {
        return false;
      }

      try {
        action.accept(processPage(pagesVisited, pagesToVisit));
        return true;
      } catch (NoPagesLeftToVisitException | MaxVisitedPagesException e) {
        return false;
      }
    }

    @Override
    public Spliterator<PageResults> trySplit() {
      return null;
    }
  }
}
