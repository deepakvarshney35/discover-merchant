package com.enormous.pkpizzas.publisher.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.enormous.pkpizzas.publisher.R;
import com.parse.ParseUser;

public class IntroActivity extends FragmentActivity {

	ActionBar actionBar;
	Button getStartedButton;
	RelativeLayout relativeLayout;
	String callingActivity; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//get callingActivity
		callingActivity = getIntent().getStringExtra("callingActivity");
		if (callingActivity != null) {
			setContentView(R.layout.activity_intro);
			findViews();
			getStartedButton.setText("Finish Tour");
			getStartedButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					finish();
					//override exit transition animation
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
				}
			});
		}
		else {
			//check if user is already logged in
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null ) {
				Log.d("TEST", "Logged in as: " + currentUser.getUsername());
				//go to MainScreen and finish() this activity
				Intent goToMainScreen = new Intent(this, MainScreenActivity.class);
				startActivity(goToMainScreen);
				//override exit transition animation
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				finish();
			}
			else {
				Log.d("TEST", "no user logged in");
				setContentView(R.layout.activity_intro);
				findViews();
				//go to Login and finish() this activity
				getStartedButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						Intent goToLogin = new Intent(IntroActivity.this, LoginActivity.class);
						startActivity(goToLogin);
						//override exit transition animation
						overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
						finish();
					}
				});
			}
		}
	}

	public void updateButtonBackground(int resid, int textColor) {
		getStartedButton.setBackgroundResource(resid);
		getStartedButton.setTextColor(textColor);
	}

	public void updateBackground(TransitionDrawable tDrawable) {
		//start background transition animation
		relativeLayout.setBackground(tDrawable);
		tDrawable.setCrossFadeEnabled(true);
		tDrawable.startTransition(200);
	}

	public void findViews() {
		getStartedButton = (Button) findViewById(R.id.getStartedButton);
		relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed(); {
			if (callingActivity != null) {
				//override exit transition animation
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
			}
			super.onBackPressed();
		}
	}

}
