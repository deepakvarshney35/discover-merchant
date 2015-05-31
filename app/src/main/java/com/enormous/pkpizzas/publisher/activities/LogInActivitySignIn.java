package com.enormous.pkpizzas.publisher.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.enormous.pkpizzas.publisher.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LogInActivitySignIn extends Activity implements OnClickListener{

	AutoCompleteTextView emailEditText;
	EditText passwordEditText;
	Button signInButton,signInWithFBButton;
	TextView forgotPasswordTextView;
	ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.fragment_login_signin);
		findViews();
		//getCallingActivity().getActionBar().show();
		//enable up button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent); 
		getActionBar().setDisplayShowHomeEnabled(false);

		//format forgot password textview
		String forgotPassword = "Forgot something? <b>Reset your password.</b>";
		forgotPasswordTextView.setText(Html.fromHtml(forgotPassword));

		//enable options menu
		//setHasOptionsMenu(true);

		//set arrayAdapter for emailEditText
		emailEditText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(LoginActivity.emailSet)));

		//set up onClick listener
		forgotPasswordTextView.setOnClickListener(this);
		signInButton.setOnClickListener(this);
		signInWithFBButton.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		//set actionBar title
		getActionBar().setTitle("Sign In");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.done_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_done) {
			//hide keyboard and then login
			hideKeyboard(passwordEditText.getWindowToken());
			attemptToLogin();
		}
		if (item.getItemId() == android.R.id.home) {
			hideKeyboard(emailEditText.getWindowToken());
			NavUtils.navigateUpFromSameTask(this);
            //finish();
            return true;
	}
	return super.onOptionsItemSelected(item);
	}

	public void findViews() {
		emailEditText = (AutoCompleteTextView) findViewById(R.id.emailEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		forgotPasswordTextView = (TextView)findViewById(R.id.forgotPasswordTextView);
		signInButton = (Button) findViewById(R.id.signInButton);
		signInWithFBButton = (Button)findViewById(R.id.signInWithFBButton);
	}

	public void hideKeyboard(IBinder token) {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(token, 0);
	}

	public void attemptToLogin() {
		String username = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if (username.trim().length() == 0) {
			emailEditText.setError("Email cannot be left blank");
		}
		else if (password.trim().length() == 0) {
			passwordEditText.setError("Password cannot be left blank");
		}
		else if (password.trim().length() < 8) {
			passwordEditText.setError("Must me at least 8 characters");
		}
		else {
			//set up progress dialog
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("Signing in...");
			progressDialog.show();

			//finally, log in
			ParseUser.logInInBackground(username, password, new LogInCallback() {

				@Override
				public void done(ParseUser user, ParseException e) {
					if (e == null) {
						Log.d("TEST", "Log in successful");
						progressDialog.dismiss();
						Intent goToMainScreen = new Intent(LogInActivitySignIn.this, MainScreenActivity.class);
						startActivity(goToMainScreen);
						LoginActivity.LogInMain.finish();
						finish();
						//override entry transition animation
						overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
						Log.d("TEST", "user has logged in before");
					}
					else {
						Log.d("TEST", "Log in failed");
						progressDialog.dismiss();
						Toast.makeText(LogInActivitySignIn.this, "Sign In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.forgotPasswordTextView) {
			//add Forgot Password Fragment
			Intent intent = new Intent(LogInActivitySignIn.this, LogInActivityForgotPassword.class);
			//overridePendingTransition(0, R.anim.slide_out_left_rit);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
		}
		if (v.getId() == R.id.signInButton) {
			//hide keyboard and then login
			hideKeyboard(passwordEditText.getWindowToken());
			attemptToLogin();
		}
		if (v.getId() == R.id.signInWithFBButton){
			//hide keyboard and then login
			hideKeyboard(passwordEditText.getWindowToken());
			attemptSigninFB();
		}
	}
	private void attemptSigninFB() {
		progressDialog = new ProgressDialog(this);
		progressDialog = ProgressDialog.show(
				LogInActivitySignIn.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList(//"public_profile","user_website","user_about_me",, "user_location"
				"email");
		ParseFacebookUtils.logIn(permissions, LogInActivitySignIn.this, new UserLogInCallback() {
			//ParseFacebookUtils.logIn( this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				progressDialog.dismiss();
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

					startActivity(new Intent(LogInActivitySignIn.this, MainScreenActivity.class));
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
					finish();
				} else {
					Log.d("TEST", "Logged in as: " + user.getObjectId()+user.getSessionToken()+user.isDataAvailable()+user.getCreatedAt() );
					if(user.getObjectId()!=null)
					{Log.d("com.enormous.whistleapp",
							"User logged in through Facebook!");
					startActivity(new Intent(LogInActivitySignIn.this, MainScreenActivity.class));
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
					finish();}
					else
					{
						ParseUser.logOut();
					}
				}
			}
		});
	}
	private class UserLogInCallback extends LogInCallback {

		public UserLogInCallback() {
			super();
		}

		@Override
		public void done(ParseUser user, ParseException e) {
			if (e == null) {
				//
			} else {
				//
			}
		}
	}
}
