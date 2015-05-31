package com.enormous.pkpizzas.publisher.fragments;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.enormous.pkpizzas.publisher.activities.BeaconSetupActivity;
import com.enormous.pkpizzas.publisher.activities.IntroActivity;
import com.enormous.pkpizzas.publisher.activities.MainScreenActivity;
import com.enormous.pkpizzas.publisher.data.Utils;
import com.enormous.pkpizzas.publisher.R;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

@SuppressLint({"InflateParams", "DefaultLocale"})
public class MainScreenProfileFragment extends Fragment {

    public ProgressDialog deleteUserDataDialog;
    public String deactiveParseUser;
    private ImageView userProfilePictureView;
    private ImageView userCoverView;
    private FrameLayout profilePictureFrame, coverPictureFrame;
    private TextView brandAboutText,
            brandEmailText,
            brandWebsiteText,
            brandPhoneText,
            brandAddressText,
            brandNameText;

    private TextView userNameView;
    private TextView userPhoneView;
    private TextView userEmailView;
    private TextView userAboutView;
    private AutoCompleteTextView userAddressAutoCompleteView;
    private TextView userWebsiteView;

    private ProgressDialog imageUploadProgress;

    private Button logoutButton;
    private Button selectpagebutton;
    private Button beaconsetup;
    private Button btnCategory;
    private Button btnDisable;


    private String Photourl = null;
    private String Coverurl = null;

    // List of additional write permissions being requested
    private static final List<String> PERMISSIONS = Arrays.asList("manage_pages");

    // Request code for reauthorization requests.
    private static final int REAUTH_ACTIVITY_CODE = 100;

    private String id;

    int CHOOSE_CAMERA = 10;
    int CHOOSE_GALLERY = 20;
    int CHOOSE_GALLERY_COVER = 30;
    private static final int BROWSE_GALLERY_REQUEST_PROFILE = 11;
    private static final int BROWSE_GALLERY_REQUEST_COVER = 22;
    final int PROFILE_CROP = 444;
    final int COVER_CROP = 555;
    //Animation slide_down,slide_up;
    private static final int CHOOSE_CAMERA_COVER = 2;

    String[] categories = {"Accessories", "Offers", "Movies", "Grocery", "Food", "Bookstore", "Computers", "People", "Billboards", "Apparels", "Amusement Park", "Bakery", "Bank", "Bar", "Beauty Salon", "Cafe", "Convenience Store", "Department Store", "Electronics Store", "Doctor", "Electrician", "Dentist"};

    ParseUser currentUser;

    private ArrayAdapter<String> addressAdapter;

    String brandNameS = "", brandPhoneS = "", brandWebsiteS = "", brandEmailS = "", brandLocationS = "", brandAboutS = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile_main_screen, container, false);

        if (ParseUser.getCurrentUser().getString("UUID") == null)
            ParseUser.getCurrentUser().put("UUID", ParseUser.getCurrentUser().getObjectId());
        ParseUser.getCurrentUser().saveInBackground();


        //Find Views
        userProfilePictureView = (ImageView) view.findViewById(R.id.imageView2);
        userCoverView = (ImageView) view.findViewById(R.id.imageView1);
        profilePictureFrame = (FrameLayout) view.findViewById(R.id.profilePictureFrame);
        coverPictureFrame = (FrameLayout) view.findViewById(R.id.coverPictureFrame);
        userNameView = (TextView) view.findViewById(R.id.brandName);
        userPhoneView = (TextView) view.findViewById(R.id.brandPhone);
        userWebsiteView = (TextView) view.findViewById(R.id.brandWebsite);
        userEmailView = (TextView) view.findViewById(R.id.brandEmail);
        userAboutView = (TextView) view.findViewById(R.id.brandAbout);
        userAddressAutoCompleteView = (AutoCompleteTextView) view.findViewById(R.id.brandAutoCompleteLocation);
        logoutButton = (Button) view.findViewById(R.id.btnLogout);
        selectpagebutton = (Button) view.findViewById(R.id.btnFacebook);
        beaconsetup = (Button) view.findViewById(R.id.btnBeacon);
        btnCategory = (Button) view.findViewById(R.id.brandCategory);
        btnDisable = (Button) view.findViewById(R.id.btnDisable);

        brandAboutText = (TextView) view.findViewById(R.id.brandAboutText);
        brandWebsiteText = (TextView) view.findViewById(R.id.brandWebsiteText);
        brandEmailText = (TextView) view.findViewById(R.id.brandEmailText);
        brandPhoneText = (TextView) view.findViewById(R.id.brandPhoneText);
        brandAddressText = (TextView) view.findViewById(R.id.brandAddressText);
        brandNameText = (TextView) view.findViewById(R.id.brandNameText);

        //Keep the KeyBoard Hidden
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        currentUser = ParseUser.getCurrentUser();

        //CHeck if Logged in through Facebook
        if (ParseFacebookUtils.getSession() == null) {
            selectpagebutton.setVisibility(View.GONE);
            logoutButton.setText("Logout");
        } else {
            selectpagebutton.setVisibility(View.VISIBLE);
            logoutButton.setText("Logout");
        }

        if (currentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
            if (currentUser.getString("brandName") != null) {
                brandNameS = currentUser.getString("brandName").toString();
                userNameView.setText(brandNameS);
                if (userNameView.length() != 0) {
                    brandNameText.setVisibility(View.VISIBLE);
                }
            }
            if (currentUser.getString("brandPhone") != null) {
                brandPhoneS = currentUser.getString("brandPhone").toString();
                userPhoneView.setText(brandPhoneS);
                if (userPhoneView.length() != 0) {
                    brandPhoneText.setVisibility(View.VISIBLE);
                }
            }
            if (currentUser.getString("brandEmail") != null) {
                brandEmailS = currentUser.getString("brandEmail").toString();
                userEmailView.setText(brandEmailS);
                if (userEmailView.length() != 0) {
                    brandEmailText.setVisibility(View.VISIBLE);
                }
            }
            if (currentUser.getString("brandAbout") != null) {
                brandAboutS = currentUser.getString("brandAbout").toString();
                userAboutView.setText(brandAboutS);
                if (userAboutView.length() != 0) {
                    brandAboutText.setVisibility(View.VISIBLE);
                }
            }
            if (currentUser.getString("brandLocation") != null) {
                brandLocationS = currentUser.getString("brandLocation").toString();
                userAddressAutoCompleteView.setText(brandLocationS);
                if (userAddressAutoCompleteView.length() != 0) {
                    brandAddressText.setVisibility(View.VISIBLE);
                }
            }
            if (currentUser.getString("brandWebsite") != null) {
                brandWebsiteS = currentUser.getString("brandWebsite").toString();
                userWebsiteView.setText(brandWebsiteS);
                if (userWebsiteView.length() != 0) {
                    brandWebsiteText.setVisibility(View.VISIBLE);
                }
            }
            if (currentUser.getString("brandCategory") != null) {
                String bC = currentUser.getString("brandCategory").toString();
                String output = Character.toUpperCase(bC.charAt(0)) + bC.substring(1);
                btnCategory.setText(output);
            }
            ParseFile prof = currentUser.getParseFile("brandProfilePicture");
            if (prof != null) {
                prof.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            // data has the bytes for the resume
                            if (data != null) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                                // Making profile PIC circular
                                //Bitmap circleBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                                //BitmapShader shader = new BitmapShader (bmp,  TileMode.CLAMP, TileMode.CLAMP);
                                //Paint paint = new Paint();
                                //        paint.setShader(shader);
                                //
                                //Canvas c = new Canvas(circleBitmap);
                                //c.drawCircle(bmp.getWidth()/2, bmp.getHeight()/2, bmp.getWidth()/2, paint);

                                userProfilePictureView.setImageBitmap(bmp);
                            }
                        } else {
                            // something went wrong
                        }
                    }
                });
            }
            ParseFile cov = currentUser.getParseFile("brandCoverPicture");
            if (cov != null) {
                cov.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            // data has the bytes for the resume
                            if (data != null) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                userCoverView.setImageBitmap(bmp);
                            }
                        } else {
                            // something went wrong
                        }
                    }
                });
            }

        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            startLoginActivity();
        }
        userNameView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //
                final String brandName = userNameView.getText().toString();
                if (!brandName.equals(brandNameS)) {
                    if (hasFocus) {

                    } else {
                        if (!brandName.isEmpty()) {

                            //If brand Name is changed then change it other classes too.
                            //This can be done on cloud later if it creates any problems.

                            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Product");
                            query1.whereEqualTo("brandName", currentUser.get("brandName"));
                            query1.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> itemList, ParseException e) {
                                    if (e == null) {
                                        for (ParseObject Item : itemList) {
                                            Item.put("brandName", brandName);
                                            Item.saveInBackground();
                                        }
                                    }
                                }
                            });
                            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("ShoppingCart");
                            query2.whereEqualTo("brandName", currentUser.get("brandName"));
                            query2.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> itemList, ParseException e) {
                                    if (e == null) {
                                        for (ParseObject Item : itemList) {
                                            Item.put("brandName", brandName);
                                            Item.saveInBackground();
                                        }
                                    }
                                }
                            });
                            //Save changed Brand Name in User class... Also update searchBrandName which is used for search.
                            currentUser.put("brandName", brandName);
                            currentUser.put("searchBrandName", brandName.toLowerCase().replaceAll("[^a-z0-9]", ""));
                            currentUser.saveInBackground();
                        }
                    }
                }
            }
        });

        userPhoneView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //
                String brandPhone = userPhoneView.getText().toString();
                if (!brandPhone.equals(brandPhoneS)) {
                    if (hasFocus) {

                    } else {
                        if (!brandPhone.isEmpty()) {
                            currentUser.put("brandPhone", brandPhone);
                            currentUser.saveInBackground();
                        }
                    }
                }
            }
        });
        userNameView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userNameView.length() == 0) {
                    brandNameText.setVisibility(View.GONE);
                    //brandPhoneText.setAnimation(slide_down);
                } else {
                    brandNameText.setVisibility(View.VISIBLE);
                    //brandPhoneText.setAnimation(slide_up);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userPhoneView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userPhoneView.length() == 0) {
                    brandPhoneText.setVisibility(View.GONE);
                    //brandPhoneText.setAnimation(slide_down);
                } else {
                    brandPhoneText.setVisibility(View.VISIBLE);
                    //brandPhoneText.setAnimation(slide_up);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userEmailView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //
                String brandEmail = userEmailView.getText().toString();
                if (!brandEmail.equals(brandEmailS)) {
                    if (hasFocus) {

                    } else {
                        if (!brandEmail.isEmpty()) {
                            currentUser.put("brandEmail", brandEmail);
                            currentUser.saveInBackground();
                        }
                    }
                }
            }
        });
        userEmailView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userEmailView.length() == 0) {
                    brandEmailText.setVisibility(View.GONE);
                    //brandEmailText.setAnimation(slide_down);
                } else {
                    brandEmailText.setVisibility(View.VISIBLE);
                    //brandEmailText.setAnimation(slide_up);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userAboutView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userAboutView.length() == 0) {
                    brandAboutText.setVisibility(View.GONE);
                    //brandAboutText.setAnimation(slide_down);
                } else {
                    brandAboutText.setVisibility(View.VISIBLE);
                    //brandAboutText.setAnimation(slide_up);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userAboutView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //
                String brandAbout = userAboutView.getText().toString();
                if (!brandAbout.equals(brandAboutS)) {
                    if (hasFocus) {

                    } else {
                        if (!brandAbout.isEmpty()) {
                            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                            currentUser.put("brandAbout", brandAbout);
                            currentUser.saveInBackground();
                        }
                    }
                }
            }
        });
        userAddressAutoCompleteView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //
                String brandLocation = userAddressAutoCompleteView.getText().toString();
                if (!brandLocation.equals(brandLocationS)) {
                    if (hasFocus) {

                    } else {
                        if (!brandLocation.isEmpty()) {
                            currentUser.put("brandLocation", brandLocation);
                            currentUser.put("searchBrandLocation", brandLocation.toLowerCase().replaceAll("[^a-z0-9]", ""));
                            currentUser.saveInBackground();
                            GetPlacesLatLong task = new GetPlacesLatLong();
                            //now pass the argument in the textview to the task
                            task.execute(brandLocation);
                        }
                    }
                }
            }
        });

        //Google Places ... AutoComplete.
        addressAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item_list);
        addressAdapter.setNotifyOnChange(true);
        userAddressAutoCompleteView.setAdapter(addressAdapter);

        userAddressAutoCompleteView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userAddressAutoCompleteView.length() == 0) {
                    brandAddressText.setVisibility(View.GONE);
                    //brandAddressText.setAnimation(slide_down);
                } else {
                    brandAddressText.setVisibility(View.VISIBLE);
                    //brandAddressText.setAnimation(slide_up);
                }
                if (count % 3 == 1) {
                    addressAdapter.clear();
                    GetPlaces task = new GetPlaces();
                    //now pass the argument in the textview to the task
                    task.execute(userAddressAutoCompleteView.getText().toString());
                    Log.d("test", "" + userAddressAutoCompleteView.getText().toString());

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userWebsiteView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //
                String brandWebsite = userWebsiteView.getText().toString();
                if (!brandWebsite.equals(brandWebsiteS)) {
                    if (hasFocus) {

                    } else {
                        if (!brandWebsite.isEmpty()) {
                            currentUser.put("brandWebsite", brandWebsite);
                            currentUser.saveInBackground();
                        }
                    }
                }
            }
        });
        userWebsiteView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userWebsiteView.length() == 0) {
                    brandWebsiteText.setVisibility(View.GONE);
                    //brandWebsiteText.setAnimation(slide_down);
                } else {
                    brandWebsiteText.setVisibility(View.VISIBLE);
                    //if(userWebsiteView.length()==1 && before==0){
                    //brandWebsiteText.setAnimation(slide_up);}
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        profilePictureFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureChooser(BROWSE_GALLERY_REQUEST_PROFILE);
            }
        });

        btnCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Select Category...");
                alert.setItems(categories, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedCategory = categories[which];
                        ParseUser user = ParseUser.getCurrentUser();
                        if (user != null) {
                            String cat = selectedCategory.toLowerCase();
                            user.put("brandCategory", cat);
                            btnCategory.setText(selectedCategory);
                            user.saveInBackground();
                        }

                    }
                });
                alert.show();


            }
        });

        coverPictureFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureChooser(BROWSE_GALLERY_REQUEST_COVER);
            }
        });
        btnDisable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deactiveParseUser = ParseUser.getCurrentUser().getObjectId();
                // TODO Auto-generated method stub
                imageUploadProgress = new ProgressDialog(getActivity());
                imageUploadProgress.setCancelable(false);
                imageUploadProgress.setMessage("Deactivating Account");
                imageUploadProgress.show();
                if (ParseFacebookUtils.getSession() != null)
                    ParseFacebookUtils.getSession().closeAndClearTokenInformation();

                currentUser.deleteInBackground(new DeleteCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            imageUploadProgress.dismiss();
                            deleteUserData();
                        } else {
                            imageUploadProgress.dismiss();
                            Toast.makeText(getActivity(), "Deactivation Unsuccessfull", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutButtonClicked();
            }
        });

        beaconsetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BeaconSetupActivity.class);
                intent.putExtra("activity", "main");
                startActivity(intent);
            }
        });

        selectpagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GettingFBUserInfo();
                graph();

                //Intent intent = new Intent(getActivity(), OfferActivity.class);
                //intent.putExtra("activity","main");
                //startActivity(intent);
            }
        });
        /*
		 * Earlier had a save button which was replaced by focus change listener.
		 * 
		 * saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ParseUser user = ParseUser.getCurrentUser();
				String b  = userNameView.getText().toString();
				String ph  = userWebsiteView.getText().toString();
				String em  = userEmailView.getText().toString();
				String in  = userAboutView.getText().toString();
				String add = userAddressView.getText().toString();

				user.put("brandName", b);
				user.put("brandEmail", em);
				user.put("brandPhone", ph);
				user.put("brandAbout", in);
				user.put("brandLocation", add);

				ParseUser.getCurrentUser().saveInBackground();

				Log.d("com.enormous.whistleapp",
						"Updated"+user);
			}
		});*/

        return view;
    }


    private void deleteUserData() {
        deleteUserDataDialog = ProgressDialog.show(getActivity(), "", "Deleting User Data", true);
        userDataChatBrandList();
    }

    private void userDataChatBrandList() {
        //ChatBrandList
        ParseQuery userDataChatBrandList = ParseQuery.getQuery("ChatBrandList");
        userDataChatBrandList.whereEqualTo("brandObjectId", deactiveParseUser);
        userDataChatBrandList.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> itemList, ParseException e) {
                if (e == null) {
                    if (itemList.size() > 0) {
                        ParseObject.deleteAllInBackground(itemList, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                deleteUserDataCheckIn();
                            }
                        });
                    } else deleteUserDataCheckIn();
                }
            }
        });
    }

    private void deleteUserDataCheckIn() {
        ParseQuery userDataCheckIn = ParseQuery.getQuery("Checkin");
        userDataCheckIn.whereEqualTo("brandObjectId", deactiveParseUser);
        userDataCheckIn.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> itemList, ParseException e) {
                if (e == null) {
                    if (itemList.size() > 0) {
                        ParseObject.deleteAllInBackground(itemList, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                deleteUserDataItem();
                            }
                        });
                    } else deleteUserDataItem();
                }
            }
        });
    }

    private void deleteUserDataItem() {
        ParseQuery userDataItem = ParseQuery.getQuery("Item");
        userDataItem.whereEqualTo("brandObjectId", deactiveParseUser);
        userDataItem.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> itemList, ParseException e) {
                if (e == null) {
                    if (itemList.size() > 0) {
                        ParseObject.deleteAllInBackground(itemList, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                deleteUserDataProduct();
                            }
                        });
                    } else deleteUserDataProduct();
                }
            }
        });
    }

    private void deleteUserDataProduct() {
        ParseQuery userDataProduct = ParseQuery.getQuery("Product");
        userDataProduct.whereEqualTo("brandObjectId", deactiveParseUser);
        userDataProduct.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> itemList, ParseException e) {
                if (e == null) {
                    if (itemList.size() > 0) {
                        ParseObject.deleteAllInBackground(itemList, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                deleteUserSharedOffer();
                            }
                        });
                    } else deleteUserSharedOffer();
                }
            }
        });
    }

    private void deleteUserSharedOffer() {
        ParseQuery userDataSharedOffer = ParseQuery.getQuery("SharedOffer");
        userDataSharedOffer.whereEqualTo("brandObjectId", deactiveParseUser);
        userDataSharedOffer.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> itemList, ParseException e) {
                if (e == null) {
                    if (itemList.size() > 0) {
                        ParseObject.deleteAllInBackground(itemList, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                deleteUserCartItems();
                            }
                        });
                    } else deleteUserCartItems();
                }
            }
        });
    }

    private void deleteUserCartItems() {
        ParseQuery userDataShoppingCart = ParseQuery.getQuery("ShoppingCart");
        userDataShoppingCart.whereEqualTo("brandObjectId", deactiveParseUser);
        userDataShoppingCart.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> itemList, ParseException e) {
                if (e == null) {
                    if (itemList.size() > 0) {
                        ParseObject.deleteAllInBackground(itemList, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                deleteUserDataDialog.dismiss();
                                ParseUser.logOut();
                                Toast.makeText(getActivity(), "Deactivated Successfully", Toast.LENGTH_SHORT).show();
                                startLoginActivity();
                            }
                        });
                    } else {
                        deleteUserDataDialog.dismiss();
                        ParseUser.logOut();
                        Toast.makeText(getActivity(), "Deactivated Successfully", Toast.LENGTH_SHORT).show();
                        startLoginActivity();
                    }
                }
            }
        });
    }


    private void graph1(String ix) {

        Session session = Session.getActiveSession();

		/* make the API call */
        new Request(
                session,
                ix,

                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        JSONObject userProfile = new JSONObject();
                        GraphObject user = response.getGraphObject();
                        JSONObject json = user.getInnerJSONObject();
                        try {
                            userProfile.put("facebookId", user.getProperty("id"));
                            userProfile.put("name", user.getProperty("name"));

                            if (user.getProperty("cover") != null) {
                                JSONObject data1;
                                try {
                                    data1 = json.getJSONObject("cover");
                                    Log.d("com.enormous.whistleapp", data1.toString());

                                    Coverurl = new String(data1.getString("source"));
                                    Log.d("com.enormous.whistleapp", Coverurl.toString());

                                } catch (JSONException e) {
                                    //
                                    e.printStackTrace();
                                }
                            }

                            if (user.getProperty("about") != null) {
                                userProfile.put("about",
                                        (String) user.getProperty("about"));
                            }
                            if (user.getProperty("website") != null) {
                                userProfile.put("website",
                                        (String) user.getProperty("website"));
                            }
                            if (user.getProperty("link") != null) {
                                userProfile.put("link", (String) user.getProperty("link"));
                            }
                            if (user.getProperty("phone") != null) {
                                userProfile.put("phone", (String) user.getProperty("phone"));
                            }
                            if (user.getProperty("location") != null) {
                                JSONObject data2;
                                try {
                                    data2 = json.getJSONObject("location");
                                    Log.d("com.enormous.whistleapp", data2.toString());

                                    String city = new String(data2.getString("city"));
                                    String country = new String(data2.getString("country"));
                                    Log.d("com.enormous.whistleapp", city.toString());
                                    userProfile.put("brandLocation", city + ", " + country);


                                } catch (JSONException e) {
                                    //
                                    e.printStackTrace();
                                }

                            }
                            ParseUser currentUser = ParseUser
                                    .getCurrentUser();

                            currentUser.put("profile", userProfile);
                            currentUser.saveInBackground();

                            // Show the user info
                            updateViewsWithProfileInfo();


                        } catch (JSONException e) {
                            Log.d("com.enormous.whistleapp",
                                    "Error parsing returned user data.");
                        }
                        if (Coverurl != null) {
                            AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
                                protected Bitmap doInBackground(Void... p) {
                                    Bitmap bm = null;
                                    try {
                                        URL aURL = new URL(Coverurl);
                                        URLConnection conn = aURL.openConnection();
                                        conn.setUseCaches(true);
                                        conn.connect();
                                        InputStream is = conn.getInputStream();
                                        BufferedInputStream bis = new BufferedInputStream(is);
                                        bm = BitmapFactory.decodeStream(bis);
                                        bis.close();
                                        is.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return bm;
                                }

                                protected void onPostExecute(Bitmap bmp) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                                    byte[] data2 = stream.toByteArray();
                                    ParseFile file = new ParseFile("brandCoverPicture.jpg", data2);
                                    ParseUser user = ParseUser.getCurrentUser();
                                    user.put("brandCoverPicture", file);
                                    user.saveInBackground();
                                    userCoverView.setImageBitmap(bmp);

                                }
                            };
                            t.execute();
                        }
                    }
                }
        ).executeAsync();


        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("height", "200");
        params.putString("type", "normal");
        params.putString("width", "200");
        new Request(
                session,
                "/" + ix + "/picture",
                params,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        Log.d("com.enormous.whistleapp",
                                "  " + response);
                        GraphObject user = response.getGraphObject();
                        JSONObject json = user.getInnerJSONObject();
                        JSONObject data1;
                        try {
                            data1 = json.getJSONObject("data");
                            Log.d("com.enormous.whistleapp", data1.toString());

                            Photourl = new String(data1.getString("url"));
                            Log.d("com.enormous.whistleapp", Photourl.toString());

                        } catch (JSONException e) {
                            //
                            e.printStackTrace();
                        }

                        if (Photourl != null) {
                            AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
                                protected Bitmap doInBackground(Void... p) {
                                    Bitmap bm = null;
                                    try {
                                        URL aURL = new URL(Photourl);
                                        URLConnection conn = aURL.openConnection();
                                        conn.setUseCaches(true);
                                        conn.connect();
                                        InputStream is = conn.getInputStream();
                                        BufferedInputStream bis = new BufferedInputStream(is);
                                        bm = BitmapFactory.decodeStream(bis);
                                        bis.close();
                                        is.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return bm;
                                }

                                protected void onPostExecute(Bitmap bmp) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                                    byte[] data2 = stream.toByteArray();
                                    ParseFile file = new ParseFile("brandProfilePicture.jpg", data2);
                                    ParseUser user = ParseUser.getCurrentUser();
                                    user.put("profilepic", file);
                                    user.saveInBackground();
                                    //circular profile pic
                                    //Bitmap circleBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                                    //BitmapShader shader = new BitmapShader (bmp,  TileMode.CLAMP, TileMode.CLAMP);
                                    //Paint paint = new Paint();
                                    //        paint.setShader(shader);

                                    //Canvas c = new Canvas(circleBitmap);
                                    //c.drawCircle(bmp.getWidth()/2, bmp.getHeight()/2, bmp.getWidth()/2, paint);
                                    userProfilePictureView.setImageBitmap(bmp);

                                }
                            };
                            t.execute();


                        }


                    }
                }
        ).executeAsync();

    }


    public void showPictureChooser(final int choose) {
        Builder builder = new Builder(getActivity());
        View customView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picture_chooser, null);
        ListView optionsListView = (ListView) customView.findViewById(R.id.dialogListView);
        DialogListViewAdapter adapter = new DialogListViewAdapter(getActivity());
        optionsListView.setAdapter(adapter);
        builder.setView(customView);

        //create and show dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        //set onItemClick listener on options listview
        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adv, View v,
                                    int pos, long arg3) {
                if (pos == 0) {
                    if (choose == BROWSE_GALLERY_REQUEST_PROFILE) {
                        captureImageUsingCamera(BROWSE_GALLERY_REQUEST_PROFILE);
                    } else {
                        captureImageUsingCamera(BROWSE_GALLERY_REQUEST_COVER);
                    }
                }
                if (pos == 1) {
                    //fire image picker intent to choose existing picture
                    if (choose == BROWSE_GALLERY_REQUEST_PROFILE) {
                        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        pictureIntent.setType("image/*");
                        startActivityForResult(pictureIntent, CHOOSE_GALLERY);
                    } else {
                        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        pictureIntent.setType("image/*");
                        startActivityForResult(pictureIntent, CHOOSE_GALLERY_COVER);
                    }
                }

                dialog.dismiss();
            }
        });
    }

    public void captureImageUsingCamera(int i) {
        Intent pictureIntent = null;
        Uri profilePicUri = null;
        if (i == BROWSE_GALLERY_REQUEST_PROFILE) {
            try {
                //fire camera intent to take picture
                pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //save image to external cache dir
                File imagesFolder = new File(MainScreenActivity.EXTERNAL_CACHE_DIR.getAbsolutePath() + "/images");
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdirs();
                }
                File tempFile = new File(imagesFolder.getAbsolutePath() + "/" + "profile_picture.jpg");
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                profilePicUri = Uri.fromFile(tempFile);
                //tell the intent to save the image in order to get full resoulution photo
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, profilePicUri);
                startActivityForResult(pictureIntent, CHOOSE_CAMERA);
            } catch (Exception e) {
                Log.d("TEST", "taking picture using camera failed: " + e.getMessage());
            }
        } else {
            try {
                //fire camera intent to take picture
                pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //save image to external cache dir
                File imagesFolder = new File(MainScreenActivity.EXTERNAL_CACHE_DIR.getAbsolutePath() + "/images");
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdirs();
                }
                File tempFile = new File(imagesFolder.getAbsolutePath() + "/" + "cover_picture.jpg");
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                profilePicUri = Uri.fromFile(tempFile);
                //tell the intent to save the image in order to get full resoulution photo
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, profilePicUri);
                startActivityForResult(pictureIntent, CHOOSE_CAMERA_COVER);
            } catch (Exception e) {
                Log.d("TEST", "taking picture using camera failed: " + e.getMessage());
            }

        }
    }

    class DialogListViewAdapter extends BaseAdapter {
        String[] options;
        Bitmap[] bitmaps;

        public DialogListViewAdapter(Context c) {
            options = new String[]{"Take a new picture", "Choose an existing picture"};
            bitmaps = new Bitmap[]{BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_camera), BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_gallery)};
        }


        @Override
        public int getCount() {
            return options.length;
        }

        @Override
        public Object getItem(int pos) {
            return options[pos];
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }


        @SuppressLint("ViewHolder")
        @Override
        public View getView(int pos, View convertView, ViewGroup container) {
            View row = getActivity().getLayoutInflater().inflate(R.layout.listview_dialog_item, container, false);
            TextView itemName = (TextView) row.findViewById(R.id.itemNameTextView);
            ImageView itemIcon = (ImageView) row.findViewById(R.id.itemIconImageView);
            itemName.setText(options[pos]);
            itemIcon.setImageBitmap(bitmaps[pos]);

            return row;
        }


    }

    private void graph() {
        Session session = Session.getActiveSession();

        if (session == null || !session.isOpened()) {
            //remove button code
            return;
        }

        if (ParseFacebookUtils.getSession() != null) {
            List<String> permissions = session.getPermissions();
            if (!permissions.containsAll(PERMISSIONS)) {
                session.addCallback(new StatusCallback() {

                    @Override
                    public void call(Session session, SessionState state,
                                     Exception exception) {
                        List<String> permissions = session.getPermissions();
                        Log.e("ddd", "CALLBACK Permissions: " + Arrays.toString(permissions.toArray()));
                        if (exception != null)
                            Log.e("ddd", "EXCEPTION: " + exception.getMessage());
                    }

                });

                requestPublishPermissions(this, session, PERMISSIONS, REAUTH_ACTIVITY_CODE);
                return;
            } else {
                GetPageId();
            }
        }
        return;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void requestPublishPermissions(MainScreenProfileFragment mainScreenProfileFragment, Session session, List<String> permissions,
                                                 int requestCode) {
        if (session != null) {
            Session.NewPermissionsRequest reauthRequest = new Session.NewPermissionsRequest(mainScreenProfileFragment, permissions)
                    .setRequestCode(requestCode);
            session.requestNewPublishPermissions(reauthRequest);
        }
    }

    private void updateViewsWithProfileInfo() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.get("profile") != null) {

            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {
                //if (userProfile.getString("facebookId") != null) {
                //	String facebookId = userProfile.get("facebookId")
                //			.toString();
                //	userProfilePictureView.setProfileId(facebookId);
                //} else {
                // Show the default, blank user profile picture
                //	userProfilePictureView.setProfileId(null);
                //}

                if (userProfile.getString("name") != null) {
                    brandNameS = userProfile.getString("name");
                    userNameView.setText(brandNameS);
                    currentUser.put("brandName", userProfile.getString("name"));
                    currentUser.put("searchBrandName", userProfile.getString("name").toLowerCase().replaceAll("[^a-z0-9]", ""));
                }
                if (userProfile.getString("website") != null) {
                    brandWebsiteS = userProfile.getString("website");
                    userWebsiteView.setText(brandWebsiteS);
                    currentUser.put("brandWebsite", userProfile.getString("website"));
                }
                if (userProfile.getString("about") != null) {
                    brandAboutS = userProfile.getString("about");
                    userAboutView.setText(brandAboutS);
                    currentUser.put("brandAbout", userProfile.getString("about"));
                }
                if (userProfile.getString("phone") != null) {
                    brandPhoneS = userProfile.getString("phone");
                    userPhoneView.setText(brandPhoneS);
                    currentUser.put("brandPhone", userProfile.getString("phone"));
                }
                if (userProfile.getString("brandLocation") != null) {
                    brandLocationS = userProfile.getString("brandLocation");
                    userAddressAutoCompleteView.setText(brandLocationS);
                    currentUser.put("brandLocation", userProfile.getString("brandLocation"));
                    currentUser.put("searchBrandLocation", userProfile.getString("brandLocation").toLowerCase().replaceAll("[^a-z0-9]", ""));
                }

            } catch (JSONException e) {
                Log.d("com.enormous.whistleapp",
                        "Error parsing saved user data.");
            }

            currentUser.saveInBackground();
        }
    }

    private void onLogoutButtonClicked() {
        imageUploadProgress = new ProgressDialog(getActivity());
        imageUploadProgress.setCancelable(true);
        imageUploadProgress.setMessage("Logging Out");
        imageUploadProgress.show();
        String b = userNameView.getText().toString();
        String ph = userPhoneView.getText().toString();
        String em = userEmailView.getText().toString();
        String in = userAboutView.getText().toString();
        String add = userAddressAutoCompleteView.getText().toString();
        String web = userWebsiteView.getText().toString();

        currentUser.put("brandWebsite", web);
        currentUser.put("brandName", b);
        currentUser.put("brandEmail", em);
        currentUser.put("brandPhone", ph);
        currentUser.put("brandAbout", in);
        currentUser.put("brandLocation", add);
        currentUser.put("searchBrandLocation", add.toLowerCase().replaceAll("[^a-z0-9]", ""));
        currentUser.put("searchBrandName", b.toLowerCase().replaceAll("[^a-z0-9]", ""));

        if (ParseFacebookUtils.getSession() != null)
            ParseFacebookUtils.getSession().closeAndClearTokenInformation();


        currentUser.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                imageUploadProgress.dismiss();
                Log.d("com.enormous.whistleapp",
                        "Updated " + currentUser);
                // Log the user out
                ParseUser.logOut();
                // Go to the login view
                Log.d("com.enormous.whistleapp",
                        "user logged out ");
                startLoginActivity();

            }
        });
    }

    //retrieve images chosen/taken by user
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REAUTH_ACTIVITY_CODE) {
                Session session = Session.getActiveSession();
                if (session != null) {
                    session.onActivityResult(getActivity(), requestCode, resultCode, data);
                    GetPageId();
                }
            } else if (requestCode == CHOOSE_GALLERY) {
                Bitmap bitmap = null;
                int reqWidth = (int) getResources().getDimension(R.dimen.profilepic_thumbnail_height);
                int reqHeight = (int) getResources().getDimension(R.dimen.profilepic_thumbnail_height);
                imageUploadProgress = new ProgressDialog(getActivity());
                imageUploadProgress.setCancelable(true);
                imageUploadProgress.setMessage("Uploading your Profile picture...");
                imageUploadProgress.show();
                File imagesFolder = new File(MainScreenActivity.EXTERNAL_CACHE_DIR.getAbsolutePath() + "/images");
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdirs();
                }
                File tempFile = new File(imagesFolder.getAbsolutePath() + "/" + "profile_picture.jpg");
                Uri imageUri = data.getData();
                InputStream is = null;
                OutputStream os = null;
                try {
                    is = getActivity().getContentResolver().openInputStream(imageUri);
                    os = new FileOutputStream(tempFile);
                    Utils.copyStream(is, os);
                    bitmap = Utils.decodeImageFile(tempFile, reqWidth, reqHeight);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                userProfilePictureView.setImageBitmap(bitmap);
                //compress image
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(CompressFormat.JPEG, 70, baos);

                //upload image to parse
                ParseFile parseFile = new ParseFile("brandProfilePicture.jpg", baos.toByteArray());
                ParseUser.getCurrentUser().put("brandProfilePicture", parseFile);
                parseFile.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {

                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("TEST", "Profile pic uploaded successfully");
                                } else {
                                    Log.d("TEST", "Profile pic upload FAILED");
                                    Toast.makeText(getActivity(), "Upload Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                imageUploadProgress.dismiss();
                            }
                        });
                    }
                });

            } else if (requestCode == CHOOSE_CAMERA) {
                //set up progress dialog
                imageUploadProgress = new ProgressDialog(getActivity());
                imageUploadProgress.setCancelable(true);
                imageUploadProgress.setMessage("Uploading your Profile picture...");
                imageUploadProgress.show();
                Bitmap bitmap = null;
                int reqWidth = (int) getResources().getDimension(R.dimen.profilepic_thumbnail_height);
                int reqHeight = (int) getResources().getDimension(R.dimen.profilepic_thumbnail_height);
                File imagesFolder = new File(MainScreenActivity.EXTERNAL_CACHE_DIR.getAbsolutePath() + "/images");
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdirs();
                }
                File tempFile = new File(imagesFolder.getAbsolutePath() + "/" + "profile_picture.jpg");
                //BITMAP = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                bitmap = Utils.decodeImageFile(tempFile, reqWidth, reqHeight);
                userProfilePictureView.setImageBitmap(bitmap);

                //compress image
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(CompressFormat.JPEG, 70, baos);

                //upload image to parse
                ParseFile parseFile = new ParseFile("brandProfilePicture.jpg", baos.toByteArray());
                ParseUser.getCurrentUser().put("brandProfilePicture", parseFile);
                parseFile.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {

                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("TEST", "Profile pic uploaded successfully");
                                } else {
                                    Log.d("TEST", "Profile pic upload FAILED");
                                    Toast.makeText(getActivity(), "Upload Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                imageUploadProgress.dismiss();
                            }
                        });
                    }
                });
            } else {
                //set up progress dialog
                imageUploadProgress = new ProgressDialog(getActivity());
                imageUploadProgress.setCancelable(true);
                imageUploadProgress.setMessage("Uploading your Cover picture...");
                imageUploadProgress.show();

                Bitmap bitmap = null;
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                double reqWidth = 640.0;
                double reqHeight = 640.0;


                if (requestCode == CHOOSE_CAMERA_COVER) {
                    File imagesFolder = new File(MainScreenActivity.EXTERNAL_CACHE_DIR.getAbsolutePath() + "/images");
                    if (!imagesFolder.exists()) {
                        imagesFolder.mkdirs();
                    }
                    File tempFile = new File(imagesFolder.getAbsolutePath() + "/" + "cover_picture.jpg");
                    //BITMAP = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                    bitmap = Utils.decodeImageFile(tempFile, reqWidth, reqHeight);
                    userCoverView.setImageBitmap(bitmap);
                }
                if (requestCode == CHOOSE_GALLERY_COVER) {
                    File imagesFolder = new File(MainScreenActivity.EXTERNAL_CACHE_DIR.getAbsolutePath() + "/images");
                    if (!imagesFolder.exists()) {
                        imagesFolder.mkdirs();
                    }
                    File tempFile = new File(imagesFolder.getAbsolutePath() + "/" + "cover_picture.jpg");
                    Uri imageUri = data.getData();
                    InputStream is = null;
                    OutputStream os = null;
                    try {
                        is = getActivity().getContentResolver().openInputStream(imageUri);
                        os = new FileOutputStream(tempFile);
                        Utils.copyStream(is, os);
                        bitmap = Utils.decodeImageFile(tempFile, reqWidth, reqHeight);

                        //bitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (os != null) {
                            try {
                                os.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                userCoverView.setImageBitmap(bitmap);
                //compress image
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(CompressFormat.JPEG, 50, baos);

                //upload image to parse
                ParseFile parseFile = new ParseFile("brandCoverPicture.jpg", baos.toByteArray());
                ParseUser.getCurrentUser().put("brandCoverPicture", parseFile);
                parseFile.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {

                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("TEST", "Cover pic uploaded successfully");
                                } else {
                                    Log.d("TEST", "Cover pic upload FAILED");
                                    Toast.makeText(getActivity(), "Upload Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                imageUploadProgress.dismiss();
                            }
                        });
                    }
                });
            }
        }
    }

    private void startLoginActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

	/*	private void checkForCrashes() {
		CrashManager.register(getActivity(), "key");
	}
	private void checkForUpdates() {
		// Remove this for store builds!
		UpdateManager.register(getActivity(), "key");
	}
	 */


    private void GetPageId() {
        Session session = Session.getActiveSession();


        if (session != null) {
            Log.d("FB", "groupToken request!");
            Request myreq = Request.newGraphPathRequest(session, "me/accounts",
                    new Request.Callback() {

                        private String[] mStringsIDs;
                        private String[] mStrings;

                        @Override
                        public void onCompleted(Response response) {
                            Log.d("FB", "received token");
                            GraphObject obj = response.getGraphObject();
                            JSONObject json = obj.getInnerJSONObject();
                            try {
                                //String token = null;

                                JSONArray data = json.getJSONArray("data");

                                mStrings = new String[data.length()];
                                mStringsIDs = new String[data.length()];

                                //Log.d("FB", frag.getAcc().getLogin());
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject acc = (JSONObject) data.get(i);
                                    mStrings[i] = new String(acc.getString("name"));
                                    mStringsIDs[i] = new String(acc.getString("id"));
                                    Log.d("FB", acc.getString("id"));
                                    Log.d("FB", acc.getString("name"));
                                    //id = acc.getString("id");
                                    //token = acc.getString("access_token");
                                    //}
                                }
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setTitle("Select Your FB Page");
                                //for(int i=0;i < mStrings.length ; i++)
                                //{ alert.setMessage(mStrings[i]);}
                                alert.setItems(mStrings, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        id = mStringsIDs[which];
                                        graph1(id);
                                        // The 'which' argument contains the index position
                                        // of the selected item

                                    }
                                });
                                alert.show();
                                //Log.d("FB Token", token);

                                //executeRequest(token);
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }

                    });
            myreq.executeAsync();
        } else {
            Log.d("FB", "Session is closed");
        }

    }


    private void GettingFBUserInfo() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Create a JSON object to hold the profile info
                            JSONObject userProfile = new JSONObject();
                            try {
                                // Populate the JSON object
                                //userProfile.put("facebookId", user.getId());
                                //userProfile.put("name", user.getName());
                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser
                                        .getCurrentUser();
                                if (user.getProperty("email") != null) {
                                    userProfile.put("email", (String) user.getProperty("email"));
                                }

                                currentUser.put("logininfo", userProfile);
                                currentUser.saveInBackground();
                                updateViewsWithUserInfo();
                            } catch (JSONException e) {
                                Log.d("com.enormous.whistleapp",
                                        "Error parsing returned user data.");
                            }

                        } else if (response.getError() != null) {
                            if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
                                    || (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
                                Log.d("com.enormous.whistleapp",
                                        "The facebook session was invalidated.");
                                onLogoutButtonClicked();
                            } else {
                                Log.d("com.enormous.whistleapp",
                                        "Some other error: "
                                                + response.getError()
                                                .getErrorMessage());
                            }
                        }
                    }
                });
        request.executeAsync();

    }

    private void updateViewsWithUserInfo() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.get("logininfo") != null) {

            JSONObject userProfile = currentUser.getJSONObject("logininfo");
            try {
                if (userProfile.getString("email") != null) {
                    brandEmailS = userProfile.getString("email");
                    userEmailView.setText(brandEmailS);
                    currentUser.put("brandEmail", userProfile.getString("email"));
                }

            } catch (JSONException e) {
                Log.d("com.enormous.whistleapp",
                        "Error parsing saved user data.");
            }
            currentUser.saveInBackground();
        }
    }

    public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

        ImageView imageView = null;

        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image((String) imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        private Bitmap download_Image(String url) {

            Bitmap bmp = null;
            try {
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)
                    return bmp;

            } catch (Exception e) {
            }
            return bmp;
        }
    }

    class GetPlaces extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        // three dots is java for an array of strings
        protected ArrayList<String> doInBackground(String... args) {

            Log.d("gottaGo", "doInBackground");

            ArrayList<String> predictionsArr = new ArrayList<String>();

            try {

                URL googlePlaces = new URL(
                        // URLEncoder.encode(url,"UTF-8");
                        "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + URLEncoder.encode(userAddressAutoCompleteView.getText().toString(), "UTF-8") + "&types=establishment&language=en&sensor=true&key=AIzaSyB3iHBPT4rstXdka5dITdJO2fKs9we5mQ8");
                URLConnection tc = googlePlaces.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        tc.getInputStream()));

                String line;
                StringBuffer sb = new StringBuffer();
                //take Google's legible JSON and turn it into one big string.
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                //turn that string into a JSON object
                JSONObject predictions = new JSONObject(sb.toString());
                //now get the JSON array that's inside that object
                JSONArray ja = new JSONArray(predictions.getString("predictions"));

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = (JSONObject) ja.get(i);
                    //add each entry to our array
                    predictionsArr.add(jo.getString("description"));
                }
            } catch (IOException e) {

                Log.e("YourApp", "GetPlaces : doInBackground", e);

            } catch (JSONException e) {

                Log.e("YourApp", "GetPlaces : doInBackground", e);

            }

            return predictionsArr;

        }

        //then our post

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            Log.d("YourApp", "onPostExecute : " + result.size());
            //update the adapter
            addressAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.item_list);
            addressAdapter.setNotifyOnChange(true);
            //attach the adapter to textview
            userAddressAutoCompleteView.setAdapter(addressAdapter);

            for (String string : result) {

                Log.d("YourApp", "onPostExecute : result = " + string);
                addressAdapter.add(string);
                addressAdapter.notifyDataSetChanged();
            }

            //Log.d("YourApp", "onPostExecute : autoCompleteAdapter" + adapter.getCount());

        }

    }

	/*
	 * Get Latitude and Longitude for a given Place.
	 * For our case we get Lat and Long for the saved Address
	 *
	*/

    class GetPlacesLatLong extends AsyncTask<String, Void, ArrayList<Double>> {
        @Override
        // three dots is java for an array of strings
        protected ArrayList<Double> doInBackground(String... args) {

            Log.d("gottaGo", "doInBackground");

            ArrayList<Double> Location = new ArrayList<Double>();
            double lat = 0, lng = 0;
            try {

                URL googlePlaces = new URL(
                        // URLEncoder.encode(url,"UTF-8");
                        "http://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(userAddressAutoCompleteView.getText().toString(), "UTF-8") + "");
                URLConnection tc = googlePlaces.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        tc.getInputStream()));

                String line = "";
                StringBuffer sb = new StringBuffer();
                //take Google's legible JSON and turn it into one big string.
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                //turn that string into a JSON object
                JSONObject results = new JSONObject(sb.toString());
                lng = ((JSONArray) results.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                lat = ((JSONArray) results.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                Location.add(lat);
                Location.add(lng);
            } catch (IOException e) {

                Log.e("YourApp", "GetPlaces : doInBackground", e);

            } catch (JSONException e) {

                Log.e("YourApp", "GetPlaces : doInBackground", e);

            }

            return Location;

        }

        //then our post

        @Override
        protected void onPostExecute(ArrayList<Double> result) {

            Log.d("YourApp", "onPostExecute : " + result.size());
            double latitude = 0, longitude = 0;
            if (result.size() == 2) {
                latitude = result.get(0);
                longitude = result.get(1);
                ParseGeoPoint brandGeoPoint = new ParseGeoPoint(latitude, longitude);
                currentUser.put("brandGeoPoint", brandGeoPoint);
                currentUser.saveInBackground();
            }
        }

    }
}
