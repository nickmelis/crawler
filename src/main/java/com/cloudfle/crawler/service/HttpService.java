package com.cloudfle.crawler.service;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HttpService {

  @Autowired
  private Logger logger;

  // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

  /**
   * Returns a document object containing the DOM representation of the page at the given URL
   *
   * @param url the URL we want to send a request to
   * @return a Jsoup {@link Document} containing the page's DOM representation
   * @throws IOException if a HTTP connection issue occurs
   */
  public Document get(URL url) throws IOException {
    Connection connection = Jsoup.connect(url.toExternalForm()).userAgent(USER_AGENT);
    Document htmlDocument = connection.get();

    if (connection.response().statusCode() != 200) {
      System.out.println("**Failure** Response status code: " + connection.response().statusCode());
      throw new IOException("Response status code: " + connection.response().statusCode());
    }

    logger.debug("Received web page at {}", htmlDocument.baseUri());

    if (!connection.response().contentType().contains("text/html")) {
      logger.error("Retrieved something other than HTML");
      throw new IOException("Retrieved something other than HTML");
    }

    return htmlDocument;
  }
}
