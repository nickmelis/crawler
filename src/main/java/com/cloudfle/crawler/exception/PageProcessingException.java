package com.cloudfle.crawler.exception;

public class PageProcessingException extends Exception {

  private static final long serialVersionUID = 6872675059528970905L;

  public PageProcessingException() {
    super();
  }

  public PageProcessingException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public PageProcessingException(
      String message,
      Throwable cause) {
    super(message, cause);
  }

  public PageProcessingException(
      String message) {
    super(message);
  }

  public PageProcessingException(
      Throwable cause) {
    super(cause);
  }
}
