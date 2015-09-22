package com.gezitech.entity;

import java.util.ArrayList;

import com.gezitech.contract.GezitechEntity_I;


public class PageList extends ArrayList<GezitechEntity_I>{
	private static final long serialVersionUID = 7737285502435406345L;
	private int pageIndex;//当前页下标
	private int pageCount;//总页数
    private int dataCount;//数据总数
    private int type;//消息类别
    private String to;//消息发送给谁   
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}	
	public int getDataCount(){
		return dataCount;
	}
	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}
	public void setType( int type ){
		this.type = type ;
	}
	public int getType(){
		
		return type;
	}
	public void setTo( String to ){
		this.to = to;
	}
	public String getTo(){
		return to;
	}	
}
