/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author gudwin
 */
public class TimeStamp {
    long t;
    static long tzero = getLongTimeStamp("00:00:00");
    static long start_time;
    public TimeStamp(long n) {
        t = n;
    }
    public TimeStamp(String ts) {
        try {
        t = Time.valueOf(ts).getTime();
        } catch (Exception e) { System.out.println(e); }
    }
    
    public static void setStartTime() {
        start_time = System.currentTimeMillis();
    }
    
    public static long getTimeSinceStart() {
        return(System.currentTimeMillis()-start_time);
    }
    
    public static String getDelaySinceStart() {
        return(getTimeSinceStart("HH:mm:ss.S"));
    }
    
    public static String getTimeSinceStart(String format) {
         return(getStringTimeStamp(getTimeSinceStart(),format));     
    }

   public static boolean isParseable(String s, String f) {
       Date date=null;
       SimpleDateFormat sdf = new SimpleDateFormat(f);
       sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
       try {
        date = sdf.parse(s);
       } catch (Exception e) {  }
       if (date != null) return(true);
       else return(false);
   }

   public static long getLongTimeStamp(String ts) {
       if (isParseable(ts, "HH:mm:ss")) return(getLongTimeStamp(ts,"HH:mm:ss"));
       else if (isParseable(ts, "HH:mm")) return(getLongTimeStamp(ts,"HH:mm"));
       else if (isParseable(ts, "dd/MM/yyyy HH:mm:ss")) return(getLongTimeStamp(ts,"dd/MM/yyyy HH:mm:ss"));
       else if (isParseable(ts, "dd/MM/yyyy HH:mm")) return(getLongTimeStamp(ts,"dd/MM/yyyy HH:mm"));
       else if (isParseable(ts, "dd/MM/yy HH:mm:ss")) return(getLongTimeStamp(ts,"dd/MM/yy HH:mm:ss"));
       else if (isParseable(ts, "dd/MM/yy HH:mm")) return(getLongTimeStamp(ts,"dd/MM/yy HH:mm"));
       else if (isParseable(ts, "dd/MM/yyyy")) return(getLongTimeStamp(ts,"dd/MM/yyyy"));
       else if (isParseable(ts, "dd/MM/yy")) return(getLongTimeStamp(ts,"dd/MM/yy"));
       else if (isParseable(ts, "d/M/yy")) return(getLongTimeStamp(ts,"d/M/yy"));
       else if (isParseable(ts, "M/d/yyyy")) return(getLongTimeStamp(ts,"M/d/yyyy"));
       else if (isParseable(ts, "M/d/yy")) return(getLongTimeStamp(ts,"M/d/yy"));
       else return(0L);
   }

   public static long getLongTimeStamp(String ts, String format) {
       long tst=0;
       try {
        SimpleDateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        tst = df.parse(ts).getTime();
        } catch (Exception e) { System.out.println(e); }
        return tst;
   }

   public static String getStringTimeStamp(long tl, String fs) {
       String s;
       SimpleDateFormat df = new SimpleDateFormat(fs);
       df.setTimeZone(TimeZone.getTimeZone("GMT"));
       s = df.format(tl);
       return(s);
   }

   public static long getLongDelay(String ts) {
       long tst=0;
       tst = TimeStamp.getLongTimeStamp(ts);
       //tzero = TimeStamp.getLongTimeStamp("00:00:00");
       return(tst-tzero);
   }

   public static String getTime(long t) {
       return(getStringTimeStamp(t,"HH:mm"));
   }

   public static String getDate(long t) {
       return(getStringTimeStamp(t,"dd/MM/yyyy"));
   }

   public static String getDateTime(long t) {
       return(getStringTimeStamp(t,"dd/MM/yyyy HH:mm"));
   }

   public static void printTime(long t) {
     System.out.print(getTime(t));
   }

   public static void printDate(long t) {
     System.out.print(getDate(t));
   }

   public static void printDateTime(long t) {
     System.out.print(getDateTime(t));
   }
   
   public static int getInt(String s) {
       return(Integer.parseInt(s));
   }
   
   public static double getDouble(String s) {
       return(Double.parseDouble(s));
   }
           
   public static boolean getBoolean(String s) {
       return(Boolean.parseBoolean(s));
   }
   
   public static long getCanonicalTime(long t) {
       String time = getTime(t);
       long tm = getLongTimeStamp(time);
       return(tm);
   }
   
   
   public static boolean isInPeriod(long t, String ini, String fim) {
       String time = getTime(t);
       //long zero = getLongTimeStamp("00:00");
       //long vq = getLongTimeStamp("24:00");
       long ti = getLongTimeStamp(ini);
       long tf = getLongTimeStamp(fim);
       long tm = getLongTimeStamp(time);
       if (tf < ti) {
           // Verificar até meia noite e depois de 0:00 até fim
           if (tm > ti || tm < tf) return(true);
           else return(false);
       }
       else {
           if (tm > ti && tm < tf) return(true);
           else return(false);
       }
   }
   
   
   public static void main(String args[]) {
       setStartTime();
       try {
       Thread.sleep(3358);
       } catch (Exception e) {}
       System.out.println(getDelaySinceStart());
   }
}
