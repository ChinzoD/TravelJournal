package grand.TravelJournal;

import grand.TravelJournal.db.DB_change_manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;



public class WidgetProvider extends AppWidgetProvider implements LocationListener{

	private LocationManager myManager;
	private Double lat = 0.0, lon = 0.0;
	private  RemoteViews updateViews;
	private DB_change_manager db_manager, db_manager2, db_manager3, db_manager4;
	private ContentResolver resolver;
	private ArrayList<String> LoginValues = new ArrayList<String>();
	private ArrayList<String> CurrentTravelValues = new ArrayList<String>();
	private ArrayList<String> LastLocations = new ArrayList<String>();
	private String OptionValues;
	private int distance, unit;
	private URL url;
	
	private final static double[] multipliers = {
		0.01,0.00621371192,10.936133
		};
	
	private final static int[] distvalue = {
		1,5,10
	};
	
		@Override
		public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		resolver = context.getContentResolver(); 
        db_manager =  new DB_change_manager();
        db_manager2 =  new DB_change_manager();
        db_manager3 =  new DB_change_manager();
        db_manager4 =  new DB_change_manager();
        
        CurrentTravelValues = db_manager.getCurrentTravel(0, resolver);
        
        if(!CurrentTravelValues.get(0).equals("0")){
        	LoginValues = db_manager2.getLogin(1, resolver);
        	if(!LoginValues.get(1).equals("0")){
        		OptionValues = db_manager3.getOptions(resolver);
        		unit = Integer.parseInt(OptionValues.substring(0, 1));
        	    distance = Integer.parseInt(OptionValues.substring(1, OptionValues.length()));
	        	myManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 
	        	Log.d("GPS", "Test00");
	        	//startListening();
        	 }
        }
        
		
		final int N = appWidgetIds.length;
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
        	
        	Intent intent1 = new Intent(context, LoginFacebookTwitter.class);
            PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, 0);
            
            Intent intent2 = new Intent(context, Post.class);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
             
            Intent intent3 = new Intent(context, Comment.class);
            PendingIntent pendingIntent3 = PendingIntent.getActivity(context, 0, intent3, 0);

            updateViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
            updateViews.setOnClickPendingIntent(R.id.ImgBtnLoginFbTwInAppWidget, pendingIntent1);	
            updateViews.setOnClickPendingIntent(R.id.ImgBtnPostInAppWidget, pendingIntent2);	
            updateViews.setOnClickPendingIntent(R.id.ImgBtnCommentsInAppWidget, pendingIntent3);	
             //updateViews.setTextViewText(R.id.text, now);           
             appWidgetManager.updateAppWidget(appWidgetIds, updateViews);
       }
        /*final LocationListener locationListener= new
        LocationListener() {
        	
                    public void onLocationChanged(Location newloc) {
                    	lat = newloc.getLatitude();
       				 lon = newloc.getLongitude();
       				updateViews.setTextViewText(R.id.TextView01, "lat "+lat);
                    	 if (myManager != null)
     		                myManager.removeUpdates(this);
                            Log.d("TAG","onLocation Changed");
                    }
                    public void onProviderDisabled(String provider) {}
                    public void onProviderEnabled(String provider) {}
                    public void onStatusChanged(String provider, int status,
                                Bundle extras) {}
                }; 
        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);*/ 
    }
		


		private void startListening() {
		        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		        Log.d("GPS", "Test55");
		    }

		    private void stopListening() {
		        if (myManager != null)
		                myManager.removeUpdates(this);
		    }
		    
			 public void onLocationChanged(Location location) {
				 /* tur comment bolgov
				 lat = location.getLatitude();
				 lon = location.getLongitude();
				 Log.d("GPS", "changed");
				 
				 LastLocations = db_manager4.getArchive(3, CurrentTravelValues.get(0), resolver);
				 Log.d("GPS", ""+LastLocations);
				 double dist = calcGeoDistance(Double.parseDouble(LastLocations.get(0)),Double.parseDouble(LastLocations.get(1)),lat,lon) * multipliers[unit];
				 Log.d("GPS dist", ""+dist);
				 if(dist >= distvalue[distance]){
					 networkthread ob = new networkthread();
				 }
				 
				 Log.d("GPS", "Test1"+lat);*/
			     stopListening();
			 }    

			   
		    public void onProviderDisabled(String provider) {}    

		    
		    public void onProviderEnabled(String provider) {}    

		    
		    public void onStatusChanged(String provider, int status, Bundle extras) {
		    }
		    
		    private double calcGeoDistance(final double lat1, final double lon1, final double lat2, final double lon2)
			{
				double distance = 0.0;
				
				try
				{
					final float[] results = new float[3];
					
					Location.distanceBetween(lat1, lon1, lat2, lon2, results);
					
					distance = results[0];
				}
				catch (final Exception ex)
				{
					distance = 0.0;
				}
				
				return distance;
			}
		    
		    class networkthread implements Runnable { 
				 
				 private Thread t;
				 private String MSG;
				 private ArrayList<String> ArchiveValue = new ArrayList();
				 
				 public networkthread() { 
					 t = new Thread(this); 
					 t.start();
				 } 
				 
				 private Handler handler = new Handler() {
					 
		          @Override 
		          public void handleMessage(Message msg) {
		  	    	 	if(MSG.equals("ok"))
		  		        {
		  	    	 		ArchiveValue.add(0, CurrentTravelValues.get(0));
		  	    	 		ArchiveValue.add(""+lat);
		  	    	 		ArchiveValue.add(""+lon);
		  	    	 		ArchiveValue.add("0");
		  	    	 		ArchiveValue.add("0");
		  	    	 		ArchiveValue.add("0");
		  	    	 		Log.d("ArchiveValue", ""+ArchiveValue.size()+" "+ArchiveValue);
		  	    	 		db_manager.insertArchive(2, ArchiveValue, resolver);		 		
		  	    	 	}else{
		  	    	 		//Toast.makeText(TravelMenu.this, "Not set locations",Toast.LENGTH_SHORT).show(); 

		  	    	 	}
		  	        }

				 };

				 private Handler handler_expeption = new Handler() {
					 @Override 
		          public void handleMessage(Message msg) {
						 //Toast.makeText(WidgetProvider.this, "Connection refused. Please try again.",Toast.LENGTH_SHORT).show(); 
					 }
				 };
				 
				 public void run(){
					 
				 try { 
					 //url = new URL("http://192.168.1.2:8080/traveljournal/android/SetOnlyLocation");
					 url = new URL("http://192.168.1.220:8080/traveljournal/android/SetOnlyLocation");
					 //url = new URL("http://www.atarkhishig.mn/traveljournal/android/SetOnlyLocation");
					 
					 HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
				     httpurlconnection.setDoOutput(true);
				     httpurlconnection.setRequestMethod("POST");
				     
				     DataOutputStream dos = new DataOutputStream(httpurlconnection.getOutputStream());
		  
				     dos.writeUTF(LoginValues.get(0));
				     dos.writeUTF(LoginValues.get(1));
				     dos.writeUTF(CurrentTravelValues.get(0));
				     dos.writeDouble(lat);
				     dos.writeDouble(lon);
				     dos.flush();
				     dos.close();
				     Log.d("test", "test");
				     
				     DataInputStream din = new DataInputStream(httpurlconnection.getInputStream());
				     Log.d("test", "test1");
				     MSG = din.readUTF();
				     if(MSG.equals("ok")){
				    	 ArchiveValue.add(""+din.readInt());
				    	 Log.d("test", "testsd"+ArchiveValue);
				     }
				     Log.d("test", "test2");
				     handler.sendEmptyMessage(0);

					 }
				 catch(Exception e){ 
					 Log.v("test11", "Exception:" +e); 
					 e.printStackTrace(); 
					 handler_expeption.sendEmptyMessage(0); 
					 }
				
				 } 
				 
			}
	/*
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action))
        {
        	 RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
             AppWidgetManager.getInstance(context).updateAppWidget(intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS), views);

        	 Log.d("onupdate", "chinzorig2");
        }
   }*/
}