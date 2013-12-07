package com.hard.targets.delhibusnavigator;

import java.util.ArrayList;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CustomListAdapter extends ArrayAdapter<String> {

	Activity context;
	ArrayList<String> bno = new ArrayList<String>();
	ArrayList<String> source = new ArrayList<String>();
	ArrayList<String> destination = new ArrayList<String>();
	
	public CustomListAdapter(Activity context, ArrayList<String> bno, ArrayList<String> source, ArrayList<String> destination) {
		super(context, R.layout.list_item, bno);
		this.context = context;
		this.bno = bno;
		this.source = source;
		this.destination = destination;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = context.getLayoutInflater().inflate(R.layout.list_item, null, true);
		TextView txtBNo = (TextView) rowView.findViewById(R.id.tvBNo);
		TextView txtSource = (TextView) rowView.findViewById(R.id.tvSource);
		TextView txtDestination = (TextView) rowView.findViewById(R.id.tvDestination);
		txtBNo.setText(bno.get(position).toString());
		txtSource.setText(rowView.getResources().getString(R.string.etSource) + " " + source.get(position).toString());
		txtDestination.setText(rowView.getResources().getString(R.string.etDestination) + " " + destination.get(position).toString());
		return rowView;
	}

}
