package com.gezitech.basic;

import java.io.File;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.GezitechEntityAbstractCollection;
import com.gezitech.http.PostParameter;
import com.gezitech.service.managers.DataManager.OrderItem;
import com.gezitech.service.managers.DataManager.OrderList;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.StringUtil;

public abstract class GezitechEntity implements java.io.Serializable ,GezitechEntity_I{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient protected JSONObject json;
	public static String defaultPrimaryKey = "id";
	@FieldInfo(isPrimary=true)
	public long id;
	/**
	 * 同步时间，即该数据从服务器上下载的时间
	 * 只有作为主数据下载保存时才会有该时间，作为主数据的附加数据没有该时间。
	 */
	@FieldInfo
	public long sync_time;
	protected GezitechEntity(JSONObject json){	
		this.json = json;
		this.init(json);
	}

	public GezitechEntity() {
		GezitechDBHelper<GezitechEntity> db = new GezitechDBHelper<GezitechEntity>(getClass());
		db.onCreate(null);
		db.close();
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	protected boolean setFieldValue(String field, Object value){
		if(field == null || field.equals(""))return false;
		Field f;
		try {
			f = this.getClass().getField(field);
			f.set(this, value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	public Object getFieldValue(String field){
		if(field == null || field.equals(""))return null;
		try{
			Field f = this.getClass().getField(field);
			return getFieldValue(f);
		}catch(Exception ex){return null;}
	}
	public Object getFieldValue(Field field) throws IllegalArgumentException, IllegalAccessException{
		return field.get(this);
	}
	/**
	 * 设置某个映射字段的值
	 * @param field
	 * @param json
	 * @param dataKey
	 * @return
	 */
	protected boolean setFieldValue(FieldInfoCache fic, JSONObject json, String dataKey){
		if(fic == null)return false;
		Field field = fic.field;
		if(dataKey == null || dataKey.equals(""))return false;
		if(json == null)return false;
		try{
			Class<?> type = field.getType();
			FieldInfo fi = fic.fieldInfo;
			Object value = json.get(dataKey);
			if(fi.isParent() || fi.isChildren()){//关联字段，设置关联对象
				String iKey = fi.innerKey();
				String oKey = fi.outerKey();
				
				ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
				if(value instanceof JSONArray){//数组
					JSONArray ja = (JSONArray)value;
					for(int i=0;i<ja.length();i++)jsonList.add(ja.getJSONObject(i));
				}
				else if(value instanceof JSONObject)//对象
					jsonList.add((JSONObject)value);
				else if(value == null || value.toString().equals("null")){}
				else{//单个值
					JSONObject jo = new JSONObject();
					jo.put(dataKey, value);					
					if(iKey!=null && !iKey.equals("")){
						ArrayList<FieldInfoCache> fics = GezitechEntity.getFieldsInfo(fi.outerType(), true, false, false);
						String key = "id";
						if(fics.size() > 0)key = fics.get(0).jsonName;
						jo.put(key, getFieldValue(iKey));//对于这种特殊的，只传了一个字段的，给它的主键赋个值。
					}
					jsonList.add(jo);
				}
				
				GezitechEntityAbstractCollection bac = (GezitechEntityAbstractCollection)field.get(this);
				for(JSONObject jo:jsonList){
					if(jo == null)continue;
					GezitechEntity be = (GezitechEntity)fi.outerType().newInstance();
					be.init(jo);
					if(fi.isParent()){
						if(iKey != null && !iKey.equals("")){//本表有关联到父表字段时，把父表关联字段的值拿来填充本表关联字段
							String pkey = (oKey == null || oKey.equals("")) ? "id" : oKey;
							this.setFieldValue(iKey, be.getFieldValue(pkey));
						}
					}
					else if(fi.isChildren()){
						if(oKey != null && !oKey.equals("")){//子表有关联到本表的字段时，将本表关联字段的值拿去填充子表的关联字段
							String ckey = (iKey == null || iKey.equals("")) ? "id" : iKey;
							be.setFieldValue(oKey, this.getFieldValue(ckey));
						}
					}
					bac.addEntity(be);
				}
				field.set(this, bac);
			}
			else{//普通字段，直接设置值
				if(type.equals(int.class) || type.equals(Integer.class))
					field.set((Object)this, (Object)Integer.valueOf(value.toString()));
				else if(type.equals(Long.class) || type.equals(long.class))
					field.set((Object)this, (Object)Long.valueOf(value.toString()));
				else  if( type.equals( double.class ) || type.equals( Double.class ) )
					field.set((Object)this, (Object)Double.valueOf(value.toString()));
				else field.set((Object)this, String.valueOf(value));
				if(fic.name.equals("id"))this.id = (Long)value;//有的id是继承的，但不会覆盖原id
			}
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	/**
	 * 设置某个映射字段的值
	 * @param field
	 * @param cursor
	 * @param dataKey
	 * @return
	 */
	protected boolean setFieldValue(FieldInfoCache fic, Cursor cursor, String dataKey){
		if(fic == null)return false;
		Field field = fic.field;
		if(dataKey == null || dataKey.equals(""))return false;
		if(cursor == null)return false;
		try{
			Class<?> type = field.getType();
			int index = cursor.getColumnIndex(dataKey);
			if(index == -1)return false;
			if(type.equals(int.class))
				field.set(this, cursor.getInt(index));
			else if(type.equals(Long.class) || type.equals(long.class))
				field.set(this, cursor.getLong(index));
			else  if( type.equals( double.class ) || type.equals( Double.class ) )
				field.set(this, cursor.getDouble(index) );
			else field.set(this, cursor.getString(index));
			if(fic.name.equals("id"))this.id = cursor.getLong(index);//有的id是继承的，但不会覆盖原id
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	public boolean init(JSONObject json){
		if(json == null || json.toString().equals("null"))return false;
		ArrayList<FieldInfoCache> fields = getFieldsInfo(getClass(), true, true, true);
		for(FieldInfoCache f:fields){
			String jsonField = f.jsonName;
			if(jsonField == null)continue;
			setFieldValue(f, json, jsonField);
		}
		return true;
	}
	public boolean init(Cursor cursor){
		if(cursor == null || cursor.isClosed())return false;
		ArrayList<FieldInfoCache> fields = getFieldsInfo(getClass(), true, true, true);
		for(FieldInfoCache f : fields){
			String name = f.fieldName;
			if(name == null)continue;
			setFieldValue(f, cursor, name);
		}
		return true;
	}

	public PostParameter [] asParameterArray(){	
		List<PostParameter> list = new ArrayList<PostParameter>();
		if(this.getId() != null){
			if(this.getId() > 0){list.add(new PostParameter("id",String.valueOf(this.getId())));}
		}
		Field[] fs =this.getClass().getDeclaredFields();
		for(Field f : fs){	
			if(getFieldValue(f.getName(),this) != null && !getFieldValue(f.getName(),this).equals("") && !(getFieldValue(f.getName(),this) instanceof File)){
				list.add(new PostParameter(f.getName(),String.valueOf(getFieldValue(f.getName(),this))));
			}
		}
		PostParameter [] params = new PostParameter[list.size()];
		for(int i=0;i<list.size();i++){	
			params[i] = (PostParameter)list.get(i);
		}
		return params;
	}
	
	private Object getFieldValue(String fieldName, Object o) {  
        try {  
            String firstLetter = fieldName.substring(0, 1).toUpperCase();  
            String getter = "get" + firstLetter + fieldName.substring(1);  
            Method method = o.getClass().getMethod(getter, new Class[] {});  
            Object value = method.invoke(o, new Object[] {});  
            return value;  
        } catch (Exception e) {  
            System.out.println("属性不存在");  
            return null;  
        }  
    }
	/**
	 * 获取实例对应的表名。
	 * @return
	 *
	public static String getTableName(Class<? extends BeelnnEntity> type){
		TableInfo ti = type.getAnnotation(TableInfo.class);
		if(ti == null)return null;
		String name = ti.tableName();
		if(name == null || name.equals(""))
			name = type.getName();
		return name;
	}*/
	private static HashMap<String, TableInfoCache> infoCache;
	public static class FieldInfoCache{
		public Field field;
		public FieldInfo fieldInfo;
		public String jsonName;
		public String fieldName;
		public String name;
	}
	public static class TableInfoCache{
		public Class<?> type;
		public String tableName;
		public String name;
		public ArrayList<FieldInfoCache> fieldList = new ArrayList<GezitechEntity.FieldInfoCache>();
	}
	/**
	 * 取某个实体对象的字段的缓存信息
	 * @param type
	 * @return
	 */
	public synchronized static TableInfoCache getEntityFieldsInfo(Class<? extends GezitechEntity> type){
		if(type == null)return null;
		String key = type.getName();
		if(infoCache == null)infoCache = new HashMap<String, TableInfoCache>();
		if(infoCache.containsKey(key))return infoCache.get(key);
		
		TableInfoCache tic = new TableInfoCache();
		tic.type = type;
		tic.name = key;
		TableInfo ti = type.getAnnotation(TableInfo.class);
		if(ti == null || ti.tableName() == null || ti.tableName().equals(""))tic.tableName = tic.name;
		else tic.tableName = ti.tableName();
		if(tic.tableName.contains("."))tic.tableName = tic.tableName.substring(tic.tableName.lastIndexOf(".") + 1);
		
		Field[] fs = type.getFields();
		HashMap<String, FieldInfoCache> hashFields = new HashMap<String, FieldInfoCache>();//用来处理重载成员
		for(Field f : fs){
			FieldInfo fi = f.getAnnotation(FieldInfo.class);
			if(fi == null)continue;
			FieldInfoCache fic = new FieldInfoCache();
			fic.fieldInfo = fi;
			fic.field = f;
			fic.name = f.getName();
			String jsonName = fi.jsonName();
			String fieldName = fi.fieldName();
			if(jsonName == null)fic.jsonName = null;
			else fic.jsonName = jsonName.equals("") ? fic.name : jsonName;
			if(fieldName == null)fic.fieldName = null;
			else fic.fieldName = fieldName.equals("") ? fic.name : fieldName;
			
			if(hashFields.containsKey(fic.name)){//这里只考虑了两重继承关系
				if(!hashFields.get(fic.name).field.getDeclaringClass().getName().equals(key)){
					hashFields.remove(fic.name);
					hashFields.put(fic.name, fic);
//					tic.fieldList.add(fic);
				}
			}
//			else tic.fieldList.add(fic);
			else hashFields.put(fic.name, fic);
		}
		//先非关联字段，再关联字段
		Collection<FieldInfoCache> fics = hashFields.values();
		for(FieldInfoCache fic : fics)if(!fic.fieldInfo.isChildren() && !fic.fieldInfo.isParent())tic.fieldList.add(fic);
		for(FieldInfoCache fic : fics)if(fic.fieldInfo.isChildren() || fic.fieldInfo.isParent())tic.fieldList.add(fic);
		
		infoCache.put(key, tic);
		return tic;
	}
	/**
	 * 获取某个类的json或数据库对应的字段信息。`														
	 * @param type 实体类型
	 * @param containPrimary 是否包含主键
	 * @param containNonPrimary 是否包含非主键
	 * @param containRelatedField 是否包含关联字段
	 * @return 字段信息列表
	 */
	public static ArrayList<FieldInfoCache> getFieldsInfo(Class<? extends GezitechEntity> type, boolean containPrimary, boolean containNonPrimary, boolean containRelatedField){
		TableInfoCache tic = getEntityFieldsInfo(type);
		if(tic == null)return new ArrayList<FieldInfoCache>();
		ArrayList<FieldInfoCache> list = new ArrayList<FieldInfoCache>();
		for(FieldInfoCache fic : tic.fieldList){
			if(fic.fieldInfo.isChildren() || fic.fieldInfo.isParent()){
				if(containRelatedField)list.add(fic);
			}
			else if(fic.fieldInfo.isPrimary() && containPrimary)list.add(fic);
			else if(!fic.fieldInfo.isPrimary() && containNonPrimary)list.add(fic);
		}
		return list;
	}
	/**
	 * 获取某个映射字段
	 * @param type 所属对象
	 * @param field 字段名称
	 * @return
	 */
	public static FieldInfoCache getFieldInfo(Class<? extends GezitechEntity> type, String field){
		ArrayList<FieldInfoCache> list = getFieldsInfo(type, true, true, true);
		if(list == null || list.size() <= 0)return null;
		for(FieldInfoCache f : list){
			if(f.name.equals(field))return f;
		}
		return null;
	}
	/**
	 * 在一定的排序条件下，判断当前对象是否比目标对象老，或者查询时后出现
	 * @param entity
	 * @return
	 */
	public boolean isOlderThan(GezitechEntity entity, OrderList order){
		if(entity == null)return true;
		if(order == null)return this.id > entity.id;
		for(Object o : order.getItems()){
			OrderItem oi = (OrderItem)o;
			Field f = oi.fieldInfo.field;
			Class<?> type = f.getType();
			try{
				String val1 = f.get(this).toString();
				String val2 = f.get(entity).toString();
				if(val1.equals(val2))continue;
				boolean isMax = false;
				if(type.equals(int.class) || type.equals(Integer.class))
					isMax = Integer.valueOf(val1) > Integer.valueOf(val2);
				else if(type.equals(long.class) || type.equals(Long.class))
					isMax = Long.valueOf(val1) > Long.valueOf(val2);
				else  if( type.equals( double.class ) || type.equals( Double.class ) )
						isMax = Double.valueOf(val1) > Double.valueOf(val2);
				else 
					isMax = val1.compareTo(val2) > 0;
				return (isMax && oi.isAsc) || (!isMax && !oi.isAsc);
			}catch(Exception ex){
				ex.printStackTrace();
				continue;
			}
		}
		return false;
	}
	/**
	 * 建表之后的操作
	 */
	public void afterCreateTable(){
		
	}
	/**
	 * 获取某个field对应的json编码某数据库字段的名称
	 * @param field
	 * @param isJsonName
	 * @return
	 *
	public static String getFieldName(Field field, boolean isJsonName){
		if(field == null)return null;
		FieldInfo fi = field.getAnnotation(FieldInfo.class);
		if(fi == null)return null;
		String name = isJsonName ? fi.jsonName() : fi.fieldName();
		if(name == null)return null;
		if(name.equals(""))name = field.getName();
		return name;
	}
	/**
	 * 字段信息注解
	 * @author Administrator
	 *
	 */
	@Documented
	@Inherited
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FieldInfo{
		//映射到数据库的名称，如果为null,表示不映射到数据库
		public String fieldName() default "";
		//映射到json对象的字段名称，如果为null，表示不映射
		public String jsonName() default "";
		//是否是数据库的主键
		public boolean isPrimary() default false;
		//字段类型
		public FieldType fieldType() default FieldType.Auto;
		//与其它表关联时对应那个表中的关联字段
		public String outerKey() default "";
		//与其它表关联时对应的本表中的关联字段
		public String innerKey() default "";
		/**
		 * 是否为子对象。
		 * 如果该对象中的所有对象有完整的值，不需要其它值来关联到本对象，则该对象是父对象。反之，若该对象中有一个字段用来存储关联字段到本对象的，则为子对象
		 * @return
		 */
		public boolean isChildren() default false;
		/**
		 * 是否为父对象。
		 * 如果该对象中的所有对象有完整的值，不需要其它值来关联到本对象，则该对象是父对象。反之，若该对象中有一个字段用来存储关联字段到本对象的，则为子对象
		 * @return
		 */
		public boolean isParent() default false;
		//外部关联对象的类型.如果反射中可以获取泛型类型就不需要这破玩意儿了
		public Class<? extends GezitechEntity> outerType() default GezitechEntity.class;
		//表明该字段是否是个人信息相关的字段，这类字段在重新登陆时值会被清空，在下载时根据设置可以自动获值
		public boolean isPersonal() default false;
	}
	/**
	 * 字段类型
	 * @author Administrator
	 *
	 */
	public enum FieldType{Auto, Integer, Varchar, Text}//auto表示自动判断
	/**
	 * 表信息注解
	 * @author Administrator
	 *
	 */
	@Documented
	@Inherited
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TableInfo{
		public String tableName() default "";
	}
	/**
	 * 查询关联对象的信息
	 * @param field 存储关联对象的field。
	 */
	public void queryRelated(String field, Context context){
		ArrayList<FieldInfoCache> fics = getFieldsInfo(this.getClass(), true, true, true);
		FieldInfoCache fic = null;
		for(FieldInfoCache f : fics){
			if(f.name.equals(field)){
				fic = f;
				break;
			}
		}
		if(fic == null)return;
		GezitechEntityAbstractCollection bac = null;
		try {
			bac = (GezitechEntityAbstractCollection)fic.field.get(this);
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if(bac == null)return;
		bac.clear();
		String key = fic.fieldInfo.innerKey();
		FieldInfoCache innerFic = null;
		if(StringUtil.isEmpty(key))key = "id";
		for(FieldInfoCache f : fics){
			if(f.name.equals(key)){
				innerFic = f;
				break;
			}
		}
		if(innerFic == null)return;
		Object value;
		try {
			value = innerFic.field.get(this);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		bac.fetchData(fic, value, context);
	}
	public static boolean entityExistInArray(ArrayList list, long id){
		for(Object o : list){
			GezitechEntity be = (GezitechEntity)o;
			if(be == null)continue;
			if(be.id == id)return true;
		}
		return false;
	}
	public static GezitechEntity getCache(long id){
		return null;
	}
}
