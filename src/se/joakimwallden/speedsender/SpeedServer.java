package se.joakimwallden.speedsender;


import java.io.IOException;
import java.net.*;

import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

public final class SpeedServer extends Thread {
	int PORT = 4444;
	String IPADRESS = "192.168.1.255";
	String dataType;
	Context serverContext;
	Handler mHandler;
	boolean stopFlag = false;
	int dataTypeInt;
	TelephonyManager teleMan;
	DatagramSocket broadcastSocket = null;
	
	public SpeedServer(Context context, int dType){
		serverContext = context;
		teleMan = (TelephonyManager)serverContext.getSystemService(Context.TELEPHONY_SERVICE);
		dataTypeInt = dType;
	}
	@Override
	public void run() {
		//Log.d("SERVER", "Starting server thread");
			try {
				broadcastSocket = new DatagramSocket(PORT);
				InetAddress broadcastAdress;
				broadcastAdress = InetAddress.getByName(IPADRESS);
				dataType = getDataType(dataTypeInt);
				DatagramPacket broadcastPacket = new DatagramPacket(dataType.getBytes(), dataType.getBytes().length, broadcastAdress, PORT);
				 								 					broadcastSocket.setBroadcast(true);
				broadcastSocket.send(broadcastPacket);
			
			} catch (UnknownHostException e) {
				Log.d("SERVER", "Unknown host");
				e.printStackTrace();
			}
				
			catch (SocketException e) {
				Log.d("SERVER", "Socket exception");
				e.printStackTrace();
			}
			
			catch (IOException e) {
				Log.d("SERVER", "IO exception");
				e.printStackTrace();
			}
			Log.d("SERVER", dataType);
			broadcastSocket.close();
			return;
		
		
			
		
		
	}
		
	public String getDataType(int type){
	    //int type = teleMan.getNetworkType();
    	switch(type){
    	  case TelephonyManager.NETWORK_TYPE_UNKNOWN:
    	   return "NETWORK_TYPE_UNKNOWN";
    	  case TelephonyManager.NETWORK_TYPE_GPRS:
    	   return "NETWORK_TYPE_GPRS";
    	  case TelephonyManager.NETWORK_TYPE_EDGE:
    	   return "NETWORK_TYPE_EDGE";
    	  case TelephonyManager.NETWORK_TYPE_UMTS:
    	   return "NETWORK_TYPE_UMTS";
    	  case TelephonyManager.NETWORK_TYPE_HSDPA:
    	   return "NETWORK_TYPE_HSDPA";
    	  case TelephonyManager.NETWORK_TYPE_HSUPA:
    	   return "NETWORK_TYPE_HSUPA";
    	  case TelephonyManager.NETWORK_TYPE_HSPA:
    	   return "NETWORK_TYPE_HSPA";
    	  case TelephonyManager.NETWORK_TYPE_CDMA:
    	   return "NETWORK_TYPE_CDMA";
    	  case TelephonyManager.NETWORK_TYPE_EVDO_0:
    	   return "NETWORK_TYPE_EVDO_0";
    	  case TelephonyManager.NETWORK_TYPE_EVDO_A:
    	   return "NETWORK_TYPE_EVDO_0";
    	  case TelephonyManager.NETWORK_TYPE_EVDO_B:
    	   return "NETWORK_TYPE_EVDO_B";
    	  case TelephonyManager.NETWORK_TYPE_1xRTT:
    	   return "NETWORK_TYPE_1xRTT";
    	  case TelephonyManager.NETWORK_TYPE_IDEN:
    	   return "NETWORK_TYPE_IDEN";
    	  case TelephonyManager.NETWORK_TYPE_LTE:
    	   return "NETWORK_TYPE_LTE";
    	  case TelephonyManager.NETWORK_TYPE_EHRPD:
    	   return "NETWORK_TYPE_EHRPD";
    	  default:
    	   return "NETWORK_TYPE_UNKNOWN";
    	  }
    }
	
	
}

