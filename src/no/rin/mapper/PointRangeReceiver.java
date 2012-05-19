package no.rin.mapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class PointRangeReceiver extends BroadcastReceiver {

 
  public PointRangeReceiver() {
    String key = LocationManager.KEY_PROXIMITY_ENTERING;
  }

 
  @Override
  public void onReceive(Context context, Intent intent) {
    String key = LocationManager.KEY_PROXIMITY_ENTERING;
    Boolean entering = intent.getBooleanExtra(key, false);
    if (entering) {
      Log.i("Entering", "Entering!");
    }
  }
}
