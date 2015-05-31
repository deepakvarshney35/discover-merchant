package com.enormous.pkpizzas.publisher.data;

import java.util.Date;

public class Customers {


	private String name;
	private String phone;
	private String email;
	private int id;
	private String profilePicture;
	private String userObjectId;
	private Date updatedAt;
	private int SharedOffer;
	private int CartItems;
	public Customers(){
        
    }
	public Customers(String name,String phone,  String email, String profilePictureUrl , String userObjectId, Date date,int SharedOffer,int CartItems) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.profilePicture = profilePictureUrl;
		this.userObjectId = userObjectId;
		this.updatedAt = date;
		this.SharedOffer=SharedOffer;
		this.CartItems = CartItems;
	}
	public Customers(String name,String phone,  String email) {
		this.name = name;
		this.phone = phone;
		this.email = email;
	}
	public Customers(int id,String name,  String phone) {
		this.id = id;
		this.phone = phone;
		this.name = name;
	}
	public Customers(String name,  String phone) {
		this.name = name;
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	
	public Date getUpdatedAt(){
		return updatedAt;
	}
	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}
	public int getId(){
		return id;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setID(int id){
		this.id = id;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
	public String getProfilePictureUrl() {
		return profilePicture;
	}
	public String getUserObjectId(){
		return userObjectId;
	}
	
	public int getCartItemsNumber(){
		return CartItems;
	}
	public int getSharedOfferNumber(){
		return SharedOffer;
	}
}
