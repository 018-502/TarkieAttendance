package com.mobileoptima.core;

import com.codepan.database.Condition;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteQuery;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.EntriesSearchType;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.constant.InventoryType;
import com.mobileoptima.constant.Status;
import com.mobileoptima.model.AnnouncementObj;
import com.mobileoptima.model.AnswerObj;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakObj;
import com.mobileoptima.model.BreakOutObj;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.ChoiceObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.FieldObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.IncidentReportObj;
import com.mobileoptima.model.InventoryObj;
import com.mobileoptima.model.LocationObj;
import com.mobileoptima.model.PageObj;
import com.mobileoptima.model.SearchObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;

import net.sqlcipher.Cursor;

import java.util.ArrayList;

import static com.codepan.database.Condition.Operator;

public class Data {

	public static ArrayList<InventoryObj> loadInventory(SQLiteAdapter db) {
		ArrayList<InventoryObj> inventoryList = new ArrayList<>();
		InventoryObj tracking = new InventoryObj();
		tracking.ID = "1";
		tracking.name = "Tracking";
		tracking.type = InventoryType.TRACKING;
		InventoryObj orders = new InventoryObj();
		orders.ID = "2";
		orders.name = "Orders";
		orders.type = InventoryType.ORDERS;
		InventoryObj pullOuts = new InventoryObj();
		pullOuts.ID = "3";
		pullOuts.name = "Pull-outs";
		pullOuts.type = InventoryType.PULL_OUTS;
		inventoryList.add(tracking);
		inventoryList.add(orders);
		inventoryList.add(pullOuts);
		return inventoryList;
	}

	public static ArrayList<FormObj> loadForms(SQLiteAdapter db) {
		ArrayList<FormObj> formList = new ArrayList<>();
		String groupID = TarkieLib.getGroupID(db);
		String table = Tables.getName(TB.FORMS);
		String query = "SELECT ID, name, logoUrl FROM " + table + " WHERE groupID = '" +
				groupID + "' AND isActive = 1";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			FormObj obj = new FormObj();
			obj.ID = cursor.getString(0);
			obj.name = cursor.getString(1);
			obj.logoUrl = cursor.getString(2);
			formList.add(obj);
		}
		cursor.close();
		return formList;
	}

	public static ArrayList<BreakObj> loadBreaks(SQLiteAdapter db) {
		ArrayList<BreakObj> breakList = new ArrayList<>();
		String table = Tables.getName(TB.BREAK);
		String query = "SELECT ID, name, duration FROM " + table;
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			BreakObj obj = new BreakObj();
			obj.ID = cursor.getString(0);
			obj.name = cursor.getString(1);
			obj.duration = cursor.getInt(2);
			obj.isDone = TarkieLib.isBreakDone(db, obj.ID);
			breakList.add(obj);
		}
		cursor.close();
		return breakList;
	}

	public static ArrayList<AttendanceObj> loadAttendance(SQLiteAdapter db) {
		ArrayList<AttendanceObj> attendanceList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		String i = Tables.getName(TB.TIME_IN);
		String o = Tables.getName(TB.TIME_OUT);
		String query = "SELECT i.ID, i.dDate, i.dTime, i.isTimeOut, o.ID, o.dDate, " +
				"o.dTime, o.signature FROM " + i + " as i LEFT JOIN " + o + " as o ON " +
				"o.timeInID = i.ID WHERE i.empID = '" + empID + "' ORDER BY i.ID DESC";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			String timeInID = cursor.getString(0);
			long totalBreak = TarkieLib.getBreakDuration(db, timeInID);
			AttendanceObj attendance = new AttendanceObj();
			TimeInObj in = new TimeInObj();
			in.ID = timeInID;
			in.dDate = cursor.getString(1);
			in.dTime = cursor.getString(2);
			in.isTimeOut = cursor.getInt(3) == 1;
			TimeOutObj out = new TimeOutObj();
			out.ID = cursor.getString(4);
			out.dDate = cursor.getString(5);
			out.dTime = cursor.getString(6);
			out.signature = cursor.getString(7);
			out.timeInID = timeInID;
			attendance.in = in;
			attendance.out = out;
			attendance.totalBreak = totalBreak;
			attendanceList.add(attendance);
		}
		cursor.close();
		return attendanceList;
	}

	public static ArrayList<LocationObj> loadLocations(SQLiteAdapter db) {
		ArrayList<LocationObj> locationList = new ArrayList<>();
		String table = Tables.getName(TB.LOCATION);
		String query = "SELECT ID, dDate, dTime, longitude, latitude, accuracy, " +
				"provider FROM " + table + " ORDER BY ID DESC";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			LocationObj obj = new LocationObj();
			obj.ID = cursor.getString(0);
			obj.dDate = cursor.getString(1);
			obj.dTime = cursor.getString(2);
			obj.longitude = cursor.getDouble(3);
			obj.latitude = cursor.getDouble(4);
			obj.accuracy = cursor.getFloat(5);
			obj.provider = cursor.getString(6);
			locationList.add(obj);
		}
		cursor.close();
		return locationList;
	}

	public static ArrayList<StoreObj> loadStores(SQLiteAdapter db, String search, String start,
												 boolean isAdded, int limit) {
		ArrayList<StoreObj> storeList = new ArrayList<>();
		String table = Tables.getName(TB.STORES);
		SQLiteQuery query = new SQLiteQuery();
		if(search != null) {
			query.add(new Condition("name", search, Operator.LIKE));
		}
		if(start != null) {
			query.add(new Condition("name", start, Operator.GREATER_THAN));
		}
		String condition = query.hasConditions() ? " WHERE " + query.getConditions() : "";
		String sql = "SELECT ID, name, address, radius, gpsLongitude, gpsLatitude FROM " +
				table + condition + " ORDER BY name LIMIT " + limit;
		Cursor cursor = db.read(sql);
		while(cursor.moveToNext()) {
			StoreObj store = new StoreObj();
			store.ID = cursor.getString(0);
			store.name = cursor.getString(1);
			store.address = cursor.getString(2);
			store.radius = cursor.getInt(3);
			store.longitude = cursor.getDouble(4);
			store.latitude = cursor.getDouble(5);
			String currentFirstChar = store.name.substring(0, 1);
			int position = cursor.getPosition();
			if(currentFirstChar.matches("[a-zA-Z]")) {
				if(position != 0) {
					StoreObj obj = storeList.get(position - 1);
					String lastFirstChar = obj.name.substring(0, 1);
					if(!lastFirstChar.equals(currentFirstChar)) {
						obj.isFooter = true;
						obj.footer = currentFirstChar;
						storeList.set(position - 1, obj);
						store.isHeader = true;
						store.header = currentFirstChar;
					}
				}
				else {
					if(isAdded) {
						if(start != null) {
							String lastFirstChar = start.substring(0, 1);
							if(!lastFirstChar.equals(currentFirstChar)) {
								store.isHeader = true;
								store.header = currentFirstChar;
							}
						}
					}
					else {
						store.isHeader = true;
						store.header = currentFirstChar;
					}
				}
			}
			else {
				if(position == 0) {
					store.isHeader = true;
					store.header = "#";
				}
			}
			storeList.add(store);
		}
		cursor.close();
		return storeList;
	}

	public static ArrayList<PageObj> loadPages(SQLiteAdapter db, String formID) {
		ArrayList<PageObj> pageList = new ArrayList<>();
		String table = Tables.getName(TB.FIELDS);
		String query = "SELECT ID, orderNo FROM " + table + " WHERE type = '" +
				FieldType.PB + "' AND formID = '" + formID + "' AND isActive = 1 " +
				"ORDER BY orderNo";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			PageObj obj = new PageObj();
			obj.tag = cursor.getString(0);
			int orderNo = cursor.getInt(1);
			int index = cursor.getPosition();
			if(pageList.size() > 0) {
				PageObj previous = pageList.get(index - 1);
				obj.start = previous.end + 2;
			}
			else {
				obj.start = 1;
			}
			obj.end = orderNo - 1;
			obj.orderNo = orderNo;
			pageList.add(obj);
		}
		if(!pageList.isEmpty()) {
			int end = TarkieLib.getLastOrderNo(db, formID);
			int index = pageList.size() - 1;
			PageObj last = pageList.get(index);
			if(end > last.orderNo) {
				PageObj obj = new PageObj();
				obj.tag = String.valueOf(end + 1);
				obj.start = last.end + 2;
				obj.orderNo = end;
				obj.end = end;
				pageList.add(obj);
			}
		}
		else {
			int start = TarkieLib.getFirstOrderNo(db, formID);
			int end = TarkieLib.getLastOrderNo(db, formID);
			PageObj obj = new PageObj();
			obj.tag = String.valueOf(end + 1);
			obj.start = start;
			obj.orderNo = end;
			obj.end = end;
			pageList.add(obj);
		}
		cursor.close();
		return pageList;
	}

	public static ArrayList<FieldObj> loadFields(SQLiteAdapter db, FormObj form,
												 EntryObj entry, PageObj page) {
		ArrayList<FieldObj> fieldList = new ArrayList<>();
		String table = Tables.getName(TB.FIELDS);
		String query = "SELECT ID, name, description, type, isRequired FROM " + table + " WHERE " +
				"formID = '" + form.ID + "' AND orderNo BETWEEN '" + page.start + "' AND '" +
				page.end + "' AND isActive = 1 ORDER BY orderNo";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			FieldObj field = new FieldObj();
			field.ID = cursor.getString(0);
			field.name = cursor.getString(1);
			field.description = cursor.getString(2);
			field.type = cursor.getString(3);
			field.isRequired = cursor.getInt(4) == 1;
			switch(field.type) {
				case FieldType.SEC:
				case FieldType.LAB:
				case FieldType.PB:
				case FieldType.LINK:
					field.isQuestion = false;
					break;
				default:
					field.isQuestion = true;
					field.answer = TarkieLib.getAnswer(db, entry, field);
					break;
			}
			fieldList.add(field);
		}
		cursor.close();
		return fieldList;
	}

	public static ArrayList<ChoiceObj> loadChoices(SQLiteAdapter db, String fieldID) {
		ArrayList<ChoiceObj> choiceList = new ArrayList<>();
		String table = Tables.getName(TB.CHOICES);
		String query = "SELECT ID, code, name FROM " + table + " WHERE fieldID = '" + fieldID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ChoiceObj obj = new ChoiceObj();
			obj.ID = cursor.getString(0);
			obj.code = cursor.getString(1);
			obj.name = cursor.getString(2);
			choiceList.add(obj);
		}
		cursor.close();
		return choiceList;
	}

	public static ArrayList<ImageObj> loadPhotos(SQLiteAdapter db) {
		ArrayList<ImageObj> imageList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		String table = Tables.getName(TB.PHOTO);
		String query = "SELECT ID, fileName FROM " + table + " WHERE empID = '" + empID + "' AND " +
				"isDelete = 0 AND isSignature = 0 ORDER BY ID DESC";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ImageObj obj = new ImageObj();
			obj.ID = cursor.getString(0);
			obj.fileName = cursor.getString(1);
			imageList.add(obj);
		}
		cursor.close();
		return imageList;
	}

	public static ArrayList<EntryObj> loadEntries(SQLiteAdapter db, SearchObj obj, int type) {
		ArrayList<EntryObj> entryList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new Condition("e.isDelete", false));
		query.add(new Condition("e.empID", empID));
		if(obj != null) {
			switch(type) {
				case EntriesSearchType.DATE:
					query.add(new Condition("e.dDate", obj.search));
					break;
//				case EntriesSearchType.STORE:
//					break;
				case EntriesSearchType.CATEGORY:
					query.add(new Condition("f.category", obj.search));
					break;
				case EntriesSearchType.FORM:
					query.add(new Condition("f.ID", obj.search));
					break;
				case EntriesSearchType.STATUS:
					switch(obj.search) {
						case Status.DRAFT:
							query.add(new Condition("isSubmit", false));
							break;
						case Status.SUBMITTED:
							query.add(new Condition("isSubmit", true));
							break;
					}
					break;
			}
		}
		String e = Tables.getName(TB.ENTRIES);
		String f = Tables.getName(TB.FORMS);
		String sql = "SELECT e.ID, e.dDate, e.dTime, e.isSubmit, e.referenceNo, f.ID, f.name, " +
				"f.logoUrl FROM " + e + " e, " + f + " f WHERE f.ID = e.formID " +
				"AND " + query.getConditions() + " ORDER BY e.ID DESC";
		Cursor cursor = db.read(sql);
		while(cursor.moveToNext()) {
			EntryObj entry = new EntryObj();
			entry.ID = cursor.getString(0);
			entry.dDate = cursor.getString(1);
			entry.dTime = cursor.getString(2);
			entry.isSubmit = cursor.getInt(3) == 1;
			entry.referenceNo = cursor.getString(4);
			FormObj form = new FormObj();
			form.ID = cursor.getString(5);
			form.name = cursor.getString(6);
			form.logoUrl = cursor.getString(7);
			entry.form = form;
			entryList.add(entry);
		}
		cursor.close();
		return entryList;
	}

	public static ArrayList<TimeInObj> loadTimeInSync(SQLiteAdapter db) {
		ArrayList<TimeInObj> timeInList = new ArrayList<>();
		String g = Tables.getName(TB.GPS);
		String i = Tables.getName(TB.TIME_IN);
		String query = "SELECT i.ID, i.empID, i.dDate, i.dTime, i.syncBatchID, i.storeID, g.gpsDate, " +
				"g.gpsTime, g.gpsLongitude, g.gpsLatitude FROM " + i + " i, " + g + " g " +
				"WHERE i.isSync = 0 AND g.ID = i.gpsID";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			TimeInObj in = new TimeInObj();
			in.ID = cursor.getString(0);
			in.empID = cursor.getString(1);
			in.dDate = cursor.getString(2);
			in.dTime = cursor.getString(3);
			in.syncBatchID = cursor.getString(4);
			in.storeID = cursor.getString(5);
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(6);
			gps.time = cursor.getString(7);
			gps.longitude = cursor.getDouble(8);
			gps.latitude = cursor.getDouble(9);
			in.gps = gps;
			timeInList.add(in);
		}
		cursor.close();
		return timeInList;
	}

	public static ArrayList<TimeOutObj> loadTimeOutSync(SQLiteAdapter db) {
		ArrayList<TimeOutObj> timeOutList = new ArrayList<>();
		String g = Tables.getName(TB.GPS);
		String o = Tables.getName(TB.TIME_OUT);
		String query = "SELECT o.ID, o.empID, o.dDate, o.dTime, o.syncBatchID, o.timeInID, " +
				"g.gpsDate, g.gpsTime, g.gpsLongitude, g.gpsLatitude FROM " + o + " o, " + g + " g " +
				"WHERE o.isSync = 0 AND g.ID = o.gpsID";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			TimeOutObj out = new TimeOutObj();
			out.ID = cursor.getString(0);
			out.empID = cursor.getString(1);
			out.dDate = cursor.getString(2);
			out.dTime = cursor.getString(3);
			out.syncBatchID = cursor.getString(4);
			out.timeInID = cursor.getString(5);
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(6);
			gps.time = cursor.getString(7);
			gps.longitude = cursor.getDouble(8);
			gps.latitude = cursor.getDouble(9);
			out.gps = gps;
			timeOutList.add(out);
		}
		cursor.close();
		return timeOutList;
	}

	public static ArrayList<BreakInObj> loadBreakInSync(SQLiteAdapter db) {
		ArrayList<BreakInObj> breakInList = new ArrayList<>();
		String g = Tables.getName(TB.GPS);
		String i = Tables.getName(TB.BREAK_IN);
		String query = "SELECT i.ID, i.empID, i.dDate, i.dTime, i.syncBatchID, g.gpsDate, " +
				"g.gpsTime, g.gpsLongitude, g.gpsLatitude FROM " + i + " i, " + g + " g " +
				"WHERE i.isSync = 0 AND g.ID = i.gpsID";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			BreakInObj in = new BreakInObj();
			in.ID = cursor.getString(0);
			in.empID = cursor.getString(1);
			in.dDate = cursor.getString(2);
			in.dTime = cursor.getString(3);
			in.syncBatchID = cursor.getString(4);
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(5);
			gps.time = cursor.getString(6);
			gps.longitude = cursor.getDouble(7);
			gps.latitude = cursor.getDouble(8);
			in.gps = gps;
			breakInList.add(in);
		}
		cursor.close();
		return breakInList;
	}

	public static ArrayList<BreakOutObj> loadBreakOutSync(SQLiteAdapter db) {
		ArrayList<BreakOutObj> breakOutList = new ArrayList<>();
		String g = Tables.getName(TB.GPS);
		String o = Tables.getName(TB.BREAK_OUT);
		String query = "SELECT o.ID, o.empID, o.dDate, o.dTime, o.syncBatchID, o.breakInID, " +
				"g.gpsDate, g.gpsTime, g.gpsLongitude, g.gpsLatitude FROM " + o + " o, " + g + " g " +
				"WHERE o.isSync = 0 AND g.ID = o.gpsID";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			BreakOutObj out = new BreakOutObj();
			out.ID = cursor.getString(0);
			out.empID = cursor.getString(1);
			out.dDate = cursor.getString(2);
			out.dTime = cursor.getString(3);
			out.syncBatchID = cursor.getString(4);
			out.breakInID = cursor.getString(5);
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(6);
			gps.time = cursor.getString(7);
			gps.longitude = cursor.getDouble(8);
			gps.latitude = cursor.getDouble(9);
			out.gps = gps;
			breakOutList.add(out);
		}
		cursor.close();
		return breakOutList;
	}

	public static ArrayList<IncidentReportObj> loadIncidentReportSync(SQLiteAdapter db) {
		ArrayList<IncidentReportObj> incidentReportList = new ArrayList<>();
		String g = Tables.getName(TB.GPS);
		String a = Tables.getName(TB.INCIDENT_REPORT);
		String query = "SELECT a.ID, a.empID, a.dDate, a.dTime, a.incidentID, a.value, a.syncBatchID, " +
				"g.gpsDate, g.gpsTime, g.gpsLongitude, g.gpsLatitude FROM " + a + " a, " + g + " g " +
				"WHERE a.isSync = 0 AND g.ID = a.gpsID";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			IncidentReportObj ir = new IncidentReportObj();
			String value = cursor.getString(5);
			ir.ID = cursor.getString(0);
			ir.empID = cursor.getString(1);
			ir.dDate = cursor.getString(2);
			ir.dTime = cursor.getString(3);
			ir.incidentID = cursor.getString(4);
			ir.value = value != null ? value : "on";
			ir.syncBatchID = cursor.getString(6);
			GpsObj gps = new GpsObj();
			gps.date = cursor.getString(7);
			gps.time = cursor.getString(8);
			gps.longitude = cursor.getDouble(9);
			gps.latitude = cursor.getDouble(10);
			ir.gps = gps;
			incidentReportList.add(ir);
		}
		cursor.close();
		return incidentReportList;
	}

	public static ArrayList<ImageObj> loadTimeInPhotoUpload(SQLiteAdapter db) {
		ArrayList<ImageObj> imageList = new ArrayList<>();
		String table = Tables.getName(TB.TIME_IN);
		String query = "SELECT ID, syncBatchID, photo FROM " + table + " WHERE isPhotoUpload = 0 " +
				"AND photo NOT NULL";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ImageObj image = new ImageObj();
			image.ID = cursor.getString(0);
			image.syncBatchID = cursor.getString(1);
			image.fileName = cursor.getString(2);
			imageList.add(image);
		}
		cursor.close();
		return imageList;
	}

	public static ArrayList<ImageObj> loadTimeOutPhotoUpload(SQLiteAdapter db) {
		ArrayList<ImageObj> imageList = new ArrayList<>();
		String table = Tables.getName(TB.TIME_OUT);
		String query = "SELECT ID, syncBatchID, photo FROM " + table + " WHERE isPhotoUpload = 0 " +
				"AND photo NOT NULL";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ImageObj image = new ImageObj();
			image.ID = cursor.getString(0);
			image.syncBatchID = cursor.getString(1);
			image.fileName = cursor.getString(2);
			imageList.add(image);
		}
		cursor.close();
		return imageList;
	}

	public static ArrayList<ImageObj> loadSignatureUpload(SQLiteAdapter db) {
		ArrayList<ImageObj> imageList = new ArrayList<>();
		String i = Tables.getName(TB.TIME_IN);
		String o = Tables.getName(TB.TIME_OUT);
		String query = "SELECT i.ID, i.syncBatchID, o.signature FROM " + i + " i, " + o + " o " +
				"WHERE o.isSignatureUpload = 0 AND o.timeInID = i.ID AND o.signature NOT NULL";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ImageObj image = new ImageObj();
			image.ID = cursor.getString(0);
			image.syncBatchID = cursor.getString(1);
			image.fileName = cursor.getString(2);
			imageList.add(image);
		}
		cursor.close();
		return imageList;
	}

	public static ArrayList<ImageObj> loadPhotosUpload(SQLiteAdapter db) {
		ArrayList<ImageObj> imageList = new ArrayList<>();
		String table = Tables.getName(TB.PHOTO);
		String query = "SELECT ID, fileName, syncBatchID, isSignature FROM " + table + " WHERE " +
				"isUpload = 0 AND isDelete = 0";
		net.sqlcipher.Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ImageObj image = new ImageObj();
			image.ID = cursor.getString(0);
			image.fileName = cursor.getString(1);
			image.syncBatchID = cursor.getString(2);
			image.isSignature = cursor.getInt(3) == 1;
			imageList.add(image);
		}
		cursor.close();
		return imageList;
	}

	public static ArrayList<EntryObj> loadEntriesSync(SQLiteAdapter db) {
		ArrayList<EntryObj> entryList = new ArrayList<>();
		String e = Tables.getName(TB.ENTRIES);
		String f = Tables.getName(TB.FIELDS);
		String a = Tables.getName(TB.ANSWERS);
		String query = "SELECT ID, dDate, dTime, referenceNo, dateSubmitted, timeSubmitted, " +
				"syncBatchID, isFromWeb, formID FROM " + e + " WHERE isSync = 0 AND isSubmit = 1 " +
				"AND isDelete = 0";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			EntryObj entry = new EntryObj();
			entry.ID = cursor.getString(0);
			entry.dDate = cursor.getString(1);
			entry.dTime = cursor.getString(2);
			entry.referenceNo = cursor.getString(3);
			entry.dateSubmitted = cursor.getString(4);
			entry.timeSubmitted = cursor.getString(5);
			entry.syncBatchID = cursor.getString(6);
			entry.isFromWeb = cursor.getInt(7) == 1;
			FormObj form = new FormObj();
			form.ID = cursor.getString(8);
			entry.form = form;
			ArrayList<FieldObj> fieldList = new ArrayList<>();
			query = "SELECT f.ID, f.type, a.ID, a.value FROM " + a + " a, " + f + " f " +
					"WHERE f.formID = " + form.ID + " AND a.entryID = " + entry.ID + " " +
					"AND a.fieldID = f.ID AND f.isActive = 1 AND a.value NOT NULL";
			Cursor c = db.read(query);
			while(c.moveToNext()) {
				FieldObj field = new FieldObj();
				field.ID = c.getString(0);
				field.type = c.getString(1);
				AnswerObj answer = new AnswerObj();
				answer.ID = c.getString(2);
				String value = c.getString(3);
				switch(field.type) {
					case FieldType.PHOTO:
					case FieldType.SIG:
						answer.value = TarkieLib.getWebPhotoIDs(db, value);
						break;
					default:
						answer.value = value;
						break;
				}
				answer.syncBatchID = entry.syncBatchID;
				field.answer = answer;
				fieldList.add(field);
			}
			c.close();
			entry.fieldList = fieldList;
			entryList.add(entry);
		}
		cursor.close();
		return entryList;
	}

	public static ArrayList<AnnouncementObj> loadAnnouncements(SQLiteAdapter db, String search) {
		ArrayList<AnnouncementObj> announcementList = new ArrayList<>();
		AnnouncementObj obj;
		obj = new AnnouncementObj();
		obj.ID = 1;
		obj.subject = "Attendance Reminder";
		obj.message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
		obj.announcedDate = "2017-01-23";
		obj.announcedTime = "10:00:00";
		obj.announcedBy = "Dana White";
		obj.announcedByImageURL = "https://lh5.googleusercontent.com/-v0YTPZ5IHqM/AAAAAAAAAAI/AAAAAAAAAAA/TLQEK58tWLI/s128-c-k/photo.jpg";
		obj.isSeen = false;
		obj.isActive = true;
		if(obj.subject.toLowerCase().contains(search.toLowerCase())) {
			announcementList.add(obj);
		}
		obj = new AnnouncementObj();
		obj.ID = 2;
		obj.subject = "Calling All Laguna Staff";
		obj.message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
		obj.announcedDate = "2017-01-23";
		obj.announcedTime = "09:00:00";
		obj.announcedBy = "Joe Schilling";
		obj.announcedByImageURL = "https://lh5.googleusercontent.com/-v0YTPZ5IHqM/AAAAAAAAAAI/AAAAAAAAAAA/TLQEK58tWLI/s128-c-k/photo.jpg";
		obj.isSeen = false;
		obj.isActive = true;
		if(obj.subject.toLowerCase().contains(search.toLowerCase())) {
			announcementList.add(obj);
		}
		obj = new AnnouncementObj();
		obj.ID = 3;
		obj.subject = "Holiday Notice";
		obj.message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
		obj.announcedDate = "2017-01-21";
		obj.announcedTime = "13:00:00";
		obj.announcedBy = "Holly Holms";
		obj.announcedByImageURL = "https://lh5.googleusercontent.com/-v0YTPZ5IHqM/AAAAAAAAAAI/AAAAAAAAAAA/TLQEK58tWLI/s128-c-k/photo.jpg";
		obj.isSeen = true;
		obj.isActive = true;
		if(obj.subject.toLowerCase().contains(search.toLowerCase())) {
			announcementList.add(obj);
		}
		obj = new AnnouncementObj();
		obj.ID = 4;
		obj.subject = "Submit your Reports";
		obj.message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
		obj.announcedDate = "2017-01-20";
		obj.announcedTime = "18:00:00";
		obj.announcedBy = "Paul Daley";
		obj.announcedByImageURL = "https://lh5.googleusercontent.com/-v0YTPZ5IHqM/AAAAAAAAAAI/AAAAAAAAAAA/TLQEK58tWLI/s128-c-k/photo.jpg";
		obj.isSeen = true;
		obj.isActive = true;
		if(obj.subject.toLowerCase().contains(search.toLowerCase())) {
			announcementList.add(obj);
		}
		return announcementList;
	}

	public static ArrayList<SearchObj> searchEntriesByDate(SQLiteAdapter db, String startDate, String endDate) {
		ArrayList<SearchObj> searchList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		String table = Tables.getName(TB.ENTRIES);
		String query = "SELECT dDate, COUNT(ID) FROM " + table + " WHERE dDate " +
				"BETWEEN '" + startDate + "' AND '" + endDate + "' AND empID = '" + empID + "' " +
				"AND isDelete = 0 GROUP BY dDate ORDER BY dDate DESC";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			SearchObj obj = new SearchObj();
			String date = cursor.getString(0);
			int count = cursor.getInt(1);
			String entry = count > 1 ? "Entries" : "Entry";
			obj.name = CodePanUtils.getCalendarDate(date, true, true);
			obj.count = count + " " + entry;
			obj.search = date;
			searchList.add(obj);
		}
		cursor.close();
		return searchList;
	}

	public static ArrayList<SearchObj> searchEntriesByCategory(SQLiteAdapter db) {
		ArrayList<SearchObj> searchList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		String e = Tables.getName(TB.ENTRIES);
		String f = Tables.getName(TB.FORMS);
		String query = "SELECT f.category, COUNT(e.ID) FROM " + e + " e, " + f + " f WHERE " +
				"e.empID = '" + empID + "' AND f.ID = e.formID AND e.isDelete = 0 " +
				"GROUP BY f.category ORDER BY f.category";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			SearchObj obj = new SearchObj();
			int count = cursor.getInt(1);
			String entry = count > 1 ? "Entries" : "Entry";
			obj.name = cursor.getString(0);
			obj.count = count + " " + entry;
			obj.search = cursor.getString(0);
			searchList.add(obj);
		}
		cursor.close();
		return searchList;
	}

	public static ArrayList<SearchObj> searchEntriesByForm(SQLiteAdapter db, String search) {
		ArrayList<SearchObj> searchList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new Condition("e.empID", empID));
		query.add(new Condition("e.isDelete", false));
		if(search != null) {
			query.add(new Condition("f.name", search, Operator.LIKE));
		}
		String e = Tables.getName(TB.ENTRIES);
		String f = Tables.getName(TB.FORMS);
		String sql = "SELECT f.name, COUNT(e.ID), f.ID FROM " + e + " e, " + f + " f WHERE " +
				"f.ID = e.formID AND " + query.getConditions() + " GROUP BY f.ID " +
				"ORDER BY f.name";
		Cursor cursor = db.read(sql);
		while(cursor.moveToNext()) {
			SearchObj obj = new SearchObj();
			int count = cursor.getInt(1);
			String entry = count > 1 ? "Entries" : "Entry";
			obj.name = cursor.getString(0);
			obj.count = count + " " + entry;
			obj.search = cursor.getString(2);
			searchList.add(obj);
		}
		cursor.close();
		return searchList;
	}

	public static ArrayList<SearchObj> searchEntriesByStatus(SQLiteAdapter db) {
		ArrayList<SearchObj> searchList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		String table = Tables.getName(TB.ENTRIES);
		String query = "SELECT CASE " +
				"WHEN isSubmit = 1 THEN '" + Status.SUBMITTED + "' " +
				"WHEN isSubmit = 0 THEN '" + Status.DRAFT + "' " +
				"END AS status, COUNT(ID) FROM " + table + " " +
				"WHERE empID = '" + empID + "' AND isDelete = 0 " +
				"GROUP BY status";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			SearchObj obj = new SearchObj();
			int count = cursor.getInt(1);
			String entry = count > 1 ? "Entries" : "Entry";
			obj.name = cursor.getString(0);
			obj.count = count + " " + entry;
			obj.search = cursor.getString(0);
			searchList.add(obj);
		}
		cursor.close();
		return searchList;
	}

	public static ArrayList<TaskObj> loadTasks(SQLiteAdapter db, String date) {
		ArrayList<TaskObj> taskList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		String t = Tables.getName(TB.TASK);
		String s = Tables.getName(TB.STORES);
		String i = Tables.getName(TB.CHECK_IN);
		String o = Tables.getName(TB.CHECK_OUT);
		String query = "SELECT t.ID, t.name, t.notes, s.ID, s.name, s.address, i.ID, i.dDate, i.dTime, " +
				"o.ID, o.dDate, o.dTime FROM " + t + " as t LEFT JOIN " + i + " as i ON i.taskID = t.ID " +
				"LEFT JOIN " + o + " as o ON o.taskID = t.ID LEFT JOIN " + s + " as s " +
				"ON s.ID = t.storeID WHERE t.startDate >= '" + date + "' AND " +
				"t.endDate <= '" + date + "' AND empID = '" + empID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			TaskObj task = new TaskObj();
			task.ID = cursor.getString(0);
			task.name = cursor.getString(1);
			task.notes = cursor.getString(2);
			String storeID = cursor.getString(3);
			if(storeID != null) {
				StoreObj store = new StoreObj();
				store.ID = storeID;
				store.name = cursor.getString(4);
				store.address = cursor.getString(5);
				task.store = store;
			}
			String checkInID = cursor.getString(6);
			if(checkInID != null) {
				CheckInObj in = new CheckInObj();
				in.ID = checkInID;
				in.dDate = cursor.getString(7);
				in.dTime = cursor.getString(8);
				task.in = in;
			}
			String checkOutID = cursor.getString(9);
			if(checkOutID != null) {
				CheckOutObj out = new CheckOutObj();
				out.ID = checkOutID;
				out.dDate = cursor.getString(10);
				out.dTime = cursor.getString(11);
				task.out = out;
			}
			taskList.add(task);
		}
		cursor.close();
		return taskList;
	}
}
