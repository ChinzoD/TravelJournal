package grand.TravelJournal;

import grand.TravelJournal.db.DB_change_manager;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
 
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import grand.TravelJournal.Login.networkthread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.google.android.maps.Overlay;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;


public class Map extends MapActivity implements LocationListener, OnClickListener{
    /** Called when the activity is first created. */
	MapView mapView; 
	MapController mc;
    GeoPoint p;
    public static double lat, lon;
    private String uname, upwd, Tname; 
    private LocationManager myManager;
    private String Type;
	private int DownUp = 0;
	private MapOverlay mapOverlay;
	Dialog dialog;
	ProgressDialog dialogProgress;
	URL url;
	ImageButton btnYes, btnNo;
	private DB_change_manager db_manager;
	private ContentResolver resolver;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
        
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
        	Type = extras.getString("TYPE");
        	Tname = extras.getString("travelName");
        	uname = extras.getString("name");
        	upwd = extras.getString("pwd");
        }
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        
        dialogProgress = new ProgressDialog(this);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        // set up the LocationManager
        myManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
       
        mapView = (MapView) findViewById(R.id.mapView);
        View zoomView = mapView.getZoomControls(); 

        mapView.displayZoomControls(true);
        mapView.setSatellite(true);
       
        mc = mapView.getController();
        p = new GeoPoint(
                (int) (47.927730738847536 * 1E6), 
                (int) (106.94353580474854 * 1E6));
        
	    mc.animateTo(p);
	    mc.setZoom(4);    
	    
	    mapOverlay = new MapOverlay();
	    mapView.getOverlays().add(mapOverlay);
	    
	    mapView.invalidate();
    }
    
    public void MyClickInMap(View view) {
		switch (view.getId()) {
		case R.id.ImgBtnLensNegInMap:		
				mc.zoomOut();
			break;
		case R.id.ImgBtnStreetInMap:
				mapView.setSatellite(false);
				mapView.setStreetView(true);
			break;
		case R.id.ImgBtnFlagInMap:
			showDialog(1);
			break;
			
		case R.id.ImgBtnSatInMap:
				mapView.setStreetView(false);
				mapView.setSatellite(true);
			break;
		case R.id.ImgBtnLensPosInMap:
				mc.zoomIn();
			break;
		}
	}
    
    private void startListening() {
        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void stopListening() {
        if (myManager != null)
                myManager.removeUpdates(this);
    }

    protected Dialog onCreateDialog(int id) {
		 		 
    	if(id == 1){        
        	dialog.setContentView(R.layout.question);
        	btnYes = (ImageButton) dialog.findViewById(R.id.ImgBtnYesInQuestion);
        	btnNo = (ImageButton) dialog.findViewById(R.id.ImgBtnNoInQuestion);
        	btnYes.setOnClickListener(this);
        	btnNo.setOnClickListener(this);
        	return dialog;
    	}else{
        	dialogProgress.setMessage("Sending location ...");
        	dialogProgress.setIndeterminate(true);
        	dialogProgress.setCancelable(true);
        	return dialogProgress;
    	}
	}
    
    public void onClick(View v) {
		  switch (v.getId()) {
	          case R.id.ImgBtnYesInQuestion:
	        	  showDialog(2);
	        	  dialog.cancel();
	        	  networkthread ob = new networkthread();
	        	  break;
	          case R.id.ImgBtnNoInQuestion:	        	  
	        	  dialog.cancel();
	        	  break;
	      }
	}
    
	class networkthread implements Runnable { 
		 
		 private Thread t;
		 int exception_count = 0;
		 private String who, MSG;
		 
		 public networkthread() { 
			 t = new Thread(this); 
			 t.start();
		 } 
		 
		 private Handler handler = new Handler() {
			 
           @Override 
           public void handleMessage(Message msg) {
   	    	 	if(MSG.equals("ok"))
   		        {
   	    	 		dialogProgress.cancel();
   	    	 		db_manager.updtCurrentTravel(1, Tname, resolver);
   	    	 		Toast.makeText(Map.this, "Okey",Toast.LENGTH_SHORT).show(); 
   	    	 		Intent TravelMenuIntent = new Intent(Map.this, TravelMenu.class);
	    	 		TravelMenuIntent.putExtra("TravelName", Tname);
	    	 		TravelMenuIntent.putExtra("name", uname);
	    	 		TravelMenuIntent.putExtra("pwd", upwd);
	    	 		startActivity(TravelMenuIntent);
	    	 		finish();
   	    	 	}else{
   	    	 		dialogProgress.cancel();
   	    	 		Toast.makeText(Map.this, "no",Toast.LENGTH_SHORT).show(); 
   		        }
   	        }

		 };

		 private Handler handler_expeption = new Handler() {
			 @Override 
           public void handleMessage(Message msg) {
				 dialog.cancel();
				 dialogProgress.cancel();
				 Toast.makeText(Map.this, "Connection refused. Please try again.",Toast.LENGTH_SHORT).show(); 
			 }
		 };
		 
		 public void run(){
			 
		 try { 
			 //url = new URL("http://192.168.1.2:8080/traveljournal/android/SetLocation");
			 url = new URL("http://192.168.1.220:8080/traveljournal/android/SetLocation");
			 //url = new URL("http://www.atarkhishig.mn/traveljournal/android/SetLocation");
			 
			 HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
		     httpurlconnection.setDoOutput(true);
		     httpurlconnection.setRequestMethod("POST");
		     
		     DataOutputStream dos = new DataOutputStream(httpurlconnection.getOutputStream());
	       
		     Log.d("name", uname+upwd+Tname);   
		     dos.writeUTF(uname);
		     dos.writeUTF(upwd);
		     dos.writeUTF(Tname);
		     dos.writeBoolean(true);
		     Log.d(""+p.getLatitudeE6()/1E6, ""+p.getLongitudeE6()/1E6);

		     dos.writeDouble(p.getLatitudeE6()/1E6);
		     dos.writeDouble(p.getLongitudeE6()/1E6 );
		     dos.flush();
		     dos.close();

		     DataInputStream din = new DataInputStream(httpurlconnection.getInputStream());

		     MSG = din.readUTF();	     
		     handler.sendEmptyMessage(0);

			 }
		 catch(Exception e){ 
			 Log.v("test11", "Exception:" +e); 
			 e.printStackTrace(); 
			 handler_expeption.sendEmptyMessage(0); 
			 }
		
		 } 
		 
	}
	

    public void onLocationChanged(Location location) {
        // we got new location info. lets display it in the textview
       /* String s = "";
        s += "Time: "        + location.getTime() + "\n";
        s += "\tLatitude:  " + location.getLatitude()  + "\n";
        s += "\tLongitude: " + location.getLongitude() + "\n";
        s += "\tAccuracy:  " + location.getAccuracy()  + "\n";*/
        p = new GeoPoint(
                (int) (location.getLatitude() * 1E6), 
                (int) (location.getLongitude() * 1E6));
        mc.animateTo(p);
        mc.setZoom(17);
        stopListening();
    }    

   
    public void onProviderDisabled(String provider) {}    

    
    public void onProviderEnabled(String provider) {}    

    
    public void onStatusChanged(String provider, int status, Bundle extras) {}
  
	
    /*public void setLocation(float f, float g){
    	
    	p = mapView.getProjection().fromPixels(
                (int) f,
                (int) g);
        		mc.animateTo(p);
    }*/
    
    class MapOverlay extends com.google.android.maps.Overlay
    {
        @Override
        public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
 
            //---translate the GeoPoint to screen pixels---
            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);
 
            //---add the marker---
            Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.pin);            
            canvas.drawBitmap(bmp, screenPts.x-10, screenPts.y-30, null);         
            return true;
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView) 
        {   
            //---when user lifts his finger---
        	if(Type.equals("manual")){
	            switch (event.getAction()) {
		            case MotionEvent.ACTION_DOWN: 
		            	Log.d("action", "1"); DownUp = 0; break;
		            case MotionEvent.ACTION_MOVE: 
		            	Log.d("action", "2"); DownUp++;  break;	            
		            case MotionEvent.ACTION_UP:  
		            	Log.d("action", "3 ni =" + DownUp);
		            	if(DownUp < 4){
		            		p = mapView.getProjection().fromPixels(
		                            (int) event.getX(),
		                            (int) event.getY());
		            		//p.getLatitudeE6() / 1E6
		            		//p.getLatitudeE6() / 1E6
		            		mc.animateTo(p);
		            	}break;	
	            }
        	}
            return false;
        }        



    } 

   
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    @Override
    protected void onDestroy() {
        stopListening();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        stopListening();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startListening();
        super.onResume();
    }

}
