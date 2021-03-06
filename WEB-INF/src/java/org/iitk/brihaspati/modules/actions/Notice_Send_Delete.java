package org.iitk.brihaspati.modules.actions;

/*
 * @(#)Notice_Send_Delete.java	
 *
 *  Copyright (c) 2005,2008,2010,2013 ETRG,IIT Kanpur. 
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
 */


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileOutputStream;

import java.util.List;
import java.util.Vector;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Properties;

import java.sql.Date;

import org.apache.turbine.util.RunData;
import org.apache.torque.util.Criteria;
import org.apache.turbine.om.security.User;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.parser.ParameterParser;

import com.workingdogs.village.Record;
import org.iitk.brihaspati.om.Quiz;
import org.iitk.brihaspati.om.QuizPeer;
import org.iitk.brihaspati.om.NewsPeer;
import org.iitk.brihaspati.om.NoticeSend;
import org.iitk.brihaspati.om.NoticeReceive;
import org.iitk.brihaspati.om.NoticeSendPeer;
import org.iitk.brihaspati.om.NoticeReceivePeer;
import org.iitk.brihaspati.om.Courses;
import org.iitk.brihaspati.om.CoursesPeer;
import org.iitk.brihaspati.modules.utils.UserUtil;
import org.iitk.brihaspati.modules.utils.GroupUtil;
import org.iitk.brihaspati.modules.utils.CourseUtil;
import org.iitk.brihaspati.modules.utils.ExpiryUtil;
import org.iitk.brihaspati.modules.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.iitk.brihaspati.modules.utils.CommonUtility;
import org.iitk.brihaspati.modules.utils.MultilingualUtil;
import org.iitk.brihaspati.modules.utils.UserGroupRoleUtil;
import org.iitk.brihaspati.modules.utils.ErrorDumpUtil;
import org.iitk.brihaspati.modules.utils.TopicMetaDataXmlReader;
import org.iitk.brihaspati.modules.utils.FileEntry;
import org.iitk.brihaspati.modules.utils.AdminProperties;
import org.iitk.brihaspati.modules.utils.MailNotification;
import org.iitk.brihaspati.modules.utils.MailNotificationThread;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.services.security.torque.om.TurbineUser;
import org.apache.turbine.services.security.torque.om.TurbineUserPeer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iitk.brihaspati.modules.utils.AutoSave;

import java.util.Calendar;
//import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/**
 * In this class,we send notice and delete self copy or all(Only Sender) notices
 * @author <a href="mailto:madhavi_mungole@hotmail.com">Madhavi Mungole</a>
 * @author <a href="mailto:awadhesh_trivedi@yahoo.co.in">Awadhesh Kumar Trivedi</a>
 * @author <a href="mailto:nksngh_p@yahoo.co.in">Nagendra Kumar Singh</a>
 * @author <a href="mailto:sunil.singh6094@gmail.com">Sunil Kumar</a>
 * @author <a href="mailto:shaistashekh@hotmail.com">Shaista</a>
 * @author <a href="mailto:sunil0711@gmail.com">Sunil Yadav</a>
 * @author <a href="mailto:tejdgurung20@gmail.com">Tej Bahadur</a>
 * @author <a href="mailto:vipulk@iitk.ac.in">vipul kumar pal</a>
 * @modified date: 28-01-2010
 * @modified date: 08-07-2010, 13-Oct-2010, 21-04-2011, 16-06-2011 (Shaista)
 * @modified date: 24-08-2012 (Sunil Yadav),23-02-2013
 * @author <a href="mailto:seemanti05@gmail.com">Seemanti Shukla</a>
 * @modified date: 18-05-2015 (Seemanti);
 */
public class Notice_Send_Delete extends SecureAction
{
	/**
	* In this method,You Send notice to all or instructor or student in particuler group when
	* you are registered
	* @param data Rundata
	* @param context Context
	*/
	private String notice_message="";
	private String msg1=null;
	private Log log = LogFactory.getLog(this.getClass());
	public void doSend(RunData data, Context context)
	{
		try{
			User user=data.getUser();
			String LangFile=(String)user.getTemp("LangFile");
			ParameterParser pp=data.getParameters();	
			String flagFrom=pp.getString("flag","");	
			String UserName=user.getName();
			int userid=UserUtil.getUID(UserName);	
			String uid=Integer.toString(userid);
			int group_id=0;
			context.put("flag",flagFrom);
			Criteria crit=new Criteria();
			String courses[]=pp.getStrings("course_list");
			int rec_no=0, notice_expiry=0;
			if(courses == null)
				rec_no=0;
			else
	 			rec_no=courses.length;
			String mode=pp.getString("mode1","notice");
			/**
		 	* Retrieve the notice details from the screen with the help of 
		 	* ParameterParser
		 	*/
			context.put("count",pp.getString("count",""));
			context.put("countTemp",pp.getString("countTemp",""));
			//ErrorDumpUtil.ErrorLog("Count temp="+pp.getString("countTemp",""));
			String notice_role=pp.getString("role");
			notice_message=pp.getString("message");	

			String notice_subject=pp.getString("subject");
			/**
			 *   Replace special character and scripts
			 */
			notice_subject =StringUtil.replaceXmlSpecialCharacters(notice_subject);

			String notice_sub_modify=notice_subject.replaceAll("'","\'");
			boolean flag=false;

			//this group id is used only for quiz


         		if(mode.equals("notice"))
	                        notice_expiry=pp.getInt("expiry");
                        else if(mode.equals("quiz"))
        	                notice_expiry=30;
			/**
			* Get current date and expiry date with yyyy-mm-dd format
			* @see ExpiryUtil from utils
			*/	
			String c_date="";
			if(mode.equals("notice") || (mode.equals("grpmgmt")))
                                c_date=ExpiryUtil.getCurrentDate("-");
                        else if(mode.equals("quiz"))
                                c_date=pp.getString("quezdate");

			Date current_date=Date.valueOf(c_date);
			String E_date=ExpiryUtil.getExpired(c_date,notice_expiry);
			Date Expiry_date=Date.valueOf(E_date);
			// Maintain Log file
			java.util.Date date = new java.util.Date();
			String Role=(String)user.getTemp("role");
			String role;
			if(Role.equals("")) role = "Admin";else role = Role;
			log.info("User Name --> "+UserName +" | Role --> "+role +"| Operation --> Send Notice | course type -->"+courses[0] +" | Send to --> "+notice_role +"| Time of operation --> "+date+ "| IP Address --> "+data.getRemoteAddr());

			for(int num=0;num<rec_no;num++){
		
				/**
			 	* Retrieves the group_id for the corresponding group name from 
			 	* TURBINE_GROUP
			 	*/
				String course_id="";
				if(userid==1){
					course_id=courses[num];
				}else{
					//course_id=CourseUtil.getCourseId(courses[num]);
					course_id = courses[num];
				}

				//this is used for notices and group management
                                        group_id=GroupUtil.getGID(course_id);

				//Check for repeat quiz announcement

				if(mode.equals("quiz")){
                                        crit=new Criteria();
					crit.add(NoticeSendPeer.GROUP_ID,group_id);
                                        crit.add(NoticeSendPeer.USER_ID,uid);
                                        crit.add(NoticeSendPeer.NOTICE_SUBJECT,notice_sub_modify);
                                        List lst1=NoticeSendPeer.doSelect(crit);
                                        if(lst1.size() !=0)
                                       flag=true;
                         	}
	                         if(flag==true) {
                                     String mssg=MultilingualUtil.ConvertedString("quiz_msg",LangFile);
                                     data.setMessage(mssg);
        	                 return;
                	        }

				/**
				 * Checks the group of users to whom this notice has to be delivered
			 	*/

				int role_id=0;
				if(notice_role.equals("instructor"))
					role_id=2;
				if(notice_role.equals("student"))
					role_id=3;
				if(notice_role.equals("teacher_assistant"))
                                        role_id=8;
				if(notice_role.equals("all"))
					role_id=7;

				/**	
			 	* Inserts the notice details in the table MESSAGE_SEND
			 	*/

				try{
					crit=new Criteria();
					crit.add(NoticeSendPeer.NOTICE_SUBJECT,notice_sub_modify);
					crit.add(NoticeSendPeer.USER_ID,uid);
					crit.add(NoticeSendPeer.GROUP_ID,group_id);
					crit.add(NoticeSendPeer.ROLE_ID,role_id);
					crit.add(NoticeSendPeer.POST_TIME,current_date);
					crit.add(NoticeSendPeer.EXPIRY,notice_expiry);
					crit.add(NoticeSendPeer.EXPIRY_DATE,Expiry_date);
					NoticeSendPeer.doInsert(crit);

					if(mode.equals("quiz")){
						crit=new Criteria();
                                        	crit.add(NewsPeer.GROUP_ID,group_id);
	                                        crit.add(NewsPeer.USER_ID,uid);
        	                                crit.add(NewsPeer.NEWS_TITLE,notice_sub_modify);
                	                        crit.add(NewsPeer.NEWS_DESCRIPTION,notice_message);
                        	                crit.add(NewsPeer.PUBLISH_DATE,current_date);
                                	        crit.add(NewsPeer.EXPIRY,notice_expiry);
                                        	crit.add(NewsPeer.EXPIRY_DATE,Expiry_date);
	                                        NewsPeer.doInsert(crit);
					}


				}
				catch(Exception e)
				{
					data.setMessage("The exception in Send Notice... :-"+e);
				}
				/**
			 	* Gets the message id of the notice entered into the table
			 	*/

				int msg_id=0;
	
				String Query_msgid="SELECT MAX(NOTICE_ID) FROM NOTICE_SEND";
				List u=NoticeSendPeer.executeQuery(Query_msgid);

				for(ListIterator j=u.listIterator();j.hasNext();){
					Record item=(Record)j.next();
					msg_id=item.getValue("MAX(NOTICE_ID)").asInt();
				}
				/**
                         	* From here starts the code do store message
                         	* in a single txt file.
                         	*/
                         	String path = data.getServletContext().getRealPath("/Courses")+"/"+course_id+"/";
                         	File first = new File(path);
                         	first.mkdirs();
                         	path = path+"/"+"Notice_Msg.txt";
                         	FileWriter fw = new FileWriter(path,true);
                         	fw.write("\r\n");
                         	fw.write("<" + msg_id + ">");
                         	fw.write("\r\n");
                         	fw.write(notice_message);
                         	fw.write("\r\n"+"</" + msg_id + ">");
                         	fw.close();
				/**
			 	* Inserts notice details in NOTICE_RECEIVE table after checking 
			 	* the users to whom the notice has to be sent
			 	*/
				try{
					if(mode.equals("grpmgmt"))
                                        {
                                                String grpname=pp.getString("val","");
						String gpath=TurbineServlet.getRealPath("/Courses"+"/"+course_id)+"/GroupManagement";
                                                TopicMetaDataXmlReader topicmetadata=new TopicMetaDataXmlReader(gpath+"/"+grpname+"__des.xml");
                                                Vector grouplist=topicmetadata.getGroupDetails();
                                               // int usrid=0;
                                                if(grouplist!=null)
                                                {
                                                        String username[]=new String[1000];
                                                        int l=0;
                                                        for(int m=0;m<grouplist.size();m++)
                                                        {//for
                                                                String uname =((FileEntry) grouplist.elementAt(m)).getUserName();
                                                                username[m]=uname;
                                                                l++;
                                                        }
                                                        username[l]=UserName;
                                                        for(int k=0;k<=l;k++)
                                                        {
                                                                String str=username[k];
                                                                userid=UserUtil.getUID(str);
								insertReceiveNotice( msg_id, userid, group_id, data, notice_sub_modify);
                                                        }
                                                }
                                        }
                                        else
                                        {

					Vector userList=new Vector();
					if(role_id==2 || role_id==3 || role_id==8)
					{
						userList=UserGroupRoleUtil.getUID(group_id,role_id);
						int rows=userList.size();
						if(rows!=0){
							for(int count=0;count<rows;count++){
								String user_id=(String)userList.elementAt(count);
								userid=Integer.parseInt(user_id);
								insertReceiveNotice( msg_id, userid, group_id, data, notice_sub_modify);
							}
						}
					}
					if(role_id==7)
					{
						userList=UserGroupRoleUtil.getUID(group_id);
						int list=userList.size();
						if(list!=0)
						{
							for(int k=0;k<list;k++)
							{
								userid=Integer.parseInt((String)userList.get(k));
								insertReceiveNotice( msg_id, userid, group_id, data, notice_sub_modify);
							}
						}
					}
					}//else
				}
				catch(Exception cx)
				{
					data.setMessage("The error in Receive "+cx);
				}
			}//for
			//String LangFile=(String)user.getTemp("LangFile");

			String msg1="";
                        if(mode.equals("quiz"))
	                        msg1=MultilingualUtil.ConvertedString("quiz_msg2",LangFile);
                        //"Quiz schedule of this course send through Notices";
                        else
				msg1=MultilingualUtil.ConvertedString("notice_msg1",LangFile);

			if(mode.equals("grpmgmt"))
                                data.setScreenTemplate("call,Group_Mgmt,Activitygroup.vm");
                        else if(mode.equals("quiz"))
				data.setScreenTemplate("call,Quiz_Mgmt,Quiz_Detail.vm");
			else
                                data.setScreenTemplate("call,Notice_User,Notices.vm");

                        data.setMessage(msg1);
			AutoSave.doDelete((String)user.getTemp("course_id")+(String)user.getTemp("Institute_id")+(String)user.getTemp("role")+user.getName()+pp.getString("page",""));
		}
		catch(Exception ex)
		{
			data.setMessage("The Error in Send Notice .."+ex);
		}
	}

	/**
        * In this method, We save message/s or Local_mail for users(Local)
        * @param data RunData
        * @param context Context
        * @exception Exception a generic exception
        */
        public void doSave(RunData data, Context context)
        {
                try{
                        User user = data.getUser();
                        ParameterParser pp=data.getParameters();
                        String id = (String)user.getTemp("course_id")+(String)user.getTemp("Institute_id")+(String)user.getTemp("role")+user.getName()+pp.getString("page","");
                        String message = pp.getString("message");//pp.getString("hexaStr").trim();
                        AutoSave.doSave(id,message);
                        data.setScreenTemplate("call,Notice_User,Notices.vm");
                }
                catch(Exception e){
                }
        }

	/**
        * In this method,we insert notice to database
        * @param msg_id int
        * @param userid int
        * @param group_id int
        */

	public void insertReceiveNotice(int msg_id,int userid,int group_id, RunData data, String noticeSubject)
	{
		try{
			Criteria crit=new Criteria();
                        crit.add(NoticeReceivePeer.NOTICE_ID,msg_id);
                        crit.add(NoticeReceivePeer.RECEIVER_ID,userid);
                        crit.add(NoticeReceivePeer.GROUP_ID,group_id);
                        crit.add(NoticeReceivePeer.READ_FLAG,0);
                        NoticeReceivePeer.doInsert(crit);
			//String lang=data.getUser().getTemp("lang").toString();
			String lang=data.getUser().getTemp("LangFile").toString();
			//String server_name=TurbineServlet.getServerName();
                        //String srvrPort=TurbineServlet.getServerPort();
			String groupName = GroupUtil.getGroupName(group_id);
			String userName=UserUtil.getLoginName(userid);
			crit=new Criteria();
                        crit.add(CoursesPeer.GROUP_NAME,groupName);
                        List Course_list=CoursesPeer.doSelect(crit);
                        String courseName=((Courses)Course_list.get(0)).getCname();
			crit =new Criteria();
                        crit.add(TurbineUserPeer.USER_ID,userid);
                        List userList=TurbineUserPeer.doSelect(crit);
			////////////////////////////////////////////////////
			String fileName=TurbineServlet.getRealPath("/WEB-INF/conf/brihaspati.properties");
			//String info_new = "";
			/*if(srvrPort == "8080")
				info_new= "brihaspatiNotice";
			else
				info_new= "brihaspatiNoticehttps";
			*/
			Properties pr =MailNotification.uploadingPropertiesFile(fileName);
                        //String subject = MailNotification.subjectFormate(info_new, courseName, pr );
			//String message = MailNotification.getMessage(info_new, courseName, "", data.getUser().getName(), "", pr);
			String message = MailNotification.getMessage("brihaspatiNotice", courseName, "", data.getUser().getName(), "", pr);
			///////////////////////////////////////////////////
			for(int c1=0;c1<userList.size();c1++) {
				TurbineUser element=(TurbineUser)(userList.get(c1));
                                String eMail=element.getEmail();
				if(!eMail.equals("")){
					//String Mail_msg= MailNotification.sendMail(message+"<br><br>"+notice_message, eMail, noticeSubject, "", lang);
					//String Mail_msg= MailNotificationThread.getController().set_Message(message+"<br><br>"+notice_message, "", "", "", eMail, noticeSubject, "", lang, "","");//last parameter added by Priyanka
					String Mail_msg= MailNotificationThread.getController().set_Message(message+"<br><br>"+notice_message, "", "", "", eMail, noticeSubject, "", lang);
				}
			}
		}
		catch(Exception ex)
                {
                       ErrorDumpUtil.ErrorLog("The Error in Insert Receive Notice .."+ex);
                }


	}

	/**
	* In this method,we deleted notice/'s 
	* @param data Rundata
	* @param context Context
	*/
	public void doDelete(RunData data,Context context)
	{
		try{
			ParameterParser pp=data.getParameters();
			context.put("count",pp.getString("count",""));
			context.put("countTemp",pp.getString("countTemp",""));
			String LangFile=(String)data.getUser().getTemp("LangFile");
			String course_id=(String)data.getUser().getTemp("course_id");
			Criteria crit=new Criteria();
			//String noticeList=data.getParameters().getString("deleteFileNames","");
			String noticeList=pp.getString("deleteFileNames","");
			if(!noticeList.equals(""))
			{
			StringTokenizer st=new StringTokenizer(noticeList,"^");
                        for(int j=0;st.hasMoreTokens();j++)
                        	{ //first 'for' loop
                                String n_id=st.nextToken();

				crit=new Criteria();
                        	crit.add(NoticeSendPeer.NOTICE_ID,n_id);
                        	List v=NoticeSendPeer.doSelect(crit);

                        	int user_id=((NoticeSend)(v.get(0))).getUserId();
                        	String userName=data.getUser().getName();
                        	int u_id=UserUtil.getUID(userName);
                        	if( u_id  != user_id )
				{
                                	crit=new Criteria();
                                	crit.add(NoticeReceivePeer.NOTICE_ID,n_id);
                                	crit.add(NoticeReceivePeer.RECEIVER_ID,u_id);
                                	NoticeReceivePeer.doDelete(crit);
					String msg1=MultilingualUtil.ConvertedString("notice_msg2",LangFile);
                        		data.setMessage(msg1);
                        	}
                        	else{
                                	crit=new Criteria();
                                	crit.add(NoticeSendPeer.NOTICE_ID,n_id);
                                	NoticeSendPeer.doDelete(crit);

                                	crit=new Criteria();
                                	crit.add(NoticeReceivePeer.NOTICE_ID,n_id);
                                	NoticeReceivePeer.doDelete(crit);
                                	/**
                                	* deleting the message from the txt file
					* @see CommonUtility in Util
                                	*/
                         		String path = data.getServletContext().getRealPath("/Courses")+"/"+course_id+"/Notice_Msg.txt";
					CommonUtility.UpdateTxtFile(path, n_id, "",false );	
		/*			String str[] = new String[10000];
                        		int i =0;
                        		int startd = 0;
                        		int stopd = 0;
                         		String path = data.getServletContext().getRealPath("/Courses")+"/"+course_id+"/Notice_Msg.txt";
                                	BufferedReader br=new BufferedReader(new FileReader (path));
                                	while ((str[i]=br.readLine()) != null)
                                	{
                                        	if (str[i].equals("<"+n_id+">"))
                                        	{
                                                	startd = i;
                                        	}
                                        	else if(str[i].equals("</"+n_id+">"))
                                        	{
                                                	stopd = i;
                                        	}
                                		i= i +1;
                                	}
                                	br.close();
                                	FileWriter fw=new FileWriter(path);
                                	for(int x=0;x<startd;x++)
                                	{
                                        	fw.write(str[x]+"\r\n");
                                	}
                                	for(int y=stopd+1;y<i;y++)
                                	{
                                        	fw.write(str[y]+"\r\n");
                                	}

                                	fw.close();
		*/
					
					String msg2=MultilingualUtil.ConvertedString("notice_msg3",LangFile);
                                	data.setMessage(msg2);
					log.info(msg2+" with name "+noticeList+" by "+userName);
                        	}
				}//end for Loop
			}//end outer if
		}
		catch(Exception ex)
		{
                	data.setMessage("The error in deletion of notice..."+ex);
		}
	}

	/**
        * In this method,we configure flash heading and System Shutdown Notice
        * @param data Rundata
        * @param context Context
        */
        public void doWrite(RunData data,Context context)
        {
                try{
			/**
		         *Getting file value from temporary variable according to selection
         		 *Replacing the value from Property file
         		 **/
                        String LangFile=(String)data.getUser().getTemp("LangFile");
			ParameterParser pp=data.getParameters();
			User user = data.getUser();
			/**
         		  * @param htype: Getting heading Type as a String from Parameter Parser 
          		  * @param Fheading: Getting Message as a String from Parameter Parser 
          		  * @param fhrole: Getting user role String from rundata 
          		  */

			String htype=pp.getString("htype","");
                        context.put("hflag",htype);
			String Fheading=pp.getString("message","");
			String fhrole=data.getUser().getName();
			/**
			* Check User role and Heading Type
			* if heading is Flash Heading
			* set message in Notificartion.properties file 
			*/
			if(fhrole.equals("admin") && htype.equals("Flash Heading")){
			String path=data.getServletContext().getRealPath("/WEB-INF")+"/conf"+"/"+"Notification.properties";
			//(new File(path)).delete();
			AdminProperties.setPropertyValue(path,Fheading,"brihaspati.admin.flashHeading.value");
			}
			/**
                        * Check User role and Heading Type
                        * if heading is Shutdown Notice
			* @param shtime: Get shutdown time as a String from Parameter Parser 
			* @param ExpiryDate: Get ExpiryTime as a String. 
                        * set message in Shutdown.properties file 
                        */
			if((fhrole.equals("admin")) && (htype.equals("Shutdown Notices"))){
			String shtime=pp.getString("shtime","");
                        int stime=Integer.parseInt(shtime);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        java.util.Date date = new java.util.Date();
			// Create Calender instance
                        Calendar now = Calendar.getInstance();
                        //add minutes to current date using Calendar.add method
                        now.add(Calendar.MINUTE,stime);
                        //Get Expiry date for Shutdown Notices.
                        String ExpiryDate=dateFormat.format(now.getTime());
                        String path=data.getServletContext().getRealPath("/WEB-INF")+"/conf"+"/"+"Shutdown.properties";
                        //(new File(path)).delete();
			/**
			* Set Message in Shutdown.Notification properties file
			* Set ExpiryDate in Shutdown.Notification properties file
			*/
                       	AdminProperties.setPropertyValue(path,ExpiryDate,"brihaspati.admin.ShutdownExpDate.value");
                       	AdminProperties.setPropertyValue(path,Fheading,"brihaspati.admin.ShutdownHeading.value");

                        }
			String Fhupdate=MultilingualUtil.ConvertedString("cal_ins",LangFile);
                        data.setMessage(Fhupdate);
			AutoSave.doDelete((String)user.getTemp("course_id")+(String)user.getTemp("Institute_id")+(String)user.getTemp("role")+user.getName()+pp.getString("page",""));
		}
		catch(Exception ex){data.setMessage("The error in do Write method in Notice Send"+ex);}
	}

	public void doSendMsg(RunData data, Context context)
        {
		try
                {
                        String mailMsg = "";
                        User user = data.getUser();
                        String username=user.getName();
                        ParameterParser pp=data.getParameters();
                        String LangFile=user.getTemp("LangFile").toString();
                        String subject =pp.getString("subject","");
                        String semail = pp.getString("senderMail","");
                        String message = pp.getString("msg_val","");
                        if( semail != null && semail != "")
                                mailMsg =  MailNotificationThread.getController().set_Message(message, "", "", "", semail, subject, "", LangFile);
			String msg1=MultilingualUtil.ConvertedString("mail_msg",LangFile);
                        data.setMessage(msg1);
                        //ErrorDumpUtil.ErrorLog("mailMsg reply---------"+mailMsg);
                }
                catch(Exception ex)
                {
                        ErrorDumpUtil.ErrorLog("The error in doSend method!! "+ex);
                }
	}

    /**
     * Default action to perform if the specified action cannot be executed.
     * @param data RunData
     * @param context Context
     */
	public void doPerform( RunData data,Context context )throws Exception
	{
		String action=data.getParameters().getString("actionName","");
		if(StringUtils.isBlank(action))
			action=data.getParameters().getString("actionValue","");
		if(action.equals("eventSubmit_doSend"))
			doSend(data,context);
		else if(action.equals("eventSubmit_doDelete"))
			doDelete(data,context);
		else if(action.equals("eventSubmit_doWrite"))
                        doWrite(data,context);
		else if(action.equals("eventSubmit_doSave"))
                        doSave(data,context);
		else if(action.equals("eventSubmit_doChange"))
			data.setMessage("");
		else if(action.equals("eventSubmit_doSendMsg"))
			doSendMsg(data,context);
		else
			data.setMessage("Cannot find the button");
	}
}
