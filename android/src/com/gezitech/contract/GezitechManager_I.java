package com.gezitech.contract;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
public interface GezitechManager_I {
	
	/**
	 * 插入一条记录
	 * @param entity
	 */
	String insert(GezitechEntity entity,GezitechManager_I.OnAsynInsertListener l);
	/**
	 * 更新一条记录
	 * @param entity
	 */
	String update(GezitechEntity entity,GezitechManager_I.OnAsynUpdateListener l);
	/**
	 * 删除一条记录
	 * @param id
	 */
	String delete(String id,GezitechManager_I.OnAsynDeleteListener l);
	/**
	 * 根据ID获取数据
	 * @param id
	 * @return
	 */
	GezitechEntity getOne(String id,GezitechManager_I.OnAsynGetOneListener l);
	/**
	 * 检测数据是否存在
	 * @param id
	 * @return
	 */
	boolean existsLocal(String id);
	/**
	 * 查询记录条数
	 * @return
	 */
	int getItemCountLocal();
	/*********************************
	 * 获取本地最大id
	 * @return
	 */
	int getMaxIdLocal();

	public interface OnAsynInsertListener extends OnAsynRequestFailListener{
		void onInsertDone(String id);
	}
	
	public interface OnAsynUpdateListener extends OnAsynRequestFailListener{
		void onUpdateDone(String id);
	}
	public interface OnAsynProgressListener extends OnAsynUpdateListener{
		void OnProgress(int bytesWritten, int totalSize);
	}
	public interface OnAsynDeleteListener extends OnAsynRequestFailListener{	
		void onDeleteDone(String id);
	}
	
	public interface OnAsynGetListListener extends OnAsynRequestFailListener{
		void OnGetListDone(ArrayList<GezitechEntity_I> list);
	}
	
	public interface OnAsynGetListArrayListListener extends OnAsynRequestFailListener{
		void OnGetListDone(ArrayList< GezitechEntity_I > list, ArrayList<String> mSections, ArrayList<Integer> mPositions );
	}
	
	public interface OnAsynGetOneListener extends OnAsynRequestFailListener{	
		void OnGetOneDone(GezitechEntity_I entity);
	}
	//回掉是jsonobject
	public interface OnAsynGetJsonObjectListener extends OnAsynRequestFailListener{	
		void OnGetJSONObjectDone(JSONObject jo );
	}
	
	public interface OnAsynRequestFailListener {	
		void OnAsynRequestFail(String errorCode,String errorMsg);
	}
	
	public interface OnAsynRequestListener extends OnAsynRequestFailListener{	
		void OnAsynRequestCallBack(Object o);
	}
	
}
