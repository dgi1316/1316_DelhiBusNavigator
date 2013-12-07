package com.hard.targets.delhibusnavigator;

import java.io.IOException;
import java.util.LinkedList;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewRouteActivity extends Activity implements AdListener {

	String source = null, destination = null;
	TextView tvSource, tvDestination;
	public static TextView tvLabel;
	ListView lv;
	public static LinkedList<String> ll = new LinkedList<String>();
	ArrayAdapter<String> aa;
	DatabaseHelper myDbHelper;
	SQLiteDatabase myDb;
	AdView av;
	
	private InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_route);
		setupActionBar();
		
		interstitial = new InterstitialAd(this, "");
		interstitial.loadAd(new AdRequest());
		interstitial.setAdListener(this);
		
		Intent t = getIntent();
		source = t.getStringExtra("source");
		destination = t.getStringExtra("destination");
		
		myDbHelper = new DatabaseHelper(this);
		try {
			myDbHelper.createDatabase();
		} catch (IOException e) {
			throw new Error("Unable to create database.");
		}
		try {
			myDbHelper.openDatabase();
		} catch (SQLException sqle) {
			throw sqle;
		}
		
		tvSource = (TextView) findViewById(R.id.tvSource);
		tvDestination = (TextView) findViewById(R.id.tvDestination);
		tvLabel = (TextView) findViewById(R.id.tvLabel);
		lv = (ListView) findViewById(R.id.lvBusRoute);
		ll.clear();
		lv.setAdapter(null);
		
		tvSource.setText(source);
		tvDestination.setText(destination);
		
		findPath(source, destination);
		aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ll);
		lv.setAdapter(aa);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String bn = lv.getItemAtPosition(arg2).toString();
				String[] bus = bn.split(" :  ");
				Intent i = new Intent(getApplicationContext(), RouteActivity.class);
				i.putExtra("bno", bus[0]);
				startActivity(i);
			}
		});
		
		av = (AdView) findViewById(R.id.avViewRoute);
		av.loadAd(new AdRequest());
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	public void findPath(String start, String end) {
		if (myDbHelper.getDirectRoute(start, end)) {
			tvLabel.setText(getResources().getString(R.string.searchSuccess));
			return;
		}
		Toast.makeText(this, getResources().getString(R.string.searchFail), Toast.LENGTH_LONG).show();
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_route, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDismissScreen(Ad arg0) {
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
	}

	@Override
	public void onPresentScreen(Ad arg0) {
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		if (arg0 == interstitial)
			interstitial.show();
	}

	@Override
	protected void onDestroy() {
		if (av != null)
			av.destroy();
		interstitial.stopLoading();
		super.onDestroy();
	}

}
