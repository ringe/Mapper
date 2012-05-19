package no.rin.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class MapperActivity extends MapActivity {
	public static String PROXIMITY_ALERT_INTENT = "no.rin.mapper.NEXTPOINT";

    Uri uriUrl = Uri.parse("http://go-ringe.rhcloud.com/contests");
        
	private String providerName;
	private LocationManager locationManager;
	private MapView mapView;
	private MapController mapController;
	private TrackingAdapter trackingDB;
	
	private List<Overlay> mapOverlays;
	private Projection projection;
	
	int trip = 0;
	private Switch trackie;
	private long mTime = 5000;
	private float mDist = 3f;

	private SeekBar timBar;
	private SeekBar disBar;

	private Button showie;
	private List<GeoPoint> trackPoints;

	private UserAdapter userDB;

	private List<UserData> users;

	private UserData user;

	private Contest contest;

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
		
		// track movement in app?
		trackie = (Switch) findViewById(R.id.trackie);
		trackie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!trackie.isChecked()) {
					trip = 0;
				}
			}
		});
		
		//timBar = (SeekBar) findViewById(R.id.timBar);
		//timBar.setProgress((int)mTime);
		//disBar = (SeekBar) findViewById(R.id.disBar);
		//disBar.setProgress((int)mDist*10);
		
		// connect in-app tracking db
		trackingDB = new TrackingAdapter(this);
		trackingDB.open();
		
		trackPoints = trackingDB.selectLast();
		mapController.setZoom(10);
		if(!trackPoints.isEmpty())
			mapController.setCenter(trackPoints.get(0));
		
		// connect user db
		userDB = new UserAdapter(this);
		userDB.open();
		user = new UserData(userDB);
		showToast("Your name: " + user.getUsername());
		
		// ikkke points
	    final Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);  
		showie = (Button) findViewById(R.id.showTrip);
		showie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(launchBrowser);
			}
		});
		
		mapOverlays = mapView.getOverlays();
		projection = mapView.getProjection();
		mapOverlays.add(new MyOverlay(trackingDB));

		locationManager =
				(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		providerName = LocationManager.GPS_PROVIDER; //alt. NETWORK_PROVIDER
		LocationListener locationListener = new LocationListener(){
			public void onLocationChanged(Location location){

				//TextView tv = (TextView) findViewById(R.id.hello);
				//tv.setText("" + lon + " " + lat);
				location = locationManager.getLastKnownLocation(providerName);		
				Double lat = location.getLatitude();
				Double lon = location.getLongitude();

				GeoPoint point = new GeoPoint(lat.intValue(), lon.intValue());
				mapController.setCenter(point);

				// if tracking, set proximity, trip id
				if (trackie.isChecked()) {
					if (trip == 0)
						trip = trackingDB.getId();
					trackingDB.insert(trip, lat.intValue(), lon.intValue());
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

		RebusFetch r = (RebusFetch) new RebusFetch().execute();
		try {
			ArrayList<Contest> b = r.get();
			contest = b.get(b.size()-1);
			showToast(contest.getname());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PointFetch p = (PointFetch) new PointFetch(contest.getid()).execute();
		try {
			no.rin.mapper.Point point = p.get();
			showToast("Next rebus is " + point.toString());
			setProximityAlarm(this, point);
			
//			final Intent happyWow = new Intent(Intent.ACTION_VIEW, uriUrl);
//			final PendingIntent happy = PendingIntent.getBroadcast(this, 0, happyWow, 0);
//			locationManager.addProximityAlert(point.getLat(), point.getLng(), 
//					point.getRange(), -1, happy);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setProximityAlarm(Context context, no.rin.mapper.Point p){
		
		Log.i("Alarm at", "Lat: " + p.getLat() + " Lng: " + p.getLng() + " Range: " + p.getRange());
		
		IntentFilter filter = new IntentFilter(PROXIMITY_ALERT_INTENT);
	     context.registerReceiver(new PointRangeReceiver(), filter);

	     Intent mIntent = new Intent(PROXIMITY_ALERT_INTENT);
	     mIntent.putExtra("alert", "Say hi to grandma");

	     PendingIntent proxIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
				
	     locationManager.addProximityAlert(p.getLat(), p.getLng(), p.getRange(), -1, proxIntent);
	}
	
	
		@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	class MyOverlay extends Overlay {
		private TrackingAdapter dba;

		public MyOverlay(TrackingAdapter db) {
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
				trackPoints = dba.selectAll(trip);

			Path path = new Path();

			if (trackPoints.size() > 0) {
				boolean first = true;
				Point p0 = new Point();
				Point p = new Point();
				
				for(GeoPoint g: trackPoints){
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