package com.gezitech.service.managers;

import java.util.ArrayList;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfoCache;
import com.gezitech.basic.GezitechEntity.TableInfoCache;
public class DataManager<T extends GezitechEntity> {
	/**
	 * 数据库的排序字段
	 * @author Administrator
	 *
	 */
	public static class OrderItem{
		public boolean isAsc;
		public FieldInfoCache fieldInfo;
		public static OrderItem create(Class<? extends GezitechEntity> genericType, String fieldName, boolean isAsc){
			ArrayList<FieldInfoCache> fics = GezitechEntity.getFieldsInfo(genericType, true, true, false);
			if(fics == null || fics.size() <= 0)return null;
			FieldInfoCache fic = null;
			for(FieldInfoCache fc : fics){
				if(fc.name.equals(fieldName)){
					fic = fc;
					break;
				}
			}
			if(fic == null)return null;
			OrderItem oi = new OrderItem();
			oi.isAsc = isAsc;
			oi.fieldInfo = fic;
			return oi;
		}
	}
	/**
	 * 排序集合，如果没有任何排序字段，则默认为id desc。
	 * @author Administrator
	 *
	 */
	public static class OrderList{
		private ArrayList<OrderItem> list;
		private Class<? extends GezitechEntity> type;
		public OrderList(Class<? extends GezitechEntity> type, OrderItem...items){
			this.type = type;
			list = new ArrayList<OrderItem>();
			if(items == null || items.length <= 0)return;
			for(OrderItem oi : items)list.add(oi);
		}
		public OrderList add(String fieldName, boolean isAsc){
			OrderItem oi = OrderItem.create(type, fieldName, isAsc);
			this.list.add(oi);
			return this;
		}
		@Override
		public String toString() {
			if(list.size() <= 0)return "id desc";
			String res = "";
			for(OrderItem oi : list)
				res += oi.fieldInfo.fieldName+" "+(oi.isAsc?"asc":"desc")+",";
			res = res.substring(0, res.length() - 1);
			return res;
		}
		public ArrayList<OrderItem> getItems(){
			if(list.size() <= 0){
				ArrayList<OrderItem> res = new ArrayList<OrderItem>();
				res.add(OrderItem.create(type, "id", false));
				return res;
			}
			return list;
		}
	}
	/**
	 * 当前使用的个人信息字段
	 * @author Administrator
	 */
	public static class PersonalField{
		public FieldInfoCache fieldInfo;
		public PersonalField(){}
		/**
		 * 根据java对象的field名称来创建一个个人关系对象。
		 * @param fieldName
		 * @throws Exception 字段不存在
		 */
		public static PersonalField create(String fieldName, Class<? extends GezitechEntity> genericType){
			
			TableInfoCache tic = GezitechEntity.getEntityFieldsInfo(genericType);
			for(FieldInfoCache fic : tic.fieldList){
				if(fic.name.equals(fieldName)){
					PersonalField pf = new PersonalField();
					pf.fieldInfo = fic;
					return pf;
				}
			}
			return null;
		}
	}
}