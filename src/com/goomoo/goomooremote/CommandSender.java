package com.goomoo.goomooremote;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import javax.net.ssl.SSLException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CommandSender {
    private Socket socket;
	private final Context context;

	private static final char[] ALLOWED_BYTES = new char[] {0x64, 0x00, 0x01, 0x00};
	private static final char[] DENIED_BYTES = new char[] {0x64, 0x00, 0x00, 0x00};
	private static final char[] TIMEOUT_BYTES = new char[] {0x65, 0x00};
    
	private static final String uniqueId = "uniqueID_goodmood";
	private static final String applicationName = "goodmood_remote";
	private static final String APP_STRING = "android.app.gary_remote";
	private static final String TV_APP_STRING = "android.app.gary_remote";
	
    public final static int COMMAND_PORT = 55000;//
	
	public static final String ALLOWED = "ALLOWED";
	public static final String DENIED = "DENIED";
	public static final String TIMEOUT = "TIMEOUT";
	
	
    private BufferedWriter writer;
    private InputStreamReader reader ;
    
    CommandSender (Context ctx)
    {
    	context = ctx ;
    }
    
	private static Writer writeText(Writer writer, String text) throws IOException {
		return writer.append((char)text.length()).append((char) 0x00).append(text);
	}
    
	private static Writer writeBase64Text(Writer writer, String text) throws IOException {
		String b64 = Base64.encodeBytes(text.getBytes());
		return writeText(writer, b64);
	}
    
	private String getRegistrationPayload(String ip) throws IOException {
		StringWriter writer = new StringWriter();
		writer.append((char)0x64);
		writer.append((char) 0x00);
		writeBase64Text(writer, ip);
		writeBase64Text(writer, uniqueId);
		writeBase64Text(writer, applicationName);
		writer.flush();
		return writer.toString();
	}
	
	private static char[] readCharArray(Reader reader) throws IOException {
		if (reader.markSupported()) reader.mark(1024);
		int length = reader.read();
		int delimiter = reader.read();
		if (delimiter != 0) {
			if (reader.markSupported()) reader.reset();
			throw new IOException("Unsupported reply exception");
		}
		char[] buffer = new char[length];
		reader.read(buffer);
		return buffer;
	}
	
	private static String readText(Reader reader) throws IOException {
		char[] buffer = readCharArray(reader);
		return new String(buffer);
	}
	
	private String readRegistrationReply(Reader reader) throws IOException {
		reader.read(); // Unknown byte 0x02
		String text1 = readText(reader); // Read "unknown.livingroom.iapp.samsung" for new RC and "iapp.samsung" for already registered RC
		char[] result = readCharArray(reader); // Read result sequence
		if (Arrays.equals(result, ALLOWED_BYTES)) {
			return ALLOWED;
		} else if (Arrays.equals(result, DENIED_BYTES)) {
			return DENIED;
		} else if (Arrays.equals(result, TIMEOUT_BYTES)) {
			return TIMEOUT;
		} else {
			StringBuilder sb = new StringBuilder();
			for (char c : result) {
				sb.append(Integer.toHexString(c));
				sb.append(' ');
			}
			String hexReturn = sb.toString();
			return hexReturn;
		}
	}
    
    public boolean init (InetAddress addr) {
        // Set up the new connection.
        try {
      	  //«Ø¥ßsocket
      	  socket = new Socket (addr, COMMAND_PORT) ;
    		InetAddress localAddress = socket.getLocalAddress();
      	  
    		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  		writer.append((char)0x00);
  		writeText(writer, APP_STRING);
  	
  		writeText(writer, getRegistrationPayload(localAddress.getHostAddress()));
  		writer.flush();

  		InputStream in = socket.getInputStream();
  		reader = new InputStreamReader(in);
  		String result = readRegistrationReply(reader);
        } catch (SSLException e) {
			Toast.makeText(context, "CommandSender::init 1 " + e.toString(), 
					Toast.LENGTH_LONG).show() ;
          return false;
        } catch (IOException e) {
			Toast.makeText(context, "CommandSender::init 2 " + e.toString(), 
					Toast.LENGTH_LONG).show() ;
        	return false ;
        }

        /*
        sender = new AnymoteSender(coreService);
        if (!sender.setSocket(socket, writer, reader)) {
          Log.e(LOG_TAG, "Initial message failed");
          sender.disconnect();
          try {
            socket.close();
          } catch (IOException e) {
            Log.e(LOG_TAG, "failed to close socket");
          }
          return ConnectionStatus.ERROR_CREATE;
        }

        // Connection successful - we need to reset connection attempts counter,
        // so next time the connection will drop we will try reconnecting.
        return ConnectionStatus.OK;
        */
        return true ;
      }
    
	private String getKeyPayload(GKey key) throws IOException {
		StringWriter writer = new StringWriter();
		writer.append((char)0x00);
		writer.append((char)0x00);
		writer.append((char)0x00);
		writeBase64Text(writer, key.getValue());
		writer.flush();
		return writer.toString();
	}
    
	public void sendKey(GKey key) throws IOException {
		writer.append((char)0x00);
		writeText(writer, TV_APP_STRING);
		writeText(writer, getKeyPayload(key));
		writer.flush();
		int i = reader.read(); // Unknown byte 0x00
		String t = readText(reader);  // Read "iapp.samsung"
		char[] c = readCharArray(reader);
//		System.out.println(i);
//		System.out.println(t);
//		for (char a : c) System.out.println(Integer.toHexString(a));
		//System.out.println(c);
	}
	
}
