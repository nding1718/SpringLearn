package hello;

public class StorageException extends RuntimeException {
	
	public StorageException(String message) {
		super(message);
	}

	public StroageException(String message, Throwable cause) {
		super(message, cause);
	}
}