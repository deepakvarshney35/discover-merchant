package com.enormous.pkpizzas.publisher.fragments;


import java.util.ArrayList;

import com.enormous.pkpizzas.publisher.activities.IntroActivity;
import com.enormous.pkpizzas.publisher.R;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class IntroFragment extends Fragment implements OnPageChangeListener{

	ViewPager pager;
	LinearLayout linearLayout;
	ImageView indicator1;
	ImageView indicator2;
	ImageView indicator3;
	ImageView indicator4;
	ImageView indicator5;
	ImageView[] indicators;
	int prevPage = 0;
	Drawable[] drawables;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_intro, container, false);
		findViews(view);
		
		//set the pager with an adapter and update indicator
		IntroPagerAdapter adapter = new IntroPagerAdapter(getActivity().getSupportFragmentManager());
		pager.setAdapter(adapter);
		//initialize and add difference drawables for page bacakground
		drawables = new Drawable[5];
		drawables[0] = new ColorDrawable(Color.parseColor("#6EB0F3"));
		drawables[1] = new ColorDrawable(Color.parseColor("#7870CC"));
		drawables[2] = new ColorDrawable(Color.parseColor("#65CDC0"));
		drawables[3] = new ColorDrawable(Color.parseColor("#68BF60"));
		drawables[4] = new ColorDrawable(Color.parseColor("#E95665"));

		//initialize indicators array and update indicator
		indicators = new ImageView[]{indicator1, indicator2, indicator3, indicator4, indicator5};
		updateIndicator(0);
		
		//set OnPageChangeListener on pager
		pager.setOnPageChangeListener(this);
		
		
		return view;
	}

	public void findViews(View view) {
		pager = (ViewPager) view.findViewById(R.id.pager);
		linearLayout = (LinearLayout) view.findViewById(R.id.pageLinearLayout);
		indicator1 = (ImageView) view.findViewById(R.id.indicator1);
		indicator2 = (ImageView) view.findViewById(R.id.indicator2);
		indicator3 = (ImageView) view.findViewById(R.id.indicator3);
		indicator4 = (ImageView) view.findViewById(R.id.indicator4);
		indicator5 = (ImageView) view.findViewById(R.id.indicator5);

	}

	public void updateIndicator(int pos) {
		indicators[prevPage].setImageResource(R.drawable.pager_indicator_unselected);
		indicators[pos].setImageResource(R.drawable.pager_indicator_selected);
	}

	//IntroPagerAdapter inner class
	class IntroPagerAdapter extends FragmentPagerAdapter {

		ArrayList<Fragment> fragments;

		public IntroPagerAdapter(FragmentManager fm) {
			super(fm);
			fragments = new ArrayList<Fragment>();
			fragments.add(IntroSlideFragmentImage.newInstance(1));
			fragments.add(IntroSlideFragmentImage.newInstance(2));
			fragments.add(IntroSlideFragmentImage.newInstance(3));
			fragments.add(IntroSlideFragmentImage.newInstance(4));
			fragments.add(IntroSlideFragmentImage.newInstance(5));
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

	//---------------onPageChangeListener methods-------------------

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int pos) {
		
		//update pager indicator
		updateIndicator(pos);
		
		//now start transition animation
		TransitionDrawable tDrawable = new TransitionDrawable(new Drawable[]{drawables[prevPage], drawables[pos]});
//		tDrawable.setCrossFadeEnabled(true);
//		linearLayout.setBackground(tDrawable);
//		tDrawable.startTransition(300);
		prevPage = pos;
		
		//get activity reference
		IntroActivity introActivity = (IntroActivity) getActivity();
		
		//update activity background
		introActivity.updateBackground(tDrawable);
		
		//change button background selector according to position
		if (pos == 3) {
			introActivity.updateButtonBackground(R.drawable.button_selector_2, Color.WHITE);
			indicators[1].setImageResource(R.drawable.pager_indicator_unselected);
			indicators[2].setImageResource(R.drawable.pager_indicator_unselected);
			indicators[0].setImageResource(R.drawable.pager_indicator_unselected);
		}
		if (pos == 4) {
			introActivity.updateButtonBackground(R.drawable.button_selector, Color.parseColor("#E95665"));
			indicators[1].setImageResource(R.drawable.pager_indicator_selected);
			indicators[2].setImageResource(R.drawable.pager_indicator_selected);
			indicators[3].setImageResource(R.drawable.pager_indicator_selected);
			indicators[0].setImageResource(R.drawable.pager_indicator_selected);
		}
	}

}
