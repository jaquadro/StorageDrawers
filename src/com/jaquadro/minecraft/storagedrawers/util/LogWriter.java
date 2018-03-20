package com.jaquadro.minecraft.storagedrawers.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LogWriter {
	private final static String name = "StorageDrawer";
	private final static Logger logger = Logger.getLogger(name);
	private final static SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
	private static Handler handler;
	  
	  static
	  {
	    try
	    {
	      File dir = new File("logs");
	      if (!dir.exists()) {
	        dir.mkdir();
	      }
	      
	      File file = new File(dir, name + "-latest.log");
	      System.out.println(file.getPath());
	      File lock = new File(dir,  name + "-latest.log.lck");
	      File file1 = new File(dir,  name + "-1.log");
	      File file2 = new File(dir,  name + "-2.log");
	      File file3 = new File(dir,  name + "-3.log");
	      if (lock.exists()) {
	        lock.delete();
	      }
	      if (file3.exists())
	      {
	        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH-mm-ss");
	        Date date = new Date();
	        File gzip = new File(dir,  name + "-gzip-" + dateFormat.format(date) + ".log.gz");
	        compressGzipFile(file3.getAbsolutePath(), gzip.getAbsolutePath());
	        file3.delete();
	      }
	      if (file2.exists()) {
	        file2.renameTo(file3);
	      }
	      if (file1.exists()) {
	        file1.renameTo(file2);
	      }
	      if (file.exists()) {
	        file.renameTo(file1);
	      }
	      handler = new StreamHandler(new FileOutputStream(file), new Formatter()
	      {
	        public String format(LogRecord record)
	        {
	          StackTraceElement element = Thread.currentThread().getStackTrace()[8];
	          String line = "[" + element.getClassName() + ":" + element.getLineNumber() + "] ";
	          String time = "[" + LogWriter.dateformat.format(new Date(record.getMillis())) + "][" + record.getLevel() + "/" + "StorageDrawer" + "]";
	          if (record.getThrown() != null)
	          {
	            StringWriter sw = new StringWriter();
	            PrintWriter pw = new PrintWriter(sw);
	            record.getThrown().printStackTrace(pw);
	            return time + sw.toString();
	          }
	          return time + record.getMessage() + System.getProperty("line.separator");
	        }
	      });
	      handler.setLevel(Level.ALL);
	      logger.addHandler(handler);
	      logger.setUseParentHandlers(false);
	      Handler consoleHandler = new ConsoleHandler();
	      consoleHandler.setFormatter(handler.getFormatter());
	      consoleHandler.setLevel(Level.ALL);
	      logger.addHandler(consoleHandler);
	      
	      logger.setLevel(Level.ALL);
	      info(new Date().toString());
	    }
	    catch (SecurityException e)
	    {
	      e.printStackTrace();
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	  }
	  
	  public static void info(Object msg)
	  {
	    logger.log(Level.FINE, msg.toString());
	    
	    handler.flush();
	  }
	  
	  public static void warn(Object msg)
	  {
	    logger.log(Level.WARNING, msg.toString());
	    handler.flush();
	  }
	  
	  public static void error(Object msg)
	  {
	    logger.log(Level.SEVERE, msg.toString());
	    handler.flush();
	  }
	  
	  public static void error(Object msg, Exception e)
	  {
	    logger.log(Level.SEVERE, msg.toString());
	    logger.log(Level.SEVERE, e.getMessage(), e);
	    handler.flush();
	  }
	  
	  public static void except(Exception e)
	  {
	    logger.log(Level.SEVERE, e.getMessage(), e);
	    handler.flush();
	  }
	  
	  private static void decompressGzipFile(String gzipFile, String newFile)
	  {
	    try
	    {
	      FileInputStream fis = new FileInputStream(gzipFile);
	      GZIPInputStream gis = new GZIPInputStream(fis);
	      FileOutputStream fos = new FileOutputStream(newFile);
	      byte[] buffer = new byte['?'];
	      int len;
	      while ((len = gis.read(buffer)) != -1) {
	        fos.write(buffer, 0, len);
	      }
	      fos.close();
	      gis.close();
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	  }
	  
	  private static void compressGzipFile(String file, String gzipFile)
	  {
	    try
	    {
	      FileInputStream fis = new FileInputStream(file);
	      FileOutputStream fos = new FileOutputStream(gzipFile);
	      GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
	      byte[] buffer = new byte['?'];
	      int len;
	      while ((len = fis.read(buffer)) != -1) {
	        gzipOS.write(buffer, 0, len);
	      }
	      gzipOS.close();
	      fos.close();
	      fis.close();
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	  }
}
