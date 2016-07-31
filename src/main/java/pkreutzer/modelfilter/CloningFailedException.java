package pkreutzer.modelfilter;

public final class CloningFailedException extends RuntimeException {

  private final Object objectToClone;

  public CloningFailedException(final Throwable cause, final Object objectToClone) {
    super(cause);
    this.objectToClone = objectToClone;
  }

  public final Object getObjectToClone() {
    return this.objectToClone;
  }

}
