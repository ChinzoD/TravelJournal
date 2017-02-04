package grand.TravelJournal;

import grand.TravelJournal.db.DB_change_manager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity {
    /** Called when the activity is first created. */
	
	TextView register, forgotPass, errorMSG;
	EditText uname, pwd;
	private URL url;
    private ProgressDialog dialog;
	private String MSG;
	private DB_change_manager db_manager;
	private ContentResolver resolver;
	private ArrayList<String> LoginValues = new ArrayList<String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
        
        dialog = new ProgressDialog(this);
        
        uname = (EditText) findViewById(R.id.EditTxtName);
        pwd = (EditText) findViewById(R.id.EditTxtPass);
        errorMSG = (TextView) findViewById(R.id.TextViewErrorMsgInLogin);
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        
        LoginValues = db_manager.getLogin(1, resolver);
        
        if(!LoginValues.get(1).equals("0")){
        	Log.d("orson", "first");
        	Intent MainMenuIntent = new Intent(Login.this,MainMenu.class);
        	MainMenuIntent.putExtra("name", LoginValues.get(0));
        	MainMenuIntent.putExtra("pwd", LoginValues.get(1));
    		startActivity(MainMenuIntent);
    		finish();
        }
              
    }
    
    public void MyClickHandler(View view) {
		switch (view.getId()) {
		case R.id.ImgBtnLogin:		
		
			showDialog(1);
			if(uname.getText().toString().matches("[a-zA-Z_0-9]{4,13}") &&  pwd.getText().toString().matches("[a-zA-Z_0-9]{4,12}") ){
					networkthread ob = new networkthread(uname.getText().toString(), pwd.getText().toString());
			}else{
				dialog.cancel();
				Log.d("pass", ": " + uname.getText().toString().matches("[a-zA-Z_0-9]{4,13}"));
				errorMSG.setText("Incorrect word!");
			}
			break;
		case R.id.ImgBtnExit:

    		finish(); 
			break;
		case R.id.TextViewRegister:
			Intent RegisterIntent = new Intent(Login.this,Register.class);
    		startActivity(RegisterIntent);
    		finish(); 
			break;
		}
	}
    
    public String sec(String s) throws NoSuchAlgorithmException{
        MessageDigest m=MessageDigest.getInstance("MD5");
        m.update(s.getBytes(),0,s.length());

        return new BigInteger(1, m.digest()).toString(16);
    }
 
    
	class networkthread implements Runnable { 
		 
		 private static final String TAG = "Test"; 
		 private Thread t;
		 String uname, upwd; 
		 int exception_count = 0;
		 
		 public networkthread(String uname, String upwd) { 
			 this.uname = uname; 
			 this.upwd = upwd;
			 t = new Thread(this); 
			 t.start();
		 } 
		 
		 private Handler handler = new Handler() {
			 
            @Override 
            public void handleMessage(Message msg) {
    	    	 	if(MSG.equals("ok"))
    		        {
    	    	 		errorMSG.setText(""); 
    	    	 		Intent MainMenuIntent = new Intent(Login.this, MainMenu.class);
    	    	 		MainMenuIntent.putExtra("name", uname);
    	    	 		MainMenuIntent.putExtra("pwd", upwd);
    	    	 		if(LoginValues.get(0).equals(uname)){
    	    	        	db_manager.updtLogin(1, uname, upwd, uname, resolver);
    	    	        }else{
    	    	        	db_manager.updtLogin(1, uname, upwd, LoginValues.get(0), resolver);
    	    	        	db_manager.updtLogin(2, "0", "0", uname, resolver);
    	    	        	db_manager.updtLogin(3, "0", "0", uname, resolver);
    	    	        	db_manager.deleteArchive(2, resolver);
    	    	        	db_manager.deleteTravelName(resolver);
    	    	        	
    	    	        }
    	    	 		startActivity(MainMenuIntent);
    	    	 		finish();
  	 		
    	    	 	}else{
    	    	 		dialog.cancel();
    	    	 		errorMSG.setText("Your name or password is incorrect. Please try again. "); 
    		        }
    	        }

		 };

		 private Handler handler_expeption = new Handler() {
			 @Override 
            public void handleMessage(Message msg) {
				 dialog.cancel();
				 errorMSG.setText("Connection refused. Please try again."); 
			 }
		 };
		 
		 public void run(){
			 
			 Log.v(TAG, "inside sub thread");

		 try { 
			 //url = new URL("http://192.168.1.2:8080/traveljournal/android/Login");
			 url = new URL("http://192.168.1.220:8080/traveljournal/android/Login");
			 //url = new URL("http://www.atarkhishig.mn/traveljournal/android/Login");
			 
			 HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
		     httpurlconnection.setDoOutput(true);
		     httpurlconnection.setRequestMethod("POST");
		     
		     DataOutputStream dos = new DataOutputStream(httpurlconnection.getOutputStream());
		      	     
		     dos.writeUTF(uname);
		     dos.writeUTF(upwd); 
		     dos.flush();
		     dos.close();

		     DataInputStream din = new DataInputStream(httpurlconnection.getInputStream());
	      
		     MSG = din.readUTF();
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
	    protected Dialog onCreateDialog(int id) {
	        if(id == 1){     
	        		dialog.setMessage("Loging in ...");
	                dialog.setIndeterminate(true);
	                dialog.setCancelable(true);
	                return dialog;
	            }
	        return null;
	    }
	 
    @Override
    protected void onDestroy() {
    	Log.d("login", "destroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
    	Log.d("login", "pause");
        super.onPause();
    }

    @Override
    protected void onResume() {
    	Log.d("login", "resume");
        super.onResume();
    }
}