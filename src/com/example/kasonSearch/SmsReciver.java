package com.example.kasonSearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import android.widget.Toast;

public class SmsReciver extends BroadcastReceiver {  
	
	
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        MainActivity ma=((LocationApplication) context.getApplicationContext()).mapActivity;
        
        SmsMessage[] smsMessages = null;
        Object[] pdus = null;

        if (bundle != null) {
            pdus = (Object[]) bundle.get("pdus");
        }
        if (pdus !=null){
            smsMessages = new SmsMessage[pdus.length];
            String sender = null;
            String content = null;
            String xy[];

            for (int i=0; i<pdus.length; i++){
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                sender = smsMessages[i].getOriginatingAddress();
                content = smsMessages[i].getMessageBody();

                if(content.indexOf("XY:")==0){
                	String s[]=content.split(":");
                	
                	if(s!=null&&s.length==3){
	                	int in= Integer.valueOf(s[1]);
	                	xy=s[2].split(",");
	                	ma .placeFriendIcon(Double.valueOf(xy[0]),Double.valueOf(xy[1]),in);
//	                	Toast.makeText(context, sender + "," + content, Toast.LENGTH_LONG).show();
                	}
                	
                	
                }else if(content.indexOf("RQXY:")==0){
                	String s[]=content.split(":");
                	int in;
                	if(s!=null&&s.length==2&&s[1]!=null&&!s[1].equals("")){
                		in=Integer.valueOf(s[1]);
                		ma.requestXY(sender,in);
                	}
                }
               
            }
        }
    }



}