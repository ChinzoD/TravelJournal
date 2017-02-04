package grand.TravelJournal.db;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Provider that holds widget configuration details, and any cached forecast
 * data. Provides easy {@link ContentResolver} access to the data when building
 * widget updates or showing detailed lists.
 */
public class JournalDB extends ContentProvider {
   
	private static final String TAG = "JournalDB";
    private static final boolean LOGD = true;

    public static final String AUTHORITY = "grand.TravelJournal";
    
    public interface OptionColumns {
    	public static final String Distance = "distance"; 
    	public static final String Unit = "unit"; 
    }
    
    public interface LoginColumns {

    	public static final String LoginName = "loginname";
        public static final String LPassword = "lpassword";
        public static final String FName = "fname";
        public static final String FPassword = "fpassword";
        public static final String TName = "tname";     
        public static final String TPassword = "tpassword";
    }
    
    
    public interface TravelNameColumns {

    	public static final String TravelName = "tname";
    }
    
    public interface CurrentTravelColumns {

    	public static final String TravelName = "tname";
    	public static final String StartPoint = "spoint";
    	public static final String TotalDistance = "totaldis";
    	public static final String StartTime = "stime";
    	
    }
    
    public interface TravelArchiveColumns {

    	public static final String TravelName = "tname";
    	public static final String Number = "number";
    	public static final String latitude = "lat";
    	public static final String longitude = "lon"; 	
    	public static final String Post_id = "post_id";
    	public static final String Post = "post";   	
    	public static final String DateTime = "date";
    	
    }
    
    public static class Options implements BaseColumns, OptionColumns{
    	 public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/options");
         public static final String CONTENT_TYPE = "vnd.android.cursor.dir/options";
         public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/options";
    }
    
    public static class Logins implements BaseColumns, LoginColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/logins");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/logins";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/logins";       
    }
    
    public static class TravelNames implements BaseColumns, TravelNameColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/travelnames");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/travelnames";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/travelnames";
        
    }
    
    public static class CurrentTravels implements BaseColumns, CurrentTravelColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/currenttravels");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/currenttravels";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/currenttravels";        
    }
    
    public static class TravelArchives implements BaseColumns, TravelArchiveColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/travelarchives");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/travelarchives";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/travelarchives";        
    }
    
    private static final String TBL_Options = "options";
    private static final String TBL_TravelName = "travelnames";
    private static final String TBL_Login = "logins";
    private static final String TBL_CurrentTravel = "currenttravels";
    private static final String TBL_TravelArchive = "travelarchives";

    private DatabaseHelper mOpenHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "forecasts.db";

        private static final int VER_ORIGINAL = 2;
        private static final int VER_ADD_METAR = 3;

        private static final int DATABASE_VERSION = VER_ADD_METAR;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            
			db.execSQL("CREATE TABLE " + TBL_Options + " ("
			        + OptionColumns.Distance + " INTEGER,"
			        + OptionColumns.Unit + " INTEGER);");
        	
            db.execSQL("CREATE TABLE " + TBL_Login + " ("
                    + LoginColumns.LoginName + " TEXT,"
                    + LoginColumns.LPassword + " TEXT,"
                    + LoginColumns.FName + " TEXT,"
                    + LoginColumns.FPassword + " TEXT,"
                    + LoginColumns.TName + " TEXT,"
                    + LoginColumns.TPassword + " TEXT);");
           
           
            db.execSQL("CREATE TABLE " + TBL_TravelName + " ("
        	        + TravelNameColumns.TravelName+ " TEXT);");
           
           
            db.execSQL("CREATE TABLE " + TBL_CurrentTravel + " ("
                    + CurrentTravelColumns.TravelName + " TEXT,"
                    + CurrentTravelColumns.StartPoint + " INTEGER,"
                    + CurrentTravelColumns.StartTime + " NUMERIC,"
                    + CurrentTravelColumns.TotalDistance + " REAL);");
           
            db.execSQL("CREATE TABLE " + TBL_TravelArchive + " ("
                    + TravelArchiveColumns.TravelName + " TEXT,"
                    + TravelArchiveColumns.Number + " INTEGER,"
                    + TravelArchiveColumns.latitude + " TEXT,"
                    + TravelArchiveColumns.longitude + " TEXT,"
                    + TravelArchiveColumns.Post_id + " INTEGER,"
                    + TravelArchiveColumns.Post + " REAL,"                 
                    + TravelArchiveColumns.DateTime + " NUMERIC);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            int version = oldVersion;
            
            switch (version) {
                case VER_ORIGINAL:
                    db.execSQL("ALTER TABLE " + TBL_Login + " ADD COLUMN "
                            + LoginColumns.LoginName + " TEXT");
                    db.execSQL("ALTER TABLE " + TBL_Login + " ADD COLUMN "
                           + " TEXT");
                    version = VER_ADD_METAR;
            }
            
            if (version != DATABASE_VERSION) {
                Log.w(TAG, "Destroying old data during upgrade.");
                db.execSQL("DROP TABLE IF EXISTS " + TBL_Login);
                onCreate(db);
            }
        }
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
        switch (sUriMatcher.match(uri)) {
            case Login: {
            	count = db.delete(TBL_Login, "1", null);
                return count;
            	}
	        case TravelName: {
	            return db.delete(TBL_TravelName, "1", null);
	        	}
	        case CurrentTravel: {
	            return db.delete(TBL_CurrentTravel, "1", null);
	        	}
	        case TravelArchive: {
	            return db.delete(TBL_TravelArchive, "1", null);
	        	}
        }
        throw new UnsupportedOperationException();
	}
	
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        	case Option:
        		return Options.CONTENT_TYPE;
            case Login:
                return Logins.CONTENT_TYPE;
            case TravelName:
                return TravelNames.CONTENT_TYPE;
            case CurrentTravel:
                return CurrentTravels.CONTENT_TYPE;
            case TravelArchive:
                return TravelArchives.CONTENT_TYPE;
        }
        throw new IllegalStateException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
    	Log.d("Insert","orson");
        if (LOGD) Log.d(TAG, "insert() with uri=" + uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri resultUri = null;

        switch (sUriMatcher.match(uri)) {
		    case Option:{
		        long rowId = db.insert(TBL_Options, OptionColumns.Distance, values);
		        if (rowId != -1) {
		            resultUri = ContentUris.withAppendedId(Options.CONTENT_URI, rowId);
		        }
		        break;
		    }
            case Login: {
                long rowId = db.insert(TBL_Login, LoginColumns.LoginName, values);
                if (rowId != -1) {
                    resultUri = ContentUris.withAppendedId(Logins.CONTENT_URI, rowId);
                }
                break;           
            }
            case TravelName:{
                long rowId = db.insert(TBL_TravelName,TravelNameColumns.TravelName, values);
                if (rowId != -1) {
                    resultUri = ContentUris.withAppendedId(TravelNames.CONTENT_URI, rowId);
                }
                break;  
            }
            case CurrentTravel:{
                long rowId = db.insert(TBL_CurrentTravel, CurrentTravelColumns.TravelName, values);
                if (rowId != -1) {
                    resultUri = ContentUris.withAppendedId(CurrentTravels.CONTENT_URI, rowId);
                }
                break;  
            }
            case TravelArchive:{
                long rowId = db.insert(TBL_TravelArchive, TravelArchiveColumns.TravelName, values);
                if (rowId != -1) {
                    resultUri = ContentUris.withAppendedId(TravelArchives.CONTENT_URI, rowId);
                }
                break;  
            }
            default:
                throw new UnsupportedOperationException();
        }
        return resultUri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        if (LOGD) Log.d(TAG, "query() with uri=" + uri);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String limit = null;

        switch (sUriMatcher.match(uri)) {
	        case Option: {
	            qb.setTables(TBL_Options);
	            break;
	        }
            case Login: {
                qb.setTables(TBL_Login);
                break;
            	}
            case TravelName: {
                qb.setTables(TBL_TravelName);
                break;
            }
            case CurrentTravel: {
                qb.setTables(TBL_CurrentTravel);
                break;
            }
            case TravelArchive: {
                qb.setTables(TBL_TravelArchive);
                break;
            }

        }

        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (LOGD) Log.d(TAG, "update() with uri=" + uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
	        case Option: {
	            return db.update(TBL_Options, values, selection, selectionArgs);
	        	}
	        case Login: {
                return db.update(TBL_Login, values, selection, selectionArgs);
            	}
	        case TravelName: {
	            return db.update(TBL_TravelName, values, selection, selectionArgs);
	        	}
	        case CurrentTravel: {
	            return db.update(TBL_CurrentTravel, values, selection, selectionArgs);
	        	}
	        case TravelArchive: {
	            return db.update(TBL_TravelArchive, values, selection, selectionArgs);
	        	}
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Matcher used to filter an incoming {@link Uri}. 
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int Login = 1;
    private static final int TravelName = 2;
    private static final int CurrentTravel = 3;
    private static final int TravelArchive = 4;
    private static final int Option = 5;
    
    private static final int APPWIDGETS_ID = 102;
    private static final int APPWIDGETS_FORECASTS = 103;
    private static final int APPWIDGETS_FORECAST_AT = 104;

    static {
    	sUriMatcher.addURI(AUTHORITY, "options", Option);
    	sUriMatcher.addURI(AUTHORITY, "logins", Login);
        sUriMatcher.addURI(AUTHORITY, "travelnames", TravelName);
        sUriMatcher.addURI(AUTHORITY, "currenttravels", CurrentTravel);
        sUriMatcher.addURI(AUTHORITY, "travelarchives", TravelArchive);
        //sUriMatcher.addURI(AUTHORITY, "options/#", APPWIDGETS_ID);
        //sUriMatcher.addURI(AUTHORITY, "options/#/forecasts", APPWIDGETS_FORECASTS);
        //sUriMatcher.addURI(AUTHORITY, "options/#/forecast_at/*", APPWIDGETS_FORECAST_AT);
    }


}
