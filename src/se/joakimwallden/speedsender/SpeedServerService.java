package se.joakimwallden.speedsender;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SpeedServerService extends Service {

	int PORT = 4444;
	String IPADRESS = "192.168.1.255";
	String dataType;
	String role;
	List<String> dataTypes = Arrays.asList("NETWORK_TYPE_EDGE","NETWORK_TYPE_UMTS","NETWORK_TYPE_HSPA");
	Context ServerContext;
	Handler mHandler;
	boolean stopFlag = false;
	DatagramSocket broadcastSocket;
	InetAddress broadcastAdress;
	SpeedServer server;
	SpeedClient client;
	TelephonyManager teleMan;
	PhoneStateListener listenerDataType;
	PowerManager.WakeLock wl;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override
	public void onCreate() {
		ServerContext = getBaseContext();
		teleMan = (TelephonyManager)ServerContext.getSystemService(Context.TELEPHONY_SERVICE);
		PowerManager pm = (PowerManager) ServerContext.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SpeedSenderWakeLock");
		wl.acquire();
		if (wl.isHeld()) {
			Log.d("SERVICE", "Wakelock is held");
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		role = intent.getExtras().getString("ROLE");
		Log.d("SERVICE", role);
		if (role.equals("SERVER")){
			Toast.makeText(ServerContext, "Starting Server", Toast.LENGTH_LONG).show();
			
			listenerDataType = new PhoneStateListener(){
				@Override
				public void onDataConnectionStateChanged(int state, int networkType){
					if (wl.isHeld()) {
						Log.d("SERVICE", "Wakelock is held");
					}
					//if (dataTypes.contains(getDataType(networkType))) {
						Log.d("SERVICE", "Trying to run server due to state change");
						server = new SpeedServer(ServerContext, networkType);
						server.start();						
					//}
				}
			};
			teleMan.listen(listenerDataType, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		}
		if (role.equals("CLIENT")){
			Toast.makeText(ServerContext, "Starting client", Toast.LENGTH_LONG).show();
			client = new SpeedClient(getBaseContext());
			client.start();
			
		}
		Log.d("SERVICE", "Starting Service");
		
		return START_STICKY;
	}
	
	public void onDestroy() {
		teleMan.listen(listenerDataType, PhoneStateListener.LISTEN_NONE);
		//wl.release();
		if (role.equals("SERVER")){
			//server.endThread();
			Toast.makeText(ServerContext, "Stopping Server ", Toast.LENGTH_LONG).show();
			try {
				server.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d("SERVER", "Closing server thread");
		}
		
		if (role.equals("CLIENT")){
			//client.endThread();
			client.socket.close();
			Toast.makeText(ServerContext, "Stopping Client ", Toast.LENGTH_LONG).show();
			try {
				client.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d("CLIENT", "Closing client thread");
		}
		
		Log.d("SERVICE", "Closing Service");
	}
}
