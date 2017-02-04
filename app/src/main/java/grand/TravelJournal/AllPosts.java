package grand.TravelJournal;

import grand.TravelJournal.db.DB_change_manager;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AllPosts extends Activity{
	
	private ListView Posts;
	private DB_change_manager db_manager, db_manager2;
	private ContentResolver resolver;
	private ArrayList<String> PostList = new ArrayList<String>();
	private ArrayList<String> OnlyPostList = new ArrayList<String>();
	private ArrayList<String> OnlyPost_ids = new ArrayList<String>();
	private ProgressDialog dialogProgress;
	private String TName, uname, upwd;;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allposts);
        
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
        
        dialogProgress = new ProgressDialog(this); 
        Posts = (ListView) findViewById(R.id.ListViewPostsInAllPosts);
        Posts.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				// TODO Auto-generated method stub
				Intent TabContainerIntent = new Intent(AllPosts.this, TabContainer.class);
				TabContainerIntent.putExtra("name", uname);
				TabContainerIntent.putExtra("pwd", upwd);
				TabContainerIntent.putExtra("post_id", Long.parseLong(OnlyPost_ids.get(position)));	
  	      		startActivity(TabContainerIntent);
  	      	    dialogProgress.cancel();
  	      		finish(); 
			}
		});
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        
        PostList = db_manager.getArchive(2, TName, resolver);
        Log.d("teeeeeeeeeeeest", ""+PostList);
        
        if(PostList.get(0).equals("0")){
        	OnlyPostList.add("No posts");
        }else{
        	for(int i = 0; i < PostList.size(); i+=3){
        		OnlyPost_ids.add(PostList.get(i));
        		OnlyPostList.add(PostList.get(i+1));      		
        	}      	
        }
        setList();
    }
    
	public void setList(){
		Posts.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , OnlyPostList));
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