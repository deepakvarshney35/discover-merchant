package com.enormous.pkpizzas.publisher.fragments;

import com.enormous.pkpizzas.publisher.data.Utils;
import com.enormous.pkpizzas.publisher.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
public class IntroSlideFragmentImage extends Fragment {

	ImageView imageView;
	TextView textView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_intro_slide_image, container, false);
		findViews(view);

		//set image according to position 
		int position = getArguments().getInt("position");
/*        imageView.setImageBitmap(Utils.decodeImageResource(getResources(),
                getResources().getIdentifier("intro"+position, "drawable", getActivity().getPackageName()),
                500,
                500));*/
        switch(position) {
		case 1: 
			imageView.setImageBitmap(Utils.decodeImageResource(getResources(),
					R.drawable.intro1,
					500,
					500));
			//imageView.setImageResource(R.drawable.intro1);
			textView.setText("Looking for a way to reach out?");
			break;
		case 2: 
			imageView.setImageBitmap(Utils.decodeImageResource(getResources(),
					R.drawable.intro2,
					500,
					500));
			//imageView.setImageResource(R.drawable.intro2);
			textView.setText("Reach out to the market with our beacons.");
			break;
		case 3: 
			imageView.setImageBitmap(Utils.decodeImageResource(getResources(),
					R.drawable.intro3,
					500,
					500));
			//imageView.setImageResource(R.drawable.intro3);
			textView.setText("Add information on offers and sales that go directly to customers.");
			break;
		case 4:
			imageView.setImageBitmap(Utils.decodeImageResource(getResources(),
					R.drawable.intro4,
					500,
					500));
			//imageView.setImageResource(R.drawable.intro4);
			textView.setText("Keep a track of your business profile and performance.");
			break;
		case 5:
			imageView.setImageBitmap(Utils.decodeImageResource(getResources(),
					R.drawable.intro,
					500,
					500));
			//imageView.setImageResource(R.drawable.intro);
			textView.setText("Run your entire business from just your Phone!");
			break;
		}
		return view;
	}

	public static Fragment newInstance(int position) {
		Fragment introSlideFragment = new IntroSlideFragmentImage();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		introSlideFragment.setArguments(bundle);
		return introSlideFragment;
	}

	public void findViews(View view) {
		imageView = (ImageView) view.findViewById(R.id.introImageView);
		textView = (TextView)view.findViewById(R.id.textView2);
	}

}
/*public class IntroSlideFragmentImage extends Fragment {
	
	ImageView imageView;
	TextView textView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_intro_slide_image, container, false);
		findViews(view);
		
		//set image according to position 
		int position = getPosition();
		switch(position) {
		case 1: 
			imageView.setImageResource(R.drawable.introd_1);
			textView.setText("Looking for a way to reach out?");
			break;
		case 2: 
			imageView.setImageResource(R.drawable.introd_2);
			textView.setText("Reach out to the market with our beacons.");
			break;
		case 3: 
			imageView.setImageResource(R.drawable.introd_3);
			textView.setText("Add information on offers and sales that go directly to customers.");
			break;
		case 4: 
			imageView.setImageResource(R.drawable.introd_4);
			textView.setText("Keep a track of your business profile and performance.");
			break;
		case 5:
			imageView.setImageResource(R.drawable.intro);
			textView.setText("Run your entire business from just your Phone!");
			break;
		}
		return view;
	}
	
	public static Fragment newInstance(int position) {
		Fragment introSlideFragment = new IntroSlideFragmentImage();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		introSlideFragment.setArguments(bundle);
		return introSlideFragment;
	}
	
	public void findViews(View view) {
		imageView = (ImageView) view.findViewById(R.id.imageView1);
		textView = (TextView) view.findViewById(R.id.textView1);
	}
	
	public int getPosition() {
		return getArguments().getInt("position");
	}
}
*/