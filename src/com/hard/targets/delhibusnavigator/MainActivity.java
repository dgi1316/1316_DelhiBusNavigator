package com.hard.targets.delhibusnavigator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, AdListener {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	
	static AdView av1, av2, av3;
	
	private static InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		interstitial = new InterstitialAd(this, "a152875a56b4164");
		interstitial.loadAd(new AdRequest());
		interstitial.setAdListener(this);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.rate:
			//String url = "http://www.samsungapps.com/appquery/appDetail.as?appId=com.nits.thedecoder";
			//Intent rate = new Intent(Intent.ACTION_VIEW);
			//rate.setData(Uri.parse(url));
			//startActivity(rate);
			return true;
		case R.id.share:
			Intent localIntent = new Intent("android.intent.action.SEND");
		    localIntent.setType("text/plain");
		    localIntent.putExtra("android.intent.extra.SUBJECT", "Delhi Bus (DTC) Navigator");
		    localIntent.putExtra("android.intent.extra.TEXT", "New to Delhi? Having difficulty in travelling? Now, travel across DELHI (NCR) through DTC Buses with 'Delhi Bus Navigator' - Get detail about every Bus Route of the DTC Network.");
		    startActivity(Intent.createChooser(localIntent, "Share Via"));
			return true;
		case R.id.about:
			Intent about = new Intent("com.hard.targets.delhibusnavigator.ABOUTACTIVITY");
			startActivity(about);
			return true;
		case R.id.disclaimer:
			Intent disclaimer = new Intent("com.hard.targets.delhibusnavigator.DISCLAIMERACTIVITY");
			startActivity(disclaimer);
			return true;
		case R.id.exit:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_action_alert)
			.setTitle(R.string.dialog_title)
			.setMessage(R.string.dialog_message)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					MainActivity.this.finish();
				}
			})
			.setNegativeButton(R.string.no, null)
			.setCancelable(false)
			.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (av1 != null)
			av1.destroy();
		if (av2 != null)
			av2.destroy();
		if (av3 != null)
			av3.destroy();
		interstitial.stopLoading();
		super.onDestroy();
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	public static class DummySectionFragment extends Fragment {
		
		public static final String ARG_SECTION_NUMBER = "section_number";
		int position;
		View rootView;
		EditText etSearch;
		ListView lv;
		CustomListAdapter clAdapter;
		DatabaseHelper myDbHelper;
		ArrayList<String> bno = new ArrayList<String>();
		ArrayList<String> source = new ArrayList<String>();
		ArrayList<String> destination = new ArrayList<String>();
		
		AutoCompleteTextView actv1, actv2;
		LinkedList<String> ll;
		ArrayAdapter<String> arrayAdapter;
		Button btn;
		
		AutoCompleteTextView actv3;
		ImageButton bFind;
		ListView lvStops;
		LinkedList<String> llSearch, llStops = new LinkedList<String>();
		ArrayAdapter<String> aa1, aa2;

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			position = getArguments().getInt(ARG_SECTION_NUMBER);
			myDbHelper = new DatabaseHelper(getActivity());
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
			switch (position) {
			case 0:
				rootView = inflater.inflate(R.layout.fragment_main_dummy1, container, false);
				etSearch = (EditText) rootView.findViewById(R.id.etSearch1);
				lv = (ListView) rootView.findViewById(R.id.lv1);
				Cursor c = myDbHelper.getAllBusNumber();
				c.moveToFirst();
				for(int i = 0; i <= c.getCount() - 1; i++) {
					bno.add(c.getString(0));
					source.add(c.getString(1));
					destination.add(c.getString(2));
					c.moveToNext();
				}
				c.close();
				clAdapter = new CustomListAdapter(getActivity(), bno, source, destination);
				lv.setAdapter(clAdapter);
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Intent i = new Intent("com.hard.targets.delhibusnavigator.ROUTEACTIVITY");
						i.putExtra("bno", "" + lv.getItemAtPosition(arg2));
						startActivity(i);
					}
				});
				etSearch.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
						try {
							bno.clear();
							source.clear();
							destination.clear();
							clAdapter.clear();
							Cursor c;
							lv.setAdapter(null);
							if (etSearch.getText().toString().contentEquals("")) {
								c = myDbHelper.getAllBusNumber();
								c.moveToFirst();
								for(int i = 0; i <= c.getCount() - 1; i++) {
									bno.add(c.getString(0));
									source.add(c.getString(1));
									destination.add(c.getString(2));
									c.moveToNext();
								}
								c.close();
							} else {
								String search = etSearch.getText().toString();
								c = myDbHelper.searchBusNumber(search);
								c.moveToFirst();
								for(int i = 0; i <= c.getCount() - 1; i++) {
									bno.add(c.getString(0));
									source.add(c.getString(1));
									destination.add(c.getString(2));
									c.moveToNext();
								}
								c.close();
							}
							clAdapter = new CustomListAdapter(getActivity(), bno, source, destination);
							lv.setAdapter(clAdapter);
						} catch (Exception e) {
							Toast.makeText(getActivity(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}
					
					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					}
					
					@Override
					public void afterTextChanged(Editable arg0) {
					}
				});
				av1 = (AdView) rootView.findViewById(R.id.av1);
				av1.loadAd(new AdRequest());
				interstitial.loadAd(new AdRequest());
				break;
			case 1:
				rootView = inflater.inflate(R.layout.fragment_main_dummy2, container, false);
				actv1 = (AutoCompleteTextView) rootView.findViewById(R.id.actvSource);
				actv2 = (AutoCompleteTextView) rootView.findViewById(R.id.actvDestination);
				btn = (Button) rootView.findViewById(R.id.bFind);
				ll = myDbHelper.getAutoFill();
				arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, ll);
				actv1.setAdapter(arrayAdapter);
				actv2.setAdapter(arrayAdapter);
				btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						InputMethodManager localInputMethodManager = (InputMethodManager) getActivity().getSystemService("input_method");
						localInputMethodManager.hideSoftInputFromWindow(actv1.getWindowToken(), 0);
						localInputMethodManager.hideSoftInputFromWindow(actv2.getWindowToken(), 0);
						if (actv1.getText().toString().equals("") && actv2.getText().toString().equals("")) {
							Toast.makeText(getActivity(), "Source and Destination cannot be blank", Toast.LENGTH_SHORT).show();
						} else if (actv1.getText().toString().equals("")) {
							Toast.makeText(getActivity(), "Source cannot be blank", Toast.LENGTH_SHORT).show();
						} else if (actv2.getText().toString().equals("")) {
							Toast.makeText(getActivity(), "Destination cannot be blank", Toast.LENGTH_SHORT).show();
						} else if (actv1.getText().toString().equalsIgnoreCase(actv2.getText().toString())) {
							Toast.makeText(getActivity(), "Source and Destination cannot be same", Toast.LENGTH_LONG).show();
						} else {
							Intent i = new Intent("com.hard.targets.delhibusnavigator.VIEWROUTEACTIVITY");
							i.putExtra("source", actv1.getText().toString());
							i.putExtra("destination", actv2.getText().toString());
							startActivity(i);
						}
					}
				});
				av2 = (AdView) rootView.findViewById(R.id.av2);
				av2.loadAd(new AdRequest());
				interstitial.loadAd(new AdRequest());
				break;
			case 2:
				rootView = inflater.inflate(R.layout.fragment_main_dummy3, container, false);
				actv3 = (AutoCompleteTextView) rootView.findViewById(R.id.actvStop);
				bFind = (ImageButton) rootView.findViewById(R.id.bFindStop);
				lvStops = (ListView) rootView.findViewById(R.id.lvStops);
				llSearch = myDbHelper.getAutoStop();
				aa1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, llSearch);
				actv3.setAdapter(aa1);
				aa2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, llStops);
				bFind.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String stop = actv3.getText().toString();
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService("input_method");
						imm.hideSoftInputFromWindow(actv3.getWindowToken(), 0);
						llStops.clear();
						aa2.clear();
						lvStops.setAdapter(null);
						if (stop.contentEquals(""))
							Toast.makeText(getActivity(), "Enter Stop Name", Toast.LENGTH_LONG).show();
						else {
							llStops = myDbHelper.searchStop(stop);
							if (llStops.isEmpty())
								Toast.makeText(getActivity(), "Nothing found", Toast.LENGTH_LONG).show();
							else {
								aa2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, llStops);
								lvStops.setAdapter(aa2);
							}
						}
					}
				});
				lvStops.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Intent i = new Intent("com.hard.targets.delhibusnavigator.ROUTEACTIVITY");
						i.putExtra("bno", "" + lvStops.getItemAtPosition(arg2).toString());
						startActivity(i);
					}
				});
				av3 = (AdView) rootView.findViewById(R.id.av3);
				av3.loadAd(new AdRequest());
				interstitial.loadAd(new AdRequest());
				break;
			default:
				break;
			}
			return rootView;
		}
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

}
