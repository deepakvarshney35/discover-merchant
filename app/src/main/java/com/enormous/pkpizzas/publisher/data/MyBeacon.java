package com.enormous.pkpizzas.publisher.data;

public class MyBeacon {
	private String BeaconUUID;
	private int BeaconMajor;
	private int BeaconMinor;
	private int BeaconProximity;
	private String Address;

	public MyBeacon(String BeaconUUID,  int BeaconMajor, int BeaconMinor,int BeaconProximity,String Address) {
		this.BeaconUUID = BeaconUUID;
		this.BeaconMajor = BeaconMajor;
		this.BeaconMinor = BeaconMinor;
		this.Address = Address;
		this.BeaconProximity = BeaconProximity;
	}
	public String getBeaconUUID(){
		return BeaconUUID;
	}
	public int getBeaconMajor(){
		return BeaconMajor;
	}
	public int getBeaconMinor(){
		return BeaconMinor;
	}
	public int getBeaconProximity(){
		return BeaconProximity;
	}
	public String getAddress() {
		return Address;
	}	
}