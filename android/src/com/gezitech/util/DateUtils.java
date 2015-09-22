package com.gezitech.util;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: jqwang
 * Date: 11-5-12
 * Time: 下午9:12
 * To change this template use File | Settings | File Templates.
 */
public class DateUtils {
    /**
     * see like is: 2003-1-1 1:12:21
     */
    public static final int LONG_DATE = 1;
    /**
     * see like is: 2003-1-1
     */
    public static final int SHORT_DATE = 2;
    /**
     * see like is:8:0:41
     */
    public static final int SHORT_TIME = 3;
    /**
     * see like is: 2003-1-1 1:12
     */
    public static final int SHORT_DATE_TIME = 4;

    /**
     * if format failure,return &amp;nbsp;
     */
    public static final int TYPE_HTML_SPACE = 2;
    /**
     * if format failure,return -
     */
    public static final int TYPE_DECREASE_SYMBOL = 3;
    /**
     * if format failure,return a white space
     */
    public static final int TYPE_SPACE = 4;
    /**
     * if format failure,return null
     */
    public static final int TYPE_NULL = 5;


    static DateFormat yearFormat = new SimpleDateFormat("yy年M月d号");
    static DateFormat monthFormat = new SimpleDateFormat("M月d号H点");
    static DateFormat dayFormat = new SimpleDateFormat("d号H点m分");
    static DateFormat todayFormat = new SimpleDateFormat("H点m分s秒");
    static DateFormat yesterdayFormat = new SimpleDateFormat("昨天H点m分");
    static DateFormat yesterdayBeforFormat = new SimpleDateFormat("前天H点m分");
    static DateFormat tomorrowFormat = new SimpleDateFormat("明天H点m分");
    static DateFormat afterTomorrowFormat = new SimpleDateFormat("后天H点m分");


    /**
     * 该类不需要实例化<br>
     */
    private DateUtils() {
    }


    /**
     * 测试方法。请保留<br>
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            //---------------------------------
            // 按照传入参数来测试
            //---------------------------------
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                Date y = parse(arg);
                System.out.println("y = " + format(y));
            }
        } else {
            //---------------------------------
            // 如果没有传入参数，
            // 则执行标准的test Suite
            //---------------------------------
            String s = "1990-1-1 26:32:12";
            Date y = parse(s);
            System.out.println("y = " + format(y));
            s = "1991-15-7";
            y = parse(s);
            System.out.println("y = " + format(y,
                    SHORT_DATE,
                    TYPE_NULL));
        }
    }

    /**
     * 将字符串parse成日期<br>
     *
     * @param str 源 格式为:yyyy-MM-dd HH:mm:ss或者yyyy-MM-dd
     * @return 日期，如果parse失败，则返回null
     */
    public static Date parse(String str) {
        //---------------------------------
        // str不能为空
        //---------------------------------

        if (str == null || str.length() == 0) {
            return null;
        }

        //---------------------------------
        // 定义返回变量
        //---------------------------------
        Date result = null;
        try {

            //---------------------------------
            // 按照yyyy-MM-dd HH:mm:ss格式来parse
            //---------------------------------
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setLenient(false);
            result = format.parse(str);
        } catch (ParseException e) {
            try {
                //---------------------------------
                // 如果按照yyyy-MM-dd HH:mm:ss parse失败。
                // 则按照yyyy-MM-dd格式来parse。
                //---------------------------------
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                format.setLenient(false);
                result = format.parse(str);
            } catch (ParseException e1) {
                //---------------------------------
                // 如果再parse失败。则不做任何处理。
                //---------------------------------
            }
        }
        return result;
    }

    /**
     * 格式化日期，格式为：yyyy-M-d H:m:s<br>
     * 参照：format(Date date, String format, int type)<br>
     *
     * @param date 要格式化的日期对象
     * @param type 如果日期为空，定义返回的类型
     * @return 返回值，如果date为空，则type定义返回类型，如果格式化失败。则返回空串
     */
    public static String format(Date date,
                                int type) {
        //---------------------------------
        // 默认为长日期格式。即：yyyy-M-d H:m:s
        //---------------------------------
        return format(date,
                LONG_DATE,
                type);
    }

    /**
     * 格式化日期；格式为：yyyy-M-d H:m:s<br>
     * 参照：format(Date date, String format, int type)<br>
     *
     * @param date 要格式化的日期对象
     * @return 返回值，如果date为空或者格式化失败。则返回空串
     */
    public static String format(Date date) {
        //---------------------------------
        // 默认为长日期格式和失败后返回。即：yyyy-M-d H:m:s
        //---------------------------------
        return format(date,
                LONG_DATE,
                1);
    }

    /**
     * 格式化日期；<br>
     * 参照：format(Date date, String format, int type)<br>
     *
     * @param date   要格式化的日期对象
     * @param format 格式
     * @param type   如果日期为空，定义返回的类型
     * @return 返回值，如果date为空，则type定义返回类型，如果格式化失败。则返回空串
     */
    public static String format(Date date,
                                int format,
                                int type) {
        switch (format) {
            case SHORT_DATE:
                return format(date, "yyyy-MM-dd", type);
            case SHORT_TIME:
                return format(date, "HH:mm:ss", type);
            case SHORT_DATE_TIME:
                return format(date, "yyyy-MM-dd HH:mm", type);
            default:
                return format(date, "yyyy-MM-dd HH:mm:ss", type);
        }
    }

    /**
     * 格式化日期，
     *
     * @param date   要格式化的日期对象
     * @param format 格式
     * @param type   如果日期为空，定义返回的类型
     * @return 返回值，如果date为空，则type定义返回类型，如果格式化失败。则返回空串
     */
    public static String format(Date date,
                                String format,
                                int type) {
        if (date != null) {
            //---------------------------------
            // 日期不为空时才格式
            //---------------------------------
            try {
                //---------------------------------
                // 调用SimpleDateFormat来格式化
                //---------------------------------
                return new SimpleDateFormat(format).format(date);
            } catch (Exception e) {
                //---------------------------------
                // 格式化失败后，返回一个空串
                //---------------------------------
                return "";
            }
        } else {
            //---------------------------------
            // 如果传入日期为空，则根据类型返回结果
            //---------------------------------
            switch (type) {
                case TYPE_HTML_SPACE: // '\002'
                    return "&nbsp;";

                case TYPE_DECREASE_SYMBOL: // '\003'
                    return "-";

                case TYPE_SPACE: // '\004'
                    return " ";

                case TYPE_NULL:
                    return null;

                default:
                    //---------------------------------
                    // 默认为空串
                    //---------------------------------
                    return "";
            }
        }
    }

    /**
     * 格式化日期
     *
     * @param date 要格式化的日期对象
     * @return 返回值，如果date为空，则type定义返回类型，如果格式化失败。则返回空串
     */
    public static String chineseFormat(Date date) {
        return chineseFormat(date, TYPE_DECREASE_SYMBOL);
    }

    /**
     * 格式化日期
     *
     * @param date 要格式化的日期对象
     * @param type 如果日期为空，定义返回的类型
     * @return 返回值，如果date为空，则type定义返回类型，如果格式化失败。则返回空串
     */
    public static String chineseFormat(Date date, int type) {
        String rc = null;
        //---------------------------------
        // 日期不为空时才格式
        //---------------------------------
        if (date != null)
            try {
                Calendar now = Calendar.getInstance();

                Calendar cre = Calendar.getInstance();
                cre.setTime(date);
                if (now.get(Calendar.YEAR) != cre.get(Calendar.YEAR)) {
                    rc = yearFormat.format(date);
                } else if (now.get(Calendar.MONTH) != cre.get(Calendar.MONTH)) {
                    rc = monthFormat.format(date);
                } else {
                    int nowDay = now.get(Calendar.DAY_OF_YEAR);
                    int creDay = cre.get(Calendar.DAY_OF_YEAR);
                    if (nowDay == creDay) {
                        rc = todayFormat.format(date);
                    } else if ((nowDay - creDay) == 1) {
                        rc = yesterdayFormat.format(date);
                    } else if ((nowDay - creDay) == 2) {
                        rc = yesterdayBeforFormat.format(date);
                    } else if ((nowDay - creDay) == -1) {
                        rc = tomorrowFormat.format(date);
                    } else if ((nowDay - creDay) == -2) {
                        rc = afterTomorrowFormat.format(date);
                    } else {
                        rc = dayFormat.format(date);
                    }
                }

            } catch (Exception e) {
            }

        if (rc == null) {

            switch (type) {
                case TYPE_HTML_SPACE: // '\002'
                    return "&nbsp;";

                case TYPE_DECREASE_SYMBOL: // '\003'
                    return "-";

                case TYPE_SPACE: // '\004'
                    return " ";

                case TYPE_NULL:
                    return null;

                default:
                    return "";
            }
        }

        return rc;
    }

    public static String getCurrentDateString(String dateFormat) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(cal.getTime());
    }


    /**
     *  Get the string representation of the input Date object
     *
     * @param date       the input Date object
     * @param dateFormat a date format string like "dd/MM/yyyy"
     * @return the string representation of the input Date object
     */
    public static String getDateString(Date date, String dateFormat) {
        if (date!=null) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.format(date);
        } else {
            return null;
        }
    }


    /**
     *  Get a java Date object from an input date string representation.
     *  <br>
     *  See the <code>java.text.SimpleDateFormat</code> API for date format string
     *  examples.
     *
     * @param  sDate       the date string representation
     * @param  dateFormat  a date format string like "dd/MM/yyyy"
     * @return             the Date object corresponding to the input date string,
     *                     or null if the conversion fails
     */
    public static Date getDate(String sDate, String dateFormat) {
        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
        ParsePosition pos = new ParsePosition(0);

        return fmt.parse(sDate, pos);
    }


    /**
     *  Add the input number of days to the startDate string representation.
     *
     * @param startDate  the start date string representation
     * @param dateFormat the start date format
     * @param days       the number of days to add to the startDate
     * @return the Date object representing the resulting date
     */
    public static Date addDays(String startDate, String dateFormat, int days) {
        return addDays(getDate(startDate, dateFormat), days);
    }


    /**
     *  Add the input number of days to the start Date object.
     *
     * @param startDate  the start Date object
     * @param days       the number of days to add to the startDate object
     * @return the Date object representing the resulting date
     */
    public static Date addDays(Date startDate, int days) {
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(startDate);
        gCal.add(Calendar.DATE, days);

        return gCal.getTime();
    }

    public static Date addWeeks(Date startDate, int weeks) {
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(startDate);
        gCal.add(Calendar.WEEK_OF_YEAR, weeks);

        return gCal.getTime();
    }

   public static Date addMonths(Date startDate, int months) {
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(startDate);
        gCal.add(Calendar.MONTH, months);

        return gCal.getTime();
    }

       /**
     *  Add the input number of days to the start Date object.
     *
     * @param startDate  the start Date object
     * @param hours       the number of days to add to the startDate object
     * @return the Date object representing the resulting date
     */
    public static Date addHours(Date startDate, int hours) {
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(startDate);
        gCal.add(Calendar.HOUR, hours);

        return gCal.getTime();
    }

     /**
     *  Add the input number of days to the start Date object.
     *
     * @param startDate  the start Date object
     * @param hours       the number of hours to add to the startDate object
     * @param dateFormat      the start date format
     * @return the Date object representing the resulting date
     */
    public static Date addHours(String startDate, String dateFormat, int hours) {
        return addHours(getDate(startDate, dateFormat), hours);
    }

     /**
     *  Add the input number of days to the start Date object.
     *
     * @param startDate  the start Date object
     * @param hours       the number of hours to add to the startDate object
     * @param dateFormat1      the start date format
     * @param dateFormat2      the end date format
     * @return the Date object representing the resulting date
     */
    public static String addHours(String startDate, String dateFormat1, int hours,String dateFormat2) {
//         return  getDateString(addDays(getDate(startDate, dateFormat1), hours),dateFormat2);

        return  getDateString(addHours(getDate(startDate, dateFormat1), hours),dateFormat2);
    }

    /**
     *  Check if the <code>d</code> input date is between <code>d1</code> and
     *  <code>d2</code>.
     *
     * @param  d   the date to check
     * @param  d1  the lower boundary date
     * @param  d2  the upper boundary date
     * @return     true if d1 <= d <= d2, false otherwise
     */
    public static boolean isDateBetween(Date d, Date d1, Date d2) {
        return ((d1.before(d) || d1.equals(d)) &&
                (d.before(d2) || d.equals(d2)));
    }

    public static String getCurrentTime() {
        return DateFormat.getDateTimeInstance().format(new Date());
    }

    public static String getCurrentDate() {
        return DateFormat.getDateInstance().format(new Date());
    }

    public static String formatDate(Date theDate) {
        Locale locale = Locale.CHINA;
        String dateString = "";
        try {
            Calendar cal = Calendar.getInstance(locale);
            cal.setFirstDayOfWeek(Calendar.TUESDAY);
            cal.setTime((Date) theDate);

            //DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.MEDIUM,locale);
            java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
            dateString = dateFormatter.format(cal.getTime());
            //System.out.println(dateString);
            //System.out.println(cal.get(Calendar.YEAR));
            //System.out.println(cal.get(cal.DAY_OF_WEEK));
        } catch (Exception e) {
            System.out.println("test result:" + e.getMessage());
        } finally {
            return dateString;
        }
    }

    public static int getDateDiff(Date date1,Date date2,int sign) {

         long base = 1;
         switch(sign){
             case Calendar.DATE:
                base *=1000*60*60*24;
                break;
             case Calendar.HOUR:
                base *=1000*60*60;
                break;
             case Calendar.MINUTE:
                base *=1000*60;
                break;
             case Calendar.SECOND:
                 base *=1000;
                break;
             default:
                break;
         }
         return (int)((date1.getTime()-date2.getTime())/base);
     }
    //获取时间字符串
  	public static String getStringToday() {
  		Date currentTime = new Date();
  		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
  		String dateString = formatter.format(currentTime);
  		String s = ""; 
  		Random ran =new Random(System.currentTimeMillis()); 
  		for (int i = 0; i < 10; i++) { 
  		s =s  + ran.nextInt(100); 
  		} 
  		return dateString+s;
  	}
  	//根据秒判断 
  	public static String getTimeStr( long second ){
  		String time ;
  		if( second/(365*24*3600) >0 ){
  			time = second/(365*24*3600) +"年";
  		}else if( second/(24*3600)>0  ){
  			time = second/(24*3600) +"天";
  		}else if( second/3600>0 ){
  			time = second/3600 +"时";
  		}else if( second/60 > 0 ){
  			time = second/60 +"分钟";
  		}else{
  			time = second +"秒";
  		}
  		return time;
  	}
  	//根据时间字符串返回时间戳
  	//time 时间格式可以为2014-12-12
  	//type 时间格式类型字符串 yyyy-MM-dd
  	public static long getTimeStamp(String time, String type){
  		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( type );
	    Date date;
	    long timeStemp = 0;
		try {
			date = simpleDateFormat .parse( time );
			timeStemp = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeStemp;
  	}
}
