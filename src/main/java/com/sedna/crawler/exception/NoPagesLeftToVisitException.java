package com.sedna.crawler.exception;

public class NoPagesLeftToVisitException extends Exception {

  private static final long serialVersionUID = 5083985302002793084L;

  public NoPagesLeftToVisitException() {
    super();
  }

  public NoPagesLeftToVisitException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public NoPagesLeftToVisitException(
      String message,
      Throwable cause) {
    super(message, cause);
  }

  public NoPagesLeftToVisitException(
      String message) {
    super(message);
  }

  public NoPagesLeftToVisitException(
      Throwable cause) {
    super(cause);
  }
}
