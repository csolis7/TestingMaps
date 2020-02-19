package com.example.testingmaps;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.NetworkLocationIgnorer;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener, MapEventsReceiver {
    MapView map = null;
    FloatingActionButton fab;
    private LocationManager locationManager;
    private CompassOverlay coy;
    protected DirectedLocationOverlay myLocationOverlay;
    protected GeoPoint startPoint, destinationPoint;
    protected ArrayList<GeoPoint> viaPoints;
    protected boolean mTrackingMode;
    boolean mIsRecordingTrack;
    FloatingActionButton mTrackingModeButton;

    float mAzimuthAngleSpeed = 0.0f;

    private RotationGestureOverlay rgo;
    private ScaleBarOverlay mboy;
    private MinimapOverlay mno;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //inflate and create the map
        setContentView(R.layout.activity_main);


        map = findViewById(R.id.mapview);
        map.setTilesScaledToDpi(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(15);
        GeoPoint center = new GeoPoint(13.710189,-89.20);
        mapController.animateTo(center);
        addMarker(center);

        coy = new CompassOverlay(ctx, new InternalCompassOrientationProvider(MainActivity.this), map);
        this.coy.enableCompass();
        map.getOverlays().add(this.coy);


        /*LatLonGridlineOverlay2 overlay = new LatLonGridlineOverlay2();
        map.getOverlays().add(overlay);*/

        rgo = new RotationGestureOverlay(MainActivity.this, map);
        rgo.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(this.rgo);

        final Context context = MainActivity.this;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mboy = new ScaleBarOverlay(map);
        mboy.setCentred(true);
//play around with these values to get the location on screen in the right place for your application
        mboy.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(this.mboy);

        ArrayList<OverlayItem> items = new ArrayList<>();
        items.add(new OverlayItem("","Creativa Consultores", new GeoPoint(13.71d,-89.25d))); // Lat/Lon decimal degrees
        items.add(new OverlayItem("","Multiplaza", new GeoPoint(13.67d,-89.24d))); // Lat/Lon decimal degrees

//the overlay
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(context, "Item", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, context);

        map.getOverlays().add(mOverlay);
        //Puntos de prueba para polyline
        final GeoPoint g1 = new GeoPoint(13.71d,-89.25d);
        GeoPoint g2 = new GeoPoint(13.67d,-89.24d);
        //Puntos de prueba para poligono
        GeoPoint g3 = new GeoPoint(13.70d,-89.71d);
        GeoPoint g4 = new GeoPoint(13.70d,-89.73d);
        GeoPoint g5 = new GeoPoint(13.72d,-89.72d);
        GeoPoint g6 = new GeoPoint(13.72d,-89.70d);

        //Polyline para Rutas
        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(g1);
        geoPoints.add(g2);
//add your points here
        Polyline line = new Polyline();   //see note below!
        line.setPoints(geoPoints);
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Toast.makeText(mapView.getContext(), "polyline with " + polyline.getPoints().size() + "pts was tapped", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        map.getOverlayManager().add(line);

       //Poligonos Para Zonas de Riesgo
        List<GeoPoint> geoPointsp = new ArrayList<>();
        geoPointsp.add(g3);
        geoPointsp.add(g4);
        geoPointsp.add(g5);
        geoPointsp.add(g6);
//add your points here
        Polygon polygon = new Polygon();    //see note below
        polygon.setFillColor(Color.argb(75, 255,0,0));
        geoPointsp.add(geoPointsp.get(0));    //forces the loop to close
        polygon.setPoints(geoPointsp);
        polygon.setTitle("A sample polygon");

//polygons supports holes too, points should be in a counter-clockwise order
        List<List<GeoPoint>> holes = new ArrayList<>();
        holes.add(geoPoints);
        polygon.setHoles(holes);

        map.getOverlayManager().add(polygon);
        myLocationOverlay = new DirectedLocationOverlay(this);



        if (savedInstanceState == null){
            Location location = null;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null)
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location != null) {
                //location known:
                //onLocationChanged(location);
            } else {
                //no location known: hide myLocationOverlay
                myLocationOverlay.setEnabled(false);
            }
            startPoint = null;
            destinationPoint = null;
            viaPoints = new ArrayList<GeoPoint>();
        } else {
            myLocationOverlay.setLocation((GeoPoint)savedInstanceState.getParcelable("location"));
            //TODO: restore other aspects of myLocationOverlay...
            startPoint = savedInstanceState.getParcelable("start");
            destinationPoint = savedInstanceState.getParcelable("destination");
            viaPoints = savedInstanceState.getParcelableArrayList("viapoints");
        }

        //Tracking system:
        mTrackingModeButton = (FloatingActionButton) findViewById(R.id.buttonTrackingMode);
        mTrackingModeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mTrackingMode = !mTrackingMode;
                updateUIWithTrackingMode();
            }
        });
        if (savedInstanceState != null){
            mTrackingMode = savedInstanceState.getBoolean("tracking_mode");
            updateUIWithTrackingMode();
        } else
            mTrackingMode = false;

        mIsRecordingTrack = false; //TODO restore state

    }


    private final NetworkLocationIgnorer mIgnorer = new NetworkLocationIgnorer();
    long mLastTime = 0; // milliseconds
    double mSpeed = 0.0; // km/h

    @Override
    public void onLocationChanged(final Location pLoc) {
        long currentTime = System.currentTimeMillis();
        if (mIgnorer.shouldIgnore(pLoc.getProvider(), currentTime))
            return;
        double dT = currentTime - mLastTime;
        if (dT < 100.0){
            //Toast.makeText(this, pLoc.getProvider()+" dT="+dT, Toast.LENGTH_SHORT).show();
            return;
        }
        mLastTime = currentTime;

        GeoPoint newLocation = new GeoPoint(pLoc);
        if (!myLocationOverlay.isEnabled()){
            //we get the location for the first time:
            myLocationOverlay.setEnabled(true);
            map.getController().animateTo(newLocation);
        }

        GeoPoint prevLocation = myLocationOverlay.getLocation();
        myLocationOverlay.setLocation(newLocation);
        myLocationOverlay.setAccuracy((int)pLoc.getAccuracy());

        if (prevLocation != null && pLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
            mSpeed = pLoc.getSpeed() * 3.6;
            long speedInt = Math.round(mSpeed);
            //TextView speedTxt = (TextView)findViewById(R.id.speed);
            //speedTxt.setText(speedInt + " km/h");

            //TODO: check if speed is not too small
            if (mSpeed >= 0.1){
               mAzimuthAngleSpeed = pLoc.getBearing();
                myLocationOverlay.setBearing(mAzimuthAngleSpeed);
            }
        }

        if (mTrackingMode){
            //keep the map view centered on current location:
            map.getController().animateTo(newLocation);
            map.setMapOrientation(-mAzimuthAngleSpeed);
        } else {
            //just redraw the location overlay:
            map.invalidate();
        }

        if (mIsRecordingTrack) {
            //recordCurrentLocationInTrack("my_track", "My Track", newLocation);
        }
    }

    public void addMarker (GeoPoint center){
        Marker marker = new Marker(map);
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(center.getLatitude(),
                    center.getLongitude(), 1);

            if (addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder(
                        "Address:\n");
                for (int i = 0; i < returnedAddress
                        .getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(
                            returnedAddress.getAddressLine(i)).append("\n");
                }
                marker.setSubDescription(returnedAddress.getAddressLine(0));
            } else {
                marker.setSubDescription
                ("No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.person));
        marker.setInfoWindow(new BasicInfoWindow(R.layout.item_detail, map));
        marker.setTitle("Mi ubicación es: ");
        //marker.setSubDescription("Estoy aca: "+ center);
        //map.getOverlays().clear();
        map.getOverlays().add(marker);
        map.invalidate();
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        map.getOverlays().add(0, mapEventsOverlay);


    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        Marker marker = new Marker(map);
        int cc =0;
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(p.getLatitude(),
                    p.getLongitude(), 1);

            if (addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder(
                        "Address:\n");
                for (int i = 0; i < returnedAddress
                        .getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(
                            returnedAddress.getAddressLine(i)).append("\n");
                }
                marker.setSubDescription(returnedAddress.getAddressLine(0));

            } else {
                Toast.makeText(this, "Vacio", Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        marker.setPosition(p);
       // marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(new BasicInfoWindow(R.layout.item_detail, map));

        marker.setTitle("Mi ubicación es: ");
        //marker.setSubDescription("Estoy aca: "+ center);
        //map.getOverlays().clear();
        map.getOverlays().add(marker);
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        map.getOverlays().add(0, mapEventsOverlay);

        String c = Integer.toString(cc);
        marker.setIcon(writeOnDrawable(R.drawable.marker_visit_in_process,c));
        map.invalidate();
        return true;
    }



    @Override public boolean longPressHelper(GeoPoint p) {
        //DO NOTHING FOR NOW:

        return false;
    }

    public void onResume(){
        super.onResume();
        //boolean isOneProviderEnabled = startLocationUpdates();
        //myLocationOverlay.setEnabled(isOneProviderEnabled);
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up

    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 20, bm.getHeight()/2, paint);

        return new BitmapDrawable(bm);
    }

    void updateUIWithTrackingMode(){
        if (mTrackingMode){
            mTrackingModeButton.setBackgroundResource(R.drawable.ml);
            map.setMapOrientation(0.0f);
            mTrackingModeButton.setKeepScreenOn(false);

        }
    }
    boolean startLocationUpdates(){
        boolean result = false;
        for (final String provider : locationManager.getProviders(true)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, 2 * 1000, 0.0f, this);
                result = true;
            }
        }
        return result;
    }
    @Override public void onProviderDisabled(String provider) {}

    @Override public void onProviderEnabled(String provider) {}

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}

}
