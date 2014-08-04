package com.lowcoupling.trackerbot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import com.lowcoupling.trackerbot.R;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public  class EventsFragment extends Fragment implements OnItemClickListener{
	private  ArrayList<String> list;
	private EventAdapter adapter;
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		list.clear();
		Context context = this.getActivity();
		if(context!=null){
			DataSource lds = new DataSource(context);
			lds.open();
			List<Event> events = lds.getAllEvents();
			Collections.reverse(events);
			int month = Calendar.getInstance().get(Calendar.MONTH); 
			int year = Calendar.getInstance().get(Calendar.YEAR);
			if(events!=null){
				Iterator<Event> evit = events.iterator();
				while (evit.hasNext()){
					Event evt = evit.next();
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(evt.getTime()*1000);
					int evMonth = c.get(Calendar.MONTH);
					int evYear = c.get(Calendar.YEAR);
					//the event happened the last year
					if(evYear<year){
						evMonth = evMonth+12;	
					}else{
						//the event happened this year
						if((month-evMonth)>2){
							//System.out.println("WARNING "+month+ "while in event is "+evMonth);
							lds.deleteEvent(evt.getTime());
						}
					}

				}
				evit = events.iterator();
				while (evit.hasNext()){
					Event ev = evit.next();
					String direction ="";
					if(ev.getDirection()==2){
						direction = "leaving";
					}else if (ev.getDirection()==1){
						direction = "entering";
					}
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy h:mm,a", Locale.ITALY);
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(ev.getTime()*1000);
					String formattedDate = sdf.format(c.getTime());
					String evString = direction+" "+ev.getName()+" , "+formattedDate;
					list.add(evString);
				}

			}
			lds.close();

		}
		adapter.notifyDataSetChanged();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.events_fragment,container, false);
		final ListView listview = (ListView) rootView.findViewById(R.id.listview);
		Button deleteButton = (Button)rootView.findViewById(R.id.button1);
		deleteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EventsFragment frag = EventsFragment.this;
				frag.list.clear();
				frag.adapter.notifyDataSetChanged();
				DataSource lds = new DataSource(frag.getActivity().getBaseContext());
				lds.open();
				lds.deleteAllEvents();
				lds.close();
			}
				
		});
		
		list = new ArrayList<String>();
		Context context = this.getActivity();
		if(context!=null){
			DataSource lds = new DataSource(rootView.getContext());
			lds.open();
			List<Event> events = lds.getAllEvents();
			if(events!=null){
				Iterator<Event> evit = events.iterator();
				while (evit.hasNext()){
					Event ev = evit.next();
					String direction ="entering";
					if(ev.getDirection()==2){
						direction = "leaving";
					}
					//System.out.println("Creating event time "+ev.getTime());

					Date date = new Date(ev.getTime()*1000);
					SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
					String formattedDate = sdf.format(date);
					String evString = direction+" "+ev.getName()+" , "+formattedDate;
					list.add(evString);
				}

			}
			lds.close();

		}
		adapter = new EventAdapter(rootView.getContext(),android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1, int position,
			long id) {

	}



}

