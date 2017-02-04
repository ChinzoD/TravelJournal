package grand.TravelJournal;

import grand.TravelJournal.db.DB_change_manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Comment extends Activity implements OnClickListener{
	
	private ListView Comments;
	private DB_change_manager db_manager;
	private ContentResolver resolver;
	private ArrayList<String> LoginValues = new ArrayList<String>();
	private ArrayList<String> post_comment_ids = new ArrayList<String>();
	private ArrayList<HashMap<String, Object>> maplistcomment = new ArrayList<HashMap<String, Object>>();
	int[] to = {R.id.TextViewUsernameInLComment, R.id.TextViewDateInLComment, R.id.TextViewCommentInLComment};
	private int page = 1;
	private ProgressBar loading;
	private URL url;
	private Button up, down, refresh, btnOk, btnCancel;
	private boolean isnextpage; 
	private Dialog dialog;
	private long commentIdPosition;
	private int delId;
	private boolean commentType = true;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        
        Comments = (ListView) findViewById(R.id.ListViewCommentsInComment);
        up = (Button) findViewById(R.id.BtnUpInComment);
        down = (Button) findViewById(R.id.BtnDownInComment);
        refresh = (Button) findViewById(R.id.BtnRefreshInComment);
        //---0 - VISIBLE; 4 - INVISIBLE; 8 - GONE---
        up.setVisibility(4); 
        
        
        loading = (ProgressBar) findViewById(R.id.ProgressBarInComment);   
        loading.setVisibility(0);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();

        LoginValues = db_manager.getLogin(1, resolver);
        if(!LoginValues.get(1).equals("0")){
        	networkthread ob = new networkthread();
        }else{
        	finish();
        	Toast.makeText(Comment.this, "You are not login!",Toast.LENGTH_SHORT).show(); 	
        }
        //mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, AllComments);        
        //Comments.setAdapter(mAdapter);
        Comments.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {
				// TODO Auto-generated method stub
				Log.d("pos id", ""+position+" "+id);
				commentIdPosition = Long.parseLong(post_comment_ids.get(position*2+1));
				delId = position;
				showDialog(1);
				return true;
			}
		});
        
        Comments.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

				Intent TabContainerIntent = new Intent(Comment.this, TabContainer.class);
				TabContainerIntent.putExtra("name", LoginValues.get(0));
				TabContainerIntent.putExtra("pwd", LoginValues.get(1));
				TabContainerIntent.putExtra("post_id", Long.parseLong(post_comment_ids.get(position*2)));
    	 		startActivity(TabContainerIntent);
    	 		finish();
				// Toast.makeText(Comment.this,  post_ids.get(position),Toast.LENGTH_SHORT).show();
			}
        	
        });
    }
    
	public void setList(){
		SimpleAdapter adapter = new SimpleAdapter(this.getApplicationContext(), 
				maplistcomment, R.layout.listviewcomment, new String[]{"username", "date", "comment"}, to);
		Comments.setAdapter(adapter);

		//Comments.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , AllComments));
	}
	
	public void check_buttons(){
		if(isnextpage == false){
	    	 //---0 - VISIBLE; 4 - INVISIBLE; 8 - GONE---
	    	 down.setVisibility(4);
	     }else{
	    	 down.setVisibility(0);
	     }
	     if(page > 1){
	    	up.setVisibility(0); 
	     }else{
	    	up.setVisibility(4); 
	     }
	}
	
	private void BlockButtons(boolean b){
		if(b == true){
			up.setClickable(true);
			down.setClickable(true);
			refresh.setClickable(true);
		}else{
			commentType = true;
			up.setClickable(false);
			down.setClickable(false);
			refresh.setClickable(false);
		}
	}
	
	protected Dialog onCreateDialog(int id) {
		 
        if(id ==1){
	       	 dialog.setContentView(R.layout.dialog_delete_comment);
	       	 btnOk = (Button) dialog.findViewById(R.id.BtnOkInDDelComment); 
	       	 btnCancel = (Button) dialog.findViewById(R.id.BtnCancelInDDelComment);
	       	 btnOk.setOnClickListener(this);
	       	 btnCancel.setOnClickListener(this);       	 
        }
        return dialog;
	}
	public void onClick(View v) {
		  switch (v.getId()) {
		  	case R.id.BtnOkInDDelComment:
				dialog.cancel();
				loading.setVisibility(0);
				Log.d("hi", "hi3"+commentIdPosition);
		  		//del = new DeleteComment(LoginValues.get(0), LoginValues.get(1), commentIdPosition);
				commentType = false;
				networkthread ob = new networkthread();
		  		loading.setVisibility(4);
		  		Log.d("hi", "hi22423423");
		  		break;
			case R.id.BtnCancelInDDelComment:
					dialog.cancel();
				break;
		  }
	}
	
	public void MyClickInComment(View view) {
			switch (view.getId()) {
				case R.id.BtnUpInComment:
					BlockButtons(false);
					page = page-1;
					networkthread ob = new networkthread();
					BlockButtons(true);
					break;
				case R.id.BtnDownInComment:
					BlockButtons(false);
					page = page+1;
					networkthread ob2 = new networkthread();
					BlockButtons(true);
					break;
				case R.id.BtnRefreshInComment:
					BlockButtons(false);
					networkthread ob3 = new networkthread();
					BlockButtons(true);
					break;
			}
	}
	 
	 class networkthread implements Runnable { 
		 
		 private Thread t;
		 private String MSG;
		 //private ArrayList<String> ArchiveValue = new ArrayList();
		 private int count=0;
		 
		 public networkthread() { 
			 t = new Thread(this); 
			 t.start();
		 } 
		 
		 private Handler handler = new Handler() {
			 
          @Override 
          public void handleMessage(Message msg) {
  	    	 	if(count != 0)
  		        {
  	    	 		
	  	    	 		check_buttons();
	  	    	 		setList();
	  	    	 		loading.setVisibility(4);
  	    	 		
  	    	 	}else{
  	    	 		if(commentType == true){
	  	    	 		Toast.makeText(Comment.this, "Don't have comment",Toast.LENGTH_SHORT).show(); 
	  	    	 		loading.setVisibility(4);
  	    	 		}else{
	    	 			Log.d("delId", ""+delId);
						Log.d("list", ""+maplistcomment);
						maplistcomment.remove(delId);
						Log.d("list", ""+maplistcomment);
						setList();
						Log.d("list", ""+post_comment_ids);
						post_comment_ids.remove(delId);
						post_comment_ids.remove(delId);
						Log.d("list", ""+post_comment_ids);
	    	 			Toast.makeText(Comment.this, "Comment deleted",Toast.LENGTH_SHORT).show();
	    	 		}
  	    	 	}
  	        }

		 };

		 private Handler handler_expeption = new Handler() {
			 @Override 
          public void handleMessage(Message msg) {
				 loading.setVisibility(4);
				 Toast.makeText(Comment.this, "Connection refused. Please try again.",Toast.LENGTH_SHORT).show(); 
			 }
		 };
		 
		 public void run(){
			 
		 try { 
			 if(commentType == true){
				 Log.d("con type", "true");
				 //url = new URL("http://192.168.1.220:8080/traveljournal/android/GetComment");
				 url = new URL("http://192.168.1.220:8080/traveljournal/android/GetComment");
				 //url = new URL("http://www.atarkhishig.mn/traveljournal/android/GetComment");
			 }else{
				 Log.d("con type", "false");
				 url = new URL("http://192.168.1.220:8080/traveljournal/android/DeleteComment");
				 //url = new URL("http://www.atarkhishig.mn/traveljournal/android/DeleteComment");
			 }
			 
			 HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
		     httpurlconnection.setDoOutput(true);
		     httpurlconnection.setRequestMethod("POST");

		     DataOutputStream dos = new DataOutputStream(httpurlconnection.getOutputStream());
		     Log.d("test", "testsdfdsaf");
		     Log.d("test", "teeeeeeeeeeeest"+LoginValues);
		     dos.writeUTF(LoginValues.get(0));
		     dos.writeUTF(LoginValues.get(1));
		     if(commentType == true){
		    	 Log.d("con type", "true");
		    	 dos.writeInt(page);
		    	 maplistcomment.clear();
			     post_comment_ids.clear();
		     }else{
		    	 Log.d("con type", "false");
		    	 dos.writeLong(commentIdPosition);
		     }
		     dos.flush();
		     dos.close();
		     Log.d("test", "test");
		     
		    
		     
		     DataInputStream din = new DataInputStream(httpurlconnection.getInputStream());
		     Log.d("test", "test1");
		     count = din.readInt();		     
		     Log.d("test", "testcount"+count);
		     for(int j = 0; j < count; j++){
		    	 HashMap<String,Object> map = new HashMap<String,Object>();
		    	 map.put("username", din.readUTF());
				 map.put("date", din.readUTF());
				 map.put("comment", din.readUTF());	 
				 Log.d("test", "testcount"+map);			
				 post_comment_ids.add(din.readUTF());
		    	 post_comment_ids.add(din.readUTF());
		    	 maplistcomment.add(map);
		     }	
		     if(commentType == true){
		    	 isnextpage = din.readBoolean();
		     }
		     //Log.d("test", "testcount"+maplistcomment);
		     //Log.d("test", "testcount"+post_comment_ids);
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