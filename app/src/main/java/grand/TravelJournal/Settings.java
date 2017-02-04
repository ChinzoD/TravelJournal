package grand.TravelJournal;


import grand.TravelJournal.db.DB_change_manager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Settings extends Activity{
	
	private Spinner mUnit, StepDistance;
	private int unitindex;
	private Button btnback;
	private DB_change_manager db_manager;
	private ContentResolver resolver;
	private int distance, unit;
	private String value, uname, upwd;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
        
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
        	uname = extras.getString("name");
        	upwd = extras.getString("pwd");
        }
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        
        value = db_manager.getOptions(resolver);
        
        unit = Integer.parseInt(value.substring(0, 1));
        distance = Integer.parseInt(value.substring(1, value.length()));
        
        Log.d("test1", ""+unit+"-"+distance+value);
        
        mUnit = (Spinner) findViewById(R.id.SpinnerMUnitInSettings);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
                this, R.array.units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        mUnit.setAdapter(adapter);
        mUnit.setSelection(unit);
        
        StepDistance = (Spinner) findViewById(R.id.SpinnerStepDistanceInSettings);
        ArrayAdapter<?> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.distance_value, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        StepDistance.setAdapter(adapter2);
        StepDistance.setSelection(distance);
        
        btnback = (Button) findViewById(R.id.ButtonBackInSettings);
        btnback.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("test", ""+unit+"-"+mUnit.getSelectedItemId()+"-"+mUnit.getSelectedItemPosition());
				if(unit != mUnit.getSelectedItemId()){
					db_manager.updtUnit(unit, mUnit.getSelectedItemId(), resolver);
				}
				if(distance != StepDistance.getSelectedItemId()){
					db_manager.updtDistance(distance, StepDistance.getSelectedItemId(), resolver);
				}
				Intent MainMenuIntent = new Intent(Settings.this, MainMenu.class);
    	 		MainMenuIntent.putExtra("name", uname);
    	 		MainMenuIntent.putExtra("pwd", upwd);
    	 		startActivity(MainMenuIntent);
    	 		finish();
			}
		});

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
