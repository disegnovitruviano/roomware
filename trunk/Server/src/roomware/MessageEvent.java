package roomware;

import java.util.*;


public class MessageEvent extends EventObject {

	private static final long serialVersionUID = 93729482759215L;
	protected Message message;
	protected Date date;

	public MessageEvent(Message message, Device receiver, Date time) {
		super(receiver);
		this.message = message;
		this.date = time;

	}


	public Message getMessage() {
		return message;
	}


	public Date getDate() {
		return date;
	}

}
