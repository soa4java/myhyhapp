package com.gezitech.entity;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 相册图片模型类
 * @author xiaobai
 */
@TableInfo(tableName="PhotoItemModel")
public class PhotoItemModel extends GezitechEntity{
	private static final long serialVersionUID = 1L;
	public PhotoItemModel(JSONObject jo) {
		super( jo );
	}
	public PhotoItemModel(){
		super();
	}
	public int  photoID;
	public boolean select = false;
	public String path;

}
