package org.iitk.brihaspati.modules.utils;

/*@(#)MailNotification.java
 *  Copyright (c) 2005-2006,2009,2010, 2013 ETRG,IIT Kanpur. 
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
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.Properties;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.apache.turbine.om.security.User;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.torque.util.Criteria;


import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.Email;
import org.apache.commons.lang.StringUtils;
import org.apache.turbine.Turbine;
import java.io.FileOutputStream;
import org.iitk.brihaspati.modules.utils.AdminProperties;
import org.apache.turbine.services.servlet.TurbineServlet;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Message;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.activation.FileDataSource;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;	

/**
 * This class is used to send the mail for the concerned activity
 * @author <a href=satyapalsingh@gmail.com>Satyapal Singh</a>
 * @author <a href="mailto:shaistashekh@hotmail.com">Shaista Bano</a>
 * @modified date: 31-08-2005, 20-03-2009, 29-12-2009, 17-02-2010, 08-07-2010;
 * @author <a href="mailto:shikha@gmail.com">Shikha Shukla</a>
 * @author <a href="mailto:rpriyanka12@ymail.com">Priyanka Rawat</a>
 * @modified date: 22-11-2010;
 * @modified date: 14-07-2011 (Shaista);
 * @modified date: 09-08-2012, 25-09-2012, 02-11-2012 (Priyanka)
 * @modified date: 27-12-201 (Shaista);
 * @author <a href="mailto:seemanti05@gmail.com">Seemanti Shukla</a>
 * @modified date: 18-05-2015 (Seemanti) ---Path updated from BrihaspatiServer.properties to Admin.properties---
 * @modified date: 15-07-2015 (Seemanti) ---Addition of 2 methods namely dummySendMail() and mailing()---Modification of existing method sendMail()---
 */

public class MailNotification{
	
	private static StringBuffer message;	

	/**
	 * @param file String, the file named  brihaspati.properties
	 * @return Properties
	**/
	public static  Properties uploadingPropertiesFile(String file)	{

       	        Properties p = new Properties();
		try{
			FileInputStream f = null;
                	f = new FileInputStream(file);
	                //String msg1=MultilingualUtil.ConvertedString("mailNotification_msg1",LangFile);
        	        p.load(f);
			f.close();
		}
		catch (Exception e){ErrorDumpUtil.ErrorLog("The error in uploadingPropertiesFile method in MailNotification" );}
	return p;
		
	}

	/**
	* @param subject String, part of a key define in brihaspati.properties file, getting value
	* @param course_id String Course ID 
	* @param pr Properties, getting the value according to the given keys like. "brihaspati.Mailnotification."+info_new+".subject" 
	*			 from loaded propertis file
	* @return String
	**/

	public static String subjectFormate(String info_new, String course_id, Properties pr ){

		message = new StringBuffer(pr.getProperty("brihaspati.Mailnotification."+info_new+".subject"));
		//ErrorDumpUtil.ErrorLog(" originalSubject in MailNotification==========="+message);
                return replaceString("course_id",course_id);
	}

	/**
	 * This method extracts the message string from brihaspati.properties and replaces
	 * the tokens in the same
	 * 
	 * @param info String Portion of a property define in brihaspati.properties file like(deleteUser, deleteUserhttps, onlineStudentRegRequest 
	 * onlineStudentRegRequesthttps etc. It is vary according to requirement.
	 * @param course_id String Course ID
	 * @param dept_name String Name of department
	 * @param uName String User Name 
	 * @param uPassword String Password of the user
	 * @param server_name String Address of the server
	 * @param server_port String Port Number of the server
	 * @param pr Properties  File in which the properties file is opened
	 *
	 * @return String
	 */
/*
	public static String getMessage(String info, String course_id, String dept_name, String uName, String uPassword, String server_name, String server_port, Properties pr) throws Exception{
		
		message = new StringBuffer(pr.getProperty("brihaspati.Mailnotification."+info+".message"));
		replaceString("course_id",course_id);
        	replaceString("dept_name",dept_name);
             	replaceString("user_name",uName);
		replaceString("user_pass",uPassword);
		replaceString("server_name",server_name);
               	return replaceString("server_port",server_port);
	}
*/
	/**
 	 * Replaces constants for the purpose of sending mail to user.
 	 * @param info String Portion of a property define in brihaspati.properties file like(deleteUser, deleteUserhttps, onlineStudentRegRequest 
 	 * onlineStudentRegRequesthttps etc. It is vary according to requirement.
 	 * @param course_id String Course ID
 	 * @param dept_name String Name of department
 	 * @param uName String User Name
 	 * @param uPassword String Password of the user
 	 * @param pr Properties  File in which the properties file is opened
 	 * @return String
 	 */ 
	public static String getMessage(String info, String course_id, String dept_name, String uName, String uPassword, Properties pr) throws Exception{
		
		message = new StringBuffer(pr.getProperty("brihaspati.Mailnotification."+info+".message"));
		replaceString("course_id",course_id);
        	replaceString("dept_name",dept_name);
             	replaceString("user_name",uName);
		return replaceString("user_pass",uPassword);
		
	}
	/**
 	 * Replaces constants for the purpose of sending mail to user.
 	 * @param info String Portion of a property define in brihaspati.properties file like(deleteUser, deleteUserhttps, onlineStudentRegRequest 
 	 * onlineStudentRegRequesthttps etc. It is vary according to requirement.
 	 * @param email email id of user
 	 * @param pr Properties  File in which the properties file is opened
 	 * @return String
 	 */ 
	public static String getMessage(String info, String email, Properties pr) throws Exception
	{
                message = new StringBuffer(pr.getProperty("brihaspati.Mailnotification."+info+".message"));
                return replaceString("new_email",email);
        }

	/**
         * Replaces constants for the purpose of  
         * sending verification mail to user.
         * @param info Main message
         * @param uName User name
         * @param aKey Verification key
         * @param mode String
         * @param lang Language selected
         * @return String
         */
	public static String getMessage(String info,String uName, String aKey, String mode, String lang) throws Exception{
	
		info=info.replaceAll("e_mail",uName);
		info=info.replaceAll("activation_key",aKey);
		info=info.replaceAll("u_mode",mode);
		info=info.replaceAll("u_lang",lang);
		return info;
	}
	
	/**
 	 * Replaces constants with institute name
 	 * and message, for the purpose of source 
 	 * mail verification before sending mail
 	 * to institute admin.
 	 */
	public static String getMessage(String messageFormate, String instName, String message) throws Exception
        {
                messageFormate=messageFormate.replaceAll("institute_name",instName);
                messageFormate=messageFormate.replaceAll("admin_message",message);
                return messageFormate;

        }

        /**
 	  * Replace the server parameter (scheme, name, port) in message .
 	  * @param info This string is actual message which will be send in the email.
          * @param serverScheme Scheme of the server.
          * @param serverName Name of the server.
          * @param serverPort Used Port of the server.
          * @return String
          */

	 public static String replaceServerPort(String info) throws Exception {
		String serverName = "";
                String serverScheme = TurbineServlet.getServerScheme();
		String serverPort = TurbineServlet.getServerPort();
		//String filePath = TurbineServlet.getRealPath("../../conf/BrihaspatiServer.properties");
                String filePath = TurbineServlet.getRealPath("/WEB-INF")+"/conf"+"/"+"Admin.properties";
		serverName = AdminProperties.getValue(filePath,"brihaspati.admin.brihaspatiServerIP.value");
	
		if(StringUtils.isBlank(serverName))
			serverName= TurbineServlet.getServerName();
	
               if(serverScheme.length() >0)
                       info=info.replaceAll("server_scheme", serverScheme);
               if(serverName.length() >0)
                       info=info.replaceAll("server_name", serverName);
               if(serverPort.length() >0)
                       info=info.replaceAll("server_port", serverPort);
               return info;
	}
	 
        /**
 	  * Replace the constant in message .
 	  * @param info This string is actual message which will be send in the email.
          * @param FName First name of the user.
          * @param LName Last name of the user.
          * @param i_name Institute name.
          * @param uName User name of user.
          * @return String
          */
	public static String getMessage_new(String info,String FName,String LName,String i_name,String uName) throws Exception {
	       if(FName.length()>0){ 
                        info=info.replaceAll("first_name",FName);
		}
                else
		 info=info.replaceAll("first_name",uName);
		if(LName.length()>0) {
                        info=info.replaceAll("last_name",LName);
		}
                 else
                 info=info.replaceAll("last_name","");
		if(i_name.length()>0) {
                        info=info.replaceAll("institute_admin",i_name);
		}
		else
		{
			info=info.replaceAll("institute_admin","");
	          }
		 return info;
        }

       /** This method replaces the a string with the replacement string
	 * @param searchString String The substring to be searched in the string
	 * @param replacement String The string with which the substring has to be replaced
	 * @return String
	 */
        public static String replaceString(String searchString, String replacement){
                try {
			if(replacement.equals("")){
                        	return(message.toString());
                        }
			else{
                        	if(replacement.length()>0){
					String str=message.toString();
                                	int startIndex=str.indexOf(searchString);
	                                int endIndex=searchString.length();
                	                if(startIndex>0) {
                        	        	message.replace(startIndex,startIndex+endIndex,replacement);
                                	}
        	                        return(message.toString());
                	        }
                	}
                }catch(Exception e){ErrorDumpUtil.ErrorLog(" Error in replaceString method in mail notification.java "+e.getMessage());}
                        return (message.toString());
        }

	/**
	 * This method is used to send the mails with suitable messages
	 * @param mail_id String E-Mail of the user
	 * @param subject String subject of Email 
	 * @param attachedFile String having attachement file name with path w.r.t. root directory of the system to be sent with mail
         * @param LangFile String Language properties file
	 * @return String
	 */

      public static String sendMail(String message , String mail_id , String subject , String attachedFile, String LangFile, String fileName)
      {           
	 String msg = "";
	 try //try 1
         {
            /**
	     * Now all these properties are kept in Admin.properties with different property name created by preapending brihaspati.
	     * in the beginnig. The code below retrieves all these values.
	     */
	    String path=TurbineServlet.getRealPath("/WEB-INF")+"/conf"+"/"+"Admin.properties";
            String mail_smtp=AdminProperties.getValue(path,"brihaspati.mail.smtp.from");
            String host_name=AdminProperties.getValue(path,"brihaspati.mail.server");
            String mail_smtp_port=AdminProperties.getValue(path,"brihaspati.mail.smtp.port");
            /** These username and password are the username used by system to authenticate itself with the smtp server for sending email.
	     */
            String mail_uname=AdminProperties.getValue(path,"brihaspati.mail.username");
            String mail_pass=AdminProperties.getValue(path,"brihaspati.mail.password");
            String local_domain=AdminProperties.getValue(path,"brihaspati.mail.local.domain.name");
	    // The properties retrievals end here.

	    //Invoking mailing() method here.
            //Below msg string gets updated after executing mailing() method.
            msg=mailing(message,mail_id,subject,attachedFile,LangFile,fileName,mail_smtp,host_name,mail_smtp_port,mail_uname,mail_pass,local_domain);
         }
         catch(Exception ex)
         {
            msg=msg+"The error in mail send !!!"+ex;
         }
         return(msg);

      }//End of sendMail()

      //This method can be invoked for sending Dummy mail for checking mail sending functionality.	
      public static boolean dummySendMail(String Message , String u_id , String sub , String attchd_File , String langFile , String filename , RunData data)
      {  
         String msg = "";//This string will hold the status whether mail sent or not by using mailing() method defined below.
         boolean check = false;//This boolean var thus returns true or false.
         try 
         {    
            //Create an object of class ValueObject to retrieve the Runtime Admin Profile parameters.
            ValueObject value = new ValueObject(data);
            String s1= value.getMailAuth().getMailFrom();
            String s2= value.getMailAuth().getMailServ();
            String s3= value.getMailAuth().getMailServPort();
            String s4= value.getMailAuth().getMuName();
            String s5= value.getMailAuth().getMPass();
            String s6= value.getMailAuth().getDomainNM();
            msg=mailing(Message,u_id,sub,attchd_File,langFile,filename,s1,s2,s3,s4,s5,s6);//msg string gets updated by invoking mailing() method.	
            if(msg.equals("Mail Sent Successfully !!"))//if mail sent succesfully,return true. 
	       check =  true;
         } 
         catch(Exception e)
         { 
            msg=msg+"Error in sending dummy mail."+e;
	    ErrorDumpUtil.ErrorLog("\nError in sending dummy mail"+msg, TurbineServlet.getRealPath("/logs/Email.txt"));
	    check = false;//else return false.
	 }
         return check;
      }//End of dummySendMail()

      
      public static String mailing(String message , String mail_id, String subject , String attachedFile , String LangFile, String fileName , String mail_smtp , String host_name , String mail_smtp_port , String mail_uname , String mail_pass , String local_domain) 
      {	 
         boolean flag = false;
         String msg = "";
         try
         {  
            
            String email_new="";
            if(StringUtils.isNotBlank(mail_id))
            {
               email_new = mail_id;
            }

            //if((!mail_smtp.equals("")) && (!mail_smtp.equals(null))){
            if(StringUtils.isNotBlank(mail_smtp))//1st if starts.
            {
               //if((!email_new.equals("")) && (!email_new.equals(null))){
               if(StringUtils.isNotBlank(email_new))//2nd if starts.
               {
                  Properties l_props = System.getProperties();
                  l_props.put("mail.smtp.host", host_name);
                  if(!mail_pass.equals(""))
                  {
                     l_props.put("mail.smtp.auth", "true");
                  }
                  if(!mail_smtp_port.equals("25"))
                  {
                     l_props.put("mail.smtp.starttls.enable", "true");
                     l_props.put("mail.smtp.port",mail_smtp_port);
                     // Use the following if you need SSL
                     l_props.put("mail.smtp.socketFactory.port", mail_smtp_port);
                     l_props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                     l_props.put("mail.smtp.socketFactory.fallback", "false");
                  }
                  Session l_session = Session.getDefaultInstance(l_props,  null);
                  l_session.setDebug(false);
	          try 
                  {
	             MimeMessage l_msg = new MimeMessage(l_session); // Create a New message
		     l_msg.setFrom(new InternetAddress(mail_smtp)); // Set the From address
				
		     // Setting the "To recipients" addresses
		     l_msg.setRecipients(Message.RecipientType.TO,
	             InternetAddress.parse(email_new, false));
	             l_msg.setSubject(subject); // Sets the Subject

		     //Create and fill the first message part
		     MimeBodyPart l_mbp = new MimeBodyPart();
                     Multipart l_mp = new MimeMultipart();
		     l_mbp.setContent(message, "text/html");
                     l_mp.addBodyPart(l_mbp);
		     // Create the Multipart and add bodyparts to it
		     if( !attachedFile.equals("tmp")&&(attachedFile.length() > 0 )) 
                     {
		        String tmpStr [] = attachedFile.split(",");
		        if(tmpStr.length >0)
                        {
		           for( int k=0; k < tmpStr.length; k++) 
                           {
		              MimeBodyPart attachment_Bp = new MimeBodyPart();
                              //DataSource source = new FileDataSource(attachedFile);
                              DataSource source = new FileDataSource(tmpStr[k]);
                              attachment_Bp.setDataHandler(new DataHandler(source));
        		      attachment_Bp.setFileName(attachedFile);
		              l_mp.addBodyPart(attachment_Bp);
			      //attachment_Bp = null;
			      //source = null;
			   }
		        }
                     }
		     l_msg.setContent(l_mp);
		     // Set the Date: header
		     java.util.Date date=new java.util.Date();
		     l_msg.setSentDate(date);
	             // Send the message
		     // Transport.send(l_msg);
		     Transport tr = l_session.getTransport("smtp");
		     //Send the message
		     if(!mail_pass.equals(""))
                     {
                        if(!mail_smtp_port.equals("25"))
                        {
                           int smtpPortInt = Integer.parseInt(mail_smtp_port);
                           tr.connect(host_name, smtpPortInt, mail_uname, mail_pass);
                        } 
                        else
                        {
                           tr.connect(host_name, mail_uname, mail_pass);
                        }
                        // Send the message
                     }
                     else
                     {
		        tr.connect();
                     }
                     l_msg.saveChanges();     // don't forget this
                     ErrorDumpUtil.ErrorLog("\nEnd MailNotification Class mail_id======"+mail_id+"\t  message========"+ message, TurbineServlet.getRealPath("/logs/Email.txt"));
                     tr.sendMessage(l_msg, l_msg.getAllRecipients());
                     tr.close();
		     /**
		       if( (attachedFile.length() > 0 )) 
	                  deletingAttachedFile(attachedFile);
		      */
                  } 
                  catch (MessagingException mex)  // Trap the MessagingException Error
                  {
                     // If here, then error in sending Mail. Display Error message.
                     msg=msg+"The error in sending Mail Message "+mex.toString();
		     ErrorDumpUtil.ErrorLog("\nThe error in send mail (MailNotification -379) "+msg, TurbineServlet.getRealPath("/logs/Email.txt"));
		     flag = true;
                  }
                  //msg="Mail send succesfully!!";
	          if(!flag)
	             msg= MultilingualUtil.ConvertedString("mail_msg2",LangFile);
               }//End of 2nd if condition.
               else
               {
                  //msg="Mail can't send since your mail id is null!!";
                  msg=msg+MultilingualUtil.ConvertedString("mailNotification_msg2",LangFile);
               }
            }//End of 1st if condition.
            else
            {
               msg=msg+MultilingualUtil.ConvertedString("mailNotification_msg",LangFile);
            }
         }
         catch(Exception ex)
         {
            msg=msg+"The error in mail send !!!"+ex;
            ErrorDumpUtil.ErrorLog("\nThe error in send mail (MailNotification) "+msg, TurbineServlet.getRealPath("/logs/Email.txt"));
	    //mailSendfailure	
	    flag = true;
         }
                              
         // msg is a message comes from BrihLang_en.properteis 
	 // message param is real message which is composed by a user.
	 /** getting path for creating EmailSpooling  directory*/
	
         String filePath = TurbineServlet.getRealPath("/EmailSpooling");
	 File f = new File(filePath);
	 if(flag)
         {
	    LangFile.trim();
            /** This is executed while Langfile is nt coming properly to read message from conf */

	    if(StringUtils.isBlank(LangFile) || LangFile.equals("english") )  
	       LangFile = TurbineServlet.getRealPath("/conf")+"BrihLang_en.properties/";
	    WriteXmlThread.getController().set_Message(filePath, mail_id, subject, message, attachedFile, LangFile, fileName);
	 }
	 if(!flag)
         {
	    f = new File(filePath+"/"+fileName);
	    if(f.exists())
	       f.delete();
	    if(attachedFile.length()>0)
	       if(!attachedFile.equals("tmp"))
	          deletingAttachedFile(attachedFile);
	 }
         return(msg);
      } //mailing() method close.



	/**
	 * This method is deleting attached file coming from Action & is stored in brihaspati's tmp folder to send in Mail. 
	 * @param attachedFile String the name of attached file with path 
	**/
	public static void deletingAttachedFile(String attachedFile){
		
		File f1 = new File(attachedFile);
		if(f1.exists())
       	        {
               		f1.delete();
                }
	}

	public static String getQuizMessage(String info, String course_id, String quiz_date,String quiz_time,String quizname,String securitystr,String uName,Properties pr) throws Exception{
                message = new StringBuffer(pr.getProperty("brihaspati.Mailnotification."+info+".message"));
                replaceString("course_id",course_id);
                replaceString("quiz_date",quiz_date);
                replaceString("quiz_time",quiz_time);
                replaceString("quiz_name",quizname);
                replaceString("security_string",securitystr);
                return replaceString("user_name",uName);
        }
	
	public static String getAssignmentMessage(String info, String course_name, String topic_name) throws Exception{
                if(StringUtils.isNotBlank(info)){
                        if(StringUtils.isNotBlank(topic_name))
                                info = info.replaceAll("topic_name",'"'+topic_name+'"');
                        if(StringUtils.isNotBlank(course_name))
                                info = info.replaceAll("course_name", '"'+course_name+'"');
                }
                return info;
        }
}

