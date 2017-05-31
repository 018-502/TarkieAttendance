package com.mobileoptima.model;

import java.util.ArrayList;

public class EntryObj {

	public String ID;
	public String dDate;
	public String dTime;
	public FormObj form;
	public StoreObj store;
	public String referenceNo;
	public String syncBatchID;
	public String dateSubmitted;
	public String timeSubmitted;
	public boolean isFromWeb;
	public boolean isSubmit;
	public boolean isDelete;
	public boolean isCheck;
	public boolean isHighlight;
	public ArrayList<FieldObj> fieldList;
}
