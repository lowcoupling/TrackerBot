package com.lowcoupling.trackerbot;

import java.util.List;

import com.lowcoupling.trackerbot.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class EventAdapter extends ArrayAdapter<String> {

	static boolean deleteMode;
	private List<String> list;
	public EventAdapter(Context context, int textViewResourceId,
			List<String> objects) {
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
		rowView = inflater.inflate(R.layout.event_row, parent, false);
		ImageView imgView;
		imgView = (ImageView) rowView.findViewById(R.id.image);
		
	
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		String [] data = list.get(position).split(",");
		String eventText = data[0];
		String [] evtData = eventText.split(" ");
		if(evtData[0].equals("entering")){
			imgView.setImageResource(R.drawable.glyphicons_386_log_in);
			textView.setText(evtData[1]);
		}else if(evtData[0].equals("leaving")){
			imgView.setImageResource(R.drawable.glyphicons_387_log_out);
			textView.setText(evtData[1]);
		}else{
			imgView.setImageResource(R.drawable.glyphicons_063_power);
			textView.setText(evtData[2].toLowerCase());
		}
		TextView textView2 = (TextView) rowView.findViewById(R.id.label2);
		textView2.setText(data[1]);
		

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

