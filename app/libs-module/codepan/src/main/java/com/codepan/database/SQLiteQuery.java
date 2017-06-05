package com.codepan.database;

import java.util.ArrayList;

public class SQLiteQuery {

	private ArrayList<FieldValue> fieldValueList;
	private ArrayList<Condition> conditionList;
	private ArrayList<Field> fieldList;
	private ArrayList<Table> tableList;

	public enum DataType {
		INTEGER,
		TEXT
	}

	public void setTableList(ArrayList<Table> tableList) {
		this.tableList = tableList;
	}

	public void setFieldList(ArrayList<Field> fieldList) {
		this.fieldList = fieldList;
	}

	public void setFieldValueList(ArrayList<FieldValue> fieldValueList) {
		this.fieldValueList = fieldValueList;
	}

	public void setConditionList(ArrayList<Condition> conditionList) {
		this.conditionList = conditionList;
	}

	public void add(Table table) {
		if(tableList == null) {
			tableList = new ArrayList<>();
		}
		tableList.add(table);
	}

	public void add(Field field) {
		if(fieldList == null) {
			fieldList = new ArrayList<>();
		}
		fieldList.add(field);
	}

	public void add(FieldValue fieldValue) {
		if(fieldValueList == null) {
			fieldValueList = new ArrayList<>();
		}
		fieldValueList.add(fieldValue);
	}

	public void add(Condition condition) {
		if(conditionList == null) {
			conditionList = new ArrayList<>();
		}
		conditionList.add(condition);
	}

	public void removeTable(int index) {
		if(tableList != null) {
			tableList.remove(index);
		}
	}

	public void removeField(int index) {
		if(fieldList != null) {
			fieldList.remove(index);
		}
	}

	public void removeFieldValue(int index) {
		if(fieldValueList != null) {
			fieldValueList.remove(index);
		}
	}

	public void removeCondition(int index) {
		if(conditionList != null) {
			conditionList.remove(index);
		}
	}

	public void clearTableList() {
		if(tableList != null) {
			tableList.clear();
		}
	}

	public void clearFieldList() {
		if(fieldList != null) {
			fieldList.clear();
		}
	}

	public void clearFieldValueList() {
		if(fieldValueList != null) {
			fieldValueList.clear();
		}
	}

	public void clearConditionList() {
		if(conditionList != null) {
			conditionList.clear();
		}
	}

	public void clearAll() {
		clearTableList();
		clearFieldList();
		clearFieldValueList();
		clearConditionList();
	}

	public boolean hasTables() {
		return tableList != null && !tableList.isEmpty();
	}

	public boolean hasFields() {
		return fieldList != null && !fieldList.isEmpty();
	}

	public boolean hasFieldsValues() {
		return fieldValueList != null && !fieldValueList.isEmpty();
	}

	public boolean hasConditions() {
		return conditionList != null && !conditionList.isEmpty();
	}

	private String createFields() {
		String fields = "";
		if(fieldList != null) {
			for(Field obj : fieldList) {
				if(obj.withDataType) {
					String dataType = obj.getDataType();
					fields += obj.field + " " + dataType;
					if(obj.isDefault) {
						String defValue = obj.getDefaultValue();
						fields += " DEFAULT " + defValue;
					}
					if(obj.isPrimaryKey) {
						fields += " PRIMARY KEY AUTOINCREMENT NOT NULL";
					}
				}
				else {
					fields += obj.field;
				}
				if(fieldList.indexOf(obj) < fieldList.size() - 1) {
					fields += ", ";
				}
			}
		}
		return fields;
	}

	public String getTables() {
		String tables = "";
		if(tableList != null) {
			for(Table obj : tableList) {
				tables += obj.name + " as " + obj.as;
				if(tableList.indexOf(obj) < tableList.size() - 1) {
					tables += ", ";
				}
			}
		}
		return tables;
	}

	public String getFields() {
		String fields = "";
		if(fieldList != null) {
			for(Field obj : fieldList) {
				fields += obj.field;
				if(fieldList.indexOf(obj) < fieldList.size() - 1) {
					fields += ", ";
				}
			}
		}
		return fields;
	}

	public String getFieldsValues() {
		String fieldsValues = "";
		if(fieldValueList != null) {
			for(FieldValue obj : fieldValueList) {
				if(obj.value != null) {
					fieldsValues += obj.field + " = " + obj.value;
				}
				else {
					fieldsValues += obj.field + " = NULL";
				}
				if(fieldValueList.indexOf(obj) < fieldValueList.size() - 1) {
					fieldsValues += ", ";
				}
			}
		}
		return fieldsValues;
	}

	public String getConditions() {
		String condition = "";
		if(conditionList != null) {
			for(Condition obj : conditionList) {
				switch(obj.operator) {
					case EQUALS:
						condition += obj.field + " = " + obj.value;
						break;
					case NOT_EQUALS:
						condition += obj.field + " != " + obj.value;
						break;
					case GREATER_THAN:
						condition += obj.field + " > " + obj.value;
						break;
					case LESS_THAN:
						condition += obj.field + " < " + obj.value;
						break;
					case GREATER_THAN_OR_EQUALS:
						condition += obj.field + " >= " + obj.value;
						break;
					case LESS_THAN_OR_EQUALS:
						condition += obj.field + " <= " + obj.value;
						break;
					case BETWEEN:
						condition += obj.field + " BETWEEN " + obj.start + " AND " + obj.end;
						break;
					case IS_NULL:
						condition += obj.field + " IS NULL";
						break;
					case NOT_NULL:
						condition += obj.field + " NOT NULL";
						break;
					case LIKE:
						condition += obj.field + " LIKE " + obj.value;
						break;
				}
				if(conditionList.indexOf(obj) < conditionList.size() - 1) {
					condition += " AND ";
				}
			}
		}
		return condition;
	}

	public String insert(String table) {
		String fields = "";
		String values = "";
		if(fieldValueList != null) {
			for(FieldValue obj : fieldValueList) {
				fields += obj.field;
				values += obj.value;
				if(fieldValueList.indexOf(obj) < fieldValueList.size() - 1) {
					fields += ", ";
					values += ", ";
				}
			}
		}
		return "INSERT INTO " + table + " (" + fields + ") VALUES (" + values + ")";
	}

	public String update(String table, String recID) {
		return "UPDATE " + table + " SET " + getFieldsValues() + " WHERE ID = '" + recID + "'";
	}

	public String update(String table, int recID) {
		return "UPDATE " + table + " SET " + getFieldsValues() + " WHERE ID = '" + recID + "'";
	}

	public String update(String table) {
		String condition = "";
		if(conditionList != null && !conditionList.isEmpty()) {
			condition = " WHERE " + getConditions();
		}
		return "UPDATE " + table + " SET " + getFieldsValues() + condition;
	}

	public String delete(String table) {
		String condition = "";
		if(conditionList != null && !conditionList.isEmpty()) {
			condition = " WHERE " + getConditions();
		}
		return "DELETE FROM " + table + condition;
	}

	public String createTable(String table) {
		return "CREATE TABLE IF NOT EXISTS " + table + " (" + createFields() + ")";
	}

	public String select(String table) {
		String condition = "";
		if(conditionList != null && !conditionList.isEmpty()) {
			condition = " WHERE " + getConditions();
		}
		return "SELECT " + getFields() + " FROM " + table + condition;
	}

	public String select() {
		return "SELECT " + getFields() + " FROM " + getTables() + " WHERE " + getConditions();
	}

	public String addColumn(String table, String column, String defText) {
		String value = defText != null ? defText : "NULL";
		return "ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT DEFAULT " + value + "";
	}

	public String addColumn(String table, String column, int defInt) {
		return "ALTER TABLE " + table + " ADD COLUMN " + column + " INTEGER DEFAULT " + defInt + "";
	}

	public String addColumn(String table, DataType type, String column) {
		String query = null;
		switch(type) {
			case INTEGER:
				query = "ALTER TABLE " + table + " ADD COLUMN " + column + " INTEGER";
				break;
			case TEXT:
				query = "ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT";
				break;
		}
		return query;
	}

	public String dropTable(String table) {
		return "DROP TABLE IF EXISTS " + table;
	}
}
