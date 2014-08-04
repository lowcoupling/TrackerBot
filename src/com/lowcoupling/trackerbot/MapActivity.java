package com.lowcoupling.trackerbot;

import java.io.IOException;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.lowcoupling.trackerbot.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity {
	private String name;
	private double latitude;
	private double longitude;
	private int range;
	private Circle circle;
	private Marker marker;
	private GoogleMap map;
	private MyBarChangeListener barListener;
	private SeekBar seekBar;

	class MyBarChangeListener implements OnSeekBarChangeListener{
		private Circle circle;

		public Circle getCircle() {
			return circle;
		}

		public void setCircle(Circle circle) {
			this.circle = circle;
		}

		int progress = 0;
		@Override
		public void onProgressChanged(SeekBar seekBar, 
				int progresValue, boolean fromUser) {
			progress = progresValue;
			if(circle!=null){
				circle.setRadius(progress);
				range = progress;
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// Do something here, 
			//if you want to do anything at the start of
			// touching the seekbar

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// Display the value in textview
			//textView.setText(progress + "/" + seekBar.getMax());
		}


	}

	class MapClickListener implements OnMapLongClickListener{

		private GoogleMap map;
		private MyBarChangeListener listener;
		public Context getContext() {
			return context;
		}


		public void setContext(Context context) {
			this.context = context;
		}


		private Context context;

		public MyBarChangeListener getListener() {
			return listener;
		}


		public void setListener(MyBarChangeListener listener) {
			this.listener = listener;
		}


		MapClickListener(GoogleMap map, Context context){
			this.map = map;
			this.context = context;
		}


		@Override
		public void onMapLongClick(LatLng arg0) {
			CircleOptions circleOptions = new CircleOptions()
			.center(arg0)
			.radius(1000);
			latitude = arg0.latitude;
			longitude = arg0.longitude;
			if(circle!=null){
				circle.remove();
			}
			marker.setPosition(new LatLng(latitude,longitude));
			circle = map.addCircle(circleOptions);
			range = 250;
			circle.setRadius(range);
			
			seekBar.setProgress(range);
			circle.setFillColor(Color.TRANSPARENT);
			circle.setStrokeColor(0x10000000);
			listener.setCircle(circle);
		}


	}



	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("name", name);
				resultIntent.putExtra("latitude", latitude);
				resultIntent.putExtra("longitude",longitude);
				resultIntent.putExtra("range",range);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		range = 250;
		// Getting reference to btn_find of the layout activity_main
		setContentView(R.layout.activity_main);
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle("Locations");

		/**/
		Button btn_find = (Button) findViewById(R.id.btn_find);

		// Defining button click event listener for the find button
		OnClickListener findClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Getting reference to EditText to get the user input location
				EditText etLocation = (EditText) findViewById(R.id.et_location);

				// Getting user input location
				String location = etLocation.getText().toString();

				if(location!=null && !location.equals("")){
					new GeocoderTask().execute(location);
				}
			}
		};

		// Setting button click event listener for the find button
		btn_find.setOnClickListener(findClickListener);



		SupportMapFragment supportMapFragment = (SupportMapFragment)
				getSupportFragmentManager().findFragmentById(R.id.map);
		map = supportMapFragment.getMap();
		seekBar = (SeekBar) findViewById(R.id.seekBar1);

		Intent i = getIntent();
		MapClickListener lst = new MapClickListener(map,this.getBaseContext());
		barListener = new MyBarChangeListener();
		seekBar.setOnSeekBarChangeListener(barListener);
		seekBar.setMax(500);
		lst.setListener(barListener);
		map.setOnMapLongClickListener(lst);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);

		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setMyLocationEnabled(true);
		range = 250;
		super.onResume();
		Intent i = getIntent();
		name = i.getStringExtra("location");
		double lat = i.getDoubleExtra("latitude", 0);
		double lng = i.getDoubleExtra("longitude", 0);
		int range = i.getIntExtra("range", 0);
		LatLng location= null;
		this.getActionBar().setTitle(name);
		
		if(lat==0 && lng==0){
			range=250;
			Criteria criteria = new Criteria();
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			android.location.Location lcz = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

			if (lcz != null) {
				LatLng myLocation = new LatLng(lcz.getLatitude(),
						lcz.getLongitude());
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
						13));
				location = myLocation;
			}else{
				location = new LatLng(0,0);
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 5.0f));

			}
		}else{
			location = new LatLng(lat,lng);
		}
		CircleOptions circleOptions = new CircleOptions()
		.center(location)
		.radius(range);
		this.latitude = lat;
		this.longitude = lng;
		if(circle==null){
			circle = map.addCircle(circleOptions);
		}
		circle.setRadius(range);
		circle.setCenter(location);
		circle.setFillColor(Color.TRANSPARENT);
		circle.setStrokeColor(0x500099cc);
		barListener.setCircle(circle);
		seekBar.setProgress(range);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
		if(marker==null){
			marker =
					map.addMarker(new MarkerOptions()
					.title(name)
					.snippet(name)
					.position(location));			
		}else{
			marker.setPosition(location);
		}
		latitude = location.latitude;
		longitude = location.longitude;
		seekBar.setProgress(range);

	}


	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;

			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(locationName[0], 3);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses) {
			if(addresses==null || addresses.size()==0){
				Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
				return;
			}
			map.clear();
			for(int i=0;i<addresses.size();i++){
				Address address = (Address) addresses.get(i);
				LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
				map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
			}
		}
	}


}
