package com.enormous.pkpizzas.publisher.activities;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.enormous.pkpizzas.publisher.R;
import com.enormous.pkpizzas.publisher.data.Chat;
import com.enormous.pkpizzas.publisher.data.ImageLoader;
import com.enormous.pkpizzas.publisher.data.Product;
import com.enormous.pkpizzas.publisher.data.Utils;
import com.enormous.pkpizzas.publisher.fragments.AddToCartFragment;
import com.firebase.client.Firebase;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Manas on 8/1/2014.
 */

public class CartActivity extends FragmentActivity {

    private static final String FIREBASE_URL = "https://pkpizzas-merchant.firebaseio.com";
    private Firebase ref,pRef;

	private ActionBar actionBar;
	private ArrayList<Product> selectedProducts = new ArrayList<Product>();
	private ArrayList<ParseObject> selectedProductsParse = new ArrayList<ParseObject>();
	private ListView cartListView;
	private LayoutInflater inflater;
	private LinearLayout nothingFoundLinearLayout;
	private LinearLayout noInternetLinearLayout;
	private ProgressBar progressBar;

	//header views
	private TextView cartHeaderTextView;

	//footer views
	private TextView itemTotalTextView;
	private TextView shippingTotalTextView;
	private TextView taxTextView;
	private TextView orderTotalTextView;
	private Button checkOutButton;

	private EditText merchantNoteEditText;

	public static NumberFormat CURRENCY_FORMATTER;

	String Address = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		//Portrait mode
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		findViews();

		CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

		inflater = getLayoutInflater();

		//set actionBar properties
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.indigo500)));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		//add header and footer to cartListView
		View header = inflater.inflate(R.layout.listview_cart_header, null);
		setUpHeader(header);
		cartListView.addHeaderView(header);
		View footer = inflater.inflate(R.layout.listview_cart_footer, null);
		cartListView.addFooterView(footer);
		setUpFooter(footer);

		setUpCart();

		//set click listener to retry when internet connection established again
		noInternetLinearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				setUpCart();
			}
		});

		checkOutButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                addCartToPastOrders();
				onBackPressed();
			}
		});

	}

    private void resetShoppingCart() {
        ParseQuery<ParseObject> shop = ParseQuery.getQuery("ShoppingCart");
        shop.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
        shop.whereEqualTo("userObjectId", getIntent().getExtras().getString("customersObjectId"));
        shop.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> cartItems, ParseException e) {
                // TODO Auto-generated method stub
                for(ParseObject cartItem : cartItems) {
                    cartItem.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("cart", "Item deleted");
                            } else {
                                Log.d("cart", "Item could not be deleted");
                            }
                        }
                    });
                }
            }
        });
    }

    private void resetCartCheckin() {
        ParseQuery<ParseObject> check = ParseQuery.getQuery("Checkin");
        check.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
        check.whereEqualTo("userObjectId", getIntent().getExtras().getString("customersObjectId"));
        check.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> checkins, ParseException e) {
                // TODO Auto-generated method stub
                for(ParseObject checkin : checkins){
                    checkin.put("CartItems", 0);
                    checkin.saveInBackground();
                }
            }
        });
    }

    private void addCartToPastOrders() {
        if(Utils.isConnectedToInternet(this)&& (selectedProducts.size()>0)){
            String brandObjectId, brandName, brandProfilePictureUrl, userObjectId, userName, products, totalCost;

            brandObjectId = ParseUser.getCurrentUser().getObjectId();
            brandName = selectedProducts.get(0).getBrandName();
            //brandProfilePictureUrl =
            userObjectId = getIntent().getExtras().getString("customersObjectId");
            userName = getIntent().getExtras().getString("customersName");
            products = createProductsString();
            totalCost = calculateTotalCost();

            ParseObject object = new ParseObject("PastOrders");

            object.put("brandObjectId",brandObjectId);
            object.put("brandName",brandName);
            object.put("userObjectId",userObjectId);
            object.put("userName",userName);
            object.put("products",products);
            object.put("totalCost",totalCost);

            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null){
                        resetCartCheckin();
                        resetShoppingCart();
                        getorderObjectId();
                    }
                }
            });
        }
    }

    private void getorderObjectId() {
        ParseQuery<ParseObject> orderQuery = new ParseQuery("PastOrders");
        orderQuery.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
        orderQuery.whereEqualTo("userObjectId", getIntent().getExtras().getString("customersObjectId"));
        orderQuery.orderByDescending("createdAt");
        orderQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if((e==null) && (parseObjects.size()>0)) sendChatNotification(parseObjects.get(0).getObjectId());
            }
        });
    }

    private void sendChatNotification(String orderObjectId) {
        // Create our 'model', a Chat object
        ref = new Firebase(FIREBASE_URL).child(getIntent().getExtras().getString("customersObjectId")+ParseUser.getCurrentUser().getObjectId());
        Date date = new Date();
        pRef=  ref.push();
        Chat chat = new Chat(pRef.getName(),"::OrderComplete:: "+orderObjectId, ParseUser.getCurrentUser().getObjectId() , date.getTime());
        // Create a new, auto-generated child of that chat location, and save our chat data there
        pRef.setValue(chat);
        //Sends Parse Notification to channel named after Unique ID. (customer + merchant ID)

        // Find users near a given location
        //ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        //userQuery.whereEqualTo("objectId", getIntent().getExtras().getString("customersObjectId"));

        // Find devices associated with these users
        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
        //pushQuery.whereMatchesQuery("user", userQuery);
        pushQuery.whereEqualTo("channels", "Consumer");
        pushQuery.whereEqualTo("deviceId", getIntent().getExtras().getString("customersObjectId"));
        try {
            JSONObject data = new JSONObject("{\"header\": \"" + "Order Completed" + "\","
                    + "\"action\": \"com.enormous.pkpizzas.consumer.UPDATE_STATUS\","
                    + "\"myObjectId\": \""+ ParseUser.getCurrentUser().getObjectId() + "\","
                    + "\"merchantUserId\": \""+ ParseUser.getCurrentUser().getObjectId() + "\","
                    + "\"customerObjectId\": \""+ getIntent().getExtras().getString("customersObjectId") + "\","
                    + "\"merchantCategory\": \""+ ParseUser.getCurrentUser().getString("brandCategory").toLowerCase().replace(" ", "") + "\","
                    + "\"brandName\": \""+ ParseUser.getCurrentUser().getString("brandName") + "\"}");
            //Send Push notification
            ParsePush push = new ParsePush();
            push.setQuery(pushQuery);
            //push.setChannel(customerObjectId+merchantUserId);
            push.setData(data);
            push.sendInBackground(new SendCallback() {

                @Override
                public void done(ParseException e) {
                    // TODO Auto-generated method stub
                    if(e==null){
                        //Toast.makeText(ChatMessageActivity.this, "DONE", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(CartActivity.this, ""+e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private String calculateTotalCost() {
        double totalCost=0;
        for (int i=0; i<selectedProducts.size(); i++){
            totalCost = totalCost + ((selectedProducts.get(i).getCost()+selectedProducts.get(i).getTax()+selectedProducts.get(i).getShippingCost())*selectedProducts.get(i).getSelectedQuantity());
        }
        return Double.toString(totalCost);
    }

    private String createProductsString() {
        ArrayList<String> Products = new ArrayList<String>();
        Products.clear();
        ArrayList<String> ProductsString = new ArrayList<String>();
        ProductsString.clear();
        String products=null;

        for (int i=0; i<selectedProducts.size(); i++){
            if (!Products.contains(selectedProducts.get(i).getName())) Products.add(selectedProducts.get(i).getName());
        }
        for (int i=0; i<Products.size(); i++) {
            ProductsString.add(i,Products.get(i)+" (");
        }

        for (int i=0; i<selectedProducts.size(); i++){
            for (int j=0; j<Products.size(); j++){
                if (selectedProducts.get(i).getName().equals(Products.get(j))){
                    ProductsString.set(j, ProductsString.get(j) +" "+ selectedProducts.get(i).getSelectedOption()+" ["+selectedProducts.get(i).getSelectedQuantity()+"],");
                }
            }
        }
        for (int i=0; i<ProductsString.size(); i++) {
            if (i==0) products = ProductsString.get(i) +" )"+"\r\n";
            else if (i==(ProductsString.size()-1)) products = products + ProductsString.get(i) +" )";
            else products = products + ProductsString.get(i) +" )"+"\r\n";
        }
        return products;
    }

    private void findViews() {
		actionBar = getActionBar();
		cartListView = (ListView) findViewById(R.id.cartListView);
		nothingFoundLinearLayout = (LinearLayout) findViewById(R.id.nothingFoundLinearLayout);
		noInternetLinearLayout = (LinearLayout) findViewById(R.id.noInternetLinearLayout);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.scale_in, R.anim.slide_out_right);
	}

	private void setUpCart() {
		selectedProducts.clear();
		selectedProductsParse.clear();
		noInternetLinearLayout.setVisibility(View.GONE);
		if (Utils.isConnectedToInternet(this)) {
			cartListView.setVisibility(View.GONE);
			nothingFoundLinearLayout.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
			//get selectedProducts from Parse
			ParseQuery<ParseObject> query = ParseQuery.getQuery("ShoppingCart");
			query.whereEqualTo("userObjectId", getIntent().getExtras().getString("customersObjectId"));
			query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
			query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> parseObjects, ParseException e) {
					if (e == null) {
						Log.i("TEST", parseObjects.size() + "");
						//keep track of all the parseObjects so that items can be removed quickly (without querying the database)
						selectedProductsParse.addAll(parseObjects);
						for (ParseObject object : parseObjects) {
							String addr ="";
							if(object.getString("address")!=null){
								addr = object.getString("address");
							}
							selectedProducts.add(new Product(
									object.getObjectId(),
									object.getString("productId"),
									object.getString("brandName"),
									object.getString("productName"),
									object.getString("productType"),
									object.getString("productPicture"),
									object.getInt("productCost"),
									object.getInt("productShippingCost"),
									object.getInt("productTax"),
									object.getString("productDescription"),
									object.getInt("productStock"),
									(ArrayList<String>) object.get("productOptions"),
									object.getInt("selectedQuantity"),
									object.getString("selectedOption"),
									object.getBoolean("checkOut"),
									addr,
									(ArrayList<Integer>) object.get("productOptionsCost")));

						}
						progressBar.setVisibility(View.GONE);
						//now set cartAdapter and update order details
						CartAdapter adapter = new CartAdapter();
						cartListView.setAdapter(adapter);
						updateOrder();
					}
					else {
						e.printStackTrace();
						Toast.makeText(CartActivity.this, "An error occurred while fetching your cart info.", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		else {
			noInternetLinearLayout.setVisibility(View.VISIBLE);
			cartListView.setVisibility(View.GONE);
			nothingFoundLinearLayout.setVisibility(View.GONE);
		}
	}

	private void setUpHeader(View header) {
		cartHeaderTextView = (TextView) header.findViewById(R.id.cartHeaderTextView);
	}

	private void setUpFooter(View footer) {
		itemTotalTextView = (TextView) footer.findViewById(R.id.itemTotalTextView);
		shippingTotalTextView = (TextView) footer.findViewById(R.id.shippingTextView);
		taxTextView = (TextView) footer.findViewById(R.id.taxTextView);
		orderTotalTextView = (TextView) footer.findViewById(R.id.orderTotalTextView);
		merchantNoteEditText = (EditText) footer.findViewById(R.id.merchantNoteEditText);
		checkOutButton = (Button)footer.findViewById(R.id.checkOutButton);
	}

	private void updateOrder() {
		int totalItems = 0;
		for (Product product : selectedProducts) {
			totalItems += product.getSelectedQuantity();
		}
		if (totalItems == 0) {
			cartListView.setVisibility(View.GONE);
			nothingFoundLinearLayout.setVisibility(View.VISIBLE);
		}
		else {
			cartListView.setVisibility(View.VISIBLE);
			nothingFoundLinearLayout.setVisibility(View.GONE);
			String totalItemsHtml = "Total items in cart: <b>" + totalItems + "</b>";
			cartHeaderTextView.setText(Html.fromHtml(totalItemsHtml));
			computeOrderTotal();
		}

	}

	private void computeOrderTotal() {
		int itemTotal = 0;
		int orderTotal = 0;
		int shipping  = 0;
		int tax = 0;

		for (Product product : selectedProducts) {
			int selectedQuantity = product.getSelectedQuantity();
			itemTotal += product.getCost() * selectedQuantity;
			shipping += product.getShippingCost() * selectedQuantity;
			tax += product.getTax() * selectedQuantity;

			if(product.getAddress()!=null)
				Address = selectedProducts.get(0).getAddress();
		}

		orderTotal = itemTotal + shipping + tax;

		//update views
		String itemTotalHtml = CURRENCY_FORMATTER.format(itemTotal) + "<small> INR</small>";
		String shippingHtml = CURRENCY_FORMATTER.format(shipping) + "<small> INR</small>";
		String taxHtml = CURRENCY_FORMATTER.format(tax) + "<small> INR</small>";
		String orderTotalHtml = CURRENCY_FORMATTER.format(orderTotal) + "<small> INR</small>";
		itemTotalTextView.setText(Html.fromHtml(itemTotalHtml));
		shippingTotalTextView.setText(Html.fromHtml(shippingHtml));
		taxTextView.setText(Html.fromHtml(taxHtml));
		orderTotalTextView.setText(Html.fromHtml(orderTotalHtml));
		merchantNoteEditText.setText(Address);
		Log.d("TAG", selectedProducts.get(0).getAddress()+" Addresssss");
	}

	private class CartAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return selectedProducts.size();
		}

		@Override
		public Object getItem(int i) {
			return selectedProducts.get(i);
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(final int i, View convertView, ViewGroup viewGroup) {
			View row = convertView;
			ViewHolder holder = null;
			if (row == null) {
				row = getLayoutInflater().inflate(R.layout.listview_cart_item, viewGroup, false);
				holder = new ViewHolder(row);
				row.setTag(holder);
			} else {
				holder = (ViewHolder) row.getTag();
			}
			final Product selectedProduct = selectedProducts.get(i);
			holder.productNameTextView.setText(selectedProduct.getName());
			holder.productCostTextView.setText(CURRENCY_FORMATTER.format(selectedProduct.getCost()));
			holder.productOptionTextView.setText(selectedProduct.getSelectedOption());
			holder.productQuantityTextView.setText(selectedProduct.getSelectedQuantity() + "");
			ImageLoader.getInstance().displayImage(CartActivity.this, selectedProduct.getPictureURL(), holder.productImageView, false, 300, 300, 0);
			View.OnClickListener onClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					switch (view.getId()) {
					case R.id.removeButton:
						removeItemFromCart(selectedProduct, selectedProductsParse.get(i), CartAdapter.this);
						break;
					case R.id.editButton:
						editItemInCart(selectedProduct, selectedProductsParse.get(i), CartAdapter.this);
						break;
					case R.id.productImageViewContainer:
						/*Intent goToProductInfo = new Intent(CartActivity.this, ProductInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("selectedPos", 0);
						ArrayList<Product> products = new ArrayList<Product>();
						products.add(selectedProduct);
						bundle.putParcelableArrayList("products", products);
						goToProductInfo.putExtras(bundle);
						startActivity(goToProductInfo);
						overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);*/
					}
				}
			};
			holder.removeButton.setOnClickListener(onClickListener);
			holder.editButton.setOnClickListener(onClickListener);
			holder.productImageViewContainer.setOnClickListener(onClickListener);

			return row;
		}
	}

	private static class ViewHolder {

		TextView productNameTextView;
		ImageView productImageView;
		FrameLayout productImageViewContainer;
		TextView productQuantityTextView;
		TextView productOptionTextView;
		TextView productCostTextView;
		Button removeButton;
		Button editButton;

		public ViewHolder(View row) {
			productNameTextView = (TextView) row.findViewById(R.id.productNameTextView);
			productImageView = (ImageView) row.findViewById(R.id.productImageView);
			productImageViewContainer = (FrameLayout) row.findViewById(R.id.productImageViewContainer);
			productQuantityTextView = (TextView) row.findViewById(R.id.productQuantittyTextView);
			productOptionTextView = (TextView) row.findViewById(R.id.productOptionTextView);
			productCostTextView = (TextView) row.findViewById(R.id.productCostTextView);
			removeButton = (Button) row.findViewById(R.id.removeButton);
			editButton = (Button) row.findViewById(R.id.editButton);
		}
	}

	private void removeItemFromCart(final Product productToRemove, final ParseObject parseObjectToRemove, final CartAdapter adapter) {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Removing item...");
		progressDialog.show();

		//first, remove item from Parse
		ParseQuery<ParseObject> delRequest = ParseQuery.getQuery("ShoppingCart");
		delRequest.whereEqualTo("objectId", productToRemove.getProductObjectId());	
		//delRequest.whereEqualTo("productId", productToRemove.getProductId());	
		delRequest.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject obj : objects){
						obj.deleteInBackground(new DeleteCallback() {

							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								//now, delete item from selectedProducts list
								selectedProducts.remove(productToRemove);
								adapter.notifyDataSetChanged();
								updateOrder();
								final Date date = new Date();
								ParseQuery<ParseObject> query = ParseQuery.getQuery("Checkin");
								query.whereEqualTo("userObjectId", getIntent().getExtras().getString("customersObjectId"));
								query.whereEqualTo("brandObjectId", MainScreenActivity.CURRENT_USER.getObjectId());
								query.findInBackground(new FindCallback<ParseObject>() {

									@Override
									public void done(List<ParseObject> checkins, ParseException e) {
										if(e==null){
											for(ParseObject checkin : checkins){
												int num = checkin.getInt("CartItems");
												if(num>0){
													num = num-1;
													checkin.put("CartItems", num);
													checkin.put("date", date);
													checkin.saveInBackground();}
											}
										}
									}
								});
							}
						});
					}

				}
				else {
					e.printStackTrace();
					Toast.makeText(CartActivity.this, "An error occurred while removing the item.", Toast.LENGTH_SHORT).show();
				}
				progressDialog.dismiss();

			}
		});
	}

	private void editItemInCart(final Product productToEdit, final ParseObject parseObjectToEdit, final CartAdapter adapter) {
		AddToCartFragment editItemDialog = new AddToCartFragment();
		Bundle bundle = new Bundle();
		bundle.putString("dialogTitle", "Edit item");
		bundle.putString("positiveButtonTitle", "Save");
		bundle.putString("customersObjectId", getIntent().getExtras().getString("customersObjectId"));
		bundle.putInt("selectedQuantity", productToEdit.getSelectedQuantity());
		bundle.putString("selectedOption", productToEdit.getSelectedOption());
		bundle.putParcelable("selectedProduct", productToEdit);
		editItemDialog.setArguments(bundle);
		editItemDialog.setOnAddToCartDialogListener(new AddToCartFragment.AddToCartDialogListener() {

			@Override
			public void onFinishAddToCartDialog(final int selectedCost,final String selectedOption, final int selectedQuantity) {
				final ProgressDialog progressDialog = new ProgressDialog(CartActivity.this);
				progressDialog.setCancelable(false);
				progressDialog.setMessage("Saving changes...");
				progressDialog.show();

				//first, make changes in Parse
				parseObjectToEdit.put("productCost", selectedCost);
				parseObjectToEdit.put("selectedOption", selectedOption);
				parseObjectToEdit.put("selectedQuantity", selectedQuantity);
				parseObjectToEdit.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							//now, edit the corresponding product in selectedProducts
							productToEdit.setSelectedCost(selectedCost);
							productToEdit.setSelectedOption(selectedOption);
							productToEdit.setSelectedQuantity(selectedQuantity);
							adapter.notifyDataSetChanged();
							updateOrder();
						}
						else {
							e.printStackTrace();
							Toast.makeText(CartActivity.this, "An error occurred while saving changes...", Toast.LENGTH_SHORT).show();
						}
						progressDialog.dismiss();
					}
				});
			}
		});
		editItemDialog.show(getFragmentManager(), "editItemDialog");
	}
}
