package com.cloudfle.crawler.exception;

public class MaxVisitedPagesException extends Exception {

  private static final long serialVersionUID = -6631894721484378128L;

  public MaxVisitedPagesException() {
    super();
  }

  public MaxVisitedPagesException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public MaxVisitedPagesException(
      String message,
      Throwable cause) {
    super(message, cause);
  }

  public MaxVisitedPagesException(
      String message) {
    super(message);
  }

  public MaxVisitedPagesException(
      Throwable cause) {
    super(cause);
  }


}
