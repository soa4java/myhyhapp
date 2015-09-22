package com.gezitech.entity;
import java.util.ArrayList;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 相册模型类
 * @author xiaobai
 */
@TableInfo(tableName="PhotoAlbumModel")
public class PhotoAlbumModel extends GezitechEntity {
	public PhotoAlbumModel(JSONObject jo) {
		super( jo );
	}
	public PhotoAlbumModel(){
		super();
	}
	private static final long serialVersionUID = 1L;
	public String name;   //相册名字
	public String count; //数量
	public int  bitmap;  // 相册第一张图片
	public String path; //图片路径
	
	public ArrayList<GezitechEntity_I> bitList = new ArrayList<GezitechEntity_I>();
}
