package grand.TravelJournal.facebook;

import org.json.JSONException;
import org.json.JSONObject;

import grand.TravelJournal.R;
import grand.TravelJournal.facebook.BaseRequestListener;
import grand.TravelJournal.facebook.BaseDialogListener;
import grand.TravelJournal.facebook.SessionEvents.AuthListener;
import grand.TravelJournal.facebook.SessionEvents.LogoutListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FacebookActivity extends Activity {
    
    //public static final String APP_ID = "380eb8bb8b15ff91c366b57a4441e76d";
    public static final String APP_ID = "175729095772478";
    private static final int SELECT_PICTURE = 1;
    private static final String[] PERMISSIONS =
        new String[] {"publish_stream", "read_stream", "offline_access"};
    private LoginButton mLoginButton;
    private TextView mText;
    private ImageButton imgPostBtn, imgDeleteBtn, imgBrowseBtn, imgUploadBtn;
    private String selectedImagePath = "hooson";
	private FileInputStream in;
	private BufferedInputStream buf;
	private byte[] img;
    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
    private ProgressDialog dialogProgress;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (APP_ID == null) {
            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
                    "specified before running this example: see Example.java");
        }
        
        dialogProgress = new ProgressDialog(this); 
        
        setContentView(R.layout.facebook);
        mLoginButton = (LoginButton) findViewById(R.id.login);
        mText = (TextView) FacebookActivity.this.findViewById(R.id.txt);
        imgPostBtn = (ImageButton) findViewById(R.id.ImgBtnPostWallInFacebook);
        imgDeleteBtn = (ImageButton) findViewById(R.id.ImgBtnDeleteInFacebook);
        imgUploadBtn = (ImageButton) findViewById(R.id.ImgBtnUploadInFacebook);
        imgBrowseBtn = (ImageButton) findViewById(R.id.ImgBtnBrowseInFacebook);
        imgBrowseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);              	
            }
        });
        
       	mFacebook = new Facebook();
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
       
        SessionStore.restore(mFacebook, this);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        mLoginButton.init(mFacebook, PERMISSIONS);
        
        imgBrowseBtn.setVisibility(mFacebook.isSessionValid() ?
                View.VISIBLE :
                View.INVISIBLE);
        
        imgUploadBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("method", "photos.upload");

                if(selectedImagePath.equals("hooson")){
                	mText.setText("doesn't browse picture");
                }else{	
                	showDialog(1);
                	getPhoto();
                	selectedImagePath = "hooson";
                	params.putByteArray("picture", img);          	
                	mAsyncRunner.request(null, params, "POST", new SampleUploadListener());
                }
            }
        });
        imgUploadBtn.setVisibility(mFacebook.isSessionValid() ?
                View.VISIBLE :
                View.INVISIBLE);
        
        imgPostBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mFacebook.dialog(FacebookActivity.this, "stream.publish", 
                        new SampleDialogListener());          
            }
        });
        imgPostBtn.setVisibility(mFacebook.isSessionValid() ?
                View.VISIBLE : 
                View.INVISIBLE);
        
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	        if (requestCode == SELECT_PICTURE) {
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
	
	 protected Dialog onCreateDialog(int id) {
         if(id == 1){
        	 dialogProgress.setMessage("Loading ...");
         	 dialogProgress.setIndeterminate(true);
         	 dialogProgress.setCancelable(true);      	  
         }
         return dialogProgress;
	}
	 
    public class SampleAuthListener implements AuthListener {
        
        public void onAuthSucceed() {
            mText.setText("You have logged in! ");

            imgBrowseBtn.setVisibility(View.VISIBLE);
            imgUploadBtn.setVisibility(View.VISIBLE);
            imgPostBtn.setVisibility(View.VISIBLE);
        }

        public void onAuthFail(String error) {
            mText.setText("Login Failed: " + error);
        }
    }
    
    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            mText.setText("Logging out...");
        }
        
        public void onLogoutFinish() {
            mText.setText("You have logged out! ");
            imgBrowseBtn.setVisibility(View.INVISIBLE);
            imgUploadBtn.setVisibility(View.INVISIBLE);
            imgPostBtn.setVisibility(View.INVISIBLE);
            imgDeleteBtn.setVisibility(View.INVISIBLE);
        }
    }
    
    public class SampleRequestListener extends BaseRequestListener {

        public void onComplete(final String response) {
            try {
                // process the response here: executed in background thread
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");
                
                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original 
                // thread that created a view hierarchy can touch its views."
                FacebookActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mText.setText("Hello there, " + name + "!");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
 
    public class SampleUploadListener extends BaseRequestListener {

        public void onComplete(final String response) {
            try {
                // process the response here: (executed in background thread)
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String src = json.getString("src");
                
                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original 
                // thread that created a view hierarchy can touch its views."
                FacebookActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    	dialogProgress.cancel();
                       // mText.setText("Photo has been uploaded at \n" + src);
                        mText.setText("Photo has been successfully uploaded");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
    public class WallPostRequestListener extends BaseRequestListener {
        
        public void onComplete(final String response) {
            Log.d("Facebook-Example", "Got response: " + response);
            String message = "<empty>";
            try {
                JSONObject json = Util.parseJson(response);
                message = json.getString("message");
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
            final String text = "Your Wall Post: " + message;
            FacebookActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mText.setText(text);
                }
            });
        }
    }
    
    public class WallPostDeleteListener extends BaseRequestListener {
        
        public void onComplete(final String response) {
            if (response.equals("true")) {
                Log.d("Facebook-Example", "Successfully deleted wall post");
                FacebookActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    	imgDeleteBtn.setVisibility(View.INVISIBLE);
                        mText.setText("Deleted Wall Post");
                    }
                });
            } else {
                Log.d("Facebook-Example", "Could not delete wall post");
            }
        }
    }
    
    public class SampleDialogListener extends BaseDialogListener {

        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null) {
                Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
                mAsyncRunner.request(postId, new WallPostRequestListener());
                imgDeleteBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        mAsyncRunner.request(postId, new Bundle(), "DELETE", 
                                new WallPostDeleteListener());
                    }
                });
                imgDeleteBtn.setVisibility(View.VISIBLE);
            } else {
                Log.d("Facebook-Example", "No wall post made");
            }
        }
    }
    
}