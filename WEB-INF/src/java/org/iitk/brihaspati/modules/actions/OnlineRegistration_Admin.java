package org.iitk.brihaspati.modules.actions;

/*
 * @(#) OnlineRegistration_Admin.java	
 *
 *  Copyright (c) 2008-09,2010-13 ETRG,IIT Kanpur. 
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
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.StringTokenizer;

import org.apache.turbine.util.RunData;
import org.apache.torque.util.Criteria; 
import org.apache.velocity.context.Context;
import org.apache.turbine.om.security.User;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.services.security.torque.om.TurbineUserPeer;
import org.apache.commons.lang.StringUtils;
//brihaspati classes
import org.iitk.brihaspati.modules.utils.XmlWriter;
import org.iitk.brihaspati.modules.utils.InstituteIdUtil;
import org.iitk.brihaspati.modules.utils.ErrorDumpUtil;
import org.iitk.brihaspati.modules.utils.UserManagement;
import org.iitk.brihaspati.modules.utils.CourseUserDetail;
import org.iitk.brihaspati.modules.utils.CourseManagement;
import org.iitk.brihaspati.modules.utils.MultilingualUtil;
import org.iitk.brihaspati.modules.utils.MailNotification;
import org.iitk.brihaspati.modules.utils.MailNotificationThread;
import org.iitk.brihaspati.modules.utils.TopicMetaDataXmlReader;
import org.iitk.brihaspati.modules.utils.TopicMetaDataXmlWriter;

import org.iitk.brihaspati.om.InstituteAdminUser;
import org.iitk.brihaspati.om.InstituteAdminUserPeer;
import org.iitk.brihaspati.modules.utils.UserUtil;
/**
 * @author  <a href="mailto:nksngh_p@yahoo.co.in">Nagendra Kumar singh</a>
 * @author  <a href="mailto:omprakash_kgp@yahoo.co.in">Om Prakash</a>
 * @author <a href="mailto:shaistashekh@hotmail.com">Shaista</a>
 * @author <a href="mailto:richa.tandon1@gmail.com">Richa Tandon</a>
 * @author <a href="mailto:shikhashuklaa@gmail.com">Shikha Shukla</a>
 * @modify 20-03-09, 08-07-2010, 20-10-2010, 23-12-2010, 05-08-2011, 16-08-2011
 * @modify 20-04-12
 * @author <a href="mailto:rpriyanka12@ymail.com">Priyanka Rawat</a>
 * @author <a href="mailto:tejdgurung20@gmail.com">Tej Bahadur</a>
 * @modify date: 09-08-2012 (Priyanka),31-05-2013,10-06-2013
 */

/**
 * This class called when institute admin accept or reject the request of registration of Student,Instructor(Course) and author
 * @author  <a href="mailto:singh_jaivir@rediffmail.com">Jaivir Singh</a>
 *@modify 20092010,1may2012
*/
public class  OnlineRegistration_Admin extends SecureAction_Institute_Admin{

	private String LangFile=null;
	private String tokn="", uName="", gName="", mailid="", instAdminName="";
        private String uname="", gname="", email="", fname="", lname="", passwd="", rollno="", program="";
	private String [] splitedTokn ;
         
        public void RejectUser(RunData data, Context context)
        {
                        String  instituteId=(data.getUser().getTemp("Institute_id")).toString();
			String lUserName = data.getUser().getName();
                        int instid=Integer.parseInt(instituteId);
		//	ErrorDumpUtil.ErrorLog("\n\n\n\n instid===========in OnlineRegistration_Admin ="+instid);
                        String instName=InstituteIdUtil.getIstName(instid);
                       	Vector userlist=new Vector();
			Vector indexList=new Vector();
			String Mail_msg="";
			context.put("status","UserResitration");	
                        User user=data.getUser();
                        ParameterParser pp=data.getParameters();
	                String accept=pp.getString("deleteFileNames");
             try{
			String path=data.getServletContext().getRealPath("/OnlineUsers");
                        TopicMetaDataXmlReader topicmetadata =new TopicMetaDataXmlReader(path+"/OnlineUser.xml");
			userlist=topicmetadata.getOnlineUserDetails();
			StringTokenizer st=new StringTokenizer(accept,"^");
			//String MsgForExpireTime= "Your Request for "; 
			//String subMsgForExpireTime= " Registration is rejected. Please contact to the administrator personally";
                        //String server_name= TurbineServlet.getServerName();
                        //String srvrPort= TurbineServlet.getServerPort();
			//String server_scheme = TurbineServlet.getServerScheme();
			//////////////////////////////////////////////////
			try{    
				Criteria crit=new Criteria();
	                        crit.add(InstituteAdminUserPeer.INSTITUTE_ID,instid);
	                        crit.add(InstituteAdminUserPeer.ADMIN_UNAME,lUserName);
        	                List inm=InstituteAdminUserPeer.doSelect(crit);
                	        InstituteAdminUser element=(InstituteAdminUser)inm.get(0);
                        	//instAdminName=element.getAdminFname() +" "+element.getAdminLname();
				/**modify by jaivir,seema 
                        	*Getting full name of user using UserUtil.
                        	*@see UserUtil in utils
                        	*/
                        	int Auid=UserUtil.getUID(element.getAdminUname());
                        	instAdminName=UserUtil.getFullName(Auid);
                	}
                	catch(Exception e){ErrorDumpUtil.ErrorLog("Error in OnlineRegistration_Admin class in acion at line 113");}

			String message ="";
			//String info_new = "", info_Opt="", msgRegard="";
       			String msgRegard="";
			/*if(srvrPort == "8080"){
                               	info_new="onLineRegReqForUserReject";
				info_Opt = "newUser";
	                }

			else {
                               	info_new="onLineRegReqForUserReject_https";
				info_Opt = "newUserhttps";
	                }
			*/
       			Properties pr =MailNotification.uploadingPropertiesFile(TurbineServlet.getRealPath("/WEB-INF/conf/brihaspati.properties"));
                        //String subject = MailNotification.subjectFormate(info_new, "", pr );
			String subject = MailNotification.subjectFormate("onLineRegReqForUserReject", "", pr );
			//msgRegard=pr.getProperty("brihaspati.Mailnotification."+info_Opt+".msgRegard");
	                msgRegard=pr.getProperty("brihaspati.Mailnotification.newUser.msgRegard");
			//msgRegard = MailNotification.replaceServerPort(msgRegard, server_name, srvrPort);
			msgRegard = MailNotification.replaceServerPort(msgRegard);
			//String msgRoleInfo = pr.getProperty("brihaspati.Mailnotification."+info_Opt+".msgInstAdmin");
                	String msgRoleInfo = pr.getProperty("brihaspati.Mailnotification.newUser.msgInstAdmin");
			msgRoleInfo = msgRoleInfo.replaceAll("institute_admin",instAdminName);
			LangFile=(String)data.getUser().getTemp("LangFile");
			while(st.hasMoreTokens())
                        {
				tokn = st.nextToken();
				//ErrorDumpUtil.ErrorLog(" reject User tokn="+tokn);
				mailid =StringUtils.substringBefore(tokn,":");
				String strTemp = StringUtils.substringAfter(tokn,":");
				gName = StringUtils.substringBeforeLast(strTemp,":");
				uName = StringUtils.substringAfterLast(strTemp,":");
/**				splitedTokn = tokn.split(":");
				mailid= splitedTokn [0];
				gName = splitedTokn [1];
				uName = splitedTokn [2];
*/				
       	                	if(userlist!= null)
               	        	{
                       	                for(int i=0;i<userlist.size();i++)
					{  
                                       						    
						email=((CourseUserDetail) userlist.elementAt(i)).getEmail();
						uname=((CourseUserDetail) userlist.elementAt(i)).getLoginName();
       	                                       	gname=((CourseUserDetail) userlist.elementAt(i)).getGroupName();
						//ErrorDumpUtil.ErrorLog("\n email==="+email+"\t mailid="+mailid+"\tgname="+gname+"\t gName="+gName+"\t uname="+uname+"\tuName ="+uName);
               	                                if(email.equals(mailid) && gname.equals(gName) && uname.equals(uName))
						{						
                               	                	//message = MailNotification.getMessage(info_new, gname, "", uname, "", pr);
							message = MailNotification.getMessage("onLineRegReqForUserReject", gname, "", uname, "", pr);
							message = MailNotification.getMessage_new(message, "","",instName,"");

							 

							//ErrorDumpUtil.ErrorLog("OnlineRegistration_Admin.java RejectUser  message="+message);
							//Mail_msg=MailNotification.sendMail(message, mailid, subject, "",LangFile);
							 //Mail_msg = MailNotificationThread.getController().set_Message(message, "", msgRegard, msgRoleInfo, mailid, subject, "", LangFile, Integer.toString(instid),"");//last parameter added by Priyanka
							 Mail_msg = MailNotificationThread.getController().set_Message(message, "", msgRegard, msgRoleInfo, mailid, subject, "", LangFile);
							indexList.add(i);
               						data.setMessage(MultilingualUtil.ConvertedString("online_msg3",LangFile));
						}
                               		} //for
                       		} //if
			} //while
		/**
		 *  Here  rejecting the particular entry from the "xml" file
		 */
	                XmlWriter xmlwriter=TopicMetaDataXmlWriter.WriteXml_OnlineUser(path,"/OnlineUser.xml",indexList);
			xmlwriter.writeXmlFile();	
		}//try
		catch(Exception e)
		{
	                data.setMessage("Please see Error log or Contact to administrator");
		}
               
	}
	/**
         * This method Accept User for online registration request.
         * get detail of user request from xml file to create profile of user.
         */

      	public void AcceptUser(RunData data, Context context)
	{
	
		try{
			Vector userlist=new Vector();
			Vector indexList=new Vector();	
			String roleName="", instName="";
			context.put("status","UserResitration");	
			User user=data.getUser();
                        ParameterParser pp=data.getParameters();
		        //String serverName=data.getServerName();
			//String serverPort=TurbineServlet.getServerPort();
	                String accept=pp.getString("deleteFileNames");
                	String path=data.getServletContext().getRealPath("/OnlineUsers"+"/OnlineUser.xml");
                	String xmlfilepath=data.getServletContext().getRealPath("/OnlineUsers");
                        TopicMetaDataXmlReader topicmetadata =new TopicMetaDataXmlReader(path);
		        userlist=topicmetadata.getOnlineUserDetails();
			LangFile=(String)data.getUser().getTemp("LangFile");
			StringTokenizer st=new StringTokenizer(accept,"^");
			for(int j=0;st.hasMoreTokens();j++)
                        {
				tokn = st.nextToken();
				//ErrorDumpUtil.ErrorLog(" reject User tokn="+tokn);
				mailid =StringUtils.substringBefore(tokn,":");
				String strTemp = StringUtils.substringAfter(tokn,":");
				gName = StringUtils.substringBeforeLast(strTemp,":");
				uName = StringUtils.substringAfterLast(strTemp,":");
/**                             splitedTokn = tokn.split(":");
                                mailid= splitedTokn [0];
                                gName = splitedTokn [1];
                                uName = splitedTokn [2];
*/
				if(userlist!=null)
                        	{
                                  				
					for(int i=0;i<userlist.size();i++)
                                	{
						
						email=((CourseUserDetail) userlist.elementAt(i)).getEmail();
						uname=((CourseUserDetail) userlist.elementAt(i)).getLoginName();
	        	                        gname=((CourseUserDetail) userlist.elementAt(i)).getGroupName();
	        	                        rollno=((CourseUserDetail) userlist.elementAt(i)).getRollNo();
	        	                        program=((CourseUserDetail) userlist.elementAt(i)).getPrgCode();
						/**
                                                 * Getting Institute id from temp
                                                 * check program value, if it is RWP ie RegistrationWithoutProgram 
                                                 * then generate random rollno
                                                 */
						String instituteId=(data.getUser().getTemp("Institute_id")).toString();
						int instid=Integer.parseInt(instituteId);
				                if(program.equals("RWP"))
				                {
				                        rollno = InstituteIdUtil.generateRollno(instid);
				                }
						//ErrorDumpUtil.ErrorLog("rollno in online registration admin action\n"+rollno);
                                                if(email.equals(mailid) && gname.equals(gName) && uname.equals(uName))
						{
                	                	        roleName=((CourseUserDetail) userlist.elementAt(i)).getRoleName();
	        	                                passwd=((CourseUserDetail) userlist.elementAt(i)).getActive();
							fname=((CourseUserDetail) userlist.elementAt(i)).getInstructorName();
							lname=((CourseUserDetail) userlist.elementAt(i)).getUserName();
							if(uname!=null)
							{
								instName = ((CourseUserDetail)userlist.get(i)).getInstAdminName();
								try{
			              					//String msg=UserManagement.CreateUserProfile(uname,passwd,fname,lname,instName,email,gname,roleName,serverName,serverPort,LangFile,rollno,program,"cnfrm_u");//last parameter added by Priyanka
									String msg=UserManagement.CreateUserProfile(uname,passwd,fname,lname,instName,email,gname,roleName,LangFile,rollno,program,"cnfrm_u");
									ErrorDumpUtil.ErrorLog("inside onlireg_Admin");
									data.setMessage(msg);
								}
								catch(Exception e){
									data.setMessage("Error in new user Registration"+e);
								}
							}//if
							indexList.add(i);
                					//data.addMessage("\n"+MultilingualUtil.ConvertedString("online_msg1",LangFile));
						}	
					}//for
				}//if
			}
			XmlWriter xmlwriter=TopicMetaDataXmlWriter.WriteXml_OnlineUser(xmlfilepath,"/OnlineUser.xml",indexList); 
			xmlwriter.writeXmlFile();

		}//try
		
		catch(Exception e){
	                data.setMessage("Please see Error log or Contact to administrator");
		}
	}

        public void doDeleteCourse(RunData data, Context context)
        {
                 String  instituteId=(data.getUser().getTemp("Institute_id")).toString();
                 int instid=Integer.parseInt(instituteId);
                 String instName=InstituteIdUtil.getIstName(instid);
 
		 try{
                        User user=data.getUser();
                        ParameterParser pp=data.getParameters();
                        String path=TurbineServlet.getRealPath("/OnlineUsers");
                        XmlWriter xmlWriter=null;
                        String accept=pp.getString("deleteFileNames");
                        Vector indexList=new Vector();
                        Vector courselist = new Vector();
			String Mail_msg="";
			context.put("status","CourseRegistration");
			//String server_name= TurbineServlet.getServerName();
			//String srvrPort= TurbineServlet.getServerPort();
			//String serverScheme = TurbineServlet.getServerScheme();
                        TopicMetaDataXmlReader topicmetadata =new TopicMetaDataXmlReader(path+"/courses.xml");
                        courselist=topicmetadata.getOnlineUserDetails();
                        StringTokenizer st= new StringTokenizer(accept,"^");
			//String MsgForExpireTime= "Your Request for ";
			//String subMsgForExpireTime= " Registration is rejected. Please contact to the administrator personally";
///////////////////////////////////////////////////////////////////////
			String message ="";
                        //String info_new = "", info_Opt="", msgRegard="", instAdminName="";
			String msgRegard="", instAdminName="";
			 try{    
				Criteria crit=new Criteria();
				crit.add(InstituteAdminUserPeer.INSTITUTE_ID,instid);
				List inm=InstituteAdminUserPeer.doSelect(crit);
	                        InstituteAdminUser element=(InstituteAdminUser)inm.get(0);
        	                //instAdminName=element.getAdminFname() +" "+element.getAdminLname();
				/**modify by jaivir,seema 
                                *Getting full name of user using UserUtil.
                                *@see UserUtil in utils
                                */
                                int Auid=UserUtil.getUID(element.getAdminUname());
                                instAdminName=UserUtil.getFullName(Auid);

			}
	                catch(Exception e){ErrorDumpUtil.ErrorLog("Error in OnlineRegistration_Admin class in action at line 245");}

                       /* if(srvrPort == "8080"){
                                info_new="onLineRegReqForCourseReject";
				info_Opt = "newUser";
	                }
        	        else {
                                info_new="onLineRegReqForCourseReject_https";
				info_Opt = "newUserhttps";
	                }*/
			Properties pr =MailNotification.uploadingPropertiesFile(TurbineServlet.getRealPath("/WEB-INF/conf/brihaspati.properties"));
                        //String subject = MailNotification.subjectFormate(info_new, "", pr );
			String subject = MailNotification.subjectFormate("onLineRegReqForCourseReject", "", pr );
			//msgRegard=pr.getProperty("brihaspati.Mailnotification."+info_Opt+".msgRegard");
        	        msgRegard=pr.getProperty("brihaspati.Mailnotification.newUser.msgRegard");
			//msgRegard = MailNotification.replaceServerPort(msgRegard, server_name, srvrPort);
	                msgRegard = MailNotification.replaceServerPort(msgRegard);
			//String msgRoleInfo = pr.getProperty("brihaspati.Mailnotification."+info_Opt+".msgInstAdmin"); 
                	String msgRoleInfo = pr.getProperty("brihaspati.Mailnotification.newUser.msgInstAdmin");
			msgRoleInfo = msgRoleInfo.replaceAll("institute_admin",instAdminName);
                        LangFile=(String)data.getUser().getTemp("LangFile");
///////////////////////////////////////////////////////////////////////
                        for(int j=0;st.hasMoreTokens();j++)
                        {
				/** @param gName getting GroupId & user name**/

				tokn=st.nextToken();
				mailid = StringUtils.substringBefore(tokn, ":");
				gName = StringUtils.substringAfter(tokn, ":");
/**		
				splitedTokn = tokn.split(":");
				mailid = splitedTokn[0]; 
                                gName = splitedTokn[1];
*/
                                if(courselist!= null)
                                {
                                        for(int i=0;i<courselist.size();i++)
                                        {
                                                email=((CourseUserDetail) courselist.elementAt(i)).getEmail();
						//String gnameTemp = (CourseUserDetail) courselist.elementAt(i)).getGroupName();
						//if(gnameTemp.contains("&colon")
							//gnameTemp = gnameTemp.replace("&colon",":");
						gname=((CourseUserDetail) courselist.elementAt(i)).getGroupName().replace("&colon",":")+((CourseUserDetail) courselist.elementAt(i)).getLoginName();
                                                if((email.equals(mailid)) && gName.equals(gname))
                                                {
							//message = MailNotification.getMessage(info_new, gname, "", "", "", pr);
							message = MailNotification.getMessage("onLineRegReqForCourseReject_https", gname, "", "", "", pr);
							message = MailNotification.getMessage_new(message, "","",instName,"");
							//ErrorDumpUtil.ErrorLog("OnlineRegistration_Admin.java RejectCourse message="+message);
							//Mail_msg=MailNotification.sendMail(message, mailid, subject, "", LangFile);
							Mail_msg = MailNotificationThread.getController().set_Message(message, instName, msgRegard, msgRoleInfo, mailid, subject, "", LangFile);
							//Mail_msg = MailNotificationThread.getController().set_Message(message, instName, msgRegard, msgRoleInfo, mailid, subject, "", LangFile, instituteId,"");//last parameter added by Priyanka
                                                        indexList.add(i);
							LangFile=(String)data.getUser().getTemp("LangFile");
                                                        data.setMessage(MultilingualUtil.ConvertedString("online_msg4",LangFile));
							if(Mail_msg.equals("Success"))
							{
								Mail_msg=MultilingualUtil.ConvertedString("mail_msg",LangFile);
								data.addMessage(Mail_msg);
							}
                                                }
                                        }
                                }
                        }
                        xmlWriter=TopicMetaDataXmlWriter.WriteXml_OnlineCourse(path,"/courses.xml",indexList);
                        xmlWriter.writeXmlFile();
                }//try
 
                catch(Exception e){
			ErrorDumpUtil.ErrorLog("The error in Online registartion action do delete course method" +e);
	                data.setMessage("Please see Error log or Contact to administrator");
			}
        }
	
	 /**
         * This method Accept Course/Instructor request for online registration.
         * get detail of user request from xml file to create profile of user.
         */

        public void doAcceptCourses(RunData data, Context context)
        {
                try{
			String gidUname="", passwd="", cname="", instName="",dept="",scname="";
			String instituteId=(data.getUser().getTemp("Institute_id").toString());
			int instId=Integer.parseInt(instituteId);
			instName= InstituteIdUtil.getIstName(instId);
                        Vector indexList=new Vector();
                        Vector courselist=new Vector();
			context.put("status","CourseRegistration");
                        User user=data.getUser();
                        ParameterParser pp=data.getParameters();
                        //String serverName=data.getServerName();
			//String serverPort=TurbineServlet.getServerPort();
                        String accept=pp.getString("deleteFileNames");
                        String path=data.getServletContext().getRealPath("/OnlineUsers"+"/courses.xml");
                        String xmlfilepath=data.getServletContext().getRealPath("/OnlineUsers");
                        TopicMetaDataXmlReader topicmetadata =new TopicMetaDataXmlReader(path);
                        courselist=topicmetadata.getOnlineCourseDetails();
                        StringTokenizer st=new StringTokenizer(accept,"^");

                        for(int j=0;st.hasMoreTokens();j++)
                        {
				tokn=st.nextToken();
				//ErrorDumpUtil.ErrorLog("tokn -----"+tokn);
                                //splitedTokn = tokn.split(":");
                                //mailid = splitedTokn[0];
                                //gName = splitedTokn[1]; //Getting gid +user name
				mailid = StringUtils.substringBefore(tokn, ":");
				gName = StringUtils.substringAfter(tokn, ":"); //Getting gid +user name
                                if(courselist!=null)
                                {
                                        for(int i=0;i<courselist.size();i++)
                                        {
 
                                                email=((CourseUserDetail) courselist.elementAt(i)).getEmail();
						gidUname=((CourseUserDetail) courselist.elementAt(i)).getGroupName().replace("&colon",":")+((CourseUserDetail) courselist.elementAt(i)).getLoginName();
						//ErrorDumpUtil.ErrorLog("\n email==="+email+"\t mailid="+mailid+"\tgname="+gidUname+"\t gName="+gName);
                                                if((email.equals(mailid)) && gName.equals(gidUname))
                                                {
 
                                                        gname=((CourseUserDetail) courselist.elementAt(i)).getGroupName().replace("&colon",":");
                                                        cname=((CourseUserDetail) courselist.elementAt(i)).getCourseName();
                                                        uname=((CourseUserDetail) courselist.elementAt(i)).getLoginName();
							// Get new field 'department' and 'scool/center' name for online course registration
							dept=((CourseUserDetail) courselist.elementAt(i)).getDept();
                                                        scname=((CourseUserDetail) courselist.elementAt(i)).getSchoolCenter();
							/**
							*Set the Password if empty.
							*password is the value of 0th position of mailid
							*/
                                                        if(passwd.equals("")){
							String []starr=email.split("@");
                					passwd=starr[0];
							}
                                                        fname=((CourseUserDetail) courselist.elementAt(i)).getInstructorName();
                                                        lname=((CourseUserDetail) courselist.elementAt(i)).getUserName();
                                                        //instName=((CourseUserDetail) courselist.elementAt(i)).getInstAdminName();
							//ErrorDumpUtil.ErrorLog(" instName =="+instName);
                                                       {
                                                                try{
                                                                        //String msg=CourseManagement.CreateCourse(gname,cname,"","",uname,passwd,fname,lname,email,serverName,serverPort,LangFile,instId,instName,"cnfrm_c");//last parameter added by Priyanka
									//Add parameter "dept" and  "scname" for accept online course(Instructor) registration.
									String msg=CourseManagement.CreateCourse(gname,cname,dept,"",uname,passwd,fname,lname,email,LangFile,instId,instName,"cnfrm_c",scname);
								/**
									String subject="";
									if(serverPort.equals("8080"))
								                   subject="newOnlineCourse";
							                else
								                   subject="newOnlineCoursehttps";
                							String fileName=TurbineServlet.getRealPath("/WEB-INF/conf/brihaspati.properties");
									String Mail_msg=MailNotification.sendMail(subject,email,cname,"","","",fileName,serverName,serverPort,LangFile);
                                                                        data.setMessage(msg+Mail_msg);
								**/
                                                                        data.setMessage(msg);
                                                                }
                                                                catch(Exception e){data.setMessage("Error in new Course Registration "+e);}
                                                        }
                                                        indexList.add(i);
 
							//LangFile=(String)data.getUser().getTemp("LangFile");
                                                        //data.addMessage(MultilingualUtil.ConvertedString("online_msg2",LangFile));
                                                } //if
                                        }  //for
                                } //if
                        }
                        XmlWriter xmlwriter=TopicMetaDataXmlWriter.WriteXml_OnlineCourse(xmlfilepath,"/courses.xml",indexList);
                        xmlwriter.writeXmlFile();

                 }
                catch(Exception e){
			ErrorDumpUtil.ErrorLog("The error in Online registartion action do accept course method" +e);
	                data.setMessage("Please see Error log or Contact to administrator");
		}
        }

	/**
          * ActionEvent responsible if no method found in this action i.e. Default Method
          * @param data RunData
          * @param context Context
          */

	public void doPerform(RunData data,Context context) throws Exception
	{
	    try{	
			LangFile=(String)data.getUser().getTemp("LangFile");
         		String action=data.getParameters().getString("actionName","");

			if(action.equals("eventSubmit_AcceptUser"))
                	{
				AcceptUser(data,context);
			}
			else if(action.equals("eventSubmit_RejectUser"))
			{
				RejectUser(data,context);
			}
			else if(action.equals("eventSubmit_doAcceptCourses"))
			{
				doAcceptCourses(data,context);	
			}
			else if(action.equals("eventSubmit_doDeleteCourse"))
			{
				doDeleteCourse(data,context);
			}
			else
			{	
                       		data.setMessage("Action is not properly defind.");
                	}   
		}
		catch(Exception e){ 
			ErrorDumpUtil.ErrorLog("The error in Online registartion admin action file" +e);
	                data.setMessage("Please see Error log or Contact to administrator");
		}			
	}

}
