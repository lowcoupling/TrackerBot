package com.lowcoupling.trackerbot;

import java.util.List;

import com.lowcoupling.trackerbot.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

class LocationAdapter extends ArrayAdapter<com.lowcoupling.trackerbot.Location> {

	static boolean deleteMode;
	private List<com.lowcoupling.trackerbot.Location> list;
	private Context context;

	public LocationAdapter(Context context, int textViewResourceId,
			List<com.lowcoupling.trackerbot.Location> objects) {
		super(context, textViewResourceId, objects);
		list = objects;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView =null;
		context = this.getContext();
		if(deleteMode==false){
			rowView = inflater.inflate(R.layout.row, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.label);
			textView.setText(list.get(position).getName());
			TextView desc = (TextView) rowView.findViewById(R.id.label2);
			desc.setText("latitude: "+String.format("%.2f",list.get(position).getLatitude())+" longitude: "+String.format("%.2f",list.get(position).getLongitude()));
		}else{
			rowView = inflater.inflate(R.layout.row_delete, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.label);
			textView.setText(list.get(position).getName());
			TextView desc = (TextView) rowView.findViewById(R.id.label2);
			desc.setText("latitude: "+String.format("%.2f",list.get(position).getLatitude())+" longitude: "+String.format("%.2f",list.get(position).getLongitude()));

			Button button = (Button) rowView.findViewById(R.id.deleteButton);
			button.setOnClickListener(new OnClickListener(){
				private int position =-1;
				private LocationAdapter adapter;
				public OnClickListener init(int pos,LocationAdapter adapter){
					position = pos;
					this.adapter = adapter;
					return this;
				}
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String name = list.get(position).getName();
					list.remove(position);
					DataSource lds = new DataSource(context);
					lds.open();
					lds.deleteLocation(name);
					
					adapter.notifyDataSetChanged();
					deleteMode =false;
				}
				
			}.init(position,this));

		}

		// Change the icon for Windows and iPhone
		//String s = values[position];
		//if (s.startsWith("Windows7") || s.startsWith("iPhone")
		//	|| s.startsWith("Solaris")) {
		//imageView.setImageResource(R.drawable.no);
		//} else {
		//simageView.setImageResource(R.drawable.ok);
		//}

		return rowView;
	}
}

