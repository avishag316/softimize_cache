import javax.management.Notification;
import javax.management.NotificationListener;

//Class for Testing 
public class Subscriber implements NotificationListener{
	private int subscriberId;
	
	public Subscriber(int id) {
		this.subscriberId = id;
	}

	//Print the notifications
	public void handleNotification(Notification anotification, Object aobject) {
		System.out.println("Notification:");
		System.out.println("subscriber id: " + String.valueOf(subscriberId) +
				": number of " +anotification.getType() + " is: " + anotification.getSequenceNumber());
	}

}