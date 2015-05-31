package com.enormous.pkpizzas.publisher.activities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.enormous.pkpizzas.publisher.R;
import com.parse.DeleteCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LoginActivity extends Activity implements OnClickListener {

	//Button signInWithTwitterButton;
	Button signUpWithEmailButton,signUpWithFBButton;
	TextView signInNowTextView;
	public static HashSet<String> emailSet;
	public static Activity LogInMain;
	private Dialog progressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.fragment_login_main);
		findViews();

		LogInMain = this;
		//get all user accounts
		AccountManager accountManager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
		Account[] accounts = accountManager.getAccounts();
		emailSet = new HashSet<String>();
		for (Account account : accounts) {
			emailSet.add(account.name);
		}
		//set up buttons and textviews
		//String signInWithTwitter = "<font color=#ffffff>Sign in with </font><font color=#ffffff><b>Twitter</b></font>";
		//signInWithTwitterButton.setText(Html.fromHtml(signInWithTwitter));
		String signUpWithEmail = "<font color=#ffffff>Sign up with </font><font color=#ffffff><b>Email</b></font>";
		signUpWithEmailButton.setText(Html.fromHtml(signUpWithEmail));
		String signInNow = "Already have an account? <b>Sign in now</b>";
		signInNowTextView.setText(Html.fromHtml(signInNow));

		//set onClick listeners
		signUpWithFBButton.setOnClickListener(this);
		signUpWithEmailButton.setOnClickListener(this);
		signInNowTextView.setOnClickListener(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null &&  ParseFacebookUtils.isLinked(currentUser) ) {
			// do stuff with the user
			startActivity(new Intent(LoginActivity.this, MainScreenActivity.class));
			finish();
		} 
		//set actionBar title
		//getActivity().getActionBar().setTitle("Whistle");
		//getActionBar().hide();
		//hide up button
		//getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	public void findViews() {
		signUpWithFBButton = (Button)findViewById(R.id.signUpWithFBButton);
		signUpWithEmailButton = (Button) findViewById(R.id.signUpWithEmailButton);
		signInNowTextView = (TextView) findViewById(R.id.signInNowTextView);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		Log.d("FB", "FB done");
	}
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.signUpWithEmailButton) {
			Intent intent = new Intent(LoginActivity.this, LogInActivitySignUp.class);

			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
		}
		if (v.getId() == R.id.signInNowTextView) {
			Intent intent = new Intent(LoginActivity.this, LogInActivitySignIn.class);

			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
		}
		if (v.getId() == R.id.signUpWithFBButton) {
			attemptSigninFB();
		}
	}
	private void attemptSigninFB() {
		LoginActivity.this.progressDialog = new Dialog(this);
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList(//"public_profile","user_website","user_about_me",, "user_location"
				"email");
		ParseFacebookUtils.logIn(permissions, LoginActivity.this,new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d("com.enormous.whistleapp",
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d("com.enormous.whistleapp",
							"User signed up and logged in through Facebook!");
					user.put("brandName", "");
					user.put("brandEmail", "");
					user.put("brandLocation", "");
					user.put("brandPhone", "");
					user.put("brandAbout", "");
					ParseUser currentUser = ParseUser.getCurrentUser();
					if (currentUser != null) {
						Log.d("TEST", "Logged in as: " + currentUser.getObjectId()+currentUser.getSessionToken()+currentUser.isDataAvailable()+currentUser.getCreatedAt() );}
					user.saveInBackground();

					startActivity(new Intent(LoginActivity.this, MainScreenActivity.class));
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
					finish();
				} else {
					Log.d("TEST", "Logged in as: " + user.getObjectId()+user.getSessionToken()+user.isDataAvailable()+user.getCreatedAt() );
					if(user.getObjectId()!=null)
					{Log.d("com.enormous.whistleapp",
							"User logged in through Facebook!");
					startActivity(new Intent(LoginActivity.this, MainScreenActivity.class));
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
					finish();}
					else
					{if(ParseFacebookUtils.getSession()!=null)
					    ParseFacebookUtils.getSession().closeAndClearTokenInformation();

						if(user != null){
							ParseFacebookUtils.unlinkInBackground(user, new SaveCallback() {
								  @Override
								  public void done(ParseException ex) {
								    if (ex == null) {
								      Log.d("MyApp", "The user is no longer associated with their Facebook account.");
								    }
								  }
								});
					    user.deleteInBackground(new DeleteCallback() {
							
							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								ParseUser.logOut();
								Toast.makeText(LoginActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
								
							}
						});}
						}
				}
			}
		});
	}
}
