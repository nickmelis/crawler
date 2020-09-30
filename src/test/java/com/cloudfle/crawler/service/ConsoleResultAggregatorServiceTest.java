package com.cloudfle.crawler.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.cloudfle.crawler.model.PageResults;
import com.cloudfle.crawler.model.StaticAssets;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith (MockitoJUnitRunner.class)
public class ConsoleResultAggregatorServiceTest {

  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private Logger logger;

  @InjectMocks
  private ConsoleResultAggregatorService service;

  @Test
  public void whenStreamingTwoResults_willPrintThemInTheConsole() throws MalformedURLException, JsonProcessingException {
    URL homePage = new URL("https://sedna.com/");
    URL contactPage = new URL(homePage + "contact");

    PageResults result1 = PageResults.builder()
      .withPageUrl(homePage)
      .withLink(contactPage)
      .withStaticAssets(StaticAssets.builder().build())
      .build();
    PageResults result2 = PageResults.builder()
      .withPageUrl(contactPage)
      .withLink(homePage)
      .withStaticAssets(StaticAssets.builder().build())
      .build();
    when(objectMapper.writeValueAsString(result1)).thenReturn("result1");
    when(objectMapper.writeValueAsString(result2)).thenReturn("result2");

    service.aggregate(Stream.of(result1, result2));

    verify(objectMapper).writeValueAsString(result1);
    verify(objectMapper).writeValueAsString(result2);

    verify(logger).info("Results: \n{}", "[result1,\nresult2]");
  }
}
