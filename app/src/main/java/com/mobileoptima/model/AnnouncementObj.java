package com.mobileoptima.model;

import java.io.Serializable;

public class AnnouncementObj implements Serializable {
	public int ID;
	public String subject;
	public String message;
	public String announcedDate;
	public String announcedTime;
	public String announcedBy;
	public String announcedByImageURL;
	public boolean isSeen;
	public boolean isActive;
}
