package com.mobileoptima.model;

import java.util.ArrayList;

public class EntryObj {

	public String ID;
	public String dDate;
	public String dTime;
	public String referenceNo;
	public String dateSubmitted;
	public String timeSubmitted;
	public String syncBatchID;
	public StoreObj store;
	public FormObj form;
	public ArrayList<FieldObj> fieldList;
	public boolean isFromWeb;
	public boolean isSubmit;
	public boolean isDelete;
	public boolean isCheck;
	public boolean isHighlight;
}
