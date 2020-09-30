package com.cloudfle.crawler.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudfle.crawler.model.PageResults;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FileResultAggregatorService implements ResultAggregatorService {

  @Autowired
  private JsonFactory jsonFactory;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private Logger logger;

  /**
   * By default this file aggregator will generate a file called `result.json` in the same directory where the application is
   * executed. If you want to override this behavour and write the results into a different file or folder, simply pass this
   * config variable in through program/JVM arguments or environment variable
   */
  @Value ("${resultOutputFilePath:result.json}")
  private Path resultOutputFilePath;

  /**
   * This file based aggregator will write all the results into a file.
   */
  @Override
  public void aggregate(Stream<PageResults> results) {
    // Open file output stream
    try (OutputStream output = new FileOutputStream(resultOutputFilePath.toFile())) {
      // Initialise json streamer
      JsonGenerator jsonGenerator = jsonFactory.createGenerator(output, JsonEncoding.UTF8);
      jsonGenerator.setCodec(objectMapper);

      // Start JSON array
      jsonGenerator.writeStartArray();

      // Write all results
      results.forEach(result -> {
        try {
          jsonGenerator.writeObject(result);
        } catch (IOException e) {
          logger.error("Error while writing result object {}: {}", result.getPageUrl(), e.getMessage());

        }
      });

      // Close array and close streamer
      jsonGenerator.writeEndArray();
      jsonGenerator.close();
    } catch (IOException e) {
      logger.error("Error while generating result file: {}", e.getMessage());
    }
  }
}
