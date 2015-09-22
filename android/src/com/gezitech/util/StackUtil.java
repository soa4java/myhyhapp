package com.gezitech.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import android.util.Log;

/**
 * 
 * @author xiaobai
 * 2014-8-20
 * @todo( 栈管理  )
 * 
 * @param <T>
 */
public class StackUtil<T>
{
	
	public ArrayList<T> list = new ArrayList<T>();
	public T[] filter;
	private OnCallBackAction onCallBackAction;
	public StackUtil(   T[] filter ){
		this.filter = filter;
	}
	
	//入栈
	public  void push(T element , boolean isRemove )
	{
		//扫描是否有未过滤的实例
		if( isRemove ){
			filterElement();
		}
		list.add( element );
	}
	
	public void filterElement(){	
		ArrayList<T> newlist = new ArrayList<T>();		
		Iterator<T> sListIterator = list.iterator();  
		while(sListIterator.hasNext()){  
			T e = sListIterator.next();  
		   boolean isRemove = true;
		   for( int i = 0; i< filter.length; i++ ){
				if(  filter[i] != null && e != null &&  filter[i].equals( e ) ){	
					newlist.add( e );
					isRemove = false;
				}
			}
			
		   if( isRemove ){
			   
			   onCallBackAction.onCallBack( e );
		   }
		}
		list = newlist;	
	}
	
	//出栈
	public T pop(){
		int size = list.size();
		
		T item = list.get( size-1 );		
		
		list.remove( size-1 );
		
		return item;
	}
	//获取顶栈
	public T peek(){
		
		int size = list.size();
		
		T item = list.get( size-1 );	
		
		return item;
	}
	
	//判断顺序栈是否为空栈
	public boolean empty(){
		return list.size() == 0;
	}
	//清空顺序栈
	public void clear(){
		list.clear();
	}
	//重置栈顺序
	public void reset(T element , boolean isRemove  ){
		
		//如果顶部是当前对象  不重置顺序
		if( this.peek().equals( element ) ) return;
		
		Iterator<T> sListIterator = list.iterator();  
		while(sListIterator.hasNext()){  
			T e = sListIterator.next();  
		    if( e.equals( element ) ){  
		    	sListIterator.remove();  
		    }  
		}
		list.add( element );
		
		if( isRemove ){
			filterElement();
		}
	
	}
	public interface OnCallBackAction{
		public void onCallBack(Object element);
	}
	
	public void setOnCallBackAction( OnCallBackAction onCallBackAction){
		this.onCallBackAction  = onCallBackAction;
	}
	
}