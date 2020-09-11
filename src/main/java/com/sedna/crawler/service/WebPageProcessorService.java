package com.sedna.crawler.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sedna.crawler.exception.PageProcessingException;
import com.sedna.crawler.model.PageResults;

@Service
public class WebPageProcessorService {

  @Autowired
  private HttpService httpService;
  @Autowired
  private Logger logger;

  /**
   * This method fetches a {@link Document} representation of a web page at the given url, and looks for links (within the same
   * domain) and static assets in the page. The result is a {@link PageResults} object containing all internal links to other
   * pages, plus all links to images, css, and js assets.
   *
   * @param url The URL to visit
   * @return a {@link PageResults} object containing pages referenced and links to static assets
   */
  public PageResults process(URL url) throws PageProcessingException {
    try {
      // Get HTML document
      Document htmlDocument = httpService.get(url);
      // Initialise results object
      PageResults results = new PageResults(new URL(htmlDocument.baseUri()));
      // Find links
      addLinks(htmlDocument, results);
      // Find static assets
      addStaticAssets(htmlDocument, results);

      return results;
    } catch (IOException e) {
      // Something went wrong when executing HTTP request. Log and return an empty object
      logger.error("Unsuccessful HTTP request: {}", e.getMessage());
      throw new PageProcessingException(e);
    }
  }

  private void addLinks(Document htmlDocument, PageResults results) {
    Elements linksOnPage = htmlDocument.select("a[href]");

      for (Element link : linksOnPage) {
        try {
          URL href = new URL(link.absUrl("href"));

          if (!results.getPageUrl().getHost().equals(href.getHost())) {
            // Not same domain/ base url. Skip
            continue;
          }
          if (results.getLinks().contains(href)) {
            // Link already found. Skip
            continue;
          }

          logger.debug("Added link: {}", href);
          results.getLinks().add(href);
        } catch (MalformedURLException e) {
          logger.error("Error while fetching element with reference {}. Malformed URL: {}", link.tagName(), e.getMessage());
        }
      }

    logger.info("Found {} links", results.getLinks().size());
  }

  private void addStaticAssets(Document htmlDocument, PageResults results) {
    addImages(htmlDocument, results.getStaticAssets().getImages());
    addCss(htmlDocument, results.getStaticAssets().getCss());
    addJS(htmlDocument, results.getStaticAssets().getJs());
  }

  private void addAssets(final Document htmlDocument, final String selector,
      final String propertyToCapture, final Set<URL> results) {
    Elements elementsOnPage = htmlDocument.select(selector);

    for (Element domElement : elementsOnPage) {
      try {
        if (!domElement.hasAttr(propertyToCapture)) {
          // The element we selected doesn't have the attribute we want to capture.
          // This can happen for example when selecting JS with the tag <script> which contain JS code instead of a link to a resource
          logger.warn("The element {} doesn't have any property with name {}", domElement.tagName(), propertyToCapture);
          continue;
        }

        URL src = new URL(domElement.absUrl(propertyToCapture));

        if (!results.contains(src)) {
          // Only add to list if not already found.
          results.add(src);
        }
      } catch (MalformedURLException e) {
        logger.error("Error while fetching element with reference {}. Malformed URL: {}", domElement.tagName(), e.getMessage());
      }
    }
  }

  private void addImages(final Document htmlDocument, final Set<URL> imagesList) {
    addAssets(htmlDocument, "img[src]", "src", imagesList);
    logger.debug("Found {} images links", imagesList.size());
  }

  private void addCss(final Document htmlDocument, final Set<URL> cssList) {
    addAssets(htmlDocument, "link[rel=stylesheet]", "href", cssList);
    logger.debug("Found {} CSS links", cssList.size());
  }

  private void addJS(final Document htmlDocument, final Set<URL> jsList) {
    addAssets(htmlDocument, "script[type=text/javascript]", "src", jsList);
    logger.debug("Found {} JS links", jsList.size());
  }
}
