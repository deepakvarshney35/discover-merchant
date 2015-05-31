package com.enormous.pkpizzas.publisher.activities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enormous.pkpizzas.publisher.adapter.SpinnerInfo;
import com.enormous.pkpizzas.publisher.data.Product;
import com.enormous.pkpizzas.publisher.data.Utils;
import com.enormous.pkpizzas.publisher.fragments.MainScreenAddItemsFragment;
import com.enormous.pkpizzas.publisher.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddItemsActivity extends ListActivity implements OnItemClickListener {

	//TAG for LOGCAT
	private String TAG = "tag.AddItemActivity";

	//Static
	public static ArrayList<Product> products;
	private static final int BROWSE_GALLERY_REQUEST = 2;

	//Views
	private TextView mapLocationInfoText;
	private TextView itemDescText;
	private TextView itemNameText;
	private TextView itemUrlText;
	private TextView itemType;
	private TextView ItemDesc;
	private TextView ItemName;
	private TextView productsHead;
	private TextView itemURL;
	private ImageView Photo;
	private AutoCompleteTextView Map;
	private ImageView productadd;
	private ListView productlist;
	private TextView uploadedItemName;
	private TextView uploadedItemSize;
	private TextView uploadedPhotoName;
	private View layout;

	//Layouts
	private LinearLayout layoutAboveAll;
	private LinearLayout uploadedItem;
	private LinearLayout productsLayout;
	private PopupWindow popup;
	private ImageView imageDelete;
	private ImageView uploadedPhotoDelete;
	private ImageView uploadedPhoto;
	private RelativeLayout uploadedPhotoPreview;
	private FrameLayout mapFrameLayout;
	private FrameLayout urlFrameLayout;

	//Buttons
	private ImageButton colord;
	private Button colorButton0;
	private Button colorButton1;
	private Button colorButton2;
	private Button colorButton3;
	private Button colorButton4;
	private Button colorButton5;
	private Button colorButton6;
	private Button colorButton7;
	private FrameLayout saveItem;
	private FrameLayout discardItem;

	//Parse
	private ParseFile file;

	//Adapters
	private ProductListAdapter listAdapter;
	private ArrayAdapter<String> mapadapter;

	//Progress Bars
	private ProgressBar progressPhoto;
	private ProgressDialog progress;

	//Color
	private Integer colorId = -5278736; 
	private int USER_COLOR;
	private int lastcolor=100;
	private  View[] colorButtons = new View[10];
	int[] colors = new int[10];

	//Misc
	public boolean filePresent = false;
	public static int check = 0;
	String maptext="";
	String url = "";
	String itemObjectId;
	private int position=0;
	private String itemTypeText = "";
	private int SAVE_TAG = 1;
	boolean saveAndFinish = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//set Content View
		setContentView(R.layout.activity_items);

		//Hide Action Bar
		getActionBar().hide();

		//findViews
		findViews();

		//Loading Layout
		layoutAboveAll.setVisibility(View.VISIBLE);

		//Products List Adapter
		productlist.setAdapter(listAdapter);
		//Retrieve data from Intent
		Intent i = getIntent();
		itemObjectId = i.getStringExtra("itemObjectId");
		itemTypeText = i.getStringExtra("OFFERTYPE");
		position = i.getIntExtra("position", 0);

		//Set Offer Type.
		itemType.setText(itemTypeText);

		//Based on type of offer enable/disable fields
		if("Map".equals(itemTypeText)){
			//uploadDocument.setVisibility(View.GONE);
			Photo.setVisibility(View.GONE);
			urlFrameLayout.setVisibility(View.GONE);
			ItemName.setHint("Map Title");
			ItemDesc.setHint("Map Description");
			itemNameText.setText("Map Title");
			itemDescText.setText("Map Description");
			mapFrameLayout.setVisibility(View.VISIBLE);
			productadd.setVisibility(View.GONE);
			productlist.setVisibility(View.GONE);
			uploadedItem.setVisibility(View.GONE);
			productsHead.setVisibility(View.GONE);
			layoutAboveAll.setVisibility(View.GONE);
			progressPhoto.setVisibility(View.GONE);
		}
		else if ("Products".equals(itemTypeText))
		{
			ItemName.setHint("Product Collection Title");
			ItemDesc.setHint("Product Collection Description");
			itemNameText.setText("Product Collection Title");
			itemDescText.setText("Product Collection Description");
			mapFrameLayout.setVisibility(View.GONE);
			urlFrameLayout.setVisibility(View.GONE);
			productadd.setVisibility(View.VISIBLE);
			//uploadDocument.setVisibility(View.GONE);
			Photo.setVisibility(View.GONE);
			ListViewFunction();
			productsLayout.setVisibility(View.VISIBLE);
			productlist.setVisibility(View.VISIBLE);
			uploadedItem.setVisibility(View.GONE);
			progressPhoto.setVisibility(View.GONE);
		}
		else if ("URL".equals(itemTypeText)){
			ItemName.setHint("URL Title");
			itemNameText.setText("URL Title");
			ItemDesc.setHint("URL Description");
			itemDescText.setText("URL Description");
			//uploadDocument.setVisibility(View.GONE);
			mapFrameLayout.setVisibility(View.GONE);
			urlFrameLayout.setVisibility(View.VISIBLE);
			productadd.setVisibility(View.GONE);
			productlist.setVisibility(View.GONE);
			uploadedItem.setVisibility(View.GONE);
			productsHead.setVisibility(View.GONE);
			layoutAboveAll.setVisibility(View.GONE);
			progressPhoto.setVisibility(View.GONE);
		}else  if ("Offer".equals(itemTypeText)){
			ItemName.setHint("Offer Title");
			itemNameText.setText("Offer Title");
			ItemDesc.setHint("Offer Description");
			itemDescText.setText("Offer Description");
			//uploadDocument.setVisibility(View.GONE);
			mapFrameLayout.setVisibility(View.GONE);
			urlFrameLayout.setVisibility(View.GONE);
			productadd.setVisibility(View.GONE);
			productlist.setVisibility(View.GONE);
			uploadedItem.setVisibility(View.GONE);
			productsHead.setVisibility(View.GONE);
			layoutAboveAll.setVisibility(View.GONE);
			progressPhoto.setVisibility(View.GONE);
		}else
		{	
			ItemName.setHint("Document Title");
			itemNameText.setText("Document Title");
			ItemDesc.setHint("Document Description");
			itemDescText.setText("Document Description");
			//uploadDocument.setVisibility(View.VISIBLE);
			uploadedPhotoPreview.setVisibility(View.GONE);
			uploadedItem.setVisibility(View.GONE);
			//Photo.setVisibility(View.VISIBLE);
			productsHead.setVisibility(View.GONE);
			mapFrameLayout.setVisibility(View.GONE);
			urlFrameLayout.setVisibility(View.GONE);
			productadd.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_attachment));
			productadd.setRotation(270);
			productadd.setVisibility(View.VISIBLE);
			productlist.setVisibility(View.GONE);
			layoutAboveAll.setVisibility(View.GONE);
			
		}

		////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////

		imageDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				uploadedItem.setVisibility(View.GONE);
				uploadedPhotoPreview.setVisibility(View.GONE);
				productadd.setVisibility(View.VISIBLE);
			}
		});
		uploadedPhotoDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uploadedPhoto.setVisibility(View.GONE);
				uploadedPhotoPreview.setVisibility(View.GONE);
				productadd.setVisibility(View.VISIBLE);
			}
		});

		//Google Places ... AutoComplete. 
		mapadapter = new ArrayAdapter<String>(this,R.layout.item_list);
		mapadapter.setNotifyOnChange(true);
		Map.setAdapter(mapadapter);
		Map.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				if(Map.length()==0){
					mapLocationInfoText.setVisibility(View.GONE);
					//brandPhoneText.setAnimation(slide_down);
				}else{
					mapLocationInfoText.setVisibility(View.VISIBLE);
					//brandPhoneText.setAnimation(slide_up);
				}
				if (count%3 == 1) {
					mapadapter.clear();
					GetPlaces task = new GetPlaces();
					//now pass the argument in the textview to the task
					task.execute(Map.getText().toString());
					Log.d("test", "Entered text"+Map.getText().toString());

				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,int after) {

			}

			public void afterTextChanged(final Editable s) {
				maptext= s.toString();
				Map.setError(null);
			}});

		/////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////

		//On Button Click Listener
		saveItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(ItemName.length()==0){
					ItemName.setError("Required");
				} else if (ItemDesc.length()==0){
					ItemDesc.setError("Required");
				} else if("Document".equals(itemTypeText)){
					if(file==null ){
						if(!filePresent){
							Toast.makeText(AddItemsActivity.this, "Add Document before saving.", Toast.LENGTH_LONG).show();
							}else{
								saveItem();
							}
						} else{
						saveItem();
					}
				} else if("Map".equals(itemTypeText)){
					if(Map.length()==0){
						Map.setError("Required");}
					else{
						saveItem();
					}
				}else if("URL".equals(itemTypeText)){
					if(itemURL.length()==0){
						itemURL.setError("Required");}
					else{
						saveItem();
					}
				}else {
					saveItem();
				}
			}
		});

		discardItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(SAVE_TAG==0 ){
					alertMessage();
				} else {
					finish();
				}
			}
		});


		//Change Text
		ItemDesc.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				Log.d(TAG, "item desc textSAVE_TAG ="+SAVE_TAG);
				if(ItemDesc.length()==0){
					itemDescText.setVisibility(View.GONE);
				} else {
					itemDescText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				ItemDesc.setError(null);
			}
		});
		ItemName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				Log.d(TAG, "item name text SAVE_TAG ="+SAVE_TAG);
				if(ItemName.length()==0){
					itemNameText.setVisibility(View.GONE);
				}else {
					itemNameText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				ItemName.setError(null);
			}
		});
		itemURL.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				Log.d(TAG, "item url text SAVE_TAG ="+SAVE_TAG);
				if(itemURL.length()==0){
					itemUrlText.setVisibility(View.GONE);
				} else {
					itemUrlText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				itemURL.setError(null);
			}
		});

		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////

		//Opened Item Activity, if older item then retrieve stuff from parse and display.
		ParseUser user = ParseUser.getCurrentUser();
		if (user != null) {  

			if(!"00000".equals(itemObjectId)){
				//old

				if (MainScreenAddItemsFragment.items.get(position).getName() != null){
					String b = MainScreenAddItemsFragment.items.get(position).getName();
					ItemName.setText(b);
				}
				if (MainScreenAddItemsFragment.items.get(position).getDescription() != null){
					String b1 =MainScreenAddItemsFragment.items.get(position).getDescription();
					ItemDesc.setText( b1);
				}

				itemURL.setText(MainScreenAddItemsFragment.items.get(position).getURL());

				if (MainScreenAddItemsFragment.items.get(position).getMap() != null){
					String b1 =MainScreenAddItemsFragment.items.get(position).getMap();
					Map.setText( b1);
				}
				if (MainScreenAddItemsFragment.items.get(position).getColorId() != 0){
					Integer b2 =MainScreenAddItemsFragment.items.get(position).getColorId();
					//relativespinner.setBackgroundColor(b2);
					colorId=b2;}
				SAVE_TAG = 1;
				Log.d(TAG, "ret SAVE_TAG ="+SAVE_TAG);
				progressPhoto.setVisibility(View.VISIBLE);
				uploadedItem.setVisibility(View.GONE);
				final ParseFile pic = MainScreenAddItemsFragment.items.get(position).getDocument();
				if(pic!=null){
					pic.getDataInBackground(new GetDataCallback() {
						@Override
						public void done(byte[] data, ParseException e) {
							if (e == null) {
								// data has the bytes for the image
								if(data!=null) {
									filePresent = true;
									StringTokenizer tokens = new StringTokenizer(pic.getName(), ".");
									String FileName = tokens.nextToken();
									String fileExt = tokens.nextToken();
									StringTokenizer tok = new StringTokenizer(FileName, "-");
									String fileName = "Document";
									String Extra = "";
									for(int j=0 ; j<15 ; j++){
										if(j<7){
										if(tok.hasMoreTokens()){
											fileName = tok.nextToken();
										}}else{
											if(tok.hasMoreTokens()){
												Extra = tok.nextToken();
											}
										}
									}
									fileName = fileName+Extra;
									if("jpg".equals(fileExt) || "png".equals(fileExt)){
										Bitmap bmp=BitmapFactory.decodeByteArray(data,0,data.length);
										uploadedPhoto.setImageBitmap(bmp);
										//ImageLoader.getInstance().displayImage(ItemsActivity.this, pic.getUrl(), uploadedPhoto, false, 100, 100, 0);
										uploadedPhotoPreview.setVisibility(View.VISIBLE);
										uploadedItem.setVisibility(View.GONE);
										productadd.setVisibility(View.GONE);
										uploadedPhotoName.setText(fileName);
									} else{
										int size = data.length/1024;
										uploadedItem.setVisibility(View.VISIBLE);
										productadd.setVisibility(View.GONE);
										uploadedItemName.setText(fileName);
										uploadedItemSize.setText(""+size+" Kb");
									}
									progressPhoto.setVisibility(View.GONE);
									SAVE_TAG = 1;
									Log.d(TAG, "ret in SAVE_TAG ="+SAVE_TAG);

								}
							}else {
								// something went wrong
								uploadedItem.setVisibility(View.GONE);
								progressPhoto.setVisibility(View.GONE);
								SAVE_TAG = 1;
								Log.d(TAG, "ret in else SAVE_TAG ="+SAVE_TAG);
							}
						}
					});
				}else {
					// something went wrong
					uploadedItem.setVisibility(View.GONE);
					progressPhoto.setVisibility(View.GONE);
					SAVE_TAG = 1;
					Log.d(TAG, "ret in else SAVE_TAG ="+SAVE_TAG);
				}
			}
		}

		productadd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if("Document".equals(itemTypeText)){
					SAVE_TAG = 0;
					browseGallery(BROWSE_GALLERY_REQUEST);
				} else if ("Products".equals(itemTypeText)){
					//SAVE_TAG = 0;
					Intent i = new Intent(AddItemsActivity.this, AddProductActivity.class);
					// sending data to new activity
					i.putExtra("productObjectId", "00000");
					startActivity(i);
				}
			}
		});

		/*		uploadDocument.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SAVE_TAG = 0;
				browseGallery(BROWSE_GALLERY_REQUEST);

			}
		});*/


		colord.setClickable(true);
		colord.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				//v.startAnimation(AnimationUtils.loadAnimation(AddItemActivity.this, R.anim.image_click));
				final Activity context = AddItemsActivity.this;

				int popupWidth = WindowManager.LayoutParams.MATCH_PARENT;
				int popupHeight = -2;

				// Inflate the popup_layout.xml
				LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
				LayoutInflater layoutInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

				/*AlertDialog.Builder alert=new AlertDialog.Builder(ItemsActivity.this);
				alert.setView(layout);
				alert.show();*/
				// Creating the PopupWindow
				popup = new PopupWindow(context);
				popup.setContentView(layout);
				popup.setWidth(popupWidth);
				popup.setHeight(popupHeight);
				//layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				//        LayoutParams.WRAP_CONTENT));
				popup.setFocusable(true);	 		

				// Clear the default translucent background
				popup.setBackgroundDrawable(new BitmapDrawable());

				// Displaying the popup at the specified location.
				popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
				colorButton0 = (Button)layout. findViewById(R.id.color1);
				colorButton1 = (Button)layout. findViewById(R.id.color2);
				colorButton2 = (Button)layout. findViewById(R.id.color3);
				colorButton3 = (Button)layout. findViewById(R.id.color4);
				colorButton4 = (Button)layout. findViewById(R.id.color5);
				colorButton5 = (Button)layout. findViewById(R.id.color6);
				colorButton6 = (Button)layout. findViewById(R.id.color7);
				colorButton7 = (Button)layout. findViewById(R.id.color8);
				colors[0] = getResources().getColor(R.color.paletteColor0);
				colors[1] = getResources().getColor(R.color.paletteColor1);
				colors[2] = getResources().getColor(R.color.paletteColor2);
				colors[3] = getResources().getColor(R.color.paletteColor3);
				colors[4] = getResources().getColor(R.color.paletteColor4);
				colors[5] = getResources().getColor(R.color.paletteColor5);
				colors[6] = getResources().getColor(R.color.paletteColor6);
				colors[7] = getResources().getColor(R.color.paletteColor7);	
				colorButtons[0] = colorButton0;
				colorButtons[1] = colorButton1;
				colorButtons[2] = colorButton2;
				colorButtons[3] = colorButton3;
				colorButtons[4] = colorButton4;
				colorButtons[5] = colorButton5;
				colorButtons[6] = colorButton6;
				colorButtons[7] = colorButton7;
				if(lastcolor!=100){
					colorButtons[lastcolor].setBackground(getResources().getDrawable(R.drawable.color_palette_item_selected));
				}	
			}
		});
	}

	private void findViews() {

		colord = (ImageButton) findViewById(R.id.colorselect);
		uploadedItem = (LinearLayout)findViewById(R.id.uploadedItem);
		uploadedItemName = (TextView)findViewById(R.id.uploadedItemName);
		uploadedItemSize = (TextView)findViewById(R.id.uploadedItemSize);
		layoutAboveAll = (LinearLayout) findViewById(R.id.layoutAboveAll);
		productsHead = (TextView) findViewById(R.id.productsHead);
		itemURL = (TextView)findViewById(R.id.itemURL);
		itemUrlText = (TextView)findViewById(R.id.itemUrlText);
		ItemName = (TextView)findViewById(R.id.itemname);
		itemNameText = (TextView)findViewById(R.id.itemNameText);
		ItemDesc = (TextView)findViewById(R.id.itemdesc);
		itemType = (TextView)findViewById(R.id.itemType);
		itemDescText = (TextView)findViewById(R.id.itemDescText);
		Photo = (ImageView) findViewById(R.id.itemimage);
		Map = (AutoCompleteTextView)findViewById(R.id.maplocationinfo);
		mapLocationInfoText = (TextView)findViewById(R.id.mapLocationInfoText);
		productadd =(ImageView)findViewById(R.id.productaddbutton);
		productlist = (ListView)findViewById(android.R.id.list);
		progressPhoto = (ProgressBar)findViewById(R.id.progressPhoto);
		saveItem = (FrameLayout)findViewById(R.id.saveItem);
		discardItem = (FrameLayout)findViewById(R.id.discardItem);
		productsLayout = (LinearLayout)findViewById(R.id.productsLayout);
		imageDelete = (ImageView) findViewById(R.id.imageDelete);
		uploadedPhotoDelete = (ImageView) findViewById(R.id.uploadedPhotoDelete);
		uploadedPhoto = (ImageView)findViewById(R.id.uploadedPhoto);
		uploadedPhotoPreview = (RelativeLayout)findViewById(R.id.uploadedPhotoPreview);
		uploadedPhotoName = (TextView)findViewById(R.id.uploadedPhotoName);
		mapFrameLayout = (FrameLayout)findViewById(R.id.mapFrameLayout);
		urlFrameLayout = (FrameLayout)findViewById(R.id.urlFrameLayout);
		ItemDesc.setSingleLine(false);
		ItemName.setSingleLine(false);
		Map.setSingleLine(false);

	}
	void ListViewFunction(){

		layoutAboveAll.setVisibility(View.VISIBLE);

		ParseUser user = ParseUser.getCurrentUser();
		if (user != null) {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
			query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
			query.addAscendingOrder("createdAt");
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> itemList, ParseException e) {
					if (e == null) {
						products = new ArrayList<Product>();
						for(ParseObject item : itemList){
							products.add(new Product(item.getString("brandObjectId"), item.getString("productType"), item.getString("productName"), item.getInt("productCost"), item.getObjectId(),item.getString("productDescription"),item.getInt("productShippingCost"),item.getInt("productTax"),item.getInt("productStock"),(ArrayList<String>) item.get("productOptions"),item.getParseFile("productPicture").getUrl(),(ArrayList<Integer>) item.get("productOptionsCost")));
						}
						listAdapter = new ProductListAdapter(AddItemsActivity.this, products);
						layoutAboveAll.setVisibility(View.GONE);
						setListAdapter(listAdapter);
						if(itemList.size()!=0){
							productsLayout.setVisibility(View.VISIBLE);
							productsHead.setVisibility(View.VISIBLE);}

					} else {
						Log.d("Item", "Error: " + e.getMessage());
					}
				}
			});

			getListView().setOnItemClickListener(this);
		}


	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View vi, int CurrentSelectedItem,long arg3) {         
		// Launching new Activity on selecting single List Item
		//SAVE_TAG = 0;
		Intent i = new Intent(AddItemsActivity.this, AddProductActivity.class);
		// sending data to new activity
		i.putExtra("productObjectId", products.get(CurrentSelectedItem).getProductObjectId());//getProductObjectId());
		i.putExtra("position", CurrentSelectedItem);
		startActivity(i);
	}
	@Override
	public void onBackPressed() {
		if(SAVE_TAG==0 )	{
			alertMessage();}
		else {
			finish();
		}
	}
	@Override
	public void onResume() {
		super.onResume();

		//Portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		if(check!=0)
		{ListViewFunction();
		check=0;}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if(SAVE_TAG==0 )	{
				alertMessage();}
			else
			{finish();}
			return true;
		case R.id.save:
			if(ItemName.length()==0){
				ItemName.setError("Required");
			} else if (ItemDesc.length()==0){
				ItemDesc.setError("Required");
			} else if("Document".equals(itemTypeText)){
				if(file==null ){
					if(!filePresent){
						Toast.makeText(AddItemsActivity.this, "Add Document before saving.", Toast.LENGTH_LONG).show();
						}else{
							saveItem();
						}
				} else{
					saveItem();
				}
			} else if("Map".equals(itemTypeText)){
				if(Map.length()==0){
					Map.setError("Required");}
				else{
					saveItem();
				}
			}else if("URL".equals(itemTypeText)){
				if(itemURL.length()==0){
					itemURL.setError("Required");}
				else{
					saveItem();
				}
			}else {
				saveItem();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void alertMessage() {
		/*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View customDialog = getActivity().getLayoutInflater().inflate(R.layout.dialog_deactivate_account, null);
        Button yesButton = (Button) customDialog.findViewById(R.id.yesButton);
        Button noButton = (Button) customDialog.findViewById(R.id.noButton);
        final LinearLayout progressLinearLayout = (LinearLayout) customDialog.findViewById(R.id.progressLinearLayout);
        builder.setView(customDialog);
        final Dialog dialog = builder.create();
        dialog.show();*/
		new AlertDialog.Builder(this)
		.setTitle("Items not saved!")
		.setMessage("Save changes?")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				saveAndFinish = true;
				if(ItemName.length()==0){
					ItemName.setError("Required");
				} else if (ItemDesc.length()==0){
					ItemDesc.setError("Required");
				} else if("Document".equals(itemTypeText)){
					if(file==null){
						if(!filePresent){
						Toast.makeText(AddItemsActivity.this, "Add Document before saving.", Toast.LENGTH_LONG).show();
						}else{
							saveItem();
						}
					} else{
						saveItem();
					}
				} else if("Map".equals(itemTypeText)){
					if(Map.length()==0){
						Map.setError("Required");}
					else{
						saveItem();
					}
				}else if("URL".equals(itemTypeText)){
					if(itemURL.length()==0){
						itemURL.setError("Required");}
					else{
						saveItem();
					}
				}else {
					saveItem();
				}
			}	
		})
		.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				finish();
			}
		})
		//.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}

	public void oneRadioButtonClicked(View v) {
		//color palette onClick listener
		SAVE_TAG=0;
		Log.d("tag text change", "color change");

		if (v.getId() == R.id.color1 || v.getId() == R.id.color2 || v.getId() == R.id.color2 || v.getId() == R.id.color3 || v.getId() == R.id.color4 || v.getId() == R.id.color5 || v.getId() == R.id.color6 || v.getId() == R.id.color7 || v.getId() == R.id.color8 ) {
			Log.e("check"," "+v.getId());
			for (int i=0;i<8;i++) {
				Log.e("check"," "+colorButtons[i].getId());
				if (colorButtons[i].getId() == v.getId()) {
					lastcolor=i;
					colorButtons[i].setBackground(getResources().getDrawable(R.drawable.color_palette_item_selected));
					USER_COLOR  = colors[i];

					Log.e("final check"," "+USER_COLOR);
					//relativespinner.setBackgroundColor(USER_COLOR);
					colorId=USER_COLOR;
					popup.dismiss();
				}
				else {

					colorButtons[i].setBackground(getResources().getDrawable(R.drawable.color_palette_item));
				}

				setPaletteItemColor(i);
			}
		}
	}
	public void setPaletteItemColor(int pos) {

		GradientDrawable drawable = (GradientDrawable) colorButtons[pos].getBackground();
		drawable.setColor(colors[pos]);
	}
	private void browseGallery(int i){
		//fire file picker intent to choose existing picture
		Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
		fileIntent.setType("*/*");
		startActivityForResult(fileIntent, i);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {  
		super.onActivityResult(requestCode, resultCode, intent);
		if(intent != null)
		{
			switch (requestCode) {
			case BROWSE_GALLERY_REQUEST:
				//Bitmap bitmap = null;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
				InputStream fis = null; 
				OutputStream os = null;
				String name = "Item.Document";
				String size = "0";
				int sized = 0;
				String fileExt = "";

				File imagesFolder = new File(MainScreenActivity.EXTERNAL_CACHE_DIR.getAbsolutePath() + "/images");
				if (!imagesFolder.exists()) {
					imagesFolder.mkdirs();
				}
				File tempFile = new File(imagesFolder.getAbsolutePath() + "/" + "document.jpg");
				Bitmap bitmap;
				{	try {					
					Uri imageUri =intent.getData();

					fis = getContentResolver().openInputStream(imageUri);
					//					Log.d("test", getContentResolver().getType(imageUri));

					if(getContentResolver().getType(imageUri)!=null)
						//					if(fi.getName()!=null){
						//					name = fi.getName();}
					{ Cursor returnCursor =
					getContentResolver().query(imageUri, null, null, null, null);
					/*
					 * Get the column indexes of the data in the Cursor,
					 * move to the first row in the Cursor, get the data,
					 * and display it.
					 */
					int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
					int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
					returnCursor.moveToFirst();

					//uploadedItem.setVisibility(View.VISIBLE);
					//uploadedItemName.setText(returnCursor.getString(nameIndex));
					//uploadedItemSize.setText(Long.toString(returnCursor.getLong(sizeIndex)/1024)+"Kb");
					name = returnCursor.getString(nameIndex);
					size = Long.toString(returnCursor.getLong(sizeIndex)/1024);
					Log.d("test name", returnCursor.getString(nameIndex));
					Log.d("test size", Long.toString(returnCursor.getLong(sizeIndex)/1024)+"Kb");
					}
					else
					{File fi = new File(imageUri.getPath());
					name = fi.getName();
					size = Long.toString(fi.length()/1024);
					sized = (int)fi.length()/1024;
					//uploadedItem.setVisibility(View.VISIBLE);
					//uploadedItemName.setText(name);
					//uploadedItemSize.setText(Long.toString(fi.length()/1024)+"Kb");
					Log.d("test name", name);
					Log.d("test size", Long.toString(fi.length()/1024)+"Kb");}


					if(sized<10000){
						StringTokenizer tokens = new StringTokenizer(name, ".");
						String FileName = tokens.nextToken();
						fileExt = tokens.nextToken();
						if("jpg".equals(fileExt) || "png".equals(fileExt)){
							uploadedPhotoPreview.setVisibility(View.VISIBLE);
							uploadedItem.setVisibility(View.GONE);
							productadd.setVisibility(View.GONE);
							uploadedPhotoName.setText(name);
							fis = getContentResolver().openInputStream(imageUri);
							os = new FileOutputStream(tempFile);
							Utils.copyStream(fis, os);
							bitmap = Utils.decodeImageFile(tempFile, 500, 500);
							uploadedPhoto.setImageBitmap(bitmap);
							uploadedPhoto.setVisibility(View.VISIBLE);
							bitmap.compress(CompressFormat.JPEG, 100, baos);
						} else{
							uploadedPhotoPreview.setVisibility(View.GONE);
							uploadedItem.setVisibility(View.VISIBLE);
							productadd.setVisibility(View.GONE);
							uploadedItemName.setText(name);
							uploadedItemSize.setText(size+" Kb");

							byte[] buf = new byte[1024];  
							int n;  
							while (-1 != (n = fis.read(buf)))  
								baos.write(buf, 0, n);  

						}
					}
				}
				catch (Exception e) {
					Log.d("TEST", e.getMessage());
				}
				finally {
					if (fis != null) {
						try {
							fis.close();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if(sized<10000){
					byte[] data1 = baos.toByteArray();

					file = new ParseFile(name, data1);
				}else {
					Toast.makeText(AddItemsActivity.this, "File size must be les than 10Mb", Toast.LENGTH_LONG).show();
				}
				break;
				}   	
			default: break;   	}}
		else
		{
			Log.e("Intent data:" , "null");
		}

	}
	private void saveItem() {
		progress = new ProgressDialog(this);
		progress = ProgressDialog.show(this, null,
				"Saving...", true);
		final String in  = ItemName.getText().toString();
		final String it  = ItemDesc.getText().toString();
		final String url = itemURL.getText().toString();
		if("00000".equals(itemObjectId)) {

			//new
			final ArrayList<String> value = new ArrayList<String>();
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
			query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
			query.addAscendingOrder("createdAt");
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> itemList, ParseException e) {
					if (e == null) {
						for(ParseObject Item: itemList)	{
							if(value.size()<4){
								value.add(""+Item.get("itemType"));}
						}
						ParseUser user = ParseUser.getCurrentUser();
						if(value.size()<4){
							value.add(itemTypeText);}
						user.put("brandTags", value);
						user.saveInBackground();
					}
				}
			});

			ParseObject Item = new ParseObject("Item");
			Item.put("brandObjectId", ParseUser.getCurrentUser().getObjectId());
			Item.put("itemName", in);
			Item.put("itemDescription", it);
			Item.put("itemColorid", colorId);
			Item.put("itemType",itemTypeText);
			Item.put("itemURL", url);
			if(file!=null)	{
				Item.put("itemDocument", file);}
			Item.put("itemMap", maptext);
			Item.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					progress.dismiss();
					SAVE_TAG=1;
					Log.d("tag ", "saved");
					finish();
				}
			}); 
		}
		else {
			//old item
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
			//query.whereEqualTo("brandName", ParseUser.getCurrentUser().get("brandName"));
			query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
			query.whereEqualTo("objectId", itemObjectId);
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> itemList, ParseException e) {
					if (e == null) {
						for(ParseObject Item: itemList)	{
							Item.put("itemName", in);
							Item.put("itemDescription", it);
							Item.put("itemColorid", colorId);
							Item.put("itemMap", maptext);
							Item.put("itemType",itemTypeText);
							Item.put("itemURL", url);
							if(file!=null)	{
								Item.put("itemDocument", file);}
							Item.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									if(saveAndFinish == true){
										progress.dismiss();
										SAVE_TAG=1;
										Log.d("tag ", "saved");
										finish();
									} else {
										finish();
										progress.dismiss();
										SAVE_TAG=1;
										Log.d("tag ", "saved");
									}
								}
							});


						}
					}else{
						Toast.makeText(AddItemsActivity.this,"Error saving item.", Toast.LENGTH_SHORT).show();
						progress.dismiss();
					}
				}
			});
		} 
	}

	public ArrayList<SpinnerInfo> populateList()
	{
		ArrayList<SpinnerInfo> myItems = new ArrayList<SpinnerInfo>();
		myItems.add(new SpinnerInfo("Offer", R.drawable.coupons)); // Image stored in /drawable
		myItems.add(new SpinnerInfo("Brochure", R.drawable.brochure));
		myItems.add(new SpinnerInfo("Portfolio", R.drawable.portfolio));
		myItems.add(new SpinnerInfo("Catalogue", R.drawable.catalogue));
		myItems.add(new SpinnerInfo("Menu", R.drawable.menu));
		myItems.add(new SpinnerInfo("Map", R.drawable.map));
		myItems.add(new SpinnerInfo("Products", R.drawable.map));
		myItems.add(new SpinnerInfo("URL", R.drawable.map));
		return myItems;
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (count%3 == 1) {
			//we don't want to make an insanely large array, so we clear it each time
			mapadapter.clear();
			//create the task
			GetPlaces task = new GetPlaces();
			//now pass the argument in the textview to the task
			task.execute(Map.getText().toString());
		} }  
	class GetPlaces extends AsyncTask<String, Void, ArrayList<String>>
	{
		@Override
		// three dots is java for an array of strings
		protected ArrayList<String> doInBackground(String... args)
		{

			Log.d("gottaGo", "doInBackground");

			ArrayList<String> predictionsArr = new ArrayList<String>();

			try
			{

				URL googlePlaces = new URL(
						// URLEncoder.encode(url,"UTF-8");
						"https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+ URLEncoder.encode(Map.getText().toString(), "UTF-8") +"&types=establishment&language=en&sensor=true&key=AIzaSyB3iHBPT4rstXdka5dITdJO2fKs9we5mQ8");
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
			} catch (IOException e)
			{

				Log.e("YourApp", "GetPlaces : doInBackground", e);

			} catch (JSONException e)
			{

				Log.e("YourApp", "GetPlaces : doInBackground", e);

			}

			return predictionsArr;

		}

		//then our post

		@Override
		protected void onPostExecute(ArrayList<String> result)
		{

			Log.d("YourApp", "onPostExecute : " + result.size());
			//update the adapter
			mapadapter = new ArrayAdapter<String>(getBaseContext(), R.layout.item_list);
			mapadapter.setNotifyOnChange(true);
			//attach the adapter to textview
			Map.setAdapter(mapadapter);

			for (String string : result)
			{

				Log.d("YourApp", "onPostExecute : result = " + string);
				mapadapter.add(string);
				mapadapter.notifyDataSetChanged();
			}

			//Log.d("YourApp", "onPostExecute : autoCompleteAdapter" + adapter.getCount());

		}

	}

	public class ProductListAdapter extends ArrayAdapter<Product> {

		private Context _context;
		private ArrayList<Product> _items;

		public ProductListAdapter(Context context, ArrayList<Product> items) {
			super(context, R.layout.list_group, items);
			_context = context;
			_items = items;
		}
		class ViewHolder {
			ImageView delete_confirm;

			public ViewHolder(View view) {
				delete_confirm = (ImageView) view.findViewById(R.id.btnDeleteConfirmImage);
			}
		}
		@SuppressLint("InflateParams")
		@Override
		public View getView(int groupPosition,View convertView, ViewGroup parent) {
			String headerTitle = _items.get(groupPosition).getName();
			//String headerTitle = (String) getItem(groupPosition);
			final ViewHolder holder;
			//final Integer childDraw = _items.get(groupPosition).getColorId();
			String ItemTypeTitle = _items.get(groupPosition).getCategory();

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_group, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			Bitmap bitmaps;
			ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1); 

			holder.delete_confirm.setTag(_items.get(groupPosition));
			holder.delete_confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 
					final PopupMenu popup = new PopupMenu(_context, holder.delete_confirm);
					popup.getMenuInflater().inflate(R.menu.graph_popup_menu, popup.getMenu());

					popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

						@Override
						public boolean onMenuItemClick(MenuItem item) {
							// 
							switch (item.getItemId()) {
							case R.id.delete:
								_items.remove(holder.delete_confirm.getTag());
								notifyDataSetChanged();
								Product itm = (Product)holder.delete_confirm.getTag();
								ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
								query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
								query.whereEqualTo("objectId", itm.getProductId());
								query.findInBackground(new FindCallback<ParseObject>() {

									@Override
									public void done(List<ParseObject> itemList, ParseException e) {
										if(e==null){
											for(ParseObject item : itemList){
												item.deleteInBackground();
											}
										}
									}
								});
								break;

							}
							return true;
						}
					});
					popup.show();
				}

			});

			if(ItemTypeTitle.equals("Offer"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.coupon_w, 0, 0, 0);
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.products);
				iv.setImageBitmap(bitmaps);}
			else if(ItemTypeTitle.equals("Brochure"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.brochure_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.products);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Portfolio"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.portfolio_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.products);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Catalogue"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.products);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Menu"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.products);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Map"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.products);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Products"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.products);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("URL"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.products);
				iv.setImageBitmap(bitmaps);
			}
			TextView lblListHeader = (TextView) convertView
					.findViewById(R.id.lblListHeader);
			RelativeLayout ChangeColor = (RelativeLayout) convertView
					.findViewById(R.id.ChangeColor);
			//ChangeColor.setBackgroundColor(-5278736);
			lblListHeader.setTypeface(null, Typeface.BOLD);
			lblListHeader.setText(headerTitle);

			return convertView;
		}
	}

}
