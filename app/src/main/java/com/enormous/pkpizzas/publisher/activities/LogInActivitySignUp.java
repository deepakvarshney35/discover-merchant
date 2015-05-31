package com.enormous.pkpizzas.publisher.activities;

import java.util.ArrayList;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.enormous.pkpizzas.publisher.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LogInActivitySignUp extends Activity {

    AutoCompleteTextView emailEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    Button signUpButton;
    TextView privacyPolicyTextView;
    ParseUser newUser;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.fragment_login_signup);
        findViews();
        //enable up button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        //format privacy policy textview
        String privacyPolicy = "By signing up, you agree to the <br><b>Privacy Policy & Terms of Service.</b>";
        privacyPolicyTextView.setText(Html.fromHtml(privacyPolicy));

        //set arrayAdapter for emailEditText
        emailEditText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(LoginActivity.emailSet)));

        //set onClick listener for signUpButton
        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard(confirmPasswordEditText.getWindowToken());
                signUp();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //set actionBar title
        getActionBar().setTitle("Sign Up");
    }

    public void findViews() {
        emailEditText = (AutoCompleteTextView) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        privacyPolicyTextView = (TextView) findViewById(R.id.privacyPolicyTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.done_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            hideKeyboard(confirmPasswordEditText.getWindowToken());
            signUp();
        }
        if (item.getItemId() == android.R.id.home) {
            hideKeyboard(emailEditText.getWindowToken());
            //getFragmentManager().popBackStack();
            NavUtils.navigateUpFromSameTask(this);
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideKeyboard(IBinder token) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    public void signUp() {
        //get username and password input
        String username = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (username.trim().length() == 0) {
            emailEditText.setError("Email cannot be left blank.");
        } else if (!username.trim().equals("pkpizzas@gmail.com")) {
            Toast.makeText(LogInActivitySignUp.this, "Email not valid", Toast.LENGTH_SHORT).show();
        } else if (password.trim().length() == 0) {
            passwordEditText.setError("Password cannot be left blank");
        } else if (confirmPassword.trim().length() == 0) {
            confirmPasswordEditText.setError("Confirm Password cannot be left blank");
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordEditText.setError("Passwords do not match");
        } else if (password.trim().length() < 8) {
            passwordEditText.setError("Must me at least 8 characters");
        } else {
            //set up progress dialog
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Signing Up...");
            progressDialog.show();

            newUser = new ParseUser();
            newUser.setUsername(username);
            newUser.setEmail(username);
            newUser.setPassword(password);
            newUser.put("brandName", "");
            newUser.put("isBrand", true);
            newUser.put("brandEmail", "");
            newUser.put("brandPhone", "");
            newUser.put("brandAbout", "");
            //check if user already exists
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo("username", username);
            userQuery.findInBackground(new FindCallback<ParseUser>() {

                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (e == null && users.size() > 0) {
                        Log.d("TEST", "User already exists");
                        progressDialog.dismiss();
                        Toast.makeText(LogInActivitySignUp.this, "That email address is already in use.", Toast.LENGTH_SHORT).show();
                    } else if (e == null && users.size() == 0) {

                        LogInActivitySignUp.this.newUser.signUpInBackground(new SignUpCallback() {

                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("TEST", "Sign Up Succesfull");
                                    progressDialog.dismiss();

                                    //ParseUser.logOut();
                                    Toast.makeText(LogInActivitySignUp.this, "Account successfully created.", Toast.LENGTH_LONG).show();

                                    Intent goToPictureScreen = new Intent(LogInActivitySignUp.this, MainScreenActivity.class);
                                    startActivity(goToPictureScreen);
                                    //override entry transition animation
                                    LoginActivity.LogInMain.finish();
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
                                } else {
                                    Log.d("TEST", "Sign Up FAILED: " + e.getMessage());
                                    progressDialog.dismiss();
                                    Toast.makeText(LogInActivitySignUp.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.d("TEST", "User Query Error: " + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(LogInActivitySignUp.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
