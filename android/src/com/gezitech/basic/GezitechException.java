package com.gezitech.basic;
/**
 * 比邻网终端程序异常基类。
 * @author heyao
 *
 */
public class GezitechException  extends Exception{
	
	private int statusCode = -1;
    private static final long serialVersionUID = -2623309261327598087L;
    
    public GezitechException(String msg){
    	super(msg);
    }
    public GezitechException(Exception cause) {
        super(cause);
    }

    public GezitechException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;

    }

    public GezitechException(String msg, Exception cause) {
        super(msg, cause);
    }

    public GezitechException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;

    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
