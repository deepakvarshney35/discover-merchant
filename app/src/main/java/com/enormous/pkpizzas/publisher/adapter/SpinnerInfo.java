package com.enormous.pkpizzas.publisher.adapter;

public class SpinnerInfo {
    private String itemName;
    private int itemLogo;
    
    public SpinnerInfo(String cName, int logoImage)
    {
        itemName = cName;
        itemLogo = logoImage;
    }
    public String getItemName()
    {
        return itemName;
    }
    public int getItemLogo()
    {
        return itemLogo;
    }
    public String toString()
    {
        return itemName;
    }
}