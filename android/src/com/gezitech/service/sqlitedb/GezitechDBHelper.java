package com.gezitech.service.sqlitedb;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechEntity.FieldInfoCache;
import com.gezitech.basic.GezitechEntity.FieldType;
import com.gezitech.basic.GezitechEntity.TableInfoCache;
import com.gezitech.entity.GezitechEntityAbstractCollection;
import com.gezitech.service.managers.DataManager.PersonalField;
import com.gezitech.util.StringUtil;

public class GezitechDBHelper<T extends GezitechEntity> extends SQLiteOpenHelper {
	String tableName;
	private Class<? extends GezitechEntity> generic;
	private static ArrayList<String> tablesInfo;//用来保存已创建的表。
	private Context context;
	private static final int DB_VERSION = 1;
	/**
	 * 初始化一个数据库处理对象
	 * @param context
	 * @param name
	 * @param 仅用来获得泛型的实际对象类型
	 */
	public GezitechDBHelper(Class<? extends GezitechEntity> generic,String databaseName ) {
		super(GezitechApplication.getContext(), databaseName, (SQLiteDatabase.CursorFactory)null, DB_VERSION);
		this.context = GezitechApplication.getContext();
		if(generic == null)throw new NullPointerException();
		
		this.generic = generic;
		TableInfoCache tic = GezitechEntity.getEntityFieldsInfo(generic);
		this.tableName = tic == null ? null :tic.tableName;
	}
	public GezitechDBHelper(Context context, Class<? extends GezitechEntity> generic) {
		this(generic);
	}
	public GezitechDBHelper(Class<? extends GezitechEntity> generic) {
		this(generic,"hanyihan_"+GezitechApplication.systemSp.getLong("uid", 0) );
	}
	private void initTablesInfo(SQLiteDatabase db){
		if(tablesInfo == null){
			try{
				boolean hasDb = db != null;
				if(!hasDb)db = this.getWritableDatabase();
				Cursor cur = db.query("sqlite_master", new String[]{"name"}, "type='table'", null, null, null, null);
				tablesInfo = new ArrayList<String>();//放这里基本上可以确保成功
				while(cur.moveToNext()){
					String name = cur.getString(0);
					tablesInfo.add(name);
				}
				cur.close();
				if(!hasDb)db.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	public static ArrayList<String> getTables(){
		GezitechDBHelper<GezitechEntity> db = new GezitechDBHelper<GezitechEntity>(GezitechEntity.class);
		SQLiteDatabase sd = db.getReadableDatabase();
		db.initTablesInfo(sd);
		sd.close();
		db.close();
		return tablesInfo;
	}
	@Override
	public synchronized void onCreate(SQLiteDatabase db) {
		if(generic.equals(GezitechEntity.class))return;
		TableInfoCache tic = GezitechEntity.getEntityFieldsInfo(generic);
		String table = tic == null ? null :tic.tableName;
		if(table == null || "".equals(table))return;
		initTablesInfo(db);
		if(tablesInfo == null || !tablesInfo.contains(table)){
			boolean hasDb = db != null;
			if(!hasDb)db = this.getWritableDatabase();
			if(createTable(db, generic)){
				if(tablesInfo != null)
					tablesInfo.add(table);
				try {
					generic.newInstance().afterCreateTable();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(!hasDb)db.close();
		}
	}
	/**
	 * 删除所有表
	 */
	public void dropAllTables(){
		ArrayList<String> tbls = getTables();
		if(tbls != null && tbls.size() > 0){
			SQLiteDatabase db = this.getWritableDatabase();
			for(String table : tbls){
				if(table.equals("android_metadata"))continue;
				db.execSQL("drop table " + table);
			}
			db.close();
			tablesInfo = null;
		}
	}
	/**
	 *  删除单个表
	 * @param entityType
	 * @return
	 */
	public int dropTable(Class<? extends GezitechEntity> entityType){
		TableInfoCache tic = GezitechEntity.getEntityFieldsInfo(entityType);
		String table = tic == null ? null :tic.tableName;
		if(table!=null){
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL("drop table " + table);
			db.close();
			return 1;
		}
		return 0;
	}
	protected boolean createTable(SQLiteDatabase db, Class<? extends GezitechEntity> entityType){
		StringBuilder sb = new StringBuilder();
		TableInfoCache tic = GezitechEntity.getEntityFieldsInfo(entityType);
		String table = tic == null ? null :tic.tableName;
		sb.append("create table if not exists " + table + " (");
		ArrayList<FieldInfoCache> fields = GezitechEntity.getFieldsInfo(entityType, true, false, false);
		for(FieldInfoCache f : fields){
			String name = f.fieldName;
			if(name == null)continue;
			String type = getFieldType(f);
			if(type == null)continue;
			sb.append(String.format("[%s] %s PRIMARY KEY NOT NULL,", name, type));
		}
		fields = GezitechEntity.getFieldsInfo(entityType, false, true, false);
		for(FieldInfoCache f : fields){
			String name = f.fieldName;
			if(name == null)continue;
			String type = getFieldType(f);
			if(type == null)continue;
			sb.append(String.format("[%s] %s,", name, type));
		}
		String sql = sb.toString();
		if(sql.endsWith(","))sql=sql.substring(0, sql.length() - 1);
		sql += ")";
		try{
			db.execSQL(sql);
		}catch(Exception ex){
			return false;
		}
		return true;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		String sql = "DROP TABLE IF EXISTS " + tableName;
//		db.execSQL(sql);
		//为用户表(WhoAmI)添加sina微博相关字段
		/*if(oldVersion==1 || oldVersion==2){
			String sql = null;
			Cursor cursor = db.query("sqlite_master", new String[]{"sql"}, "tbl_name='whoami' and type='table' ", null, null, null, null);
			if(cursor.moveToFirst()){
				sql = cursor.getString(0);
			}
			cursor.close();
			if(sql!=null && sql.indexOf("sina_uid")==-1){
				db.execSQL("ALTER TABLE whoami ADD sina_uid varchar");
				db.execSQL("ALTER TABLE whoami ADD sina_oauth_token varchar");
				db.execSQL("ALTER TABLE whoami ADD auto_login_type integer");
			}
			if(sql!=null && sql.indexOf("qq_uid")==-1){
				db.execSQL("ALTER TABLE whoami ADD qq_uid varchar");
				db.execSQL("ALTER TABLE whoami ADD qq_oauth_token varchar");
				db.execSQL("ALTER TABLE whoami ADD qq_expires_in integer");
			}
		}
		//删除微博字段和类表的字段
		if(oldVersion==1 || oldVersion==2 || oldVersion == 3){
			String sql = null;
			Cursor cursor = db.query("sqlite_master", new String[]{"sql"}, "tbl_name='look' and type='table' ", null, null, null, null);
			if(cursor.moveToFirst()){
				sql = cursor.getString(0);
			}
			cursor.close();
			if(sql!=null && sql.indexOf("uname")==-1){
				db.execSQL("DROP TABLE IF EXISTS look");
				db.execSQL("DROP TABLE IF EXISTS sort");
			}
		}
		//更新当前小区的值
		if(oldVersion==1 || oldVersion==2 || oldVersion == 3 || oldVersion == 4){
			String sql = null;
			Cursor cursor = db.query("sqlite_master", new String[]{"sql"}, "tbl_name='resident' and type='table' ", null, null, null, null);
			if(cursor.moveToFirst()){
				sql = cursor.getString(0);
			}
			cursor.close();
			if(sql!=null && sql.indexOf("current_resident")==-1){
				db.execSQL("update resident set current_resident = 0");
			}
		}*/
	}
	/**
	 * 获取实体字段映射的数据库字段类型
	 * @param f
	 * @return
	 */
	public String getFieldType(FieldInfoCache fic){
		Field f = fic.field;
		FieldInfo fi = fic.fieldInfo;
		FieldType ft = fi.fieldType();
		if(ft == FieldType.Auto){
			Class<?> type = f.getType();
			if(type.equals(int.class) || type.equals(Integer.class) || type.equals(long.class) || type.equals(Long.class))
				ft = FieldType.Integer;
			else ft = FieldType.Varchar;
		}
		return ft.toString();
	}
	protected boolean setUpdateValue(ContentValues cv, String name, Field field, T t){
		if(cv == null)return false;
		if(name == null || "".equals(name))return false;
		if(field == null)return false;
		if(t == null)return false;
		Type type = field.getType();
		try{
			String value = String.valueOf(field.get(t));
			if(type.equals(int.class))cv.put(name, Integer.valueOf(value));
			else if(type.equals(Long.class))cv.put(name, Long.valueOf(value));
			else cv.put(name, value);
		}catch(Exception ex){
			return false;
		}
		return true;
	}
	/**
	 * 插入数据
	 * @param t
	 * @return
	 */
	public boolean insert(T t, PersonalField...personalFields){
		if(t.id <= 0)t.id = getMaxId() + 1;
		ContentValues cv = new ContentValues();
		ArrayList<FieldInfoCache> fs = GezitechEntity.getFieldsInfo(t.getClass(), true, true, false);
		for(FieldInfoCache f : fs){
			if(f.fieldInfo.isPersonal()){//对于个人信息字段，要判断是否对其更新，以防止在下载数据时覆盖掉这些数据。
				boolean use = false;
				if(personalFields != null && personalFields.length > 0){
					for(PersonalField pf : personalFields){
						if(pf == null)continue;
						if(pf.fieldInfo.name.equals(f.name)){
							use = true;
							break;
						}
					}
				}
				if(!use)continue;
			}
			setUpdateValue(cv, f.fieldName, f.field, t);
		}
		SQLiteDatabase db = this.getWritableDatabase();
		onCreate(db);
		boolean res = db.insert(tableName, null, cv) != -1;
		db.close();
		return res;
	}
	/**
	 * 查询数据，如果异常，返回null，否则始终返回集合，即使是空集合。
	 * @param where
	 * @param count
	 * @param order
	 * @return
	 */
	public ArrayList<T> query(String where, int count, String order){
		ArrayList<T> list = new ArrayList<T>();
		try{
			SQLiteDatabase db = this.getReadableDatabase();
			onCreate(this.getWritableDatabase());
			Cursor cur = db.query(tableName, null, where, new String[]{}, null, null, order, count<=0? null : String.valueOf(count));
			if(!cur.equals(null) && !cur.isClosed()){
				while(cur.moveToNext()){
					T t;
					try {
						t = (T)generic.newInstance();
					} catch (Exception e) {
						continue;
					}
					t.init(cur);
					list.add(t);
				}
				cur.close();
			}
			db.close();
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		return list;
	}
	/**
	 * 基于某条数据来查询数据，即返回的结果都是该数据之后的数据。这里的条数不是直接分页的
	 * 如果异常，返回null，否则始终返回集合，即使是空集合。
	 * 
	 * @param where
	 * @param count
	 * @param order
	 * @param rel 参照记录，即只会返回该记录之后的数据
	 * @param isNew false 向下取  true向上取值
	 * @return
	 */
	public ArrayList<T> query(String where, int count, String order, T rel, boolean isNew){
		if(StringUtil.isEmpty(where))where = null;
		ArrayList<T> list = new ArrayList<T>();
		try{
			boolean dataStarted = false;//当此变量为true时，才是真正的数据，否则都不是需要的数据
			if(rel == null || this.find(rel.id) == null)dataStarted = true;
			SQLiteDatabase db = this.getReadableDatabase();
			onCreate(this.getWritableDatabase());
			Cursor cur = db.query(tableName, null, where, new String[]{}, null, null, order, null);
			if(!cur.equals(null) && !cur.isClosed()){
				boolean located = false;
				if(!dataStarted){//定位rel
					while(cur.moveToNext()){
						long id = cur.getLong(cur.getColumnIndex("id"));
						if(id == rel.id){
							located = true;
							break;
						}
					}
				}
				while(true){
					boolean move = (located && isNew) ? cur.moveToPrevious() : cur.moveToNext();
					if(!move)break;
					T t;
					try {
						t = (T)generic.newInstance();
					} catch (Exception e) {
						continue;
					}
					t.init(cur);
					list.add(t);
					if(count > 0 && count <= list.size())break;
				}
				cur.close();
			}
			db.close();
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		return list;
	}
	/**
	 * 更新数据
	 * @param t 要更新的数据实体
	 * @param personalFields 要保存的用户相关字段，如果某字段是用户相关字段，且没有出现在此数组中，该字段将不会被保存
	 * @return
	 */
	public int update(T t, PersonalField...personalFields){
		SQLiteDatabase db = this.getWritableDatabase();
		onCreate(db);
		ContentValues cv = new ContentValues();
		
		ArrayList<FieldInfoCache> primaries = GezitechEntity.getFieldsInfo(t.getClass(), true, false, false);
		ArrayList<FieldInfoCache> fieldsOther = GezitechEntity.getFieldsInfo(t.getClass(), false, true, false);
		for(FieldInfoCache f : fieldsOther){
			if(f.fieldInfo.isPersonal()){//对于个人信息字段，要判断是否对其更新，以防止在下载数据时覆盖掉这些数据。
				boolean use = false;
				if(personalFields != null && personalFields.length > 0){
					for(PersonalField pf : personalFields){
						if(pf == null)continue;
						if(pf.fieldInfo.name.equals(f.name)){
							use = true;
							break;
						}
					}
				}
				if(!use)continue;
			}
			setUpdateValue(cv, f.fieldName, f.field, t);
		}
		
		String where = "";
		for(FieldInfoCache f : primaries){
			String name = f.fieldName;
			Type type = f.field.getType();
			try{
				if(type.equals(int.class) || type.equals(Long.class))
					where = where +" and " + name + "=" + t.getFieldValue(f.field);
				else where = where +" and " + name + "='" + t.getFieldValue(f.field) + "'";
			}catch(Exception ex){
				return -1;
			}
		}
		if(where.startsWith(" and "))where = where.substring(5);
		int res = db.update(tableName, cv, where, null);
		db.close();
		return res;
	}
	/**
	 * 保存数据
	 * 如果数据已存在则更新，否则插入
	 * @param t
	 * @return
	 */
	public boolean save(T t, PersonalField...personalFields){
		return save(t, false, false, personalFields);
	}
	/**
	 * 保存一条数据，如果已存在，则更新或退出，否则插入。
	 * @param t 要保存的数据
	 * @param exitIfExist 如果存在，是否取消更新。
	 * @return 保存成功与否
	 */
	public boolean save(T t, boolean saveRelated, PersonalField...personalFields){
		return save(t, false, saveRelated, personalFields);
		
	}
	protected boolean save(T t, boolean isRelated, boolean saveRelated, PersonalField...personalFields){
		if(t == null)return false;
		boolean saveSelf = true;
		boolean res = true;
		if(isRelated){
			T tt = this.find(t.id);
			if(tt != null && tt.sync_time > 0)saveSelf = false;//如果当前是关联数据，且原库中已经有下载记录(不是以关联数据的方式保存)的数据存在，则不覆盖
		}
		if(saveSelf){
			try{
				int result = update(t, personalFields);
				if(result <= 0)result = insert(t, personalFields) ? 1 : 0;
				res = result > 0;
			}catch(Exception ex){
				res = false;
				ex.printStackTrace();
			}
		}
		if(saveRelated){
			ArrayList<FieldInfoCache> fs = GezitechEntity.getFieldsInfo(t.getClass(), true, true, true);
			for(FieldInfoCache f : fs){
				FieldInfo fi = f.fieldInfo;
				if(!fi.isChildren() && !fi.isParent())continue;
				GezitechEntityAbstractCollection bac;
				try {
					bac = (GezitechEntityAbstractCollection)f.field.get(t);
				} catch (Exception e) {
					continue;
				}
				if(bac == null || bac.getEntityList().size() < 1)continue;
				
				GezitechDBHelper<GezitechEntity> db = new GezitechDBHelper<GezitechEntity>(f.fieldInfo.outerType());
				//如果是子表数据，则要先消除原来的数据
				if(fi.isChildren()){
					try{
						String outerKey = fi.outerKey();
						String innerKey = fi.innerKey();
						if(StringUtil.isEmpty(innerKey))innerKey = "id";
						FieldInfoCache iFic = GezitechEntity.getFieldInfo(t.getClass(), innerKey);
						Object value = null;
						if(iFic != null)value = iFic.field.get(t);
						if(!StringUtil.isEmpty(outerKey) && iFic != null){
							FieldInfoCache oFic = GezitechEntity.getFieldInfo(f.fieldInfo.outerType(), outerKey);
							if(oFic != null){
								String sp = GezitechDBHelper.getDbSpliter(oFic.field.getType());
								String where = oFic.fieldName + "=" + sp + value + sp;
								db.delete(where);
//								for(BeelnnEntity be : bac.getEntityList())oFic.field.set(be, value);//更新字段的值
							}
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				for(GezitechEntity be : bac.getEntityList())db.save(be, true, saveRelated);
				db.close();
			}
		}
		return res;
	}
	public void delete(String where) {
		try{
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(tableName, where, null);
			db.close();
		}catch(Exception ex){
			
		}
	}
	public int updateAll(HashMap<String, String> values, String where){
		if(values == null || values.size() <= 0)return 0;
		TableInfoCache tic = GezitechEntity.getEntityFieldsInfo(generic);
		if(tic == null)return -1;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues kvs = new ContentValues();
		for(FieldInfoCache fic : tic.fieldList){
			if(!values.containsKey(fic.fieldName))continue;
			Class<?> type = fic.field.getType();
			String val = values.get(fic.fieldName);
			if(type.equals(int.class))kvs.put(fic.fieldName, Integer.valueOf(val));
			else if(type.equals(Long.class))kvs.put(fic.fieldName, Long.valueOf(val));
			else kvs.put(fic.fieldName, val);
		}
		int result = db.update(tableName, kvs, where, new String[]{});
		db.close();
		return result;
	}
	public T find(long id){
		try{
			String where = "id=" + id;
			ArrayList<T> list = query(where, 1, null);
			if(list == null || list.size() <= 0)return null;
			return list.get(0);
		}catch(Exception ex){
			return null;
		}
	}
	public long getMaxId(){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(tableName, new String[]{"max("+ GezitechEntity.defaultPrimaryKey +") as id"}, null, null, null, null, null);
		long result = 0;
		if(cursor.moveToFirst()){
			result = cursor.getLong(0);
		}
		cursor.close();
		db.close();
		return result;
	}
	public ArrayList<Long> getIds(String where, String order){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(tableName, new String[]{GezitechEntity.defaultPrimaryKey}, where, new String[]{}, null, null, order);
		if(cursor == null || cursor.isLast() || cursor.isClosed())return new ArrayList<Long>();
		ArrayList<Long> list = new ArrayList<Long>();
		while(cursor.moveToNext())
			list.add(cursor.getLong(0));
		cursor.close();
		db.close();
		return list;
	}
	/**
	 * 根据类型获取数据库分隔符
	 * @param type 类型
	 * @return
	 */
	public static String getDbSpliter(Class<?> type){
		String sp = "'";
		if(type.equals(int.class) || type.equals(Integer.class) || type.equals(Long.class) || type.equals(long.class))
			sp = "";
		return sp;
	}
}
