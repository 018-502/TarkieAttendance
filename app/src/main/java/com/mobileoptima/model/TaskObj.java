package com.mobileoptima.model;

import java.util.ArrayList;

public class TaskObj extends TransactionObj {

	public String name;
	public String notes;
	public String status;
	public StoreObj store;
	public int notesLimit;
	public String endDate;
	public String startDate;
	public String webTaskID;
	public boolean isFromWeb;
	public boolean isCheckIn;
	public boolean isCheckOut;
	public ArrayList<EntryObj> entryList;
	public ArrayList<PhotoObj> photoList;
}
