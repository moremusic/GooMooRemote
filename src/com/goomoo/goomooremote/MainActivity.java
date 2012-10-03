package com.goomoo.goomooremote;

import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
//import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
//import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;  
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.egoomoo.goomooremote.R;

public class MainActivity extends Activity {

	//
	private Context context ;
	private DeviceFinder deviceFinder ;
	private CommandSender commandSender ;
    String[] vItemText = new String[] {"TV Remote", "Game Pad", "Web Links",
    									"Setup"} ;
	
    private InetAddress inputDeviceAddr ;//額外輸入的 
    
	//cur mode
	private static int MODE_MAIN = -1 ;
	private static int MODE_TV_REMOTE = 0 ;
	private static int MODE_GAME_PAD = 1 ;
	private static int MODE_WEB_LINKS = 2 ;
	private static int MODE_SETUP = 3 ;

	int curMode ;
	
	boolean findDevice ()
	{
		if (deviceFinder == null)
			deviceFinder = new DeviceFinder (context) ;
		
		if (!deviceFinder.checkWifi ())
		{
			//找不到wifi
			Toast.makeText(context, "no wifi network!", 
					Toast.LENGTH_SHORT).show() ;
			
			return false ;
		}
		
		deviceFinder.init() ;
		
			//先設定固定ip
//		deviceFinder.find() ;
		if(deviceFinder.find() != null)
		{
			return true ;
		}else
		{
			//找不到smart tv
			Toast.makeText(context, "no deivce!", 
									Toast.LENGTH_SHORT).show() ;
			deviceFinder = null ;
			return false ;
		}
	}
	
	boolean initCommandSender (InetAddress addr)
	{
		commandSender = new CommandSender (context) ;
		return commandSender.init(addr) ;
	}

	void initSetup ()
	{
		curMode = MODE_SETUP ;
		
        setContentView(R.layout.setup);
        
        final EditText ed = (EditText)findViewById (R.id.ed_ip) ;

        String str = inputDeviceAddr.toString() ;
        String vStr[] = str.split("/") ;

//        ed.requestFocus() ;
        ed.setText(vStr[1]) ;
        
//		InputMethodManager imm = (InputMethodManager) 
//				getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

		
        /*
        ed.setOnClickListener (new OnClickListener (){
			public void onClick (View v){
				
//				ed.requestFocus() ;
//				imm.restartInput(ed) ;
			}
        }) ;
        */
	}
	
	void initTVRemote ()
	{
//		if (deviceFinder == null)
		findDevice () ;
		
		if (commandSender == null)
		{
//			if (deviceFinder.hasDevice())//用找到的
			if (deviceFinder != null)
				initCommandSender (deviceFinder.deviceAddr) ;
			else//使用輸入的
				initCommandSender (inputDeviceAddr) ;
		}
		
//		if (findDevice ())
//		if (deviceFinder.hasDevice())//有設備
		if (true)
		{
			curMode = MODE_TV_REMOTE ;
	        setContentView(R.layout.tv_remote);
			
	        //up
			Button btn = (Button)findViewById (R.id.btn_num_2) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_UP) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//left
			btn = (Button)findViewById (R.id.btn_num_4) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_LEFT) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//right
			btn = (Button)findViewById (R.id.btn_num_6) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_RIGHT) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//down
			btn = (Button)findViewById (R.id.btn_num_8) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_DOWN) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//enter
			btn = (Button)findViewById (R.id.btn_enter) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_ENTER) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//menu
			btn = (Button)findViewById (R.id.btn_menu) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_MENU) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//return
			btn = (Button)findViewById (R.id.btn_return) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_RETURN) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//mute
			btn = (Button)findViewById (R.id.btn_mute) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_MUTE) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//vol down
			btn = (Button)findViewById (R.id.btn_vol_down) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_VOLDOWN) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//vol up
			btn = (Button)findViewById (R.id.btn_vol_up) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_VOLUP) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//ch down
			btn = (Button)findViewById (R.id.btn_ch_down) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_CHDOWN) ;
					} catch (IOException e) {
					}
				}
			}) ;
			
			//ch up
			btn = (Button)findViewById (R.id.btn_ch_up) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_CHUP) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//play
			btn = (Button)findViewById (R.id.btn_play) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_PLAY) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//pause
			btn = (Button)findViewById (R.id.btn_pause) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_PAUSE) ;
					} catch (IOException e) {
					}
				}
			}) ;

			//stop
			btn = (Button)findViewById (R.id.btn_stop) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_STOP) ;
					} catch (IOException e) {
					}
				}
			}) ;
			
			//catv
			btn = (Button)findViewById (R.id.btn_catv) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
						commandSender.sendKey(GKey.KEY_CATV_MODE) ;
					} catch (IOException e) {
					}
				}
			}) ;

			/*
			//smart
			btn = (Button)findViewById (R.id.btn_smart) ;
			btn.setOnClickListener(new OnClickListener (){
				public void onClick (View v){
					try {
//						commandSender.sendKey(GKey.KEY_APP_LIST) ;
						commandSender.sendKey(GKey.KEY_BOOKMARK) ;
					} catch (IOException e) {
					}
				}
			}) ;
			*/
		}
	}

	void initMainList ()
	{
		curMode = MODE_MAIN ;
		
        setContentView(R.layout.activity_main);
        ListView listMain = (ListView)findViewById (R.id.listMain) ;

        
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,
        				android.R.layout.simple_list_item_1, 
        				android.R.id.text1, vItemText) ;
        
        listMain.setAdapter (adapter) ;
        
        listMain.setOnItemClickListener
        (
       		new OnItemClickListener ()
       		{
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) 
				{
					if (position == MODE_TV_REMOTE)
					{
						initTVRemote () ;
					}else if (position == MODE_GAME_PAD)
					{
				        setContentView(R.layout.game_pad);
					}else if (position == MODE_WEB_LINKS)
					{
				        setContentView(R.layout.web_links);
					}else if (position == MODE_SETUP)
					{
						initSetup () ;
					}
				}
       		}
        ) ;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	context = getApplicationContext () ;
        
		byte ip[] = new byte[4] ;
		ip[0] = (byte)192 ;
		ip[1] = (byte)168 ;
		ip[2] = (byte)11 ;
		ip[3] = (byte)5 ;
	
		try {
			inputDeviceAddr = InetAddress.getByAddress(ip) ;
		} catch (UnknownHostException e) {
		}
        
        initMainList () ;
//        findDevice () ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
    	
    	if (curMode == MODE_MAIN)
    	{
    		finish () ;
    	}else
    	{
        	if (keyCode==KeyEvent.KEYCODE_BACK) 
        	{              
        		if (curMode == MODE_SETUP)
        		{
        			//設定ip
        	        EditText ed = (EditText)findViewById (R.id.ed_ip) ;
        	        String str = ""+ed.getText () ;
        	        try {
						inputDeviceAddr = InetAddress.getByName(str) ;
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
        			
        		}else
        		{
        		}
    	        initMainList () ;
    	        return true ;
        	}
    	}
    	
    	return super.onKeyDown(keyCode, event) ;
    }
}
