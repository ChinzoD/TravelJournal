package grand.TravelJournal.db;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import grand.TravelJournal.db.JournalDB.OptionColumns;
import grand.TravelJournal.db.JournalDB.CurrentTravelColumns;
import grand.TravelJournal.db.JournalDB.LoginColumns;
import grand.TravelJournal.db.JournalDB.TravelNameColumns;
import grand.TravelJournal.db.JournalDB.TravelArchiveColumns;


public class DB_change_manager extends Activity implements URIS{
	
	public ContentValues newValues;
	public ContentResolver resolver;
	private static Cursor cursor;
	private ArrayList<String> Values = new ArrayList<String>();
	private String dest_unit;
	
	public String getOptions(ContentResolver resolver){
     	
		try {
            cursor = resolver.query(OPTION_URI, OPTIONS, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
            	Log.d("orson", "db change manager");
            	dest_unit = cursor.getString(1);
            	dest_unit += cursor.getString(0);
            	Log.d("test", dest_unit);
            }else{
            	createtbls(resolver);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		return dest_unit;
	}
	
	public void updtDistance(int old_dist, long new_dist, ContentResolver resolver){
		newValues = new ContentValues();
		newValues.put(OptionColumns.Distance, new_dist);
		switch(old_dist){
			case 0: 
				resolver.update(OPTION_URI, newValues, "distance = 0", null);
				break;
			case 1: 
				resolver.update(OPTION_URI, newValues, "distance = 1", null);
				break;
			case 2: 
				resolver.update(OPTION_URI, newValues, "distance = 2", null);
				break;
		}
	}
	
	public void updtUnit(int old_unit, long new_unit, ContentResolver resolver){
		newValues = new ContentValues();
		newValues.put(OptionColumns.Unit, new_unit);
		switch(old_unit){
			case 0: 
				resolver.update(OPTION_URI, newValues, "unit = 0", null);
				break;
			case 1: 
				resolver.update(OPTION_URI, newValues, "unit = 1", null);
				break;
			case 2: 
				resolver.update(OPTION_URI, newValues, "unit = 2", null);
				break;
		}
	}
	public ArrayList<String> getTravelName( ContentResolver resolver){
		Values.clear();
		try {
            cursor = resolver.query(TRAVELNAME_URI, TRAVELNAME, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
            	Log.d("orson", "travel name");
            	do{ 	
            		Values.add(cursor.getString(0));    			
        		}while (cursor.moveToNext());  
            	
            }else{
            	Log.d("orson", "baaz null");
            	Values.add("0");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		return Values;
	}
	
	public void insertTravelName(ArrayList<String> value, ContentResolver resolver){
		newValues = new ContentValues();

		for(int i=0; i < value.size(); i++){
			Log.d("insert hiij bn", "-size-"+ value.size());
			Log.d("insert hiij bn", " i "+ i + " " + value.get(i));
			newValues.put(TravelNameColumns.TravelName, value.get(i));
	        resolver.insert(TRAVELNAME_URI, newValues);
	        newValues.clear();
	        
		}
		
	}

	public void deleteTravelName(ContentResolver resolver){
		resolver.delete(TRAVELNAME_URI, "tname", null);	
	}
	
	public ArrayList<String> getLogin(int i, ContentResolver resolver){
		try {
			Values.clear();
            cursor = resolver.query(LOGIN_URI, LOGIN, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
            	Log.d("orson", "db change manager");
            	if(i == 1 || i == 4){
            		Log.d("hi", "hi1");
            		Values.add(cursor.getString(0));
            		Values.add(cursor.getString(1));
            	}
            	if(i == 2 || i == 4){
            		Values.add(cursor.getString(2));
            		Values.add(cursor.getString(3));
            		Log.d("facebook", ""+Values);
            	}
            	if(i == 3 || i == 4){
            		 //while (cursor.moveToNext())
                     //{       		 
        			Values.add(cursor.getString(4));
             		Values.add(cursor.getString(5));
             		Log.d("twitter", ""+Values);
                      //}
            	}
            }else{
            	Log.d("hi", "hi2");
            	createtbls(resolver);
            	Values.add("0");
            	Values.add("0");
            	Values.add("0");
            	Values.add("0");
            	Values.add("0");
            	Values.add("0");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Log.d("LoginValues", ""+Values);
		return Values;
	}
	
	/*public void insertLogin(int i, String uname, String pwd, ContentResolver resolver){
		newValues = new ContentValues();

        switch(i){
        	case 1:
        		newValues.put(LoginColumns.LoginName, uname);
		        newValues.put(LoginColumns.LPassword, pwd);       					
		        break;
			case 2:
				newValues.put(LoginColumns.FName, uname);
		        newValues.put(LoginColumns.FPassword, pwd);
				 break;
			case 3:
				newValues.put(LoginColumns.TName, uname);
		        newValues.put(LoginColumns.TPassword, pwd);
				 break;
        }
        resolver.insert(LOGIN_URI, newValues); 
	}*/
	
	public void updtLogin(int i, String uname, String pwd, String equalName, ContentResolver resolver){
		newValues = new ContentValues();
		Log.d("updateLogin", "update");
		switch(i){
			case 1: 
				newValues.put(LoginColumns.LoginName, uname);
				newValues.put(LoginColumns.LPassword, pwd);
				break;
			case 2:
				newValues.put(LoginColumns.FName, uname);
				newValues.put(LoginColumns.FPassword, pwd);
				break;
			case 3: 
				newValues.put(LoginColumns.TName, uname);
				newValues.put(LoginColumns.TPassword, pwd);		
				break;
		}
		resolver.update(LOGIN_URI, newValues, "loginname ='"+ equalName +"'", null);
	}
	
	public ArrayList<String> getCurrentTravel(int i, ContentResolver resolver){
		try {
			Values.clear();
			cursor = resolver.query(CURRENTTRAVEL_URI, CURRENTTRAVEL, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
            	if(i == 0 || i == 3){
            		Values.add(cursor.getString(0));
            	}
            	if(i == 1 || i == 3){
            		Values.add(cursor.getString(1));
            	}
            	if(i == 2 || i == 3){
            		Values.add(cursor.getString(2));
            		Values.add(cursor.getString(3));
            		Log.d("ctravel", ""+Values);
            	}
            }else{
            	Log.d("baaz", "null baina");
            	Values.add("0");
            	Values.add("0");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
		return Values;
	}
	
	public void updtCurrentTravel(int i, String travel_name, ContentResolver resolver){
		newValues = new ContentValues();
		Log.d("updateLogin", "update");
		switch(i){
			case 1: 
				newValues.put(CurrentTravelColumns.StartPoint, "true");
				break;
			case 2:
				//newValues.put(CurrentTravelColumns.TotalDistance, tDistance);
				break;
		}
		resolver.update(CURRENTTRAVEL_URI, newValues, "tname ='"+ travel_name +"'", null);
	}
	
	public ArrayList<String> getArchiveFindByPostId(long post_id, ContentResolver resolver){
		try {
			Values.clear();
			Log.d("zia", "iishee orj bn u");
			cursor = resolver.query(TRAVELARCHIVE_URI, TRAVELARCHIVE, "post_id like "+"'"+ post_id+"'",  null, null);
			Log.d("zia", "iishee orj bn u2");
			if (cursor != null && cursor.moveToFirst()) {   	
    			Values.add(cursor.getString(0)); //travel name 0
    			Values.add(cursor.getString(2)); //latitude    1
    			Values.add(cursor.getString(3)); //longitude   2
    			Values.add(cursor.getString(4)); //post id     3
    			Values.add(cursor.getString(5)); //post        4
    			Values.add(cursor.getString(6)); //date        5
            }else{
            	Log.d("baaz", "null baina");
            	Values.add("0");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
		return Values;
	}
	
	public ArrayList<String> getArchive(int i, String travelname, ContentResolver resolver){
		try {
			Log.d("getarchive", ""+travelname);
			Values.clear();
			if(i == 2){
				cursor = resolver.query(TRAVELARCHIVE_URI, TRAVELARCHIVE, "tname like "+"'"+ travelname+"' AND post_id != " + 0, null, "number ASC");
			}else{
				cursor = resolver.query(TRAVELARCHIVE_URI, TRAVELARCHIVE, "tname like "+"'"+ travelname+"'", null, null);
			}
			if (cursor != null && cursor.moveToFirst()) {   
					if(i == 3){
						Log.d("last point", "1");
						cursor.moveToLast();
						Log.d("last point", "2");
						Values.add(cursor.getString(2));
            			Values.add(cursor.getString(3));
            			Log.d("last point", "3");
					}else{
	            		do{ 	
	            			if(i == 1){
		            			Values.add(cursor.getString(2));
		            			Values.add(cursor.getString(3));
	            			}
	            			if(i == 2){
	            				Values.add(cursor.getString(4));
		            			Values.add(cursor.getString(5));
		            			Values.add(cursor.getString(6));
	            			}     			
	            		}while (cursor.moveToNext()); 
					}
            }else{
            	Log.d("baaz", "null baina");
            	Values.add("0");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
		return Values;
	}
	
	public void deleteArchive(int i, ContentResolver resolver){
		newValues = new ContentValues();
		Log.d("delArchive", "delete");
		switch(i){
			case 1: 
				resolver.delete(CURRENTTRAVEL_URI, "tname", null);
				break;
			case 2:
				resolver.delete(TRAVELARCHIVE_URI, "tname", null);
				resolver.delete(CURRENTTRAVEL_URI, "tname", null);
				break;
		}
		Log.d("delArchive", "ok");
	}
	
	public void insertArchive(int i, ArrayList<String> value, ContentResolver resolver){
		newValues = new ContentValues();
		if(i == 1){		
			newValues.put(CurrentTravelColumns.TravelName, value.get(0));
			newValues.put(CurrentTravelColumns.StartPoint, value.get(1));
	        newValues.put(CurrentTravelColumns.StartTime, value.get(2));
	        resolver.insert(CURRENTTRAVEL_URI, newValues);
		}else{
			for(int j=0; j < value.size(); j+=7){
				Log.d("insert hiij bn", "-size-"+ value.size());
				newValues.put(TravelArchiveColumns.TravelName, value.get(j));
		        newValues.put(TravelArchiveColumns.Number, Integer.parseInt(value.get(j+1)));
		        newValues.put(TravelArchiveColumns.latitude, value.get(j+2));
		        newValues.put(TravelArchiveColumns.longitude, value.get(j+3));
		        newValues.put(TravelArchiveColumns.Post_id, value.get(j+4));
		        newValues.put(TravelArchiveColumns.Post, value.get(j+5));
		        newValues.put(TravelArchiveColumns.DateTime, value.get(j+6));
		        Log.d("ALDAAAAA GARAAD", "BN U GUI YU");
		        resolver.insert(TRAVELARCHIVE_URI, newValues);
		        Log.d("insert hiij bgaa count", ""+j);
		        newValues.clear();
			}
		}
		
	}
	
	public void createtbls(ContentResolver resolver){
        cursor = null;
        newValues = new ContentValues();
        newValues.put(LoginColumns.LoginName, "0");
        newValues.put(LoginColumns.LPassword, "0");
        newValues.put(LoginColumns.FName, "0");
        newValues.put(LoginColumns.FPassword, "0");
        newValues.put(LoginColumns.TName, "0");
        newValues.put(LoginColumns.TPassword, "0");
        resolver.insert(LOGIN_URI, newValues);
        newValues.clear();
        newValues.put(OptionColumns.Distance, 0);
        newValues.put(OptionColumns.Unit, 0);
        resolver.insert(OPTION_URI, newValues);
	}
	
}

