package com.enormous.pkpizzas.publisher.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.enormous.pkpizzas.publisher.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class LogInActivityForgotPassword extends Activity {

	EditText emailEditText;
	ProgressDialog progressDialog;
	Button resetPasswordButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.fragment_login_forgot_password);
		findViews();
		getActionBar().show();
		//enable up button
		getActionBar().setDisplayHomeAsUpEnabled(true);	
		getActionBar().setIcon(android.R.color.transparent); 
		getActionBar().setDisplayShowHomeEnabled(false);
		//set onClick listener for resetPasswordButton
		resetPasswordButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard(emailEditText.getWindowToken());
				resetPassword();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		getActionBar().setTitle("Reset Password");
	}

	public void findViews() {
		emailEditText = (EditText)findViewById(R.id.emailEditText);
		resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_reset_password, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_reset_password) {
			hideKeyboard(emailEditText.getWindowToken());
			resetPassword();
		}
		if (item.getItemId() == android.R.id.home) {
			//getFragmentManager().popBackStack();
			NavUtils.navigateUpFromSameTask(this);
            //finish();
            return true;
	}
	return super.onOptionsItemSelected(item);}

	public void resetPassword() {
		String email = emailEditText.getText().toString().trim();

		if (email.trim().length() == 0) {
			emailEditText.setError("Email cannot be left blank");
		}
		else {
			//set up progress dialog
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("Sending reset instructions...");
			progressDialog.show();

			ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						Log.d("TEST", "An email was successfully sent with reset instructions.");
						progressDialog.dismiss();
						Toast.makeText(LogInActivityForgotPassword.this, "An email was successfully sent with reset instructions.", Toast.LENGTH_LONG).show();
					}
					else {
						Log.d("TEST", "Password Reset Failed: " + e.getMessage());
						progressDialog.dismiss();
						Toast.makeText(LogInActivityForgotPassword.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

	public void hideKeyboard(IBinder token) {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(token, 0);
	}
}