package grand.TravelJournal;

import grand.TravelJournal.db.DB_change_manager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TravelName extends Activity{
	
	private ListView list;
	private DB_change_manager db_manager, db_manager2;
	private ContentResolver resolver;
	private ArrayList<String> Travels = new ArrayList<String>();
	private ArrayList<String> Login = new ArrayList<String>();
	private ProgressDialog dialogProgress;
	private URL url;
	private boolean isgetTravelName = false;
	private String listItem;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travelarchive);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
        
        dialogProgress = new ProgressDialog(this); 
        list = (ListView) findViewById(R.id.ListViewTravelInTArchive);
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        db_manager2  =  new DB_change_manager();
        
        Travels = db_manager.getTravelName(resolver);

    	Login = db_manager2.getLogin(1, resolver);
    	
        Log.d("test", "1");  
        if(Travels.get(0).equals("0")){
        	Log.d("test", "2");  
        	//Toast.makeText(TravelArchive.this, "Already set startpoint",Toast.LENGTH_SHORT).show(); 
        	showDialog(1);      	
        	isgetTravelName = true;
        	networkthread ob = new networkthread();
        }else{
        	Log.d("test", "3"); 
        	setList();       	
        }
        Log.d("test", "4 "+Login);  
		list.setTextFilterEnabled(true);
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				// TODO Auto-generated method stub
				isgetTravelName = false;
				listItem = ""+ list.getItemAtPosition(position);
				Travels = db_manager.getArchive(1,  listItem, resolver);
				 
				if(Travels.get(0)=="0"){
					showDialog(1);
					networkthread ob = new networkthread();
				}else{
					showDialog(1);
					Intent();
	  	      	    dialogProgress.cancel();
				}
			}
		});
    }
    
	public void Intent(){
		Intent AllPostsIntent = new Intent(TravelName.this, AllPosts.class);
	        AllPostsIntent.putExtra("TravelName", listItem);
	        AllPostsIntent.putExtra("name", Login.get(0));
	        AllPostsIntent.putExtra("pwd", Login.get(1));
    		startActivity(AllPostsIntent);
    		finish(); 
	}
	public void setList(){
		list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , Travels));
	}
	
	 protected Dialog onCreateDialog(int id) {
         if(id == 1){
        	 dialogProgress.setMessage("Loading ...");
         	 dialogProgress.setIndeterminate(true);
         	 dialogProgress.setCancelable(true);      	 
         }
         return dialogProgress; 
	}
	 
		class networkthread implements Runnable { 
			 
			 private static final String TAG = "Test"; 
			 private Thread t;
			 private int count;
			 int exception_count = 0;
			 
			 public networkthread() { 
				 t = new Thread(this); 
				 t.start();
			 } 
			 
			 private Handler handler = new Handler() {
				 
	            @Override 
	            public void handleMessage(Message msg) {
	    	    	 	if(count != 0)
	    		        {
	    	    	 		 if(isgetTravelName == true){
		    	    	 		db_manager.insertTravelName(Travels, resolver);
		    	    	 		setList(); 		    	    	 		
	    	    	 		 }else{
	    	    	 			db_manager.insertArchive(2, Travels, resolver);
	    	    	 			Toast.makeText(TravelName.this, "Okey", Toast.LENGTH_SHORT).show(); 
	    	    	 			Intent();
	    	    	 		 }
	    	    	 		dialogProgress.cancel();
	    	    	 	}else{
	    	    	 		dialogProgress.cancel();
	    	    	 		Toast.makeText(TravelName.this, "Don't have travel",Toast.LENGTH_SHORT).show(); 
	    		        }
	    	        }

			 };

			 private Handler handler_expeption = new Handler() {
				 @Override 
	            public void handleMessage(Message msg) {
					 dialogProgress.cancel();
					 Toast.makeText(TravelName.this, "Connection refused. Please try again.",Toast.LENGTH_SHORT).show();
				 }
			 };
			 
			 public void run(){
				 
				 Log.v(TAG, "inside sub thread");

			 try {
				 if(isgetTravelName == true){
					 Log.d("isgetTravelName", "true");
					 url = new URL("http://192.168.1.220:8080/traveljournal/android/GetTravelName");
				 }else{
					 Log.d("isgetTravelName", "false");
					 url = new URL("http://192.168.1.220:8080/traveljournal/android/GetLocation");
				 }

				 HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
			     httpurlconnection.setDoOutput(true);
			     httpurlconnection.setRequestMethod("POST");
			     
			     DataOutputStream dos = new DataOutputStream(httpurlconnection.getOutputStream());
			     Log.d("teset", Login.get(0) +" "+Login.get(1));     
			     dos.writeUTF(Login.get(0));
			     dos.writeUTF(Login.get(1)); 
			     
			     Log.d("isgetTravelName", "= "+isgetTravelName);
			     if(isgetTravelName == false){
			    	 Log.d("isgetTravelName", "false again"+listItem);
			    	 dos.writeUTF(listItem);
			     }
			     dos.flush();
			     dos.close();

			     
			     DataInputStream din = new DataInputStream(httpurlconnection.getInputStream());
		      
			     Travels.clear();
			     
			     count = din.readInt();
			     
			     if(isgetTravelName == true){
			    	 
			    	 for(int i=0; i<count; i++){
				    	 Travels.add(din.readUTF());
				     }
				     
			     }else{			     
			    	 Log.d("test", "ok"+count);
				     for(int j = 0; j < count; j++){			    	 
				    	 Travels.add(din.readUTF());
				    	 Travels.add(din.readUTF());
				    	 Travels.add(din.readUTF());
				    	 Travels.add(din.readUTF());
				    	 Travels.add(din.readUTF());
				    	 Travels.add(din.readUTF());
				    	 Travels.add(din.readUTF());
				    	 Log.d("value", ""+j+"--"+Travels);
				     }
			     }
			     
			     handler.sendEmptyMessage(0);
				 }
			 catch(Exception e){ 
				 Log.v(TAG, "Exception:" +e); 
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