package grand.TravelJournal;

import grand.TravelJournal.db.DB_change_manager;
import java.util.ArrayList;
import java.util.Vector;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.google.android.maps.MapView.LayoutParams;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyTrackMap extends MapActivity{
	
	MapView mapView; 
	MapController mc;
    GeoPoint p, p1, p2;
    public static double lat, lon;
    LinearLayout zoomLayout;
    private DB_change_manager db_manager;
    private DB_change_manager db_manager2;
	private ContentResolver resolver;
	private ArrayList<String> TrackValues = new ArrayList<String>();
	private ArrayList<String> CurrentTravelValues = new ArrayList<String>();
	private String TName, uname, upwd;
	private long post_id;
	private ImageButton btnStartpoint, btnBack;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
        
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
        	TName = extras.getString("TravelName");
        	uname = extras.getString("name");
        	upwd = extras.getString("pwd");
        	post_id = extras.getLong("post_id");
        }
        Log.d("test", TName+post_id);
        
        btnBack = (ImageButton) findViewById(R.id.ImgBtnBackInMap);
        btnStartpoint = (ImageButton) findViewById(R.id.ImgBtnFlagInMap);
        //---0 - VISIBLE; 4 - INVISIBLE; 8 - GONE---
        btnStartpoint.setVisibility(8);
        
        resolver = getContentResolver(); 
        db_manager =  new DB_change_manager();
        db_manager2  =  new DB_change_manager();
        
        if(post_id != 0){
        	Log.d("lat lon1", ""+CurrentTravelValues);
        	CurrentTravelValues = db_manager.getArchiveFindByPostId(post_id, resolver);
        	Log.d("lat lon2", ""+CurrentTravelValues);
        	TrackValues = db_manager2.getArchive(1, CurrentTravelValues.get(0), resolver);
        	Log.d("lat lon3", ""+TrackValues);
    	}else{   
    		btnBack.setVisibility(0);
    		TrackValues = db_manager2.getArchive(1, TName, resolver);
    		Log.d("lat lon4", ""+CurrentTravelValues);
    	}
        
        mapView = (MapView) findViewById(R.id.mapView);
        View zoomView = mapView.getZoomControls(); 
 
        mapView.displayZoomControls(true);
        mapView.setSatellite(true);

        Log.d("lat lon5", ""+TrackValues);
        p = new GeoPoint(
                (int) ( Double.parseDouble(TrackValues.get(0)) * 1E6), 
                (int) ( Double.parseDouble(TrackValues.get(1)) * 1E6)
                );  
        p2 = p;
        
        for(int i=2; i < TrackValues.size(); i+=2){
        	p1 = p2;
        	p2 = new GeoPoint(
                    (int) ( Double.parseDouble(TrackValues.get(i)) * 1E6), 
                    (int) ( Double.parseDouble(TrackValues.get(i+1)) * 1E6)
                    );       	
        	mapView.getOverlays().add(new MyOverLay(p1,p2,2,Color.YELLOW));     	
        }   
        mapView.getOverlays().add(new MyOverLay(p,p,1));  
        mc = mapView.getController();
        if(post_id != 0){
        	 Log.d("lat lon6", ""+CurrentTravelValues);
        	 p2 = new GeoPoint(
                     (int) ( Double.parseDouble(CurrentTravelValues.get(1)) * 1E6), 
                     (int) ( Double.parseDouble(CurrentTravelValues.get(2)) * 1E6)
                     );  
        	 mapView.getOverlays().add(new MyOverLay(p2,p2,1)); 
        	
        }
        
        mc.animateTo(p2);
        mc.setZoom(17); 
        mapView.invalidate(); 
    	
    }
    
    public void MyClickInMap(View view) {
		switch (view.getId()) {
		case R.id.ImgBtnLensNegInMap:		
				mc.zoomOut();
			break;
		case R.id.ImgBtnStreetInMap:
				mapView.setSatellite(false);
				mapView.setStreetView(true);
			break;	
		case R.id.ImgBtnBackInMap:
			Intent TravelMenuIntent = new Intent(MyTrackMap.this, TravelMenu.class);
  	 		TravelMenuIntent.putExtra("TravelName", TName);
  	 		TravelMenuIntent.putExtra("name", uname);
  	 		TravelMenuIntent.putExtra("pwd", upwd);
			startActivity(TravelMenuIntent);
			finish();
			break;
		case R.id.ImgBtnSatInMap:
				mapView.setStreetView(false);
				mapView.setSatellite(true);
			break;
		case R.id.ImgBtnLensPosInMap:
				mc.zoomIn();
			break;
		}
	}
/*
    public class My1Overlay extends com.google.android.maps.Overlay {
        private Projection projection;
        private Paint linePaint;
        private Vector<GeoPoint> points;
        public My1Overlay(Drawable defaultMarker) {
            points = new Vector<GeoPoint>();
            linePaint = new Paint();
            linePaint.setDither(true);
            linePaint.setColor(Color.YELLOW);
            linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            linePaint.setStrokeJoin(Paint.Join.ROUND);
            linePaint.setStrokeCap(Paint.Cap.ROUND);
            linePaint.setStrokeWidth(3);
            linePaint.setAlpha(120);

        }
        
        public void addPoint(GeoPoint point) {
            points.addElement(point);
        }

        public void setProjection(Projection projection) {
            this.projection = projection;
        }
        
        
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        	projection = mapView.getProjection(); 
            int size = points.size();
            Point lastPoint = new Point();
            projection.toPixels(points.get(0), lastPoint);
            Point point = new Point();
            for(int i = 1; i<size; i++){
            	projection.toPixels(points.get(i), point);
                canvas.drawLine(lastPoint.x, lastPoint.y, point.x, point.y, linePaint);
                lastPoint = point;
            }
            Bitmap bmp = BitmapFactory.decodeResource(
                    getResources(), R.drawable.pin);            
                canvas.drawBitmap(bmp, lastPoint.x-10, lastPoint.y-30, null);
        }

     }
*/

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


	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}