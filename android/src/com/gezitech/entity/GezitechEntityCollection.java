package com.gezitech.entity;

import java.util.ArrayList;

import android.content.Context;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfoCache;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.StringUtil;
/**
 * 关联对象集合
 * @author Administrator
 *
 * @param <T>
 */
public class GezitechEntityCollection<T extends GezitechEntity> extends GezitechEntityAbstractCollection implements java.io.Serializable{
	public GezitechEntityCollection(){
		super();
	}
	public void add(T t){
		addEntity(t);
	}
	public T get(int index){
		GezitechEntity be = getEntity(index);
		return (T)be;
	}
	public ArrayList<T> getList(){
		ArrayList<GezitechEntity> bes = getEntityList();
		ArrayList<T> res = new ArrayList<T>();
		for(GezitechEntity be : bes)res.add((T)be);
		return res;
	}
	@Override
	public void fetchData(FieldInfoCache fic, Object innerValue, Context context) {
		if(fic.fieldInfo.outerKey() == null || fic.fieldInfo.outerKey().equals(GezitechEntity.class))return;
		GezitechDBHelper<T> db = new GezitechDBHelper<T>(context, fic.fieldInfo.outerType());
		String key = fic.fieldInfo.outerKey();
		if(StringUtil.isEmpty(key))key = "id";
		FieldInfoCache tarFic = GezitechEntity.getFieldInfo(fic.fieldInfo.outerType(), key);
		if(tarFic == null)return;
		String sp = GezitechDBHelper.getDbSpliter(tarFic.field.getType());
		
		String where = tarFic.fieldName + "=" + sp + innerValue + sp;
		ArrayList<T> list = db.query(where, 0, null);
		if(list == null || list.size() <= 0)return;
		for(T t : list)add(t);
	}
}
