package com.enormous.pkpizzas.publisher.data;

import com.parse.ParseFile;

public class ProductPhotos {

	private String brandObjectId;
	private ParseFile productPicture;
	
	public ProductPhotos(String brandObjectId, ParseFile parseFile) {
		this.brandObjectId = brandObjectId;
		this.productPicture = parseFile;
	}
	
	public ParseFile getPicture(){
		return productPicture;
	}
	
	public String getBrandObjectId() {
		return brandObjectId;
	}	
}
