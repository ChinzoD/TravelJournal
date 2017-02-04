package grand.TravelJournal;


import grand.TravelJournal.db.DB_change_manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class TravelMenu extends Activity implements OnClickListener{
	
	private String TName;
	private Dialog dialog;
	private ProgressDialog dialogProgress;
	TextView txtTName, txtDistance;
	ImageButton btnStartTravel, btnExport, btnDetails;
	ImageButton btnGPS, btnManual, btnCancelDialog;
	String uname, upwd;
	private URL url;
	private DB_change_manager db_manager, db_manager2;
	private ContentResolver resolver;
	private ArrayList<String> BaseValues = new ArrayList<String>();
	private double TotalDistance = 0;
	private String OptionValues;
	private int unit;
	
	/*private final static double[] multipliers = {
		0.01,0.00621371192,10.936133
	};*/
	private final static double[] multipliers = {
		0.001,0.000621371192, 1.0936133
	};
	
	private final static String[] unitstrings = {
		"km","miles","yard"
	};
	/*private final static double[] multipliers = {
		1.0,1.0936133,0.001,0.000621371192
	};*/
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travelmenu);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
        
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
        	TName = extras.getString("TravelName");
        	uname = extras.getString("name");
        	upwd = extras.getString("pwd");
        }
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        db_manager2 =  new DB_change_manager();
        
        dialogProgress = new ProgressDialog(this); 
        dialog = new Dialog(this);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        
        txtTName = (TextView) findViewById(R.id.TextViewTNameHereInTMenu);
        txtTName.setText(TName);
        txtDistance = (TextView) findViewById(R.id.TextViewTotalDistInTMenu);
	
		BaseValues = db_manager.getCurrentTravel(1, resolver);
		Log.d("this is", ""+BaseValues);
		if(BaseValues.get(0).equals("true")){
			BaseValues = db_manager.getArchive(1, TName, resolver);
			Log.d("this is", ""+BaseValues);
			OptionValues = db_manager2.getOptions(resolver);
    		unit = Integer.parseInt(OptionValues.substring(0, 1));
    		
			if(BaseValues.get(0)=="0"){
				Log.d("this is", "test333333333");
				networkthread ob = new networkthread();
			}else{	
				if(BaseValues.size() > 2){
					for(int i = 0; i < BaseValues.size()-2; i +=2){
						TotalDistance += calcGeoDistance(Double.parseDouble(BaseValues.get(i)),Double.parseDouble(BaseValues.get(i+1)),Double.parseDouble(BaseValues.get(i+2)),Double.parseDouble(BaseValues.get(i+3))) * multipliers[unit];
						Log.d("dist", ""+TotalDistance);
					}
				}else
					txtDistance.setText("0 " + unitstrings[unit]);
			}
			txtDistance.setText("" + RoundDecimal(TotalDistance,2) + " " + unitstrings[unit]);
			
		}else{
			//distance iig 0 baina gj gargana.
			txtDistance.setText("0 " + unitstrings[unit]);
		}
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
	
	public double RoundDecimal(double value, int decimalPlace)
	{
		BigDecimal bd = new BigDecimal(value);
		
		bd = bd.setScale(decimalPlace, 6);
		
		return bd.doubleValue();
	}
	
	public void MyClickInTMenu(View view) {
		switch (view.getId()) {
		case R.id.ImgBtnStartInTMenu:
			BaseValues.clear();
			BaseValues = db_manager.getCurrentTravel(1, resolver);
			Log.d("values", ""+BaseValues);
			if(BaseValues.get(0).equals("true")){
				Toast.makeText(TravelMenu.this, "Already set startpoint",Toast.LENGTH_SHORT).show(); 			
			}else{
				showDialog(1);
			}    
            break;
	    case R.id.ImgBtnPostsInTMenu:	  	
	    	Intent AllPostsIntent = new Intent(TravelMenu.this, AllPosts.class);
	        AllPostsIntent.putExtra("TravelName", TName);
	        AllPostsIntent.putExtra("name", uname);
	        AllPostsIntent.putExtra("pwd", upwd);
    		startActivity(AllPostsIntent);
    		finish(); 
	  	  	break;
	    case R.id.ImgBtnMapInTMenu:
	    	if(!txtDistance.getText().equals("0")){
	    		Intent trackIntent = new Intent(TravelMenu.this, MyTrackMap.class);
	    	    trackIntent.putExtra("TravelName", TName);
  	      		startActivity(trackIntent);
  	      		finish();
			}else{
				Toast.makeText(TravelMenu.this, "Not set startpoint",Toast.LENGTH_SHORT).show();
			}	
	    break;
	    case R.id.ImgBtnBackInTMenu:
		    	Intent MainMenuIntent = new Intent(TravelMenu.this, MainMenu.class);
		    	MainMenuIntent.putExtra("name", uname);
    	 		MainMenuIntent.putExtra("pwd", upwd);
	      		startActivity(MainMenuIntent);
	      		finish(); 
	    	break;
		}
	}
	
	
		public void onClick(View v) {
		  switch (v.getId()) {
	          
	          case R.id.ImgBtnGPS:
	        	  ToMap("auto");
	        	  break;
	          case R.id.ImgBtnManual:
	        	  ToMap("manual");
	        	  break;
	          case R.id.ImgBtnCancelInLocation:
	        	  	dialog.cancel();
	        	  break;
	          }
		}
		
	 public void ToMap(String type){
		 Intent MapIntent = new Intent(TravelMenu.this,Map.class);
	   	 MapIntent.putExtra("TYPE", type);
	   	 MapIntent.putExtra("travelName", TName);
	   	 MapIntent.putExtra("name", uname);
	   	 MapIntent.putExtra("pwd", upwd);
 		 startActivity(MapIntent);
 		 finish();
	 }
	
	 protected Dialog onCreateDialog(int id) {
		 dialog.setContentView(R.layout.location);
		 
         switch(id) {
         case 1:  	 
        	 btnGPS = (ImageButton) dialog.findViewById(R.id.ImgBtnGPS);
        	 btnManual = (ImageButton) dialog.findViewById(R.id.ImgBtnManual);
        	 btnCancelDialog = (ImageButton) dialog.findViewById(R.id.ImgBtnCancelInLocation);
        	 btnGPS.setOnClickListener(this);
        	 btnManual.setOnClickListener(this);
        	 btnCancelDialog.setOnClickListener(this);
             break;
         case 0:
        	 
        	 break;
         default:
             //dialog = null;
         }
         if(id == 2){
        	 dialogProgress.setMessage("Loading ...");
         	 dialogProgress.setIndeterminate(true);
         	 dialogProgress.setCancelable(true);
         	 return dialogProgress; 
         }else{
        	 return dialog;
         }
	}
	
		class networkthread implements Runnable { 
			 
			 private Thread t;
			 private String MSG;
			 private ArrayList<String> ArchiveValue = new ArrayList();
			 private int count=0;
			 
			 public networkthread() { 
				 t = new Thread(this); 
				 t.start();
			 } 
			 
			 private Handler handler = new Handler() {
				 
	          @Override 
	          public void handleMessage(Message msg) {
	  	    	 	if(count != 0)
	  		        {
	  	    	 		db_manager.insertArchive(2, ArchiveValue, resolver);
	  	    	 		Log.d("size ni ", ""+ArchiveValue.size());
	  	    	 		Log.d("value ni ", ""+ArchiveValue);
	  	    	 		if(ArchiveValue.size() > 7){
		  	    	 		for(int i = 0; i < ArchiveValue.size()-13; i +=7){
		  	    	 			Log.d("i ni ", ""+i);
		  	    	 			TotalDistance += calcGeoDistance(Double.parseDouble(ArchiveValue.get(i+2)),Double.parseDouble(ArchiveValue.get(i+3)),Double.parseDouble(ArchiveValue.get(i+9)),Double.parseDouble(ArchiveValue.get(i+10))) * multipliers[unit];	  						
		  	    	 		}
		  	    	 		txtDistance.setText(""+RoundDecimal(TotalDistance,2)+" " + unitstrings[unit]);
	  	    	 		}else{
	  	    	 			txtDistance.setText("0 " + unitstrings[unit]);
	  	    	 		}
	  	    	 		/*Log.d("ArchiveValue", ""+ArchiveValue.size());
	  	    	 		
	  	    	 		Toast.makeText(TravelMenu.this, "Okey", Toast.LENGTH_SHORT).show(); 
	  	    	 		Intent trackIntent = new Intent(TravelMenu.this, MyTrackMap.class);
	  	    	 		trackIntent.putExtra("TravelName", TName);
	  	    	 		dialogProgress.cancel();
		  	      		startActivity(trackIntent);
		  	      		finish(); */
		 		
	  	    	 	}else{
	  	    	 		dialogProgress.cancel();
	  	    	 		Toast.makeText(TravelMenu.this, "Not set locations",Toast.LENGTH_SHORT).show(); 
	  	    	 		t.stop();
	  	    	 	}
	  	        }

			 };

			 private Handler handler_expeption = new Handler() {
				 @Override 
	          public void handleMessage(Message msg) {
					 txtDistance.setText("0 " + unitstrings[unit]);
					 Toast.makeText(TravelMenu.this, "Connection refused. Please try again.",Toast.LENGTH_SHORT).show(); 
				 }
			 };
			 
			 public void run(){
				 
			 try { 
				 //url = new URL("http://192.168.1.2:8080/traveljournal/android/GetLocation");
				 url = new URL("http://192.168.1.220:8080/traveljournal/android/GetLocation");
				 //url = new URL("http://www.atarkhishig.mn/traveljournal/android/GetLocation");
				 
				 HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
			     httpurlconnection.setDoOutput(true);
			     httpurlconnection.setRequestMethod("POST");
			     
			     DataOutputStream dos = new DataOutputStream(httpurlconnection.getOutputStream());
	  
			     dos.writeUTF(uname);
			     dos.writeUTF(upwd);
			     dos.writeUTF(TName);
			     dos.flush();
			     dos.close();
			     Log.d("test", "test");
			     
			     DataInputStream din = new DataInputStream(httpurlconnection.getInputStream());
			     Log.d("test", "test1");
			     count = din.readInt();
			     Log.d("test", "ok"+count);
			     for(int j = 0; j < count; j++){			    	 
			    	 ArchiveValue.add(din.readUTF());
			    	 ArchiveValue.add(din.readUTF());
			    	 ArchiveValue.add(din.readUTF());
			    	 ArchiveValue.add(din.readUTF());
			    	 ArchiveValue.add(din.readUTF());
			    	 ArchiveValue.add(din.readUTF());
			    	 ArchiveValue.add(din.readUTF());
			    	 Log.d("value", ""+j+"--"+ArchiveValue);
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
		
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}