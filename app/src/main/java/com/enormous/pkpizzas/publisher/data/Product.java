package com.enormous.pkpizzas.publisher.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Product implements Parcelable {

    private String objectId;
    private String brandObjectId;
    private String productId;
    private String brandName;
    private String name;
    private String category;
    private String pictureURL;
    private int cost;
    private int shippingCost;
    private int tax;
    private String description;
    private int numberOfItemsInStock;
    private ArrayList<String> options;
    private ArrayList<String> optionsCost;
    private int selectedQuantity;
    private String selectedOption;
    private boolean checkOut;
    private String Address;
    
	public Product(String brandObjectId,String category, String name, int cost, String objectId,
			String description, int shippingCost, int tax, int numberOfItemsInStock,
			ArrayList<String> options, String pictureURL,  ArrayList<Integer> optionsCost) {
		this.brandObjectId = brandObjectId;
        this.name = name;
        this.category = category;
        this.pictureURL = pictureURL;
        this.cost = cost;
        this.shippingCost = shippingCost;
        this.tax = tax;
        this.description = description;
        this.numberOfItemsInStock = numberOfItemsInStock;
        this.options = options;
        this.objectId = objectId;
        ArrayList<String> newList = new ArrayList<String>(optionsCost.size()); 
		for (Integer myInt : optionsCost) { 
			newList.add(String.valueOf(myInt)); 
		}
		this.optionsCost = newList;	}

    
    public Product(String objectId, String brandName, String name, String category, String pictureURL, int cost, int shippingCost, int tax,  String description, int numberOfItemsInStock, ArrayList<String> options, ArrayList<Integer> optionsCost) {
        this.objectId = objectId;
        this.brandName = brandName;
        this.name = name;
        this.category = category;
        this.pictureURL = pictureURL;
        this.cost = cost;
        this.shippingCost = shippingCost;
        this.tax = tax;
        this.description = description;
        this.numberOfItemsInStock = numberOfItemsInStock;
        this.options = options;
        ArrayList<String> newList = new ArrayList<String>(optionsCost.size()); 
		for (Integer myInt : optionsCost) { 
			newList.add(String.valueOf(myInt)); 
		}
		this.optionsCost = newList;
		}

    //Constructor for ShoppingCart
    public Product(String objectId, String productId, String brandName, String name, String category, String pictureURL, int cost, int shippingCost, int tax,  String description, int numberOfItemsInStock, ArrayList<String> options, int selectedQuantity, String selectedOption ,boolean checkOut,String Address, ArrayList<Integer> optionsCost) {
        this.objectId = objectId;
        this.productId = productId;
        this.brandName = brandName;
        this.name = name;
        this.category = category;
        this.pictureURL = pictureURL;
        this.cost = cost;
        this.shippingCost = shippingCost;
        this.tax = tax;
        this.description = description;
        this.numberOfItemsInStock = numberOfItemsInStock;
        this.options = options;
        this.selectedQuantity = selectedQuantity;
        this.selectedOption = selectedOption;
        this.checkOut = checkOut;
        ArrayList<String> newList = new ArrayList<String>(optionsCost.size()); 
		for (Integer myInt : optionsCost) { 
			newList.add(String.valueOf(myInt)); 
		}
		this.optionsCost = newList;
		this.Address = Address;
		}
    public String getAddress(){
    	return Address;
    }
    
    public boolean getCheckOutStatus(){
    	return checkOut;
    }
    
    public String getProductObjectId() {
        return objectId;
    }
    
    public String getBrandObjectId(){
    	return brandObjectId;
    }
    public String getPictureUrl(){
    	return pictureURL;
    }
    public String getProductId() { return productId; }

    public String getBrandName() {
        return brandName;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public int getCost() {
        return cost;
    }

    public int getShippingCost() {
        return shippingCost;
    }

    public int getTax() {
        return tax;
    }
    
    public ArrayList<String> getOptionCost(){
    	return optionsCost;
    }
    public String getDescription() {
        return description;
    }

    public int getNumberOfItemsInStock() {
        return numberOfItemsInStock;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
    public void setSelectedCost(int selectedCost) {
		this.cost = selectedCost;
	}
    //Parcelling
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectId);
        dest.writeString(productId);
        dest.writeString(brandName);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(pictureURL);
        dest.writeString(description);
        dest.writeInt(cost);
        dest.writeInt(shippingCost);
        dest.writeInt(tax);
        dest.writeInt(numberOfItemsInStock);
        dest.writeStringList(options);
        dest.writeInt(selectedQuantity);
        dest.writeString(selectedOption);
        dest.writeStringList(optionsCost);
    }

    public Product(Parcel in) {
        this.objectId = in.readString();
        this.productId = in.readString();
        this.brandName = in.readString();
        this.name = in.readString();
        this.category = in.readString();
        this.pictureURL = in.readString();
        this.description = in.readString();
        this.cost = in.readInt();
        this.shippingCost = in.readInt();
        this.tax = in.readInt();
        this.numberOfItemsInStock = in.readInt();
        this.options = new ArrayList<String>();
        in.readStringList(this.options);
        this.selectedQuantity = in.readInt();
        this.selectedOption = in.readString();
		this.optionsCost = new ArrayList<String>();
		in.readStringList(this.optionsCost);
    }


	public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }
        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

}

/*package com.enormous.pkpizzas.publisher.data;

import java.util.ArrayList;

import com.parse.ParseFile;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

	private String brandObjectId;
	private String productDescription;
	private int productShippingCost;
	private int productTax;
	private int productStock;
	private ArrayList<String> productOptions;
	private ParseFile productPicture;
	private String name;
	private String type;
	private int productColorid;
	private int cost;
	private String productObjectId;
	
	public Product(String brandObjectId, String name, String type, int productColorid, int cost, String productObjectId, String productDescription, int productShippingCost, int productTax, int productStock, ArrayList<String> productOptions, ParseFile parseFile) {
		this.brandObjectId = brandObjectId;
		this.name = name;
		this.type = type;
		this.productDescription = productDescription;
		this.productColorid = productColorid;
		this.cost = cost;
		this.productObjectId = productObjectId;
		this.productShippingCost = productShippingCost;
		this.productTax = productTax;
		this.productStock = productStock;
		this.productOptions = productOptions;
		this.productPicture = parseFile;
	}
	public ArrayList<String> getOptions(){
		return productOptions;
	}
	
	public ParseFile getPicture(){
		return productPicture;
	}
	
	public int getShppingCost() {
		return productShippingCost;
	}
	public int getTax() {
		return productTax;
	}
	public int getStock() {
		return productStock;
	}
	public String getDescription() {
		return productDescription;
	}

	public String getBrandObjectId() {
		return brandObjectId;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public int getColorId() {
		return productColorid;
	}

	public int getCost() {
		return cost;
	}

	public String getProductObjectId() {
		return productObjectId;
	}
	
	//Parcelling
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(brandObjectId);
		dest.writeString(name);
		dest.writeString(type);
		dest.writeInt(productColorid);
		dest.writeString(productObjectId);
		dest.writeInt(cost);
	}
	
	public Product(Parcel in) {
		this.brandObjectId = in.readString();
		this.name = in.readString();
		this.type = in.readString();
		this.productColorid = in.readInt();
		this.productObjectId = in.readString();
		this.cost = in.readInt();
	}
	
	public Product(String objectId, String productObjectId, String productName,
			String productType, String productPicture, int productCost, int productShippingCost, int productTax,
			String productDescription, int productStock, ArrayList<String> productOptions, int selectedQuantity,
			String selectedOption, boolean checkOut) {
		this.brandObjectId = objectId;
		this.name = productName;
		this.type = productType;
		this.productDescription = productDescription;
		this.productColorid = productColorid;
		this.cost = productCost;
		this.productObjectId = productObjectId;
		this.productShippingCost = productShippingCost;
		this.productTax = productTax;
		this.productStock = productStock;
		this.productOptions = productOptions;
		this.productPicture = productPicture;
	}

	public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
		@Override
		public Product createFromParcel(Parcel source) {
			return new Product(source);
		}
		@Override
		public Product[] newArray(int size) {
			return new Product[size];
		}
	};
	
}
*/