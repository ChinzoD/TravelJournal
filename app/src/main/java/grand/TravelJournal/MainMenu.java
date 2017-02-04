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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainMenu extends Activity implements OnClickListener{
	
	private ImageButton btnCreate, btnCancel;
	private Button btnLogout, btnExit, btnCancelInLogout;
	private EditText travelName;
	private Dialog dialog;
	private ProgressDialog dialogProgress;
	private DB_change_manager db_manager;
	private ContentResolver resolver;
	private URL url;
	private ArrayList<String> Values = new ArrayList<String>();
	private String uname, upwd;
	private boolean whatButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
        
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
        	uname = extras.getString("name");
        	upwd = extras.getString("pwd");
        }
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        //LoginValues = db_manager.getLogin(1, resolver);
        
        dialogProgress = new ProgressDialog(this);       
        dialog = new Dialog(this);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
    }
    
	 public void MyClickInMainMenu(View view) {
			switch (view.getId()) {
			case R.id.ImgBtnNewTravel:		
				showDialog(1);
				break;
			case R.id.ImgBtnContinue:
				Values = db_manager.getCurrentTravel(0, resolver);
				Log.d("hey", Values.get(0));
				showDialog(3);
				if(Values.get(0)!="0")
				{
					Intent TravelMenuIntent = new Intent(MainMenu.this, TravelMenu.class);
  	    	 		TravelMenuIntent.putExtra("TravelName", Values.get(0));
  	    	 		TravelMenuIntent.putExtra("name", uname);
  	    	 		TravelMenuIntent.putExtra("pwd", upwd);
					startActivity(TravelMenuIntent);
					dialogProgress.cancel();
					finish();
				}else{
					whatButton = false;
					networkthread ob = new networkthread();
				}
				break;
			case R.id.ImgBtnSettings:
				Intent SettingsIntent = new Intent(MainMenu.this,Settings.class);
				SettingsIntent.putExtra("name", uname);
				SettingsIntent.putExtra("pwd", upwd);
	    		startActivity(SettingsIntent);
	    		finish();
				break;
				
			case R.id.ImgBtnAlltravel:
				Intent TravelNameIntent = new Intent(MainMenu.this,TravelName.class);
	    		startActivity(TravelNameIntent);
	    		finish();
				break;
			case R.id.ImgBtnLogOut:
				showDialog(2);
				break;
			}
		}
	 
	protected Dialog onCreateDialog(int id) {
	 
         switch(id) {
         case 1:
        	 dialog.setContentView(R.layout.new_travel);
        	 travelName = (EditText) dialog.findViewById(R.id.EditTxtTravelNameInNew_travel); 
        	 btnCreate = (ImageButton) dialog.findViewById(R.id.ImgBtnOkInNew_travel);
        	 btnCancel = (ImageButton) dialog.findViewById(R.id.ImgBtnCancelInNew_travel);
        	 btnCreate.setOnClickListener(this);
        	 btnCancel.setOnClickListener(this);
             break;
         case 2:
        	 dialog.setContentView(R.layout.logout_or_not);
        	 btnLogout = (Button) dialog.findViewById(R.id.BtnLogoutInLogout);
        	 btnExit = (Button) dialog.findViewById(R.id.BtnExitInLogout);
        	 btnCancelInLogout = (Button) dialog.findViewById(R.id.BtnCancelInLogout);
        	 btnLogout.setOnClickListener(this);
        	 btnExit.setOnClickListener(this);
        	 btnCancelInLogout.setOnClickListener(this);
        	 break;
         default:
             //dialog = null;
         }
         if(id == 3){
        	 dialogProgress.setMessage("Loading ...");
         	 dialogProgress.setIndeterminate(true);
         	 dialogProgress.setCancelable(true);
         	 return dialogProgress; 
         }else{
        	 return dialog;
         }
	}
	 
	
	public void onClick(View v) {
		  switch (v.getId()) {
	          case R.id.ImgBtnOkInNew_travel:
	        	  if(travelName.getText().length() > 0){
	        		  showDialog(3);
	        		  whatButton = true;
	        		  networkthread ob = new networkthread();		        	  
	      		  }else{
	      			  Toast.makeText(MainMenu.this, "You must enter new travel name.",Toast.LENGTH_SHORT).show();
	      		  }
	          break;
	          case R.id.ImgBtnCancelInNew_travel:	        	  
	        	  dialog.cancel();
	        	  travelName.setText("");
	        	  break;
	          case R.id.BtnLogoutInLogout:	
	        	  db_manager.updtLogin(1, uname, "0", uname, resolver);
	        	  Intent LoginIntent = new Intent(MainMenu.this,Login.class);
		    	  startActivity(LoginIntent);
		    	  finish();
	        	  break;
	          case R.id.BtnExitInLogout:	        	  
		    	  finish();
	        	  break;
	          case R.id.BtnCancelInLogout:	        	  
	        	  dialog.cancel();
	        	  break;
	      }
	}
	
	class networkthread implements Runnable { 
		 
		 private Thread t;
		 int exception_count = 0;
		 private int MSG;
		 private ArrayList<String> currentTravelValue = new ArrayList<String>();
		 java.util.Date date = new java.util.Date();
		 
		 public networkthread() { 
			 t = new Thread(this); 
			 t.start();
		 } 
		 
		 private Handler handler = new Handler() {
			 
          @Override 
          public void handleMessage(Message msg) {
        	  switch(MSG){
	        	  case 1: 
	        		    db_manager.deleteArchive(1, resolver);
	  	    	 		currentTravelValue.add("" + travelName.getText());
	  	    	 		currentTravelValue.add("0");
	  	    	 		currentTravelValue.add("" + date);
	  	    	 		db_manager.insertArchive(1, currentTravelValue, resolver); 	
	  	    	 		Toast.makeText(MainMenu.this, "Okey",Toast.LENGTH_SHORT).show(); 
	  	    	 		Intent TravelMenuIntent = new Intent(MainMenu.this, TravelMenu.class);
	  	    	 		TravelMenuIntent.putExtra("TravelName", "" + travelName.getText());
	  	    	 		TravelMenuIntent.putExtra("name", uname);
	  	    	 		TravelMenuIntent.putExtra("pwd", upwd);
						startActivity(TravelMenuIntent);
						dialogProgress.cancel();
						finish(); 
						break;
	        	  case 2: 
	        		   dialogProgress.cancel();
	  	    	 		//dialog.cancel();
	  	    	 		Toast.makeText(MainMenu.this, "This travel name has already been taken",Toast.LENGTH_SHORT).show(); 
	  	    	 		//t.stop();
	  	    	 		break;
	        	  case 3: 
	        		    db_manager.deleteArchive(1, resolver);
	        		    Log.d("currentTravelValue", "" + currentTravelValue);
	        		    db_manager.insertArchive(1, currentTravelValue, resolver); 
	        		    Toast.makeText(MainMenu.this, "Okey",Toast.LENGTH_SHORT).show(); 
	  	    	 		Intent TravelMenu2Intent = new Intent(MainMenu.this, TravelMenu.class);
	  	    	 		TravelMenu2Intent.putExtra("TravelName", currentTravelValue.get(0));
	  	    	 		TravelMenu2Intent.putExtra("name", uname);
	  	    	 		TravelMenu2Intent.putExtra("pwd", upwd);
						startActivity(TravelMenu2Intent);
						dialogProgress.cancel();
						finish();
	        		  break;
	        	  case 4: 
	        		  dialogProgress.cancel();
	        		  Toast.makeText(MainMenu.this, "Travel not found!",Toast.LENGTH_SHORT).show();
	        		  break;
        	  }   	 	
  	        }

		 };

		 private Handler handler_expeption = new Handler() {
			 @Override 
          public void handleMessage(Message msg) {
				 dialogProgress.cancel();
				 Toast.makeText(MainMenu.this, "Connection refused. Please try again.",Toast.LENGTH_SHORT).show(); 
			 }
		 };
		 
		 public void run(){
			 
		 try { 
			 if(whatButton == true){
				 Log.d("what btn", "true");
				 //url = new URL("http://192.168.1.2:8080/traveljournal/android/NewTravel");
				 url = new URL("http://192.168.1.220:8080/traveljournal/android/NewTravel");
				 //url = new URL("http://www.atarkhishig.mn/traveljournal/android/NewTravel");
				 
			 }else{
				 Log.d("what btn", "false");
				 //url = new URL("http://192.168.1.2:8080/traveljournal/android/ContinueTravel");
				 url = new URL("http://192.168.1.220:8080/traveljournal/android/ContinueTravel");
				 //url = new URL("http://www.atarkhishig.mn/traveljournal/android/ContinueTravel"); 
			 }
			 
			 HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
		     httpurlconnection.setDoOutput(true);
		     httpurlconnection.setRequestMethod("POST");
		     
		     DataOutputStream dos = new DataOutputStream(httpurlconnection.getOutputStream());
  
		     //Log.d("" + travelName.getText(), uname+upwd);
		     dos.writeUTF(uname);
		     dos.writeUTF(upwd);
			     
			 if(whatButton == true){
			     Log.d("" + travelName.getText(), uname+upwd);
			     dos.writeUTF("" + travelName.getText());
		     }
		     
		     dos.flush();
		     dos.close();
		     Log.d("test", "test");
		     
		     DataInputStream din = new DataInputStream(httpurlconnection.getInputStream());
		     Log.d("test", "test1");
		     MSG = din.readInt();
		     if(whatButton == false && MSG == 3){
		    	 currentTravelValue.add(din.readUTF());
		    	 currentTravelValue.add(din.readUTF());
		    	 currentTravelValue.add(din.readUTF());	 
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
    	Log.d("mainmenu", "destroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
    	Log.d("mainmenu", "pause");
        super.onPause();
    }

    @Override
    protected void onResume() {
    	Log.d("mainmenu", "resume");
        super.onResume();
    }

}
