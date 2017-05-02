package com.mobileoptima.constant;

public class Module {
	public enum Action {
		AUTHORIZE_DEVICE,
		LOGIN,
		UPDATE_MASTER_FILE,
		SEND_BACKUP,
		SYNC_DATA,
		VALIDATE_SERVER_TIME
	}

	public static String getTitle(Action action) {
		String title = null;
		switch(action) {
			case AUTHORIZE_DEVICE:
				title = "Authorization";
				break;
			case LOGIN:
				title = "Login";
				break;
			case UPDATE_MASTER_FILE:
				title = "Update Master File";
				break;
			case SEND_BACKUP:
				title = "Send Back-up";
				break;
			case SYNC_DATA:
				title = "Sync Data";
				break;
			case VALIDATE_SERVER_TIME:
				title = "Validation of Server Time";
				break;
		}
		return title;
	}
}