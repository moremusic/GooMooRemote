package com.goomoo.goomooremote;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import android.util.Log;
import android.widget.Toast;

//import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.content.Context;

public class DeviceFinder {

	private final Context context;
	private WifiManager wifiManager;//wifi
//	private boolean active;
	
	public final static InetAddress MULTICAST_ADDRESS;
    public final static short MULTICAST_PORT = 1900;//廣播用的port
    public final static int COMMAND_PORT = 55000;//送指令用的port
//    private static final int DEFAULT_TIMEOUT = 10000;
    private static final String MEDIA_RENDERER_UUID = "SamsungMRDesc.xml";
    private static final String PERSONAL_MESSAGE_RECEIVER_UUID = "PersonalMessageReceiver.xml";
    private static final String REMOTE_CONTROL_RECEIVER_UUID = "RemoteControlReceiver.xml";
    private static final String REMOCON_SN = "urn:samsung.com:device:RemoteControlReceiver:1";

    private InetAddress mBroadcastAddress;
    private String SEARCH_TEAMPLTE ; 
    private MulticastSocket socket ;
    
	InetAddress addr ;

	protected boolean isCSeries = false;
    
	static {
        try {
            MULTICAST_ADDRESS = InetAddress.getByName("239.255.255.250");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
	  public DeviceFinder(Context ctx) {
		  	context = ctx ;
		    mBroadcastAddress = MULTICAST_ADDRESS ;
			  
			try {
				socket = new MulticastSocket();
				
				socket.setReuseAddress(true);
				socket.joinGroup(mBroadcastAddress);
		    
				SEARCH_TEAMPLTE = 
				        "M-SEARCH * HTTP/1.1\r\n" +
						"HOST: " + mBroadcastAddress.getHostAddress() + ":55000\r\n" +
				        "MAN: \"ssdp:discover\"\r\n" + 
				        "USER-AGENT: " + System.getProperty("os.name") + "/" + System.getProperty("os.version") + " UPnP/1.1 SamyGo Remote\r\n" +
				        "ST: %s\r\n" + 
				        "MX: %d\r\n" +
				        "\r\n";
			}catch (IOException e) {
				Toast.makeText(context, "DeviceFinder::DeviceFinder " + e.toString(), 
						Toast.LENGTH_LONG).show() ;
			}
	  }
	  
	  private boolean procPacket (DatagramPacket packet)
	  {
			String data = new String(packet.getData(), 0, packet.getLength());
			Log.d("TESTSDASDSADA", data);
			if (data.contains(REMOTE_CONTROL_RECEIVER_UUID)) {
				
				// We have a C- or D-Series TV
				addr = packet.getAddress();
				this.isCSeries  = true;
			} else if (data.contains(PERSONAL_MESSAGE_RECEIVER_UUID) || data.contains(MEDIA_RENDERER_UUID)) {
				// B-Series
				addr = packet.getAddress();
			}else
			{
			}
			
//		BroadcastAdvertisement advert;
//	    String serviceName = "Samsung Smart TV";
//	    advert = new BroadcastAdvertisement(serviceName, addr, COMMAND_PORT);
	    
	    return true ;
	  }
	
	  public boolean checkWifi ()
	  {
		    wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

		    return wifiManager.isWifiEnabled() ;
	  }
	  
	  public boolean find ()
	  {
			try {
				String searchMessage = String.format(SEARCH_TEAMPLTE, REMOCON_SN, 3);
				byte[] buffer;
				buffer = searchMessage.getBytes("UTF-8");
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, mBroadcastAddress, MULTICAST_PORT);
				socket.send(packet);
			
				searchMessage = String.format(SEARCH_TEAMPLTE, "urn:schemas-upnp-org:device:MediaRenderer:1", 3);
				buffer = searchMessage.getBytes("UTF-8");
				packet = new DatagramPacket(buffer, buffer.length, mBroadcastAddress, MULTICAST_PORT);
				socket.send(packet);
			} catch (IOException e) {
				Toast.makeText(context, "DeviceFinder::find 1 "+e.toString(), 
						Toast.LENGTH_LONG).show() ;
			}
			
			
			byte[] buffer = new byte[8192];
			while (true) 
			{
			      try {
		    		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		    		socket.receive(packet);
		    		
		    		procPacket (packet) ;
			      } catch (InterruptedIOException e) {
						Toast.makeText(context, "DeviceFinder::find 2 "+e.toString(), 
								Toast.LENGTH_LONG).show() ;
			      }	catch (IOException e) {
			          // SocketException - stop() was called
						Toast.makeText(context, "DeviceFinder::find 3 "+e.toString(), 
								Toast.LENGTH_LONG).show() ;
			          break;
			      }
			}
			
			return false ;
	  }
}
