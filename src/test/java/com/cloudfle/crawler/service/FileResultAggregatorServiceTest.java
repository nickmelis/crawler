package com.cloudfle.crawler.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import com.cloudfle.crawler.model.PageResults;
import com.cloudfle.crawler.model.StaticAssets;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith (MockitoJUnitRunner.class)
public class FileResultAggregatorServiceTest {

  @Mock
  private JsonFactory jsonFactory;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private Logger logger;

  @InjectMocks
  private FileResultAggregatorService service;

  @Before
  public void setUp() {
    ReflectionTestUtils.setField(service, "resultOutputFilePath", Paths.get("test.json"));
  }

  @Test
  public void whenStreamingTwoResults_willSaveThemIntoAFile() throws IOException {
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

    JsonGenerator jsonGenerator = mock(JsonGenerator.class);
    when(jsonFactory.createGenerator(any(OutputStream.class), eq(JsonEncoding.UTF8))).thenReturn(jsonGenerator);

    service.aggregate(Stream.of(result1, result2));

    verify(jsonGenerator).writeStartArray();

    verify(jsonGenerator).writeObject(result1);
    verify(jsonGenerator).writeObject(result2);

    verify(jsonGenerator).writeEndArray();
    verify(jsonGenerator).close();
  }
}
