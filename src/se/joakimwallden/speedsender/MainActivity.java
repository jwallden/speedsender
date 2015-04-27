package se.joakimwallden.speedsender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Handler;

import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.app.Activity;
import android.app.Service;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.Context;
import android.content.Intent;


public class MainActivity extends Activity {
	private TextView dataType;// = (TextView)findViewById(R.id.textView1);
	private TextView netType;// = (TextView)findViewById(R.id.textView2);
	private TextView serverStatus;
	private ToggleButton toggleServer;
	private ToggleButton toggleClient;
	private Button startServer;
	private Button startClient;
	SpeedServer speedServer; 
	SpeedClient speedClient;
	Service server;
	Intent nu;
	//PowerManager.WakeLock wl;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataType = (TextView)findViewById(R.id.textView1);
        netType = (TextView)findViewById(R.id.textView2);
        serverStatus = (TextView)findViewById(R.id.serverStatus);
        toggleServer = (ToggleButton)findViewById(R.id.toggleServer);
        toggleClient = (ToggleButton)findViewById(R.id.toggleClient);
        
        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SpeedSenderWakeLock");
		
        
        serverStatus.setText("Client or Server, what's it gonna be?");
        nu = new Intent(this, SpeedServerService.class);
        
               
        
        toggleServer.setChecked(false);
        toggleServer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(toggleServer.isChecked()){
                	serverStatus.setText("Server");
                	stopService(nu);
                	toggleClient.setChecked(false);
                	nu.putExtra("ROLE", "SERVER");
                	//wl.acquire();
                	startService(nu);
                }
                else {
                	stopService(nu);
                	//wl.release();
                }
            }
        });
        
        toggleClient.setChecked(false);
        toggleClient.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (toggleClient.isChecked()){
					serverStatus.setText("Client");
			      	stopService(nu);
			      	toggleServer.setChecked(false);
	            	nu.putExtra("ROLE", "CLIENT");
	            	startService(nu);
				}
				else {
					stopService(nu);
				}
			}
		});
        
       dataType.setText(getDataType());
       netType.setText(getNetworkType());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onResume() {
     super.onResume();
     dataType = (TextView)findViewById(R.id.textView1);
     netType = (TextView)findViewById(R.id.textView2);
    }
   
    public String getDataType(){
    	       
    	TelephonyManager teleMan = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    	int type = teleMan.getNetworkType();
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
    
    public String getNetworkType(){
    	ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo info = cm.getActiveNetworkInfo();
    	return info.getTypeName().toUpperCase();
    }
    
    public String getIpAddr() {
    	   WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    	   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    	   Log.d("SERVER", wifiInfo.toString());
    	   DhcpInfo d = wifiManager.getDhcpInfo();
    	   return intToIp(d.gateway);  	   
    	 }
    
    public String intToIp(int i) {
    	   
    	   return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ((i >> 24 ) & 0xFF );
    	}
}
