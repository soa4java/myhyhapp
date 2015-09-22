package com.gezitech.entity;

import java.util.ArrayList;

import android.content.Context;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechEntity.FieldInfoCache;

public abstract class GezitechEntityAbstractCollection {
	private ArrayList<GezitechEntity> list;
	protected GezitechEntityAbstractCollection(){
		list = new ArrayList<GezitechEntity>();
	}
	public void addEntity(GezitechEntity be){
		list.add(be);
	}
	public GezitechEntity getEntity(int index){
		return list.get(index);
	}
	public ArrayList<GezitechEntity> getEntityList(){
		return list;
	}
	public int size(){
		return list.size();
	}
	public void clear(){
		list.clear();
	}
	public void fetchData(FieldInfoCache fic, Object value, Context context) {
		
	}
}
