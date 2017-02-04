package grand.TravelJournal;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TabContainer extends TabActivity {
	
	private String uname, upwd;
	private long post_id;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
        	uname = extras.getString("name");
        	upwd = extras.getString("pwd");
        	post_id = extras.getLong("post_id");
        }
        
        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("Travel Post")
                .setContent(new Intent(this, PostComment.class)
                .putExtra("name", uname)
                .putExtra("pwd", upwd)
                .putExtra("post_id", post_id)));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("Post Location")
                .setContent(new Intent(this, MyTrackMap.class)
                .putExtra("post_id", post_id)));
        /*
        // This tab sets the intent flag so that it is recreated each time
        // the tab is clicked.
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator("destroy")
                .setContent(new Intent(this, Controls2.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));*/
        
        
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