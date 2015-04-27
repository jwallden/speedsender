package se.joakimwallden.speedsender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
//import android.widget.TextView;
import android.widget.TextView;

public class SpeedClient extends Thread {

	boolean stopFlag = false;
	Context clientContext;
	TextView serverOutput;
	DatagramSocket socket;
	String msg = "No connection speed set yet";
	NotificationCompat.Builder noti;
	NotificationManager notificationManager; 
	
	public SpeedClient(Context context){
		clientContext = context;
		notificationManager = (NotificationManager) clientContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notifyConnectionSpeed(msg);
	}
	
	@Override
	public void run() {
				
		try {
			socket = new DatagramSocket(4444);
			//socket.setSoTimeout(2000);
		} catch (Exception e) {
			Log.d("CLIENT", "Kunde inte skapa socket");
			e.printStackTrace();
		}
	    
	    byte[] buf = new byte[1024];
	    DatagramPacket packet = new DatagramPacket(buf, buf.length);
	    Log.d("CLIENT", "Waiting for data");
	    while (!stopFlag) {
	      try {
			socket.receive(packet);
			msg = new String(buf, 0, packet.getLength());
			updateNotification(msg);
	          Log.d("CLIENT", packet.getAddress().getHostName()
	                  + ": " + msg);
	      } catch (SocketTimeoutException e) {
			
	      } 
	      
	      catch (SocketException e){
	    	  endThread(); 
	      } 
	      
	      catch (IOException e) {
			Log.d("CLIENT", "Kunde inte ta emot paket");
			e.printStackTrace();
	      }
	      
	    }
	}
	
	
	public void endThread() {
		stopFlag = true;
		cancelNotification();
  	    Log.d("CLIENT", "Avslutar klienten");
	}

	public void notifyConnectionSpeed(String speed){
		noti = new NotificationCompat.Builder(clientContext)
			         .setContentTitle("Speed Sender Client")
			         .setContentText(speed)
			         .setSmallIcon(R.drawable.connection_none)
			         .setOngoing(true);
		
		notificationManager.notify(0, noti.build());
	}

	public void updateNotification(String speed){
		noti = new NotificationCompat.Builder(clientContext)
		.setContentTitle("Speed Sender Client")
		.setOngoing(true);
		
		if (speed.equals("NETWORK_TYPE_EDGE")) {
			noti.setContentText("EDGE")
				.setSmallIcon(R.drawable.connection_edge);
		}
		else if (speed.equals("NETWORK_TYPE_UMTS")){
			noti.setContentText("UMTS")
				.setSmallIcon(R.drawable.connection_3g);
		}
		else if (speed.equals("NETWORK_TYPE_HSPA")){
			noti.setContentText("HSPA")
				.setSmallIcon(R.drawable.connection_hsdpa);
		}
		else{
			noti.setContentText("No connection")
				.setSmallIcon(R.drawable.connection_none);
		}
				
		notificationManager.notify(0, noti.build());
	}
	
	public void cancelNotification(){
		notificationManager.cancel(0);
	}
	
}
