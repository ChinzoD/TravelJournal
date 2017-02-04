package grand.TravelJournal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;


public class Register extends Activity {
	
	private EditText name, pass, repass, mail;
	private Button butsah, burtgeh;
	private TextView aldaa1;
	private ProgressDialog dialogProgress;
	 
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.register);
	        
	        WindowManager.LayoutParams lp = getWindow().getAttributes();
	        lp.screenBrightness = 100 / 100.0f;
	        getWindow().setAttributes(lp);
	        
	        dialogProgress = new ProgressDialog(this); 
	        name = (EditText) findViewById(R.id.EditText_bur_ner);
	        pass = (EditText) findViewById(R.id.EditText_bur_pass);
	        repass = (EditText) findViewById(R.id.EditText_bur_repass);
	        mail = (EditText) findViewById(R.id.EditText_bur_mail);


	        aldaa1 = (TextView) findViewById(R.id.TextView_bur_exp);

	        
	        burtgeh = (Button) findViewById(R.id.Btn_bur_burtgeh);
	        burtgeh.setOnClickListener(new OnClickListener(){

				
				public void onClick(View arg0) {
					if(pass.getText().toString().equals(repass.getText().toString())){
						if(name.getText().toString().matches("[a-zA-Z_0-9]{4,12}")){
								if(pass.getText().toString().matches("[a-zA-Z_0-9]{4,12}")){
									if(mail.getText().toString().matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
										aldaa1.setVisibility(8);
										showDialog(1);
										networkthread ob = new networkthread(name.getText().toString(), pass.getText().toString(), 
													mail.getText().toString());
									}else{
										setAldaa(4);
									}
	        					}else{
	        						setAldaa(3);
	        					}
						}else{
							setAldaa(2);
						}
					}else{
						setAldaa(1);
					}
				}
				
			});
	        butsah = (Button) findViewById(R.id.Btn_bur_butsah);
	        butsah.setOnClickListener(new OnClickListener(){

				
				public void onClick(View arg0) {
					Intent LoginIntent = new Intent(Register.this,Login.class);
		    		startActivity(LoginIntent);
		    		finish(); 
				}
				
			});
	        

	        
	 }
	 
	 protected Dialog onCreateDialog(int id) {
         if(id == 1){
        	 dialogProgress.setMessage("Registering ...");
         	 dialogProgress.setIndeterminate(true);
         	 dialogProgress.setCancelable(true);      	  
         }
         return dialogProgress;
	}
	 
	 private void setAldaa(int exp){

			 switch(exp){
			 	case 1: aldaa1.setText("The two passwords you filled in did not match."); break;
			 	case 2: aldaa1.setText("Your name has an invalid amount of characters."); break;
			 	case 3: aldaa1.setText("Your password has an invalid amount of characters."); break;
			 	case 4: aldaa1.setText("The e-mail address you filled in is invalid."); break;
			 }
		//---0 - VISIBLE; 4 - INVISIBLE; 8 - GONE---
		 aldaa1.setVisibility(0);
	 }
	 

		
		  public String sec(String s) throws NoSuchAlgorithmException{
		        MessageDigest m=MessageDigest.getInstance("MD5");
		        m.update(s.getBytes(),0,s.length());

		        return new BigInteger(1, m.digest()).toString(16);
		 }
		  
class networkthread implements Runnable { 
	 
	 private static final String TAG = "Test"; 
	 private Thread t;
	 String uname, upwd, umail, who, id_pass;
	 private int i;
	 
	 public networkthread(String uname, String upwd, String mail) { 
		 this.uname = uname; 
		 this.upwd = upwd;
		 this.umail = mail;

		 try {
			this.id_pass = uname+ sec(upwd);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		 t = new Thread(this); 
		 t.start();
	 } 
	 

	 private Handler handler = new Handler() {
		 @Override 
        public void handleMessage(Message msg) {
			 
			 if(i == 1){
				 	aldaa1.setVisibility(8);
				 	Intent LoginIntent = new Intent(Register.this, Login.class);
		    		startActivity(LoginIntent);
		    		dialogProgress.cancel();
		    		Toast.makeText(Register.this, "Successfully registered",Toast.LENGTH_SHORT).show(); 
		    		finish();
			 }else{
				 dialogProgress.cancel();
				 aldaa1.setText("This username or email already exists, try a different one.");
				 aldaa1.setVisibility(0);
			 }
				 
			 
		 }
	 };
	 
	 private Handler handler_expeption = new Handler() {
		 @Override 
        public void handleMessage(Message msg) {
			 aldaa1.setText("Connection refused. Please try again.");
			 aldaa1.setVisibility(0);
			 dialogProgress.cancel();
		 }
	 };
	 
	 public void run(){
		 
		 Log.v(TAG, "inside sub thread");

	 try { 
		 URL url = new URL("http://192.168.1.220:8080/traveljournal/android/UserRegister");
		 //URL url = new URL("http://www.epubers.info/traveljournal/android/UserRegister");
		 
	     HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
	     httpurlconnection.setDoOutput(true);
	     httpurlconnection.setRequestMethod("POST");
	     
	     DataOutputStream dos = new DataOutputStream(
	    	       httpurlconnection.getOutputStream());
	     dos.writeUTF(uname);
	     dos.writeUTF(upwd);
	     dos.writeUTF(umail);
	     dos.flush();
	     dos.close();     
	     
	     
	     DataInputStream is = new DataInputStream(httpurlconnection.getInputStream());
	     
	     i = is.readInt();

	     handler.sendEmptyMessage(0); 
	     
		 }
	 catch(Exception e){ 
		 Log.v(TAG, "Exception:" +e); 
		 e.printStackTrace(); 
		 handler_expeption.sendEmptyMessage(0); 
		 }
	 
	 
	
	 } 
	  
	}
}