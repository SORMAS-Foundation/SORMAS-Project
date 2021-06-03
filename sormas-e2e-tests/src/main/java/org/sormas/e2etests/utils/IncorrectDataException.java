package org.sormas.e2etests.utils;

public class IncorrectDataException extends Throwable {
  public IncorrectDataException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}
