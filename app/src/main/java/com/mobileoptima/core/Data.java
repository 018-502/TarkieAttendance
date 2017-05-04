package com.mobileoptima.core;

import com.codepan.database.Condition;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteQuery;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.constant.InventoryType;
import com.mobileoptima.constant.Status;
import com.mobileoptima.model.AnswerObj;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakObj;
import com.mobileoptima.model.BreakOutObj;
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
//	public static ArrayList<FormObj> loadForms(SQLiteAdapter db) {
//		ArrayList<FormObj> formList = new ArrayList<>();
//		FormObj overtime = new FormObj();
//		overtime.ID = "1";
//		overtime.name = "Overtime Request";
//		overtime.type = FormType.OVERTIME;
//		FormObj schedule = new FormObj();
//		schedule.ID = "2";
//		schedule.name = "Schedule";
//		schedule.type = FormType.SCHEDULE;
//		FormObj compliance = new FormObj();
//		compliance.ID = "3";
//		compliance.name = "Compliance";
//		compliance.type = FormType.COMPLIANCE;
//		formList.add(overtime);
//		formList.add(schedule);
//		formList.add(compliance);
//		return formList;
//	}

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
		String i = Tables.getName(TB.TIME_IN);
		String o = Tables.getName(TB.TIME_OUT);
		String query = "SELECT i.ID, i.dDate, i.dTime, i.isTimeOut, o.ID, o.dDate, " +
				"o.dTime, o.signature FROM " + i + " as i LEFT JOIN " + o + " as o ON " +
				"o.timeInID = i.ID ORDER BY i.ID DESC";
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
		SQLiteQuery sql = new SQLiteQuery();
		if(search != null) {
			sql.add(new Condition("name", search, Operator.LIKE));
		}
		if(start != null) {
			sql.add(new Condition("name", start, Operator.GREATER_THAN));
		}
		String condition = sql.hasConditions() ? " WHERE " + sql.getConditions() : "";
		String query = "SELECT ID, name, address, radius, gpsLongitude, gpsLatitude FROM " +
				table + condition + " ORDER BY name LIMIT " + limit;
		Cursor cursor = db.read(query);
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

	public static ArrayList<EntryObj> loadEntries(SQLiteAdapter db) {
		ArrayList<EntryObj> entryList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		String query = "SELECT e.ID, e.dDate, e.dTime, e.isSubmit, e.referenceNo, f.ID, f.name, f.logoUrl FROM " +
				Tables.getName(TB.ENTRIES) + " e , " + Tables.getName(TB.FORMS) + " f " +
				"WHERE e.empID = '" + empID + "' AND e.isDelete = 0 AND f.ID = e.formID " +
				"ORDER BY e.ID DESC";
		Cursor cursor = db.read(query);
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
		String table = Tables.getName(TB.TIME_IN);
		String query = "SELECT ID, empID, dDate, dTime, gpsDate, gpsTime, gpsLongitude, " +
				"gpsLatitude, syncBatchID, storeID FROM " + table + " WHERE isSync = 0";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			TimeInObj in = new TimeInObj();
			in.ID = cursor.getString(0);
			in.empID = cursor.getString(1);
			in.dDate = cursor.getString(2);
			in.dTime = cursor.getString(3);
			in.gpsDate = cursor.getString(4);
			in.gpsTime = cursor.getString(5);
			in.gpsLongitude = cursor.getDouble(6);
			in.gpsLatitude = cursor.getDouble(7);
			in.syncBatchID = cursor.getString(8);
			in.storeID = cursor.getString(9);
			timeInList.add(in);
		}
		cursor.close();
		return timeInList;
	}

	public static ArrayList<TimeOutObj> loadTimeOutSync(SQLiteAdapter db) {
		ArrayList<TimeOutObj> timeOutList = new ArrayList<>();
		String table = Tables.getName(TB.TIME_OUT);
		String query = "SELECT ID, empID, dDate, dTime, gpsDate, gpsTime, gpsLongitude, " +
				"gpsLatitude, syncBatchID, timeInID FROM " + table + " WHERE isSync = 0";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			TimeOutObj out = new TimeOutObj();
			out.ID = cursor.getString(0);
			out.empID = cursor.getString(1);
			out.dDate = cursor.getString(2);
			out.dTime = cursor.getString(3);
			out.gpsDate = cursor.getString(4);
			out.gpsTime = cursor.getString(5);
			out.gpsLongitude = cursor.getDouble(6);
			out.gpsLatitude = cursor.getDouble(7);
			out.syncBatchID = cursor.getString(8);
			out.timeInID = cursor.getString(9);
			timeOutList.add(out);
		}
		cursor.close();
		return timeOutList;
	}

	public static ArrayList<BreakInObj> loadBreakInSync(SQLiteAdapter db) {
		ArrayList<BreakInObj> breakInList = new ArrayList<>();
		String table = Tables.getName(TB.BREAK_IN);
		String query = "SELECT ID, empID, dDate, dTime, gpsDate, gpsTime, gpsLongitude, " +
				"gpsLatitude, syncBatchID FROM " + table + " WHERE isSync = 0";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			BreakInObj in = new BreakInObj();
			in.ID = cursor.getString(0);
			in.empID = cursor.getString(1);
			in.dDate = cursor.getString(2);
			in.dTime = cursor.getString(3);
			in.gpsDate = cursor.getString(4);
			in.gpsTime = cursor.getString(5);
			in.gpsLongitude = cursor.getDouble(6);
			in.gpsLatitude = cursor.getDouble(7);
			in.syncBatchID = cursor.getString(8);
			breakInList.add(in);
		}
		cursor.close();
		return breakInList;
	}

	public static ArrayList<BreakOutObj> loadBreakOutSync(SQLiteAdapter db) {
		ArrayList<BreakOutObj> breakOutList = new ArrayList<>();
		String table = Tables.getName(TB.BREAK_OUT);
		String query = "SELECT ID, empID, dDate, dTime, gpsDate, gpsTime, gpsLongitude, " +
				"gpsLatitude, syncBatchID, breakInID FROM " + table + " WHERE isSync = 0";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			BreakOutObj out = new BreakOutObj();
			out.ID = cursor.getString(0);
			out.empID = cursor.getString(1);
			out.dDate = cursor.getString(2);
			out.dTime = cursor.getString(3);
			out.gpsDate = cursor.getString(4);
			out.gpsTime = cursor.getString(5);
			out.gpsLongitude = cursor.getDouble(6);
			out.gpsLatitude = cursor.getDouble(7);
			out.syncBatchID = cursor.getString(8);
			out.breakInID = cursor.getString(9);
			breakOutList.add(out);
		}
		cursor.close();
		return breakOutList;
	}

	public static ArrayList<IncidentReportObj> loadIncidentReportSync(SQLiteAdapter db) {
		ArrayList<IncidentReportObj> incidentReportList = new ArrayList<>();
		String table = Tables.getName(TB.INCIDENT_REPORT);
		String query = "SELECT ID, empID, dDate, dTime, incidentID, value, gpsDate, gpsTime, " +
				"gpsLongitude, gpsLatitude, syncBatchID FROM " + table + " WHERE isSync = 0";
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
			ir.gpsDate = cursor.getString(6);
			ir.gpsTime = cursor.getString(7);
			ir.gpsLongitude = cursor.getDouble(8);
			ir.gpsLatitude = cursor.getDouble(9);
			ir.syncBatchID = cursor.getString(10);
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
		String query = "SELECT ID, dDate, dTime, referenceNo, dateSubmitted, timeSubmitted, syncBatchID, isFromWeb, formID " +
				"FROM " + Tables.getName(TB.ENTRIES) + " WHERE isSync = 0 AND isSubmit = 1 AND " +
				"isDelete = 0";
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
			query = "SELECT f.ID, f.type, a.ID, a.value FROM " + Tables.getName(TB.ANSWERS) + " a, " +
					Tables.getName(TB.FIELDS) + " f WHERE f.formID = " + form.ID + " " +
					"AND a.entryID = " + entry.ID + " AND a.fieldID = f.ID " +
					"AND f.isActive = 1 AND a.value NOT NULL";
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

	public static ArrayList<SearchObj> searchEntriesByDate(SQLiteAdapter db, String startDate, String endDate) {
		ArrayList<SearchObj> searchList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		String table = Tables.getName(TB.ENTRIES);
		String query = "SELECT dDate, COUNT(ID) FROM " + table + " WHERE dDate " +
				"BETWEEN '" + startDate + "' AND '" + endDate + "' AND empID = '" + empID + "' " +
				"GROUP BY dDate ORDER BY dDate DESC";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			SearchObj search = new SearchObj();
			String date = cursor.getString(0);
			search.name = CodePanUtils.getCalendarDate(date, true, true);
			search.count = cursor.getInt(1) + " Entries";
			searchList.add(search);
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
				"e.empID = '" + empID + "' AND f.ID = e.formID GROUP BY f.category " +
				"ORDER BY f.category";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			SearchObj search = new SearchObj();
			search.name = cursor.getString(0);
			search.count = cursor.getInt(1) + " Entries";
			searchList.add(search);
		}
		cursor.close();
		return searchList;
	}

	public static ArrayList<SearchObj> searchEntriesByStatus(SQLiteAdapter db) {
		ArrayList<SearchObj> searchList = new ArrayList<>();
		String empID = TarkieLib.getEmployeeID(db);
		String table = Tables.getName(TB.ENTRIES);
		String query = "SELECT CASE " +
				"WHEN isDelete = 1 THEN '" + Status.DELETED + "' " +
				"WHEN isSubmit = 1 THEN '" + Status.SUBMITTED + "' " +
				"WHEN isSubmit = 0 THEN '" + Status.DRAFT + "' " +
				"END AS status, COUNT(ID) FROM " + table + " " +
				"WHERE empID = '" + empID + "' GROUP BY status";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			SearchObj search = new SearchObj();
			search.name = cursor.getString(0);
			search.count = cursor.getInt(1) + " Entries";
			searchList.add(search);
		}
		cursor.close();
		return searchList;
	}
}
