package com.cloudfle.crawler.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.cloudfle.crawler.exception.PageProcessingException;
import com.cloudfle.crawler.model.PageResults;

@RunWith (MockitoJUnitRunner.class)
public class WebPageProcessorServiceTest {

  @Mock
  private HttpService httpService;
  @Mock
  private Logger logger;

  @InjectMocks
  public WebPageProcessorService service;

  @Test
  public void whenProcessingProductPage_willReturnResultWithLinksAndStaticAssets() throws IOException, PageProcessingException {
    URL productUrl = new URL("https://sedna.com/product");
    String productHtml = "<html>"
      + "  <head>"
      + "    <script src=\"/static/js/abc.js\" type=\"text/javascript\" ></script>"
      + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"/static/css/style.css\">"
      + "  </head>"
      + "  <body>"
      + "    <a href=\"/cart\">Blah</a>"
      + "    <img src=\"/static/images/image.png\" alt=\"SEDNA\">"
      + "  </body>"
      + "</html>";
    when(httpService.get(productUrl)).thenReturn(Jsoup.parse(productHtml, productUrl.toExternalForm()));

    URL cartUrl = new URL("https://sedna.com/cart");
    URL imageUrl = new URL("https://sedna.com/static/images/image.png");
    URL cssUrl = new URL("https://sedna.com/static/css/style.css");
    URL jsUrl = new URL("https://sedna.com/static/js/abc.js");

    PageResults results = service.process(productUrl);

    assertThat(results.getPageUrl(), is(productUrl));
    assertThat(results.getLinks(), hasItems(cartUrl));
    assertThat(results.getStaticAssets().getImages(), hasItems(imageUrl));
    assertThat(results.getStaticAssets().getCss(), hasItems(cssUrl));
    assertThat(results.getStaticAssets().getJs(), hasItems(jsUrl));
  }

  @Test
  public void whenProcessingPageWithExternalLinks_willReturnInternalLinksOnly() throws IOException, PageProcessingException {
    URL productUrl = new URL("https://sedna.com/product");
    String productHtml = "<html>"
      + "  <head>"
      + "    <script src=\"/static/js/abc.js\" type=\"text/javascript\" ></script>"
      + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"/static/css/style.css\">"
      + "  </head>"
      + "  <body>"
      + "    <a href=\"/cart\">Blah</a>"
      + "    <a href=\"https://www.linkedin.com/company/sedna-network/\">LinkedIn</a>\""
      + "  </body>"
      + "</html>";
    when(httpService.get(productUrl)).thenReturn(Jsoup.parse(productHtml, productUrl.toExternalForm()));

    URL cartUrl = new URL("https://sedna.com/cart");
    URL cssUrl = new URL("https://sedna.com/static/css/style.css");
    URL jsUrl = new URL("https://sedna.com/static/js/abc.js");

    PageResults results = service.process(productUrl);

    assertThat(results.getPageUrl(), is(productUrl));
    assertThat(results.getLinks(), hasSize(1));
    assertThat(results.getLinks(), hasItems(cartUrl));
    assertThat(results.getStaticAssets().getImages(), hasSize(0));
    assertThat(results.getStaticAssets().getCss(), hasItems(cssUrl));
    assertThat(results.getStaticAssets().getJs(), hasItems(jsUrl));
  }
}
