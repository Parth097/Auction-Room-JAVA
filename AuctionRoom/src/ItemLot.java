import net.jini.core.entry.*;

import java.util.ArrayList;
import java.util.Collections;

public class ItemLot implements Entry {

    // Variables
    public Integer itemID;
    public String itemName;
    public String itemDescription;
    public String itemSeller;
    public String itemBuyer;
    public Double buyNow;
    public ArrayList<Integer> itemBids;
    public Boolean isPurchased;
    public Boolean isDeleted;


    // No arg constructor
    public ItemLot() {
    }

    // Arg constructor
    public ItemLot(int lotID, String lotName, String lotSeller, String lotDescription, Double lotBuyNow, ArrayList<Integer> lotBid, Boolean purchased, Boolean deleted, String buyer) {
        itemID = lotID;
        itemName = lotName;
        itemSeller = lotSeller;
        itemDescription = lotDescription;
        buyNow = lotBuyNow;
        itemBids = lotBid;
        isPurchased = purchased;
        isDeleted = deleted;
        itemBuyer = buyer;


    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemSeller() {
        return itemSeller;
    }

    public void setItemSeller(String itemSeller) {
        this.itemSeller = itemSeller;
    }

    public ArrayList<Integer> getItemBids() {
        return itemBids;
    }

    public void setItemBids(ArrayList<Integer> itemBids) {
        this.itemBids = itemBids;
    }

    public void addBid(Integer value) {
        itemBids.add(value);
    }

    public Integer returnHighestBid() {
        return Collections.max(itemBids);
    }

    public String returnItemBuyer(String name) {
        return this.itemBuyer;
    }

    public String showItemDetails() {
        String item;
        item = "\n ID: " + itemID + "\n"
                + " Item: " + itemName + "\n"
                + " Description: " + itemDescription + "\n"
                + " Seller Name: " + itemSeller + "\n"
                + " Buy Now: £" + buyNow + "\n"
                + " \n Current Bids: " + itemBids + "\n";
        return item;
    }

}
