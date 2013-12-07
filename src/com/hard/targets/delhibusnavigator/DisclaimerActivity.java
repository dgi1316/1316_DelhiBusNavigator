package com.hard.targets.delhibusnavigator;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.support.v4.app.NavUtils;

public class DisclaimerActivity extends Activity implements AdListener {

	WebView wv;
	AdView av;
	private InterstitialAd interstitial;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disclaimer);
		setupActionBar();
		
		interstitial = new InterstitialAd(this, "a152875a56b4164");
		interstitial.loadAd(new AdRequest());
		interstitial.setAdListener(this);
		
		url = "file:///android_asset/disclaimer.html";
		
		wv = (WebView) findViewById(R.id.wvDisclaimer);
		wv.loadUrl(url);
		av = (AdView) findViewById(R.id.avDisclaimer);
		av.loadAd(new AdRequest());
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setSubtitle(getResources().getString(R.string.app_disclaimer));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.disclaimer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		interstitial.stopLoading();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (av != null)
			av.destroy();
		interstitial.stopLoading();
		super.onDestroy();
	}

}
