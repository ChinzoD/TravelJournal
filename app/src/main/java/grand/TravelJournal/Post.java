package grand.TravelJournal;

import grand.TravelJournal.db.DB_change_manager;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Post extends Activity implements LocationListener{
	
	private DB_change_manager db_manager, db_manager2;
	private ContentResolver resolver;
	private ArrayList<String> LoginValues = new ArrayList<String>();
	private ArrayList<String> CurrentTravelValues = new ArrayList<String>();
	private byte[] img;
	private Bitmap returnedpic;
	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath = "hooson", postText="bggf";
	private FileInputStream in;
	private BufferedInputStream buf;
	private ProgressDialog dialogProgress;
	private URL url;
	private EditText post;
	private LocationManager myManager;
	GeoPoint p;
	private Double lat = 0.0, lon = 0.0;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-d HH.mm.ss" );
	private Boolean _taken;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);
        

        Log.d("test", selectedImagePath);
        dialogProgress = new ProgressDialog(this); 
        
        Log.d("image5555", ""+returnedpic);
        post = (EditText) findViewById(R.id.EditTextPostInPost);
        //post.setText(postText);
        
        myManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        db_manager2 =  new DB_change_manager();
        
        CurrentTravelValues = db_manager.getCurrentTravel(3, resolver);
        if(CurrentTravelValues.get(1).equals("false") || CurrentTravelValues.get(0).equals("0")){
        	Toast.makeText(Post.this, "Does't set startpoint or travel not created",Toast.LENGTH_SHORT).show(); 
        	finish();
        }
        LoginValues = db_manager2.getLogin(4, resolver);
        Log.d("Test2", "test1"+CurrentTravelValues.get(0));

    }
    
	 public void MyClickInPost(View view) {
			switch (view.getId()) {
				case R.id.ImgBtnTakePicInPost:		
					Log.d("testname", ""+formatter.format(new Date()));
					selectedImagePath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_"+formatter.format(new Date())+".jpg";
					startCameraActivity();
					break;
				case R.id.ImgBtnBrowsePicInPost:
						Intent intent = new Intent();
	                    intent.setType("image/*");
	                    intent.setAction(Intent.ACTION_GET_CONTENT);
	                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
					break;
				case R.id.ImgBtnCancelInPost:
					finish();  
					break;
				case R.id.ImgBtnPostInPost:
					lat = 47.927126855659694;
					lon = 106.94344997406006;
					if(lat != 0){
					
						if(!selectedImagePath.equals("hooson")){
							getPhoto();
						}		
						showDialog(1);
						networkthread ob = new networkthread();
					}else{
						Toast.makeText(Post.this, "GPS doesn't get locations. GPS turn on after send post",Toast.LENGTH_SHORT).show();
					}
					
					break;
			}
	 }
	 public void getPhoto(){
		 try {
			in = new FileInputStream(selectedImagePath);
			buf = new BufferedInputStream(in,1070);
			Log.d("Test", ""+buf);
			img = new byte[buf.available()];  
			Log.d("Test", ""+img.length);
			buf.read(img); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	 
	@Override
	protected Dialog onCreateDialog(int id) {
	    if(id == 1){     
	        	dialogProgress.setMessage("Sending ...");
	        	dialogProgress.setIndeterminate(true);
	        	dialogProgress.setCancelable(false);
	        }
	    return dialogProgress;
	}
	 
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 Log.d("teeessetst", ""+RESULT_OK+" "+SELECT_PICTURE+" ");
		    if (resultCode == RESULT_OK) {
		        if (requestCode == SELECT_PICTURE ) {
		            Uri selectedImageUri = data.getData();
		            selectedImagePath = getPath(selectedImageUri);
		            Log.d("Test", selectedImagePath);
		        }
		    }
		}

	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	class networkthread implements Runnable { 
		 
		 private Thread t;
		 private String MSG;
		 private ArrayList<String> ArchiveValue = new ArrayList();
		 private String date;
		 
		 public networkthread() { 
			 t = new Thread(this); 
			 t.start();
		 } 
		 
		 private Handler handler = new Handler() {
			 
         @Override 
         public void handleMessage(Message msg) {
 	    	 	if(MSG.equals("ok"))
 		        {
 	    	 		db_manager.insertArchive(2, ArchiveValue, resolver);
 	    	 		Toast.makeText(Post.this, "Post sent", Toast.LENGTH_SHORT).show(); 
 	    	 		selectedImagePath = "hooson";
 	    	 		dialogProgress.cancel();
	 		
 	    	 	}else{
 	    	 		Log.d("test", MSG);
 	    	 		dialogProgress.cancel();
 	    	 		Toast.makeText(Post.this, "Wrong Username password",Toast.LENGTH_SHORT).show(); 
 	    	 		t.stop();
 	    	 	}
 	        }

		 };

		 private Handler handler_expeption = new Handler() {
			 @Override 
         public void handleMessage(Message msg) {
				 dialogProgress.cancel();
				 Toast.makeText(Post.this, "Connection refused. Please try again.",Toast.LENGTH_SHORT).show(); 
			 }
		 };
		 
		 public void run(){
			 
		 try { 
			 //url = new URL("http://192.168.1.2:8080/traveljournal/android/SetPost");
			 url = new URL("http://192.168.1.220:8080/traveljournal/android/SetPost");
			 //url = new URL("http://www.atarkhishig.mn/traveljournal/android/SetPost");
			 
			 HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
		     httpurlconnection.setDoOutput(true);
		     httpurlconnection.setRequestMethod("POST");
		     
		     DataOutputStream dos = new DataOutputStream(httpurlconnection.getOutputStream());
		     Log.d("test9999999", ""+LoginValues);
		     Log.d("test9111111", "heey");
		     Log.d("test9111111", ""+CurrentTravelValues);
		    
		     date = formatter.format(new Date());
		     ArchiveValue.clear();
		     ArchiveValue.add(CurrentTravelValues.get(0));     
		     ArchiveValue.add(""+lat);
		     ArchiveValue.add(""+lon);
		     ArchiveValue.add(""+post.getText());
		     ArchiveValue.add(date);
		     
		     dos.writeUTF(LoginValues.get(0));
		     dos.writeUTF(LoginValues.get(1));
		     dos.writeUTF(CurrentTravelValues.get(0));
		     dos.writeUTF(""+post.getText());
		     dos.writeUTF(formatter.format(new Date()));
		     dos.writeDouble(lat);
		     dos.writeDouble(lon);
		     Log.d("path test", selectedImagePath);
		     dos.writeUTF(selectedImagePath);
		     if(!selectedImagePath.equals("hooson")){
			     dos.writeInt(img.length);
			     dos.write(img);
		     }
		     dos.flush();
		     dos.close();
		     Log.d("test", "test");
		     
		     DataInputStream din = new DataInputStream(httpurlconnection.getInputStream());

		     MSG = din.readUTF();
		     if(MSG.equals("ok")){
		    	 ArchiveValue.add(1, din.readUTF());
		    	 ArchiveValue.add(4, din.readUTF());
		     }
		     
		     Log.d("test", "test2 "+MSG);
		     handler.sendEmptyMessage(0);

			 }
		 catch(Exception e){ 
			 Log.v("test11", "Exception:" +e); 
			 e.printStackTrace(); 
			 handler_expeption.sendEmptyMessage(0); 
			 }
		
		 } 
		 
	}
	
    private void startListening() {
        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void stopListening() {
        if (myManager != null)
                myManager.removeUpdates(this);
    }
    
	 public void onLocationChanged(Location location) {

		 lat = location.getLatitude();
		 lon = location.getLongitude();

	     stopListening();
	    }    

	   
    public void onProviderDisabled(String provider) {}    

    
    public void onProviderEnabled(String provider) {}    

    
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    
    @Override
    protected void onDestroy() {
    	Log.d("status", "ondestroy");
    	stopListening();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
    	Log.d("status", "onPause");
    	stopListening();
        super.onPause();
    }

    @Override
    protected void onResume() {
    	Log.d("status", "onResume");
    	startListening();
        super.onResume();
    }
    
    //Zurag avdag source endees ehlene
	
    protected void startCameraActivity()
    {
    	Log.i("MakeMachine", "startCameraActivity()" );
    	File file = new File(selectedImagePath);
    	Uri outputFileUri = Uri.fromFile(file);
    	
    	
    	//Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE" );  	
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri );
    	
    	startActivityForResult( intent, 0 );
    }
        
	//duusna
}
