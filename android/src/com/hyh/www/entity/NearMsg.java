package com.hyh.www.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 附近人消息实体  )
 */
@TableInfo(tableName="nearMsg")
public class NearMsg extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String content; 
	@FieldInfo
	public long uid;
	@FieldInfo
	public long range;
	@FieldInfo
	public String address ;
	@FieldInfo
	public JSONArray attachment; 
	@FieldInfo(jsonName="long")
	public String longs;
	@FieldInfo
	public String lat ;
	@FieldInfo
	public long ctime;
	@FieldInfo
	public int like_count;
	@FieldInfo
	public int  comment_count;
	@FieldInfo
	public double m ;
	@FieldInfo
	public int islike;
	@FieldInfo
	public JSONObject publisher; //
	@FieldInfo
	public JSONArray likeList; //喜欢列表
	@FieldInfo
	public JSONArray feedbackList;	//回复列表
//	
//	id: "16",
//	content: "发一条路",
//	attachment: false,
//	long: "106.47",
//	lat: "29.5631",
//	address: "重庆市沙坪坝区小龙坎新街49号-附2",
//	like_count: "0",
//	comment_count: "0",
//	ctime: "1433575654",
//	uid: "102",
//	range: "10",
//	m: "21.270764647273",
//	islike: 0,
//	publisher: {
//	id: "102",
//	sex: "1",
//	nickname: "xcfg",
//	head: "http://shout.star-lai.cn//uploads/timthumb.php?x=1&h=100&w=100&zc=1&src=http%3A%2F%2Fshout.star-lai.cn%2Fuploads%2F%2Fhead%2F0%2F102.jpg"
//	},
//	likeList: [ ],
//	feedbackList: [ ]
	
/*	"id": 10,
    "uid": "41",
    "content": "test",
    "range": 30,
    "address": "重庆市沙坪坝区G212",
    "attachment": "a:1:{i:0;s:168:\"http://shout.star-lai.cn//uploads/timthumb.php?x=1&amp;h=118&amp;w=160&amp;zc=1&amp;src=http%3A%2F%2Fshout.star-lai.cn%2Fuploads%2Fnearby%2F20150605%2F5571a3f1bb2f0.jpg\";}",
    "long": "106.45495",
    "ctime": 1433510899,
    "lat": "29.576074"*/

	public NearMsg( JSONObject jo ){
		super( jo );
	}
	public NearMsg(){
		super();
	}
}
