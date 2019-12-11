package com.example.vivu;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivu.Remote.IGoogleApi;
import com.example.vivu.model.Distance;
import com.example.vivu.model.Duration;
import com.example.vivu.model.Route;
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

    private static final String TAG = "test";
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
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
    private List<LatLng> polylineList;
    private Polyline bluePoly, cyanPoly;
    private LatLng myLocation;
    private ProgressDialog progressDialog;
    IGoogleApi mService;
    LatLng addr;
    private String Tag= "Marker";
    DBManager dbManager = new DBManager(this);
    ArrayList<com.example.vivu.model.Marker> allMarker;
    TextView txtTen, txtDiaChi;
    Button btnTim, btnHuy;
    private Dialog dialog;

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

                mMap.clear();
                addDefaultMarkers();
                destination=edtPlace.getText().toString();
                if (destination.isEmpty()) {
                    Toast.makeText(MapsActivity.this, "Vui lòng nhập địa chỉ đến!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    progressDialog = ProgressDialog.show(MapsActivity.this,
                            "Đang tìm đường đi..!", "", true);
                    destination=destination.replace(" ","+");
                    direction(destination);
                }

            }
        });
        mService=Common.getGoogleApi();
        Spinner spinner_maps_type = (Spinner) findViewById(R.id.spinner_map_type);
        String arrMap[] = getResources().getStringArray(R.array.maps_type);
        ArrayAdapter<String> adapterMap = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, arrMap);
        adapterMap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_maps_type.setAdapter(adapterMap);
        spinner_maps_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int type = GoogleMap.MAP_TYPE_NORMAL;
                switch (arg2) {
                    case 0:
                        type = GoogleMap.MAP_TYPE_NORMAL;
                        break;
                    case 1:
                        type = GoogleMap.MAP_TYPE_SATELLITE;
                        break;
                }
                mMap.setMapType(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
       MyLocation();

        //--------ADD MARKERS TO MAPS---------
        addDefaultMarkers();

//        addr= new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()) ;

        //---------SET MY LOCATION----------
        if (mMap != null) {
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {
                    //mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    addr = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                }
            });
        LatLng ll= new LatLng(10.762976, 106.682150);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target)
                .zoom(17)
                .bearing(30)
                .tilt(45)
                .build()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll,16));
        }

//


//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng point) {
//                marker.setPosition(point);
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
//            }
//        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                double lat,lng;
                lat = marker.getPosition().latitude;
                lng = marker.getPosition().longitude;
                String name=marker.getTitle();
                String info=marker.getSnippet();
                destination= lat +","+lng;

                dialog = new Dialog(MapsActivity.this);
                dialog.setTitle(name);
                dialog.setContentView(R.layout.layout_info);
                dialog.show();

                txtTen=(TextView)dialog.findViewById(R.id.ten);
                txtTen.setText(name);
                txtDiaChi=(TextView)dialog.findViewById(R.id.diaChi);
                txtDiaChi.setText(info);

                btnTim=(Button)dialog.findViewById(R.id.btnTim);
                btnTim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.clear();
                        addDefaultMarkers();
                        direction(destination);
                        dialog.dismiss();

                    }
                });

                btnHuy=(Button)dialog.findViewById(R.id.btnHuy);
                btnHuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mMap.clear();
                        addDefaultMarkers();
                        dialog.dismiss();
                    }
                });

                return false;
            }



        });

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
                    .target(mMap.getCameraPosition().target)
                    .zoom(16)
                    .bearing(30)
                    .tilt(45)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            this.addr = latLng;
        }

    }

    //------------DIRECTION TO THE DESTINATION-----------
    private void direction(String destination) {
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

                            try {
                                progressDialog.dismiss();

                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                Route jroute = null;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    //Them phan duration va distance
                                    jroute = new Route();
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    JSONArray jsonLegs = route.getJSONArray("legs");
                                    JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                                    JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                                    JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                                    jroute.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
                                    jroute.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
                                    String polyline = poly.getString("points");
                                    polylineList = decodePoly(polyline);
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

                                mMap.addMarker(new MarkerOptions().position(polylineList.get(polylineList.size() - 1))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mk2)));


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
                                ((TextView) findViewById(R.id.tvDuration)).setText(jroute.duration.text);
                                ((TextView) findViewById(R.id.tvDistance)).setText(jroute.distance.text);
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

    //--------ADDING MARKERS TO MAPS FUNCTION---------

    public void addDefaultMarkers() {
        allMarker = (ArrayList<com.example.vivu.model.Marker>) dbManager.getAllMarker();
        for (com.example.vivu.model.Marker marker : allMarker) {
            LatLng latLng = new LatLng(marker.getmLat(), marker.getmLng());
            mMap.addMarker(new MarkerOptions().position(latLng).title(marker.getmInfo())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mk)));
            //Log.d("create marker", "successfully");
        }
    }


}