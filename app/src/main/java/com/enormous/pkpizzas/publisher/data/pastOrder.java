package com.enormous.pkpizzas.publisher.data;

import android.os.Parcel;
import android.os.Parcelable;

public class pastOrder implements Parcelable {

    private String objectId;
    private String brandObjectId;
    private String customerName;
    private String customerProfilePictureUrl;
    private String userObjectId;
    private String products;
    private String totalCost;
    private String orderDate;

	//Constructor for pastOrder
	public pastOrder(String objectId, String brandObjectId, String customerName, String customerProfilePictureUrl, String userObjectId, String products, String totalCost, String orderDate) {
        this.objectId = objectId;
        this.brandObjectId=brandObjectId;
		this.customerName = customerName;
        this.customerProfilePictureUrl = customerProfilePictureUrl;
        this.userObjectId = userObjectId;
        this.products = products;
        this.totalCost = totalCost;
        this.orderDate = orderDate;
	}

    public String getObjectId() {
        return objectId;
    }

	public String getBrandObjectId(){ return brandObjectId;	}

	public String getCustomerName() {
		return customerName;
	}

    public String getCustomerProfilePictureUrl() { return customerProfilePictureUrl; }

    public String getUserObjectId(){ return userObjectId;	}

    public String getProducts() {
        return products;
    }

    public String getTotalCost() { return totalCost; }

    public String getOrderDate(){ return orderDate;	}


	//Parcelling
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectId);
		dest.writeString(brandObjectId);
		dest.writeString(customerName);
        dest.writeString(customerProfilePictureUrl);
        dest.writeString(userObjectId);
        dest.writeString(products);
        dest.writeString(totalCost);
        dest.writeString(orderDate);
	}

	public pastOrder(Parcel in) {
        this.objectId = in.readString();
		this.brandObjectId = in.readString();
		this.customerName = in.readString();
        this.customerProfilePictureUrl = in.readString();
        this.userObjectId = in.readString();
        this.products = in.readString();
        this.totalCost = in.readString();
        this.orderDate = in.readString();
	}

	public static final Creator<pastOrder> CREATOR = new Creator<pastOrder>() {
		@Override
		public pastOrder createFromParcel(Parcel source) {
			return new pastOrder(source);
		}
		@Override
		public pastOrder[] newArray(int size) {
			return new pastOrder[size];
		}
	};

}
