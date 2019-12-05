package com.example.vivu;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vivu.Remote.IGoogleApi;
import com.example.vivu.repository.DBManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private List<LatLng> polylineList;
    private Marker marker;
    private float v;
    private double lat,lng;
    private Handler handler;
    private LatLng startPos, endPos;
    private int index, next;
    private Button btnSearch;
    private EditText edtPlace;
    private String destination;
    private PolylineOptions polylineOptions, bluePolylineOptions;
    private Polyline bluePoly, cyanPoly;
    private LatLng myLocation;
    private ProgressDialog progressDialog;
    IGoogleApi mService;
    LatLng addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        progressDialog = ProgressDialog.show(this,
//                "Đang tải map...!","", true);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
        polylineList =new ArrayList<>();
        btnSearch=(Button)findViewById(R.id.btnSearch);
        edtPlace=(EditText)findViewById(R.id.edtPlace);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progressDialog = ProgressDialog.show(MapsActivity.this,
//                        "Đang tìm đường đi..!","", true);
                destination=edtPlace.getText().toString();
                destination=destination.replace(" ","+");
                mapFragment.getMapAsync(MapsActivity.this);
            }
        });
        mService=Common.getGoogleApi();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //progressDialog.dismiss();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        addr = new LatLng(10.762934, 106.682338);
        mMap.addMarker(new MarkerOptions().position(addr).title("KHTN"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addr,16));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target)
                .zoom(17)
                .bearing(30)
                .tilt(45)
                .build()));


        //--------CREATE DEFAULT MARKER--------
//        com.example.vivu.model.Marker marker1= new com.example.vivu.model.Marker(10.762984, 106.686797,"quan1",1);
//        com.example.vivu.model.Marker marker2= new com.example.vivu.model.Marker(10.763154, 106.677991,"quan5",0);
//        DBManager dbManager = new DBManager(this);
//        dbManager.addMarker(marker1);
//        dbManager.addMarker(marker2);
//        com.example.vivu.model.Marker marker = new com.example.vivu.model.Marker(10.767222, 106.684348,"quan1",0);
//        dbManager.addMarker(marker);

        //--------ADD MARKERS TO MAPS---------
        DBManager dbManager = new DBManager(this);
        ArrayList<com.example.vivu.model.Marker> allMarker = (ArrayList<com.example.vivu.model.Marker>) dbManager.getAllMarker();
        for (com.example.vivu.model.Marker marker:allMarker) {
            LatLng latLng = new LatLng(marker.getmLat(),marker.getmLng());
            mMap.addMarker(new MarkerOptions().position(latLng).title(marker.getmInfo()));
            Log.d("create marker","successfully");
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        MyLocation();
        String requestUrl=null;
        try{
            requestUrl="https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+addr.latitude+","+addr.longitude+"&"+
                    "destination="+destination+"&"+
                    "key="+getResources().getString(R.string.google_direction_key);
            Log.d("URL",requestUrl);
            mService.getDataFromGoogleApi(requestUrl)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
 //                           progressDialog.dismiss();
                            try {

                                JSONObject jsonObject=new JSONObject(response.body().toString());
                                JSONArray jsonArray=jsonObject.getJSONArray("routes");
                                for(int i=0; i<jsonArray.length();i++)
                                {
                                    JSONObject route=jsonArray.getJSONObject(i);
                                    JSONObject poly= route.getJSONObject("overview_polyline");
                                    String polyline=poly.getString("points");
                                    polylineList=decodePoly(polyline);

                                }

                                //Điều chỉnh đường vẽ
                                LatLngBounds.Builder builder=new LatLngBounds.Builder();
                                for (LatLng latLng:polylineList)
                                    builder.include(latLng);
                                LatLngBounds bounds=builder.build();
                                CameraUpdate mCameraUpdate=CameraUpdateFactory.newLatLngBounds(bounds,2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions=new PolylineOptions();
                                polylineOptions.color(Color.CYAN);
                                polylineOptions.width(10);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polylineList);
                                cyanPoly=mMap.addPolyline(polylineOptions);

                                bluePolylineOptions=new PolylineOptions();
                                bluePolylineOptions.color(Color.BLUE);
                                bluePolylineOptions.width(10);
                                bluePolylineOptions.startCap(new SquareCap());
                                bluePolylineOptions.endCap(new SquareCap());
                                bluePolylineOptions.jointType(JointType.ROUND);
                                bluePolylineOptions.addAll(polylineList);
                                bluePoly=mMap.addPolyline(bluePolylineOptions);

                                mMap.addMarker(new MarkerOptions().position(polylineList.get(polylineList.size()-1)));

                                //Animator
                                ValueAnimator polylineAnimator= ValueAnimator.ofInt(0,100);
                                polylineAnimator.setDuration(2000);
                                polylineAnimator.setInterpolator(new LinearInterpolator());
                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points= cyanPoly.getPoints();
                                        int percentValue=(int)valueAnimator.getAnimatedValue();
                                        int size=points.size();
                                        int newPoints=(int) (size*(percentValue/100.0f));
                                        List<LatLng> p=points.subList(0,newPoints);
                                        bluePoly.setPoints(p);
                                    }
                                });
                                polylineAnimator.start();
//                                marker=mMap.addMarker(new MarkerOptions().position(addr)
//                                        .flat(true)
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconxe)));

                                //Xe chuyển động
//                                handler=new Handler();
//                                index=-1;
//                                next=1;
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if(index<polylineList.size()-1)
//                                        {
//                                            index++;
//                                            next=index+1;
//                                        }
//                                        if (index<polylineList.size()-1){
//                                            startPos=polylineList.get(index);
//                                            endPos=polylineList.get(next);
//                                        }
//                                        ValueAnimator valueAnimator= ValueAnimator.ofFloat(0,1);
//                                        valueAnimator.setDuration(3000);
//                                        valueAnimator.setInterpolator(new LinearInterpolator());
//                                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                            @Override
//                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                               v= valueAnimator.getAnimatedFraction();
//                                               lng=v*endPos.longitude+(1-v)*startPos.longitude;
//                                               lat=v*endPos.latitude+(1-v)*startPos.latitude;
//                                               LatLng newPos= new LatLng(lat,lng);
//                                               marker.setPosition(newPos);
//                                               marker.setAnchor(0.5f,0.5f);
//                                               marker.setRotation(getBearing(startPos,newPos));
//                                               mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
//                                               .target(newPos)
//                                               .zoom(15.5f)
//                                               .build()));
//                                            }
//                                        });
//                                        valueAnimator.start();
//                                    }
//                                },3000);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(MapsActivity.this,""+t.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    private float getBearing(LatLng startPos, LatLng newPos) {
//        double lat=Math.abs(startPos.latitude-newPos.latitude);
//        double lng=Math.abs(startPos.longitude-newPos.longitude);
//
//        if(startPos.latitude < newPos.latitude && startPos.longitude < newPos.longitude)
//            return (float) (Math.toDegrees(Math.atan(lng/lat)));
//        else if (startPos.latitude >= newPos.latitude && startPos.longitude < newPos.longitude)
//            return (float) ((90-Math.toDegrees(Math.atan(lng/lat)))+90);
//        else if (startPos.latitude >= newPos.latitude && startPos.longitude >= newPos.longitude)
//            return (float) ((Math.toDegrees(Math.atan(lng/lat)))+180);
//        else if (startPos.latitude < newPos.latitude && startPos.longitude >= newPos.longitude)
//            return (float) ((90-Math.toDegrees(Math.atan(lng/lat)))+270);
//        return -1;
//    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void MyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location lastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (lastLocation != null)
        {
            LatLng latLng=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 16));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

}
