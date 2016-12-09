package com.example.kasonSearch;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.map.BaiduMap;

import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;

import com.baidu.mapapi.map.Overlay;

import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
  private MapView mMapView = null;
  private BaiduMap mBaiduMap;
  private Button locateBtn;
  private Button friendsBtn;
  private LocationService locService;
  private Overlay me;
  private Queue<ReXY> rexy= new LinkedList<ReXY>();
  private List<Friend> firends=new ArrayList<Friend>();
  private String fstring;
  private Context context;

  class ReXY{
	  public String phone;
	  public int i;
	  public ReXY(String p,int in){
		  phone=p;
		  i=in;
	  }
  }
  public void requestXY(String phone,int i){
    rexy.offer(new ReXY(phone,i));
    locService.start();
  }
  private void saveFriends(){
    SharedPreferences mySharedPreferences = getSharedPreferences("fList", Activity.MODE_PRIVATE); 
    SharedPreferences.Editor editor = mySharedPreferences.edit(); 
    editor.putString("friends", fstring); 
    editor.commit(); 
  }
  private void stringToFriends(String fss){
	  int i;
	  Friend f;
	  String friendstrs[]=fstring.split(",");
	  for(i=friendstrs.length-1;i>=0;i--){
		  f=strToFriend(friendstrs[i]);
		 if(f !=null){
			 firends.add(f);
		 }
	  }
  }
  private void readFriends(){
    SharedPreferences sharedPreferences = getSharedPreferences("fList", Activity.MODE_PRIVATE); 
    fstring = sharedPreferences.getString("friends", ""); 
    firends.clear();
    stringToFriends(fstring);
  }
  private void  changeFriends(String fs){
    fstring=fs;
    firends.clear();
    stringToFriends(fstring);
    saveFriends();
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);
    context=this;
    readFriends();
    mMapView = (MapView) findViewById(R.id.bmapView);
    locateBtn = (Button) findViewById(R.id.locate);
    friendsBtn= (Button) findViewById(R.id.friends);
    mBaiduMap = mMapView.getMap();
    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
    locService = ((LocationApplication) getApplication()).locationService;
    LocationClientOption mOption = locService.getDefaultLocationClientOption();
    locService.setLocationOption(mOption);
    locService.registerListener(listener);
    
    ((LocationApplication) getApplication()).mapActivity=this;
  }
  @Override
  protected void onStart(){
    super.onStart();
    locService.start();
    locateBtn.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        locService.start();
        locateFriends();
        if (mBaiduMap != null)
          mBaiduMap.clear();
      }
    });
    friendsBtn.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        final EditText tv=new EditText(context);
        tv.setText(fstring);
        new AlertDialog.Builder(context)  
        .setTitle("请输入（名字:号码,名字:号码....）")  
        .setIcon(android.R.drawable.ic_dialog_info)  
        .setView(tv)  
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface arg0, int arg1) {
            // TODO Auto-generated method stub
            changeFriends(tv.getText().toString());
          }
        })  
        .setNegativeButton("取消", null)  
        .show();  
      }
    });
  }
  public void locateFriends(){
    for(int i=firends.size()-1;i>=0;i--){
        sendms(firends.get(i).phone,"RQXY:"+i);
    } 
  }
  public Overlay  placeICON(double Lati,double Longi,String name) {

    LatLng point = new LatLng(Lati,Longi);

    TextOptions textOptions=new TextOptions().bgColor(0xAAFFFF00) 
         .fontSize(28)  
         .fontColor(0xFFFF00FF)
         .text(name)  
         .position(point);
  
    Overlay a=mBaiduMap.addOverlay(textOptions);
    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
    return a;
  }

  public boolean placeFriendIcon(double Lati,double Longi,int i){
	  if(firends.size()>i){
		  Friend f=firends.get(i);
		  if(f!=null){
			  placeICON(Lati,Longi,f.name);
			  return true;
		  }
	  }
	return false;
	  
	  
  }
  public void placeMyIcon(double Lati,double Longi){
    if(me!=null)
      me.remove();
    me=placeICON( Lati, Longi,"我");
  }

  BDLocationListener listener = new BDLocationListener() {
            
    @Override
    public void onReceiveLocation(BDLocation location) {
      // TODO Auto-generated method stub
      locService.stop();
      if (location != null && ( location.getLocType() != BDLocation.TypeServerError)) {
        if (location != null) {
          placeMyIcon(location.getLatitude(), location.getLongitude());
          ReXY re;
          while((re=rexy.poll())!=null){
            sendXY(re.phone,location.getLatitude(),location.getLongitude(),re.i);
          }
        }
    
      }
    }
  };
  
  public void sendXY(String phone,double X,double Y,int i)  {
    String ms="XY:"+i+":"+Double.toString(X)+","+Double.toString(Y);
    sendms(phone,ms);
  }

     
    private void sendms(String phone, String message){
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone, null, message, null, null);
    }
  



  @Override
  protected void onDestroy() {
    super.onDestroy();
    locService.unregisterListener(listener);
    locService.stop();
    mMapView.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mMapView.onResume();

  }

  @Override
  protected void onPause() {
    super.onPause();
    mMapView.onPause();
  }

  public Friend strToFriend(String fs) {
  	if(fs==null||fs.equals("")) return null;
  	String s[]=fs.split(":");
  	if(s==null||s.length!=2||s[0]==null||s[0].equals("")||s[1]==null||s[1].equals(""))return null;
  	return new Friend(s[0], s[1]);
  }
  class Friend {
    public   String name;
    public String phone;

    public  Friend(String n,String p){
    	name=n;
      	phone=p;
    }
  }
  
}
