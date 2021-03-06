package org.iitk.brihaspati.modules.utils;
/*
 * @(#)EmailSpoolingThread.java
 *
 *  Copyright (c) 2010-2011, 2013 ETRG,IIT Kanpur.
 *  All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or
 *  without modification, are permitted provided that the following
 *  conditions are met:
 *
 *  Redistributions of source code must retain the above copyright
 *  notice, this  list of conditions and the following disclaimer.
 *
 *  Redistribution in binary form must reproducuce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL ETRG OR ITS CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL,SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 *  OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 *  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 *  Contributors: Members of ETRG, I.I.T. Kanpur
 *
 */

import java.io.File;
import java.util.LinkedList;
import java.util.Vector;

import org.iitk.brihaspati.modules.utils.ErrorDumpUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.services.servlet.TurbineServlet;

/**
 * @author <a href="mailto:nksinghiitk@gmail.com">Nagendra Kumar Singh</a>
 * @author <a href="mailto:shaistashekh@hotmail.com">Shaista Bano</a>
 * @modify date: 27-12-2012 (Shaista)
 */

public class EmailSpoolingThread implements Runnable {

	private static String attachFile="", temp = "";
	private static String mailSpoolFilePath="";
	private static Vector v=null;
	private static LinkedList mailnotification1 = new LinkedList();
	private boolean flag=false;
	private static Thread runner=null;
	private static EmailSpoolingThread mailSpoolingThread=null;
	private static MultilingualUtil mu=new MultilingualUtil();

	/**
	 * Controller for this class to use as a singleton.
	 */
        public static EmailSpoolingThread getController(){
                if(mailSpoolingThread==null)
                        mailSpoolingThread = new EmailSpoolingThread();
                return mailSpoolingThread;
        }	
	
	/**
 	 * Add message to a vector which store in linkedlist.
	 */
	public String set_Message(String mailId, String sub, String msg, String attachedFile, String langFile, String fileName)
	{
		String strng="";
		//ErrorDumpUtil.ErrorLog("Lang File in MailNotificationThread Class ="+LangFile);
		v=new Vector();
		v.add(mailId); //0   
                v.add(sub);    //1
                v.add(msg);  //2 
                v.add(attachedFile);//3
                v.add(langFile);//4
                v.add(fileName);//5
		//ErrorDumpUtil.ErrorLog("\n\nmailSpoolingThread.v="+v);
		mailnotification1.add(0,v);
		start();
			strng= mu.ConvertedString("mail_msg", langFile);
			//"Message is in queue";
	//....................
		return strng;
	}

	
	/**
        * Start EmailSpoolingThread Thread.
        */ 
        private void start(){
                if (runner == null) {
			flag=true;	
                        runner = new Thread(this);
                        runner.start();
                }
        }

        /**
         * Stop MailNotificationThread Thread.
         */
        private void stop() {
                if (runner != null) {
			flag=false;
                        runner.interrupt();
                        runner = null;
                }
        }
	
	/**
	 * MailNotification thread for send mails.
	 */
      	public synchronized void run() {

		while(flag) {
			try{ 	Thread.sleep(200); }catch(Exception e){ErrorDumpUtil.ErrorLog("\nI am  in EmailSpoolingThread  Class  sleep section "+e, TurbineServlet.getRealPath("/logs/Email.txt"));}
			try { 
				File f = new File(mailSpoolingThread.mailSpoolFilePath);
				while(mailnotification1.size() > 0) {
                                        Vector mail_data=(Vector)mailnotification1.pop();
                                        String mailId = mail_data.get(0).toString().trim();
                                        String sub = mail_data.get(1).toString();
                                        String msg = mail_data.get(2).toString().trim();
                                        String attachedFile = mail_data.get(3).toString().trim();
                                        String LangFile = mail_data.get(4).toString();
                                        String fileName = mail_data.get(5).toString();
					//ErrorDumpUtil.ErrorLog("\n\n EmailSpoolingThread===============mailId"+mailId);
					MailNotification.sendMail(msg, mailId, sub, attachedFile, LangFile, fileName);	
				} //main while close
				
			}catch(Exception es){ErrorDumpUtil.ErrorLog("\nI am  in EmailSpoolingThread  Class  sleep section "+es, TurbineServlet.getRealPath("/logs/Email.txt"));}
			stop();
		}	
    	}
}// End of file.
