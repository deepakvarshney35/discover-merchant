package com.enormous.pkpizzas.publisher.activities;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.enormous.pkpizzas.publisher.adapter.SlidingTabsMainScreen;
import com.enormous.pkpizzas.publisher.fragments.MainScreenAddItemsFragment;
import com.enormous.pkpizzas.publisher.fragments.MainScreenGraphFragment;
import com.enormous.pkpizzas.publisher.fragments.MainScreenProfileFragment;
import com.enormous.pkpizzas.publisher.R;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;

public class MainScreenActivity extends FragmentActivity implements OnPageChangeListener, OnClickListener {

	private SlidingTabsMainScreen slidingTabStrip;
	ViewPager pager;
	int prevPage = 1;
	int currentPage = 1;
	ImageButton searchButton;
	Button homeButton;
	ImageButton profileButton;
	ArrayList<String> actionBarTitles;
	public static ParseUser CURRENT_USER;
	public static File EXTERNAL_CACHE_DIR;

	ArrayList<Fragment> fragments;

    @Override
	protected void onResume() {

		//Portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		super.onResume();

	}

    @Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main_screen);
		findViews();

		//get current user
		CURRENT_USER = ParseUser.getCurrentUser();

		//get app's cache directory
		EXTERNAL_CACHE_DIR = getExternalCacheDir();

		if(CURRENT_USER!=null){
			ParseInstallation installation = ParseInstallation.getCurrentInstallation();
			installation.put("deviceId", ParseUser.getCurrentUser().getObjectId());
			installation.saveInBackground();
			setUpPagerAdpter();
		}
		ParsePush.subscribeInBackground("Publisher");
	}
	public void setUpPagerAdpter() {
		CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);

        pager.setCurrentItem(1);

		pager.setOffscreenPageLimit(2);
		slidingTabStrip.setDelegatePageListener(this);
		slidingTabStrip.setViewPager(pager);
	}

	//FragmentPagerAdapter for view pager
	class CustomPagerAdapter extends FragmentPagerAdapter {

		public CustomPagerAdapter(FragmentManager fm) {
			super(fm);
			//initialize add fragments to fragments list
			fragments = new ArrayList<Fragment>();
			fragments.add(new MainScreenGraphFragment());
			fragments.add(new MainScreenProfileFragment());
			fragments.add(new MainScreenAddItemsFragment());
		}

		@Override
		public Fragment getItem(int pos) {
			return fragments.get(pos);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}	
	}

	public void findViews() {
		pager = (ViewPager) findViewById(R.id.pager);
		slidingTabStrip = (SlidingTabsMainScreen) findViewById(R.id.slidingTabStripMainScreen);
	}

	@Override
	public void onBackPressed() {
		if (currentPage == 0 || currentPage == 2) {
			pager.setCurrentItem(1);
		}
		else {
			super.onBackPressed();
		}
	}

	//----------------------OnPageChangedListener Methods---------------------------

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override	
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int pos) {
		final InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

		if(pos==0){
			MainScreenGraphFragment myFragment = (MainScreenGraphFragment) fragments.get(0);
			myFragment.new GetCustomersTask().execute();
		}
	}

	//-----------------------onClickListener method----------------------------------

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.searchButton:
			pager.setCurrentItem(0);

			break;
		case R.id.homeButton:
			pager.setCurrentItem(1);
			break;
		case R.id.profileButton:
			pager.setCurrentItem(2);
			break;
		}
	}
}
