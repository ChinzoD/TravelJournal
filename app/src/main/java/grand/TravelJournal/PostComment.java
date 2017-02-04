package grand.TravelJournal;

import grand.TravelJournal.db.DB_change_manager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class PostComment extends Activity implements OnClickListener{
	
	private String uname, upwd;
	private long post_id;
	private DB_change_manager db_manager;
	private ContentResolver resolver;
	private URL url;
	private ArrayList<String> post = new ArrayList<String>();
	private ArrayList<Long> comment_ids = new ArrayList<Long>();
	private ArrayList<HashMap<String, Object>> maplistcomment = new ArrayList<HashMap<String, Object>>();
	int[] to = {R.id.TextViewUsernameInLComment, R.id.TextViewDateInLComment, R.id.TextViewCommentInLComment};
	private TextView txtTName, txtPost, txtDate;
	private ListView listComments;
	private EditText eTextComment;
	private Button btnSend, btnOk, btnCancel;
	private String strComment;
	//WhatConnection >> 1 - postuudaa avna, 2 commentuudaa avna, 3 comment ilgeene
	private int WhatConnection;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-d HH:mm:ss" );
	private String commentDate;
	private long comment_id;
	private int com_pos_id;
	private Dialog dialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postcomment);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
        
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
        	uname = extras.getString("name");
        	upwd = extras.getString("pwd");
        	post_id = extras.getLong("post_id");
        }
        
        dialog = new Dialog(this);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        
        txtTName = (TextView) findViewById(R.id.TextViewTravelNameInPComment);
        txtPost = (TextView) findViewById(R.id.TextViewPostInPComment);
        txtDate = (TextView) findViewById(R.id.TextViewDateInPComment); 
        listComments = (ListView) findViewById(R.id.ListViewCommentsInPComment);
        listComments.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				com_pos_id = position;
				comment_id = comment_ids.get(position);	
				showDialog(1);
				return true;
			}
		});
        
        eTextComment = (EditText) findViewById(R.id.EditTxtCommentInPComment);
        btnSend = (Button) findViewById(R.id.BtnSendInComment);
        btnSend.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnSend.setClickable(false);
				if(eTextComment.length() > 0){
					strComment = "" + eTextComment.getText();
					WhatConnection = 3;
					getNetwork();
					
					eTextComment.setText("");
				}else{
					Toast.makeText(PostComment.this, "Null comment!",Toast.LENGTH_SHORT).show(); 
				}
				btnSend.setClickable(true);
			}
		});
        
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        Log.d("post umnu", "asdfsdf"+post_id);
        getPost();
        Log.d("post daraa ", "44444"+post);
        if(!post.get(0).equals("0")){
        	Log.d("post", ""+post);
        	txtTName.setText(post.get(0));
        	txtPost.setText(post.get(4));
        	txtDate.setText(post.get(5));
        //WhatConnection >> 1 - postuudaa avna, 2 commentuudaa avna, 3 comment ilgeene
        	WhatConnection = 2;
        }else{
        	WhatConnection = 1;
        }
        getNetwork();
    }
    
	public void getPost(){
		post = db_manager.getArchiveFindByPostId(post_id, resolver);
	}
	public void setList(){
		SimpleAdapter adapter = new SimpleAdapter(this.getApplicationContext(), maplistcomment, R.layout.listviewcomment, new String[]{"username", "date", "comment"}, to);
		listComments.setAdapter(adapter);
	}
	public void getNetwork(){
		networkthread ob = new networkthread();
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
				WhatConnection = 4;
				networkthread ob = new networkthread();
		  		break;
			case R.id.BtnCancelInDDelComment:
					dialog.cancel();
				break;
		  }
	}
	
	class networkthread implements Runnable { 
		 
		 private Thread t;
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
 	    	 	//WhatConnection >> 1 - postuudaa avna, 2 commentuudaa avna, 3 comment ilgeene
 	    	 		switch(WhatConnection){
	 	    	 		case 1: 
	 	    	 			Log.d("test", "post hiihgui bgaamu");
	 	    	 			db_manager.insertArchive(2, post, resolver);
	 	    	 			Log.d("test", "tttttt"+post);
	 	    	 			Log.d("test", ""+post_id);
	 	    	 			getPost();	
 	    	 				txtTName.setText(post.get(0));
 	    	 				txtPost.setText(post.get(4));
 	    	 				txtDate.setText(post.get(5));

			 	           	Log.d("test", "what con");
			 	           	WhatConnection = 2;
			 	            //networkthread ob2 = new networkthread();
			 	           getNetwork();
			 	            break;
	 	    	 		case 2: 
	 	    	 			setList();
	 	    	 			break;
	 	    	 		case 3:
	 	    	 			HashMap<String,Object> map = new HashMap<String,Object>();
		  	 		    	map.put("username", uname);
		  	 				map.put("date", commentDate);
		  	 				map.put("comment", strComment);
		  	 		    	maplistcomment.add(0, map);
	 						setList();
	 	    	 			break;
 	    	 		}
 	    	 		//dialogProgress.cancel();
	 		
 	    	 	}else{
 	    	 		//dialogProgress.cancel();
 	    	 		if(WhatConnection != 4){
	 	    	 		Toast.makeText(PostComment.this, "No comment",Toast.LENGTH_SHORT).show(); 
	 	    	 		t.stop();
	 	    	 	}else{
						maplistcomment.remove(com_pos_id);
						setList();
						comment_ids.remove(com_pos_id);
	    	 			Toast.makeText(PostComment.this, "Comment deleted",Toast.LENGTH_SHORT).show();
	    	 		}
 	    	 	}
 	        }

		 };

		 private Handler handler_expeption = new Handler() {
			 @Override 
         public void handleMessage(Message msg) {
				// dialogProgress.cancel();
				 Toast.makeText(PostComment.this, "Connection refused. Please try again.",Toast.LENGTH_SHORT).show(); 
			 }
		 };
		 
		 public void run(){
			 
		 try { 
			//WhatConnection >> 1 - postuudaa avna, 2 commentuudaa avna, 
			// 3 comment ilgeene, 4 comment ustgana
			 switch(WhatConnection){
	  	 		case 1: 
	  	 			Log.d("test", "get post");
					//url = new URL("http://192.168.1.2:8080/traveljournal/android/GetPost");
	  	 			url = new URL("http://192.168.1.220:8080/traveljournal/android/GetPost");
					//url = new URL("http://www.atarkhishig.mn/traveljournal/android/GetPost");	
		  	 		break;
	  	 		case 2: 
	  	 			Log.d("test", "current comment");
					//url = new URL("http://192.168.1.2:8080/traveljournal/android/GetCurrentComment");
	  	 			url = new URL("http://192.168.1.220:8080/traveljournal/android/GetCurrentComment");
					//url = new URL("http://www.atarkhishig.mn/traveljournal/android/GetCurrentComment");
	  	 			break;
	  	 		case 3: 
	  	 			Log.d("test", "orsoon");
					//url = new URL("http://192.168.1.2:8080/traveljournal/android/SetComment");
	  	 			url = new URL("http://192.168.1.220:8080/traveljournal/android/SetComment");
					//url = new URL("http://www.atarkhishig.mn/traveljournal/android/SetComment");
	  	 			break;
	  	 		case 4: 
	  	 			Log.d("test", "orsoon");
					//url = new URL("http://192.168.1.2:8080/traveljournal/android/DeleteComment");
	  	 		    url = new URL("http://192.168.1.220:8080/traveljournal/android/DeleteComment");
					//url = new URL("http://www.atarkhishig.mn/traveljournal/android/DeleteComment");
	  	 			break;
	 		}

			 HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
		     httpurlconnection.setDoOutput(true);
		     httpurlconnection.setRequestMethod("POST");
		     
		     DataOutputStream dos = new DataOutputStream(httpurlconnection.getOutputStream());
		     Log.d("asdasd", ""+uname+upwd+post_id);
		     dos.writeUTF(uname);
		     dos.writeUTF(upwd);
		     if(WhatConnection == 4){
		    	 dos.writeLong(comment_id);
		     }else{
		    	 dos.writeLong(post_id);
		     }
		     if(WhatConnection == 3){
		    	 commentDate = formatter.format(new Date());
		    	 dos.writeUTF(strComment);
		    	 dos.writeUTF(commentDate);
		     }
		     dos.flush();
		     dos.close();
		     Log.d("test", "test00000000000");
		     
		     DataInputStream din = new DataInputStream(httpurlconnection.getInputStream());
		     Log.d("test", "test11111111111");
		     post.clear();
		     count = din.readInt();
		     Log.d("test", "te4444411111");
		    //WhatConnection >> 1 - postuudaa avna, 2 commentuudaa avna, 3 comment ilgeene
		    switch(WhatConnection){
		    	case 1:
		  	 		 Log.d("test", "ok"+count);
				     for(int j = 0; j < count; j++){
				    	 Log.d("fortest", "0");
				    	 post.add(din.readUTF());
				    	 Log.d("fortest", "1");
				    	 post.add(din.readUTF());
				    	 Log.d("fortest", "2");
				    	 post.add(din.readUTF());
				    	 Log.d("fortest", "3");
				    	 post.add(din.readUTF());
				    	 Log.d("fortest", "4");
				    	 post.add(din.readUTF());
				    	 Log.d("fortest", "5");
				    	 post.add(din.readUTF());
				    	 Log.d("fortest", "6");
				    	 post.add(din.readUTF());
				    	 Log.d("fortest", "7");
				    	 Log.d("value1", ""+j+"--"+post);
				     }
				     break;
		    	case 2:
			    	Log.d("test", "ok2"+count);
	  	 			for(int j = 0; j < count; j++){		
	  	 		    	 HashMap<String,Object> map = new HashMap<String,Object>();
	  	 		    	 map.put("username", din.readUTF());
	  	 				 map.put("date", din.readUTF());
	  	 				 map.put("comment", din.readUTF());
	  	 				 comment_ids.add(din.readLong());
	  	 		    	 maplistcomment.add(map);
				     }
	  	 			break;
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