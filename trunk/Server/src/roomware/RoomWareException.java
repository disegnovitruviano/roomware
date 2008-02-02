package roomware;

public class RoomWareException extends Exception {

	private final static long serialVersionUID = 9857843872700482L;
	public RoomWareException() {
		super();
	}

	public RoomWareException(String message) {
		super(message);
	}

	public RoomWareException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoomWareException(Throwable cause) {
		super(cause);
	}

}
