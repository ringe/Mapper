package no.rin.mapper;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class MapperActivity extends MapActivity {
	public static String PROXIMITY_ALERT = "no.rin.mapper.LASTPOINT";

	private String providerName;
	private LocationManager locationManager;
	private MapView mapView;
	private MapController mapController;
	private SQLiteAdapter dba;
	
	private List<Overlay> mapOverlays;
	private Projection projection;
	
	int trip = 0;
	private Switch trackie;
	private long mTime = 5000;
	private float mDist = 3f;

	private SeekBar timBar;
	private SeekBar disBar;

	private Button showie;
	private List<GeoPoint> points;

	 void showToast(CharSequence msg) {
		 Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	 }
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mapView = (MapView) findViewById(R.id.mappie);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();

		trackie = (Switch) findViewById(R.id.trackie);
		trackie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!trackie.isChecked()) {
					trip = 0;
				}
			}
		});
		
		timBar = (SeekBar) findViewById(R.id.timBar);
		timBar.setProgress((int)mTime);
		disBar = (SeekBar) findViewById(R.id.disBar);
		disBar.setProgress((int)mDist*10);
		
		dba = new SQLiteAdapter(this);
		dba.open();
		
		points = dba.selectLast();
		mapController.setZoom(10);
		mapController.setCenter(points.get(0));

		showie = (Button) findViewById(R.id.showTrip);
		showie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showToast("Trip points [x,y]:\n" + points.toString());
			}
		});
		
		mapOverlays = mapView.getOverlays();
		projection = mapView.getProjection();
		mapOverlays.add(new MyOverlay(dba));

		locationManager =
				(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		providerName = LocationManager.GPS_PROVIDER; //alt. NETWORK_PROVIDER
		LocationListener locationListener = new LocationListener(){
			public void onLocationChanged(Location location){

				//TextView tv = (TextView) findViewById(R.id.hello);
				//tv.setText("" + lon + " " + lat);
				location = locationManager.getLastKnownLocation(providerName);		
				Double lat = location.getLatitude() *1E6;
				Double lon = location.getLongitude() *1E6;

				GeoPoint point = new GeoPoint(lat.intValue(), lon.intValue());
				mapController.setCenter(point);

				// if tracking, set proximity, trip id
				if (trackie.isChecked()) {
					if (trip == 0)
						trip = dba.getId();
					dba.insert(trip, lat.intValue(), lon.intValue());
					resetLocationListener();
				}
			}

			private void resetLocationListener() {
				boolean update = false;
				
				if (timBar.getProgress() != mTime) {
					update = true;
					mTime = timBar.getProgress();
				}
				
				if ((disBar.getProgress() / 10.0f) != mDist) {
					update = true;
					mDist = disBar.getProgress();
				}
				
				if (update)
					locationManager.requestLocationUpdates(providerName, mTime, mDist, this);
			}

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub

			}
		};

		locationManager.requestLocationUpdates(providerName, mTime, mDist, locationListener);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	class MyOverlay extends Overlay {
		private SQLiteAdapter dba;

		public MyOverlay(SQLiteAdapter db) {
			dba = db;
		}

		public void draw(Canvas canvas, MapView view, boolean shadow) {
			super.draw(canvas, view, shadow);

			Paint paint = new Paint();
			paint.setDither(true);
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeWidth(2);

			if (trip != 0)
				points = dba.selectAll(trip);

			Path path = new Path();

			if (points.size() > 0) {
				boolean first = true;
				Point p0 = new Point();
				Point p = new Point();
				
				for(GeoPoint g: points){
					projection.toPixels(g, p);
					if (first) {
						path.moveTo(p.x, p.y);
						first = false;
					} else{
						path.moveTo(p.x, p.y);
						path.lineTo(p0.x, p0.y);
					}
					p0.x = p.x;
					p0.y = p.y;
				}
			}
			
			canvas.drawPath(path, paint);
		}
	}
}