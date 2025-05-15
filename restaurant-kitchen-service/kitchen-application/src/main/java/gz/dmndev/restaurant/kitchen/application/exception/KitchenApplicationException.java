package gz.dmndev.restaurant.kitchen.application.exception;

/** Exception thrown when an application-level error occurs in the Kitchen service */
public class KitchenApplicationException extends RuntimeException {

  /**
   * Creates a new KitchenApplicationException with the specified message
   *
   * @param message the detail message
   */
  public KitchenApplicationException(String message) {
    super(message);
  }

  /**
   * Creates a new KitchenApplicationException with the specified message and cause
   *
   * @param message the detail message
   * @param cause the cause
   */
  public KitchenApplicationException(String message, Throwable cause) {
    super(message, cause);
  }
}
