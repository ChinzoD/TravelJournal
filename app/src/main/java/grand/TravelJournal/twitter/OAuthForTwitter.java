package grand.TravelJournal.twitter;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class OAuthForTwitter extends Activity {
	
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;
	public final static String consumerKey = 		"X9nFu1GPxmiQs572HaRF3g";
	public final static String consumerSecret = 	"mONha4pVJQl1uzg1XPZ7xq8E1BuskvlaYQT5aPz9w";
	private final String CALLBACKURL = "http://twitter.com/ch_mongol";
    /*
     * 
     * OnCreate method for class
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doOauth();
    }
    /**
	 * Opens the browser using signpost jar with application specific 
	 * consumerkey and consumerSecret.
	 */
	private void doOauth() {
		try {
			httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
			httpOauthprovider = new DefaultOAuthProvider("http://twitter.com/oauth/request_token",
												"http://twitter.com/oauth/access_token",
												"http://twitter.com/oauth/authorize");
			String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACKURL);
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	/**
	* After use authorizes this is the function where we get back callbac with
	* user specific token and secret token. You might want to store this token
	* for future use. 
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);

		Uri uri = intent.getData();
		//Check if you got NewIntent event due to Twitter Call back only
		if (uri != null && uri.toString().startsWith(CALLBACKURL)) {

			String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

			try {
				// this will populate token and token_secret in consumer
				httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
				String userKey = httpOauthConsumer.getToken();
				String userSecret = httpOauthConsumer.getConsumerSecret();
			}
			catch(Exception e){
				Log.d("", e.getMessage());
			}
	
	}
	}
}