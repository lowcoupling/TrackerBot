package com.lowcoupling.trackerbot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lowcoupling.trackerbot.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public  class LocationsFragment extends Fragment implements android.widget.AdapterView.OnItemClickListener{



	private View rootView;
	private ArrayList<com.lowcoupling.trackerbot.Location> list;
	LocationAdapter adapter;
	Button deleteButton;
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LocationAdapter.deleteMode =false;
	}
	public void updateList(){
		list.clear();
		DataSource lds = new DataSource(rootView.getContext());
		lds.open();
		List<Location> locations = lds.getAllLocations();
		if(locations!=null){
			Iterator<Location> locIt = locations.iterator();
			while (locIt.hasNext()){
				Location lc = locIt.next();
				list.add(lc);
			}

		}
		LocationAdapter.deleteMode =false;
	}
	    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_locations_mgmt,
				container, false);
		ListView listview = (ListView) rootView.findViewById(R.id.listview);

		list = new ArrayList<com.lowcoupling.trackerbot.Location>();
		Context context = this.getActivity();
		if(context!=null){
			updateList();
		}
		adapter = new LocationAdapter(rootView.getContext(),android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		deleteButton = (Button)rootView.findViewById(R.id.button2);
		deleteButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				LocationAdapter.deleteMode = !LocationAdapter.deleteMode;
				if (LocationAdapter.deleteMode){
					LocationsFragment.this.deleteButton.setText("Cancel");
				}else{
					LocationsFragment.this.deleteButton.setText("Delete");
				}
				LocationsFragment.this.adapter.notifyDataSetChanged();
				//adapter.notifyAll();
			}
		});
	
		Button button = (Button)rootView.findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// get prompts.xml view
				LayoutInflater li = LayoutInflater.from(rootView.getContext());
				View promptsView = li.inflate(R.layout.prompts, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						rootView.getContext());

				alertDialogBuilder.setView(promptsView);

				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);

				alertDialogBuilder.setCancelable(false)
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// get user input and set it to result
						// edit text
						//result.setText(userInput.getText());
					}
				})
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {

						if(list!=null && adapter !=null){
							String name = userInput.getText().toString();
							Context context = getActivity();
							DataSource lds = new DataSource(rootView.getContext());
							lds.open();
							Location location = lds.getLocation(name);
							Intent i = new Intent(context, MapActivity.class);
							if(location==null){
								location = lds.createLocation(name, 0, 0, 0);
								lds.close();
								list.add(location);
								i.putExtra("location", name);
								i.putExtra("latitude",location.getLatitude());
								i.putExtra("longitude",location.getLongitude());
								i.putExtra("range",location.getRange());
								startActivityForResult(i,1);
								adapter.notifyDataSetChanged();
							}
							lds.close();
							
						}else{
							//System.out.println("null objects");
						}
						//System.out.println(list);
						//System.out.println(adapter);
						dialog.cancel();
					}

				});


				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();

			}
		});



		return rootView;
	}


	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1, int position,
			long id) {
		ListView listview = (ListView) rootView.findViewById(R.id.listview);
		Location o = (Location) listview.getItemAtPosition(position);
		String keyword = o.getName();
		DataSource lds = new DataSource(rootView.getContext());
		lds.open();
		Location location = lds.getLocation(keyword);
		Intent i = new Intent(adapterView.getContext(), MapActivity.class);
		lds.close();
		if(location!=null){
			i.putExtra("location", keyword);
			i.putExtra("latitude",location.getLatitude());
			i.putExtra("longitude",location.getLongitude());
			i.putExtra("range",location.getRange());
			startActivityForResult(i,1);
		}
		updateList();
		adapter.notifyDataSetChanged();
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1){
			if (data!=null){
				String name = data.getStringExtra("name");
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				int range = data.getIntExtra("range",0);
				//System.out.println("Latitude "+latitude+" Longitude "+longitude+" range"+range);
				Location loc = new Location();
				loc.setName(name);
				loc.setLatitude(latitude);
				loc.setLongitude(longitude);
				loc.setRange(range);
				DataSource lds = new DataSource(rootView.getContext());
				lds.open();
				lds.updateLocation(loc);
				lds.close();
			}
		}
		updateList();
		if(adapter!=null){
			adapter.notifyDataSetChanged();
		}
	}

}

