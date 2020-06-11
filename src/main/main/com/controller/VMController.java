package com.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vending.Coin;
import com.vending.Inventory;
import com.vending.Item;

@RestController
@RequestMapping("/vendingMachine")
public class VMController {
	
	private Inventory<Coin> cashInventory = new Inventory<Coin>();
    private Inventory<Item> itemInventory = new Inventory<Item>();  
    private long totalSales;
    private Item currentItem;
    private long currentBalance; 

	
	private void initialize(){       
        //initialize machine with 5 coins of each denomination
        //and 5 cans of each Item       
        for(Coin c : Coin.values()){
            cashInventory.put(c, 5);
        }
       
        for(Item i : Item.values()){
            itemInventory.put(i, 5);
        }
       
    }
	
	@RequestMapping(method = RequestMethod.GET, value = "/{item}")
	    long selectItemAndGetPrice(Item item) {
		if(itemInventory.hasItem(item)){
            currentItem = item;
            return currentItem.getPrice();
        }
		return 0;
	    }
	 
	@RequestMapping(method = RequestMethod.POST, value = "/{coin}")
	public void insertCoin(Coin coin)
	{
		currentBalance = currentBalance + coin.getDenomination();
        cashInventory.add(coin);
		
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/refund")
	public List<Coin> refund()
	{
		
			List<Coin> refund = getChange(currentBalance);
	        updateCashInventory(refund);
	        currentBalance = 0;
	        currentItem = null;
	        return refund;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/collectItemAndChange")
	public HashMap<Item, List<Coin>> collectItemAndChange()
	{
		 	Item item = collectItem();
	        totalSales = totalSales + currentItem.getPrice();
	        List<Coin> change = collectChange();
	        HashMap<Item, List<Coin>> collectItemAndChange = new HashMap<Item, List<Coin>>();
	        collectItemAndChange.put(item, change);
	        return collectItemAndChange;
			
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/reset")
	public void reset() {
		
		    cashInventory.clear();
	        itemInventory.clear();
	        totalSales = 0;
	        currentItem = null;
	        currentBalance = 0;
	}
	
	private List<Coin> getChange(long amount){
        List<Coin> changes = Collections.EMPTY_LIST;
       
        if(amount > 0){
            changes = new ArrayList<Coin>();
            long balance = amount;
            while(balance > 0){
                if(balance >= Coin.QUARTER.getDenomination() 
                            && cashInventory.hasItem(Coin.QUARTER)){
                    changes.add(Coin.QUARTER);
                    balance = balance - Coin.QUARTER.getDenomination();
                    continue;
                   
                }else if(balance >= Coin.DIME.getDenomination() 
                                 && cashInventory.hasItem(Coin.DIME)) {
                    changes.add(Coin.DIME);
                    balance = balance - Coin.DIME.getDenomination();
                    continue;
                   
                }else if(balance >= Coin.NICKLE.getDenomination() 
                                 && cashInventory.hasItem(Coin.NICKLE)) {
                    changes.add(Coin.NICKLE);
                    balance = balance - Coin.NICKLE.getDenomination();
                    continue;
                   
                }else if(balance >= Coin.PENNY.getDenomination() 
                                 && cashInventory.hasItem(Coin.PENNY)) {
                    changes.add(Coin.PENNY);
                    balance = balance - Coin.PENNY.getDenomination();
                    continue;
                   
                }else{
                    System.out.println("Sufficient Change is not avaialbe, Please try another product");
                }
            }
        }
       
        return changes;
    }
	
	private void updateCashInventory(List<Coin> change) {
        for(Coin c : change){
            cashInventory.deduct(c);
        }
    }
	
	 private Item collectItem() {
	        if(isFullPaid()){
	            if(hasSufficientChange()){
	                itemInventory.deduct(currentItem);
	                return currentItem;
	            }           

	        System.out.println("Not Sufficient change in Inventory");
	           
	        }
	        long remainingBalance = currentItem.getPrice() - currentBalance;
			return currentItem;
	    }
	 private List<Coin> collectChange() {
	        long changeAmount = currentBalance - currentItem.getPrice();
	        List<Coin> change = getChange(changeAmount);
	        updateCashInventory(change);
	        currentBalance = 0;
	        currentItem = null;
	        return change;
	    }
	 private boolean isFullPaid() {
	        if(currentBalance >= currentItem.getPrice()){
	            return true;
	        }
	        return false;
	    }
	 private boolean hasSufficientChange(){
	        return hasSufficientChangeForAmount(currentBalance - currentItem.getPrice());
	    }
	   
	    private boolean hasSufficientChangeForAmount(long amount){
	        boolean hasChange = true;
	        try{
	            getChange(amount);
	        }catch(Exception ex){
	            return hasChange = false;
	        }
	       
	        return hasChange;
	    }
	   
}
