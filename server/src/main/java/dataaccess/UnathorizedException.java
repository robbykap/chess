package dataaccess;

public class UnathorizedException extends RuntimeException {
  public UnathorizedException(String message) {
    super(message);
  }
}
