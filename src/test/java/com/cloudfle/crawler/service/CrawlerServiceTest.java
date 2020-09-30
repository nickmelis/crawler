package com.cloudfle.crawler.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import com.cloudfle.crawler.exception.PageProcessingException;
import com.cloudfle.crawler.model.PageResults;
import com.cloudfle.crawler.model.StaticAssets;

@RunWith (MockitoJUnitRunner.class)
public class CrawlerServiceTest {

  @Mock
  private WebPageProcessorService webPageProcessorService;
  @Mock
  private Logger logger;

  @InjectMocks
  private CrawlerService service;

  @Before
  public void setUp() {
    // Set variable for tests - as this is set by Spring automagically in the app, we need to manually set its value in unit tests
    ReflectionTestUtils.setField(service, "maxPagesToSearch", 100);
  }

  @Test
  public void whenFetchingUrl_willReturnASinglePageWithNoLinks() throws PageProcessingException, MalformedURLException {
    URL urlToVisit = new URL("https://sedna.com");
    when(webPageProcessorService.process(urlToVisit))
      .thenReturn(PageResults.builder()
        .withPageUrl(urlToVisit)
        .withStaticAssets(StaticAssets.builder().build())
        .build());

    List<PageResults> result = service.search(urlToVisit.toExternalForm()).collect(Collectors.toList());

    assertThat(result, hasSize(1));
    assertThat(result.get(0).getPageUrl(), is(urlToVisit));
    assertThat(result.get(0).getStaticAssets().getImages(), hasSize(0));
    assertThat(result.get(0).getStaticAssets().getCss(), hasSize(0));
    assertThat(result.get(0).getStaticAssets().getJs(), hasSize(0));
  }

  @Test
  public void whenFetchingUrl_willReturnTwoInterLinkedPages() throws PageProcessingException, MalformedURLException {
    URL homePage = new URL("https://sedna.com/");
    URL contactPage = new URL(homePage + "contact");
    when(webPageProcessorService.process(homePage))
      .thenReturn(PageResults.builder()
        .withPageUrl(homePage)
        .withLink(contactPage)
        .withStaticAssets(StaticAssets.builder().build())
        .build());
    when(webPageProcessorService.process(contactPage))
      .thenReturn(PageResults.builder()
        .withPageUrl(contactPage)
        .withLink(homePage)
        .withStaticAssets(StaticAssets.builder().build())
        .build());

    List<PageResults> result = service.search(homePage.toExternalForm()).collect(Collectors.toList());

    assertThat(result, hasSize(2));
    // Home page
    assertThat(result.get(0).getPageUrl(), is(homePage));
    assertThat(result.get(0).getLinks(), hasSize(1));
    assertThat(result.get(0).getLinks(), hasItem(contactPage));
    // Contact page
    assertThat(result.get(1).getPageUrl(), is(contactPage));
    assertThat(result.get(1).getLinks(), hasSize(1));
    assertThat(result.get(1).getLinks(), hasItem(homePage));
  }

  @Test
  public void whenFetchingUrl_willReturnASinglePageWithStaticAssets() throws PageProcessingException, MalformedURLException {
    URL urlToVisit = new URL("https://sedna.com/");
    URL imageUrl = new URL(urlToVisit + "assets/images/image.jpg");
    URL cssUrl = new URL(urlToVisit + "assets/css/style.css");
    URL jsUrl = new URL(urlToVisit + "assets/js/script.js");

    when(webPageProcessorService.process(urlToVisit))
      .thenReturn(PageResults.builder()
        .withPageUrl(urlToVisit)
        .withStaticAssets(StaticAssets.builder()
          .withImages(Set.of(imageUrl))
          .withCss(Set.of(cssUrl))
          .withJs(Set.of(jsUrl))
          .build())
        .build());

    List<PageResults> result = service.search(urlToVisit.toExternalForm()).collect(Collectors.toList());

    assertThat(result, hasSize(1));
    assertThat(result.get(0).getPageUrl(), is(urlToVisit));
    assertThat(result.get(0).getStaticAssets().getImages(), hasSize(1));
    assertThat(result.get(0).getStaticAssets().getImages(), hasItem(imageUrl));

    assertThat(result.get(0).getStaticAssets().getCss(), hasSize(1));
    assertThat(result.get(0).getStaticAssets().getCss(), hasItem(cssUrl));

    assertThat(result.get(0).getStaticAssets().getJs(), hasSize(1));
    assertThat(result.get(0).getStaticAssets().getJs(), hasItem(jsUrl));
  }
}
