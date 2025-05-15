package gz.dmndev.restaurant.kitchen.domain.exception;

/** Exception thrown when a domain-level error occurs in the Kitchen service */
public class KitchenDomainException extends RuntimeException {

  /**
   * Creates a new KitchenDomainException with the specified message
   *
   * @param message the detail message
   */
  public KitchenDomainException(String message) {
    super(message);
  }

  /**
   * Creates a new KitchenDomainException with the specified message and cause
   *
   * @param message the detail message
   * @param cause the cause
   */
  public KitchenDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
