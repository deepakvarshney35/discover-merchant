package com.enormous.pkpizzas.publisher.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enormous.pkpizzas.publisher.adapter.SpinnerInfo;
import com.enormous.pkpizzas.publisher.data.ImageLoader;
import com.enormous.pkpizzas.publisher.data.ProductPhotos;
import com.enormous.pkpizzas.publisher.data.Utils;
import com.enormous.pkpizzas.publisher.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddProductActivity extends Activity  {
	private ParseFile file;
	//private int USER_COLOR;
	private int SAVE_TAG=1;
	ProgressDialog progress;

	GridLayout photosContainer;
	private TextView ProductName;
	private TextView ProductDesc;
	private TextView productcost;
	private TextView productTax,productStock,productShippingCost,productType;
	
	private TextView productTypeText,
	productNameText,
	productdescText,
	productcostText,
	productShippingCostText,
	productTaxText,
	productStockText;
	
	private ImageView uploadedPhotoMain;
	private ImageView uploadedPhotoMainDelete;
	private RelativeLayout uploadedPhotoMainLayout;
	private TextView uploadedPhotoMainName;
	private FrameLayout discardProduct;
	private FrameLayout saveProduct;

	private ImageButton colord;
	private LinearLayout layoutAboveAll;
	private  View[] colorButtons = new View[10];
	int[] colors = new int[10];
	private Button colorButton0;
	private Button colorButton1;
	private Button colorButton2;
	private Button colorButton3;
	private Button colorButton4;
	private Button colorButton5;
	private Button colorButton6;
	private Button colorButton7;

	String productObjectId;
	int position= 0;

	private PopupWindow popup;
	private Bitmap bmp;
	private View layout;
	ArrayAdapter<CharSequence> adapter; 

	private static final int BROWSE_GALERY_REQUEST = 2;
	private static final int BROWSE_GALERY_REQUEST2 = 3;
	final int PHOTO_CROP = 555;
	int co=0;
	int c1=0, c2=0, c3=0;
	ArrayList<SpinnerInfo> SpinnerProductList;

	Button optionButton;
	TextView optionText1,optionText2,optionText3,optionText4;
	FrameLayout optionFrame1,optionFrame2,optionFrame3,optionFrame4;
	ImageButton optionCancel1,optionCancel2,optionCancel3,optionCancel4;
	TextView optionCost1,optionCost2,optionCost3,optionCost4;
	private int optionNumber=1;

	private ArrayList<String> optionsRetreived;
	private ArrayList<Integer> optionsCostRetreived;
	private ArrayList<String> optionCostRetreived;
	public static ArrayList<ProductPhotos> productPhotos;
	ImageView addImageButton;
	View photoView ;

	boolean saveAndFinish = false;
	private Resources res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		// getting attached intent data
		productObjectId = i.getStringExtra("productObjectId");
		position = i.getIntExtra("position", 0);
		SpinnerProductList = populateList();

		res = getResources();

		setContentView(R.layout.activity_add_product);
		getActionBar().hide();

		//findviews
		colord = (ImageButton) findViewById(R.id.colorselect);
		layoutAboveAll =(LinearLayout) findViewById(R.id.layoutAboveAll);
		addImageButton = (ImageView)findViewById(R.id.addImageButton);
		uploadedPhotoMainDelete = (ImageView)findViewById(R.id.uploadedPhotoMainDelete);
		uploadedPhotoMainLayout = (RelativeLayout)findViewById(R.id.uploadedPhotoMainLayout);
		uploadedPhotoMainName = (TextView)findViewById(R.id.uploadedPhotoMainName);
		productType = (TextView)findViewById(R.id.productType);
		discardProduct = (FrameLayout)findViewById(R.id.discardProduct);
		saveProduct = (FrameLayout)findViewById(R.id.saveProduct);

		addImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_attachment));
		addImageButton.setRotation(270);
		
		discardProduct.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(SAVE_TAG==0)	{
					alertMessage();
				}
				else{
					finish();
				}
			}
		});

		saveProduct.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveItem();
			}
		});

		addImageButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!"00000".equals(productObjectId)){
					SAVE_TAG=0;
					Log.d("tag text change", "gallery change");
					browseGallery(BROWSE_GALERY_REQUEST2);}
				else{
					Toast.makeText(AddProductActivity.this, "Save Product before adding more photos.", Toast.LENGTH_SHORT).show();
				}

			}
		});
		//myAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, SpinnerProductList);

		optionFrame1 = (FrameLayout)findViewById(R.id.optionFrame1);
		optionFrame2 = (FrameLayout)findViewById(R.id.optionFrame2);
		optionFrame3 = (FrameLayout)findViewById(R.id.optionFrame3);
		optionFrame4 = (FrameLayout)findViewById(R.id.optionFrame4);

		optionText1 = (EditText)findViewById(R.id.optionText1);
		optionText2 = (EditText)findViewById(R.id.optionText2);
		optionText3 = (EditText)findViewById(R.id.optionText3);
		optionText4 = (EditText)findViewById(R.id.optionText4);

		optionCost1 = (EditText)findViewById(R.id.optionCost1);
		optionCost2 = (EditText)findViewById(R.id.optionCost2);
		optionCost3 = (EditText)findViewById(R.id.optionCost3);
		optionCost4 = (EditText)findViewById(R.id.optionCost4);


		optionCancel1 = (ImageButton)findViewById(R.id.optionCancel1);
		optionCancel2 = (ImageButton)findViewById(R.id.optionCancel2);
		optionCancel3 = (ImageButton)findViewById(R.id.optionCancel3);
		optionCancel4 = (ImageButton)findViewById(R.id.optionCancel4);

		optionButton = (Button)findViewById(R.id.optionButton);
		optionButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(optionNumber==1){
					optionFrame1.setVisibility(View.VISIBLE);
				}
				if(optionNumber==2){
					optionFrame2.setVisibility(View.VISIBLE);
				}
				if(optionNumber==3){
					optionFrame3.setVisibility(View.VISIBLE);
				}
				if(optionNumber==4){
					optionFrame4.setVisibility(View.VISIBLE);
					optionButton.setVisibility(View.GONE);
				}
				optionNumber++;
			}
		});

		optionsRetreived = new ArrayList<String>();
		optionsCostRetreived = new ArrayList<Integer>();
		optionCostRetreived = new ArrayList<String>();

		ProductName = (TextView)findViewById(R.id.productname);
		ProductDesc = (TextView)findViewById(R.id.productdesc);
		productcost = (TextView)findViewById(R.id.productcost);
		uploadedPhotoMain = (ImageView) findViewById(R.id.uploadedPhotoMain);
		photosContainer = (GridLayout) findViewById(R.id.photosContainer);;
		productShippingCost = (TextView)findViewById(R.id.productShippingCost);
		productTax = (TextView)findViewById(R.id.productTax);
		productStock = (TextView)findViewById(R.id.productStock);
		
		productTypeText = (TextView)findViewById(R.id.productTypeText);
		productNameText = (TextView)findViewById(R.id.productNameText);
		productcostText = (TextView)findViewById(R.id.productcostText);
		productShippingCostText = (TextView)findViewById(R.id.productShippingCostText);
		productdescText = (TextView)findViewById(R.id.productdescText);
		productTaxText = (TextView)findViewById(R.id.productTaxText);
		productStockText = (TextView)findViewById(R.id.productStockText);
		
		new GetPhotoUrlsTask().execute();

		productType.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				if(productType.length()==0){
					productTypeText.setVisibility(View.GONE);
				}else {
					productTypeText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				productType.setError(null);
			}
		});
		
		
		ProductName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				if(ProductName.length()==0){
					productNameText.setVisibility(View.GONE);
				}else {
					productNameText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				ProductName.setError(null);
			}
		});
		
		ProductDesc.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				if(ProductDesc.length()==0){
					productdescText.setVisibility(View.GONE);
				}else {
					productdescText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				ProductDesc.setError(null);
			}
		});
		
		productcost.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				if(productcost.length()==0){
					productcostText.setVisibility(View.GONE);
				}else {
					productcostText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				productcost.setError(null);
			}
		});
		
		productShippingCost.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				if(productShippingCost.length()==0){
					productShippingCostText.setVisibility(View.GONE);
				}else {
					productShippingCostText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				productShippingCost.setError(null);
			}
		});
		
		productTax.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				if(productTax.length()==0){
					productTaxText.setVisibility(View.GONE);
				}else {
					productTaxText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				productTax.setError(null);
			}
		});
		
		productStock.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SAVE_TAG = 0;
				if(productStock.length()==0){
					productStockText.setVisibility(View.GONE);
				}else {
					productStockText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				productStock.setError(null);
			}
		});

		ParseUser user = ParseUser.getCurrentUser();
		if (user != null) {


			if(!"00000".equals(productObjectId)){
				//old
				addImageButton.setVisibility(View.VISIBLE);
				uploadedPhotoMainName.setText("Click to Change");
				SAVE_TAG = 1;
				if (AddItemsActivity.products.get(position).getName() != null)
				{String b = AddItemsActivity.products.get(position).getName();
				ProductName.setText(b);}
				if (AddItemsActivity.products.get(position).getDescription() != null)
				{String b1 = AddItemsActivity.products.get(position).getDescription();
				ProductDesc.setText( b1);}
				int b1 =AddItemsActivity.products.get(position).getCost();
				productcost.setText( ""+b1);
				int c1 =AddItemsActivity.products.get(position).getShippingCost();
				productShippingCost.setText( ""+c1);
				int c2 =AddItemsActivity.products.get(position).getTax();
				productTax.setText( ""+c2);
				int c3 =AddItemsActivity.products.get(position).getNumberOfItemsInStock();
				productStock.setText( ""+c3);
				//Integer b2 =AddItemActivity.products.get(position).getColorId();
				//+relativespinner.setBackgroundColor(-5278736);
				//colorId=b2;
				if (AddItemsActivity.products.get(position).getCategory() != null)
				{String b3 =AddItemsActivity.products.get(position).getCategory();
				//spinner.setSelection(myAdapter);
				//spinner.setSelection(myAdapter.getPosition(b3));
				productType.setText(b3);
				}
				optionsRetreived = AddItemsActivity.products.get(position).getOptions();
				optionCostRetreived = AddItemsActivity.products.get(position).getOptionCost();
				optionsCostRetreived = new ArrayList<Integer>(optionCostRetreived.size()); 
				for (String myStr : optionCostRetreived) { 
					optionsCostRetreived.add(Integer.parseInt(myStr)); 
				}
				int size = optionsRetreived.size();
				if(0<size){
					optionText1.setText(optionsRetreived.get(0));
					optionCost1.setText(""+optionsCostRetreived.get(0));
					optionFrame1.setVisibility(View.VISIBLE);
					optionNumber=2;}
				if(1<size){
					optionText2.setText(optionsRetreived.get(1));
					optionCost2.setText(""+optionsCostRetreived.get(1));
					optionNumber=3;
					optionFrame2.setVisibility(View.VISIBLE);}
				if(2<size){
					optionText3.setText(optionsRetreived.get(2));
					optionCost3.setText(""+optionsCostRetreived.get(2));
					optionNumber=4;
					optionFrame3.setVisibility(View.VISIBLE);}
				if(3<size){
					optionText4.setText(optionsRetreived.get(3));
					optionCost4.setText(""+optionsCostRetreived.get(3));
					optionNumber=5;
					optionButton.setVisibility(View.GONE);
					optionFrame4.setVisibility(View.VISIBLE);}
				String picurl =  AddItemsActivity.products.get(position).getPictureURL();
				ImageLoader.getInstance().displayImage(AddProductActivity.this, picurl, uploadedPhotoMain, false, 300, 300, 0);

				/*ParseFile pic = AddItemActivity.products.get(position).getPicture();
				if(pic!=null){
					pic.getDataInBackground(new GetDataCallback() {
						@Override
						public void done(byte[] data, ParseException e) {
							if (e == null) {
								// data has the bytes for the image
								if(data!=null) {
									bmp=BitmapFactory.decodeByteArray(data,0,data.length);
									Photo.setImageBitmap(bmp);
								}
							}else {
								// something went wrong
							}
						}
					});
				}*/
			}
		}

		uploadedPhotoMain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SAVE_TAG=0;
				Log.d("tag text change", "gallery change");
				browseGallery(BROWSE_GALERY_REQUEST);
			}
		});
		uploadedPhotoMainDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//Perform delete
				Toast.makeText(AddProductActivity.this, "Delete?", Toast.LENGTH_LONG).show();
			}
		});

		colord.setClickable(true);
		colord.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				//v.startAnimation(AnimationUtils.loadAnimation(AddProductActivity.this, R.anim.image_click));
				final Activity context = AddProductActivity.this;

				int popupWidth = WindowManager.LayoutParams.MATCH_PARENT;
				int popupHeight = -2;

				// Inflate the popup_layout.xml
				LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
				LayoutInflater layoutInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

				// Creating the PopupWindow
				popup = new PopupWindow(context);
				popup.setContentView(layout);
				popup.setWidth(popupWidth);
				popup.setHeight(popupHeight);
				//layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				//        LayoutParams.WRAP_CONTENT));
				popup.setFocusable(true);

				/*		 			Button done = (Button)layout.findViewById(R.id.popupdone);
		 			done.setOnClickListener(new View.OnClickListener() {
		 				@Override
		 				public void onClick(View v) {
		 					spinner.setBackgroundColor(USER_COLOR);
		 					colorId=USER_COLOR;
		 					popup.dismiss();
		 				}
		 			});*/		 		

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


			}		
		});
	}
	private class GetPhotoUrlsTask extends AsyncTask<Void, Void, ArrayList<String>> {
  
		ArrayList<String> urls = new ArrayList<String>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//photoProgress.setVisibility(View.VISIBLE);
			photosContainer.setVisibility(View.GONE);
			urls.clear();
			//  nothingFoundTextView.setVisibility(View.GONE);
		}

		@Override
		protected ArrayList<String> doInBackground(Void... voids) {
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ProductPhotos");
			if(!"00000".equals(productObjectId)){
				query.whereEqualTo("productObjectId", productObjectId);
				query.addAscendingOrder("createdTime");
				try {
					List<ParseObject> objects = query.find();
					for (ParseObject object : objects) {
						urls.add(object.getParseFile("productPicture").getUrl());
					}
					return urls;
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}}else{
					return null;
				}
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);
			//photoProgress.setVisibility(View.GONE);
			if (result != null) {
				if (result.size() > 0) {
					photosContainer.setVisibility(View.VISIBLE);
					setUpPhotos(result);
				}
				else {
					// nothingFoundTextView.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void setUpPhotos(final ArrayList<String> urls) {
		final float photoDimension = Utils.convertDpToPixel(100);
		final float photoPadding = Utils.convertDpToPixel(6);
		final LinearLayout.LayoutParams frameLayoutParams = new LinearLayout.LayoutParams((int) photoDimension, (int) photoDimension);
		final LinearLayout.LayoutParams photoLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		final View.OnClickListener deleteClickListener = new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				//Perform delete
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ProductPhotos");
				if(!"00000".equals(productObjectId)){
					photosContainer.removeViewAt((Integer) v.getTag());
					query.whereEqualTo("productObjectId", productObjectId);
					query.addAscendingOrder("createdTime");
					query.findInBackground(new FindCallback<ParseObject>() {

						@Override
						public void done(List<ParseObject> urls, ParseException e) {
							if(e==null){
								int i=0;
								for(ParseObject url : urls){
									if(i==(Integer) v.getTag()){
										url.deleteInBackground(new DeleteCallback() {
											
											@Override
											public void done(ParseException e) {
												new GetPhotoUrlsTask().execute();
											}
										});	
										}
									i++;
								}
							}
						}
					});
					Toast.makeText(AddProductActivity.this, ""+(Integer) v.getTag(), Toast.LENGTH_LONG).show();
					urls.remove((Integer) v.getTag());
					
				}
			}
		};
		final View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent goToViewPhotos = new Intent(AddProductActivity.this, ProductPhotoViewerActivity.class);
				goToViewPhotos.putStringArrayListExtra("imageUrls", urls);
				goToViewPhotos.putExtra("imagePos", (Integer) view.getTag());
				startActivity(goToViewPhotos);
			}
		};
		photosContainer.removeAllViews();
		for (int i = 0; i < urls.size(); i++) {
			StringTokenizer tok = new StringTokenizer(urls.get(i), "-");
			String fileName = "Image";
			String Extra = "";
			for(int j=0 ; j<15 ; j++){
				if(j<11){
					if(tok.hasMoreTokens()){
						fileName = tok.nextToken();
					}}else{
						if(tok.hasMoreTokens()){
							Extra = tok.nextToken();
						}
					}
			}
			fileName = fileName+Extra;
			View photoView = getLayoutInflater().inflate(R.layout.product_photo_container, null);
			ImageView addedPhoto = (ImageView)photoView.findViewById(R.id.uploadedPhoto);
			ImageView uploadedPhotoDelete = (ImageView)photoView.findViewById(R.id.uploadedPhotoDelete);
			TextView uploadedPhotoName = (TextView)photoView.findViewById(R.id.uploadedPhotoName);
			uploadedPhotoName.setText(fileName);
			RelativeLayout uploadedPhotoPreview = (RelativeLayout)photoView.findViewById(R.id.uploadedPhotoPreview);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((getResources().getDisplayMetrics().widthPixels/2)-25,(getResources().getDisplayMetrics().widthPixels/2)-25);
			uploadedPhotoPreview.setLayoutParams(params);

			uploadedPhotoDelete.setTag(i);
			uploadedPhotoDelete.setOnClickListener(deleteClickListener);
			photosContainer.addView(photoView, i);
			ImageLoader.getInstance().displayImage(AddProductActivity.this, urls.get(i), addedPhoto, false, 250, 250, 0);
			addedPhoto.setTag(i);
			addedPhoto.setOnClickListener(clickListener);
		}
	}

	@Override
	protected void onResume() {
		//Portrait mode
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		if(SAVE_TAG==0)	{
			alertMessage();}
		else
		{finish();}
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
			if(SAVE_TAG==0)	{
				alertMessage();}
			else
			{finish();}
			return true;
		case R.id.save:
			saveItem();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void alertMessage() {
		new AlertDialog.Builder(this)
		.setTitle("Item not saved!!")
		.setMessage("save changes?")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				saveAndFinish = true;
				saveItem();

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
					colorButtons[i].setBackground(getResources().getDrawable(R.drawable.color_palette_item_selected));
					//USER_COLOR  = colors[i];
					//Log.e("final check"," "+USER_COLOR);
					//relativespinner.setBackgroundColor(USER_COLOR);
					//colorId=USER_COLOR;
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
		//fire image picker intent to choose existing picture
		Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pictureIntent.setType("image/*");
		startActivityForResult(pictureIntent, i);
		//Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//startActivityForResult(intent, i);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {  
		super.onActivityResult(requestCode, resultCode, intent);
		if(intent != null)
		{
			switch (requestCode) {
			case BROWSE_GALERY_REQUEST:
				Bitmap bitmap = null;
				{			try {					
					Uri imageUri =intent.getData();
					bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
				}
				catch (Exception e) {
					Log.d("TEST", e.getMessage());
				}
				uploadedPhotoMain.setImageBitmap(bitmap);
				//uploadedPhotoMainLayout.setVisibility(View.VISIBLE);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
				byte[] data1 = stream.toByteArray();        
				file = new ParseFile("productPicture.jpg", data1);

				ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
				query.whereEqualTo("objectId", productObjectId);
				//query.whereEqualTo("brandName", ParseUser.getCurrentUser().get("brandName"));
				query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
				query.findInBackground(new FindCallback<ParseObject>() {
					public void done(List<ParseObject> itemList, ParseException e) {
						if (e == null) {
							for(ParseObject Item: itemList)
							{
								Item.put("productPicture",file);
								Item.saveInBackground();
							}
						}
					}
				});
				}
				break;
			case BROWSE_GALERY_REQUEST2:
				Bitmap bitmap5 = null;
				String imageName = "productPicture.jpg";
				String imageSize = "0";
				try {					
					Uri imageUri =intent.getData();
					bitmap5 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
					if(getContentResolver().getType(imageUri)!=null){
						Cursor returnCursor =
								getContentResolver().query(imageUri, null, null, null, null);
						/*
						 * Get the column indexes of the data in the Cursor,
						 * move to the first row in the Cursor, get the data,
						 * and display it.
						 */
						int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
						int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
						returnCursor.moveToFirst();
						imageName = returnCursor.getString(nameIndex);
						imageSize = Long.toString(returnCursor.getLong(sizeIndex)/1024);
					}
					else
					{File fi = new File(imageUri.getPath());
					imageName = fi.getName();
					imageSize = Long.toString(fi.length()/1024);
					}
				}
				catch (Exception e) {
					Log.d("TEST", e.getMessage());
				}

				File myDir = new File(getExternalCacheDir().getAbsoluteFile() + "/images/"+position);    
				myDir.mkdirs();
				Random generator = new Random();
				int n = 10000;
				n = generator.nextInt(n);
				String fname = "Image-"+ n +".jpg";
				File filed = new File (myDir, fname);
				if (filed.exists ()) filed.delete (); 
				try {
					FileOutputStream out = new FileOutputStream(filed);
					bitmap5.compress(Bitmap.CompressFormat.JPEG, 70, out);
					out.flush();
					out.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
				//Photo.setImageBitmap(bitmap);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap5.compress(Bitmap.CompressFormat.JPEG, 50, stream);
				byte[] data1 = stream.toByteArray();        
				file = new ParseFile(imageName, data1);
				ParseObject ProductPhotos = new ParseObject("ProductPhotos");
				ProductPhotos.put("productObjectId", productObjectId);
				ProductPhotos.put("productPicture", file);
				progress = new ProgressDialog(this);
				progress = ProgressDialog.show(this, null,
						"Saving...", true);
				ProductPhotos.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						// TODO Auto-generated method stub
						progress.dismiss();
						new GetPhotoUrlsTask().execute();
					}
				});
				break;

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
		final ArrayList<String> options;
		final ArrayList<Integer> optionsCost;
		String temp;
		options = new ArrayList<String>();
		temp = optionText1.getText().toString();
		if(!"".equals(temp)){
			options.add(temp);}
		temp = optionText2.getText().toString();
		if(!"".equals(temp)){
			options.add(temp);}
		temp = optionText3.getText().toString();
		if(!"".equals(temp)){
			options.add(temp);}
		temp = optionText4.getText().toString();
		if(!"".equals(temp)){
			options.add(temp);}

		String temp1;
		optionsCost = new ArrayList<Integer>();
		temp1 = optionCost1.getText().toString();
		if(!"".equals(temp1)){
			optionsCost.add(Integer.parseInt(temp1));}
		temp1 = optionCost2.getText().toString();
		if(!"".equals(temp1)){
			optionsCost.add(Integer.parseInt(temp1));}
		temp1 = optionCost3.getText().toString();
		if(!"".equals(temp1)){
			optionsCost.add(Integer.parseInt(temp1));}
		temp1 = optionCost4.getText().toString();
		if(!"".equals(temp1)){
			optionsCost.add(Integer.parseInt(temp1));}

		final String in  = ProductName.getText().toString();
		final String it  = ProductDesc.getText().toString();

		if(!"".equals(productcost.getText().toString())){
			co = Integer.parseInt(productcost.getText().toString());}
		if(!"".equals(productShippingCost.getText().toString())){
			c1 = Integer.parseInt(productShippingCost.getText().toString());}
		if(!"".equals(productStock.getText().toString())){
			c2 = Integer.parseInt(productStock.getText().toString());}
		if(!"".equals(productTax.getText().toString())){
			c3 = Integer.parseInt(productTax.getText().toString());}
		if(optionsCost.size()!=options.size()){
			progress.dismiss();
			Toast.makeText(AddProductActivity.this, "For all options there must be option cost mentioned!", Toast.LENGTH_LONG).show();
		}else {
			AddItemsActivity.check =1;
			if("00000".equals(productObjectId)) {
				//new
				ParseObject Item = new ParseObject("Product");
				Item.put("brandName", ParseUser.getCurrentUser().get("brandName"));
				Item.put("brandObjectId", ParseUser.getCurrentUser().getObjectId());
				Item.put("productName", in);
				Item.put("productDescription", it);
				//Item.put("productColorid", colorId);
				Item.put("productType",productType.getText().toString());
				Item.put("productCost", co);
				Item.put("productShippingCost", c1);
				Item.put("productStock", c2);
				Item.put("productTax", c3);
				if(options.size()==0){
					options.add(in);
					optionsCost.add(co);
				}
				Item.put("productOptions", options);
				Item.put("productOptionsCost", optionsCost);
				if(file!=null){
					Item.put("productPicture", file);
				Item.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						// TODO Auto-generated method stub
						SAVE_TAG=1;
						//Update objectId
						Log.d("tag ", "saved");
						progress.dismiss();
						finish();
					}
				});}
				else{
					progress.dismiss();
					Toast.makeText(AddProductActivity.this, "Upload Photo", Toast.LENGTH_LONG).show();
				}
			}
			else {
				//old item
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
				//query.whereEqualTo("brandName", ParseUser.getCurrentUser().get("brandName"));
				query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
				query.whereEqualTo("objectId", productObjectId);
				query.findInBackground(new FindCallback<ParseObject>() {
					public void done(List<ParseObject> itemList, ParseException e) {
						if (e == null) {
							for(ParseObject Item: itemList)	{
								Item.put("productName", in);
								Item.put("productDescription", it);
								//Item.put("productColorid", colorId);
								Item.put("productCost", co);
								Item.put("productType",productType.getText().toString());
								Item.put("productShippingCost", c1);
								Item.put("productStock", c2);
								Item.put("productTax", c3);
								Item.put("productOptions", options);
								Item.put("productOptionsCost", optionsCost);
								Item.saveInBackground(new SaveCallback() {

									@Override
									public void done(ParseException e) {
										// TODO Auto-generated method stub
										if(saveAndFinish == true){
											progress.dismiss();
											SAVE_TAG=1;
											Log.d("tag ", "saved");
											finish();
										}else{
											progress.dismiss();
											SAVE_TAG=1;
											Log.d("tag ", "saved");
											finish();
										}
									}
								});

							}
						}
					}
				});
			} 	}

	}
	public ArrayList<SpinnerInfo> populateList()
	{
		ArrayList<SpinnerInfo> myItems = new ArrayList<SpinnerInfo>();
		myItems.add(new SpinnerInfo("Product Type 1", R.drawable.coupons)); // Image stored in /drawable
		myItems.add(new SpinnerInfo("Product Type 2", R.drawable.brochure));
		myItems.add(new SpinnerInfo("Product Type 3", R.drawable.portfolio));
		myItems.add(new SpinnerInfo("Product Type 4", R.drawable.catalogue));
		myItems.add(new SpinnerInfo("Product Type 5", R.drawable.menu));
		myItems.add(new SpinnerInfo("Product Type 6", R.drawable.map));
		return myItems;
	}

	public class MyImageAdapter extends BaseAdapter {

		int mGalleryItemBackground;
		private Context mContext;
		//File images[];
		File files[];
		public MyImageAdapter(Context c, int folderID) {
			mContext = c;

			File dir = new File(getExternalCacheDir().getAbsoluteFile() + "/images/"+folderID);
			files = dir.listFiles();
			//images = files[folderID].listFiles();

		}
		public int getCount() {
			if (files==null){
				return 0;
			} else {
				return files.length;
			}
		}
		public Object getItem(int positiond) {
			return files[positiond].getAbsolutePath();
		}
		public long getItemId(int positiond) {
			return positiond;
		}
		public String getAlbumName(int folderID) {
			return files[folderID].getName();
		}
		public View getView(int positiond, View convertView, ViewGroup parent) {
			ImageView imageView;
			//Bitmap bm = BitmapFactory
			//      .decodeFile(images[position].getAbsolutePath());
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setPadding(5, 5, 5, 5);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageBitmap(Utils.decodeImageFile(files[positiond].getAbsoluteFile(), 100, 100));
			return imageView;
		}
	}
}


