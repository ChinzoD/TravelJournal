package grand.TravelJournal.db;

import grand.TravelJournal.db.JournalDB.CurrentTravelColumns;
import grand.TravelJournal.db.JournalDB.LoginColumns;
import grand.TravelJournal.db.JournalDB.OptionColumns;
import grand.TravelJournal.db.JournalDB.TravelNameColumns;
import grand.TravelJournal.db.JournalDB.TravelArchiveColumns;
import android.net.Uri;

public interface URIS {
	
	final Uri OPTION_URI = Uri.parse("content://grand.TravelJournal/options");
	final Uri LOGIN_URI = Uri.parse("content://grand.TravelJournal/logins");
	final Uri TRAVELNAME_URI = Uri.parse("content://grand.TravelJournal/travelnames");
	final Uri CURRENTTRAVEL_URI = Uri.parse("content://grand.TravelJournal/currenttravels");
	final Uri TRAVELARCHIVE_URI = Uri.parse("content://grand.TravelJournal/travelarchives");
	
	final String[] OPTIONS = {
		    OptionColumns.Distance,
		    OptionColumns.Unit,
			};
	
	final String[] LOGIN = {
	    LoginColumns.LoginName,
	    LoginColumns.LPassword,
	    LoginColumns.FName,
	    LoginColumns.FPassword,
	    LoginColumns.TName,
	    LoginColumns.TPassword,
		};
	
	final String[] TRAVELNAME = {
			TravelNameColumns.TravelName,
	    };
	
	final String[] CURRENTTRAVEL = {
			CurrentTravelColumns.TravelName,
			CurrentTravelColumns.StartPoint,
			CurrentTravelColumns.StartTime,
			CurrentTravelColumns.TotalDistance,
			};
	final String[] TRAVELARCHIVE = {
			TravelArchiveColumns.TravelName,
			TravelArchiveColumns.Number,
			TravelArchiveColumns.latitude,
			TravelArchiveColumns.longitude,
			TravelArchiveColumns.Post_id,
			TravelArchiveColumns.Post,
			TravelArchiveColumns.DateTime,
			};
}
