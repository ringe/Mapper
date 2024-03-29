package no.rin.mapper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;

public class IntentReceiver extends BroadcastReceiver{
	
	   private static final int NOTIFICATION_ID = 0;

	@Override
	   public void onReceive(Context context, Intent intent) {
	      String key = LocationManager.KEY_PROXIMITY_ENTERING;
			
	      Boolean entering = intent.getBooleanExtra(key, false);
			
	      if(entering){
	         String alertMessage;
	         alertMessage = intent.getStringExtra("alert");

	         PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
					
	         Notification notification = createNotification();
	         notification.setLatestEventInfo(context, "Proximity Alert!", alertMessage , pendingIntent);

	         NotificationManager notificationManager = (NotificationManager)              
	        		 context.getSystemService(Context.NOTIFICATION_SERVICE);

	         notificationManager.notify(NOTIFICATION_ID, notification);
	      }
	   }

	   private Notification createNotification(){
		Notification notification = new Notification();
			
		notification.when = System.currentTimeMillis();
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		
		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = 1500;
		notification.ledOffMS = 1500;
			
		return notification;
	   }
	}