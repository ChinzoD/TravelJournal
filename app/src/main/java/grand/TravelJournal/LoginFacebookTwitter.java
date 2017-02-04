package grand.TravelJournal;


import java.util.ArrayList;
import grand.TravelJournal.db.DB_change_manager;
import grand.TravelJournal.facebook.FacebookActivity;
import grand.TravelJournal.twitter.OAuthForTwitter;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginFacebookTwitter extends Activity{
	
	ImageButton btnBack;
	EditText faceUname, facePass, twitUname, twitPass;
	
	RelativeLayout layoutFacebook, layoutTwitter, layoutFaceLogin, layoutTwitLogin, layoutTwitChange;
	
	MediaPlayer mp;
	private DB_change_manager db_manager;
	private ContentResolver resolver;
	private ArrayList<String> LoginValues = new ArrayList<String>();

	//private TwitterWriter twit;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginfacebooktwitter);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
     
       
    }
    
	
	 public void MyClickInLoginFbTw(View view) {
			switch (view.getId()) {
			case R.id.ImgBtnBackInLFT:		
				break;
			case R.id.ImgBtnFbLoginInLFT:	
				Intent FBIntent = new Intent(LoginFacebookTwitter.this,FacebookActivity.class);
	    		startActivity(FBIntent);
	    		finish(); 
				break;
			case R.id.ImgBtnTwLoginInLFT:	
				Intent TWIntent = new Intent(LoginFacebookTwitter.this,OAuthForTwitter.class);
	    		startActivity(TWIntent);
	    		finish(); 
				break;			
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
