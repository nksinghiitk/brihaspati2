package org.iitk.brihaspati.modules.actions;
/*
 * @(#) AddUser.java	
 *
 *  Copyright (c) 2004-2006,2009,2010,2012 ETRG,IIT Kanpur. 
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
import java.util.List;
import java.util.Vector;
import org.apache.turbine.util.RunData;
import org.apache.torque.util.Criteria;
import org.apache.velocity.context.Context;
import org.apache.turbine.om.security.User; 
import org.apache.turbine.util.parser.ParameterParser;
import org.iitk.brihaspati.modules.utils.UserManagement;
import org.iitk.brihaspati.modules.utils.MultilingualUtil;
import org.iitk.brihaspati.modules.utils.ErrorDumpUtil;
import org.iitk.brihaspati.om.InstituteAdminRegistration;
import org.iitk.brihaspati.om.InstituteAdminRegistrationPeer;
import org.iitk.brihaspati.modules.utils.InstituteDetailsManagement;
import org.iitk.brihaspati.modules.utils.StringUtil;
import org.iitk.brihaspati.modules.utils.InstituteIdUtil;
import org.iitk.brihaspati.modules.utils.ListManagement;

/**
 * This class is responsible for adding a new user in specified group and 
 * assigned role to the system.
 * @author <a href="mailto:madhavi_mungole@hotmail.com">Madhavi Mungole</a> 
 * @author <a href="mailto:awadhk_t@yahoo.com">Awadhesh Kumar Trivedi</a> 
 * @author <a href="mailto:singh_jaivir@rediffmail.com">Jaivir Singh</a>28oct2012 
 * @author <a href="mailto:richa.tandon1@gmail.com">Richa Tandon</a>
 * @author <a href="mailto:palseema@rediffmail.com">Manorama Pal</a>
 * @modified date: 20-10-2010, 23-12-2010 
 */

public class AddUser extends SecureAction_Admin 
{
/**
 *
 * Method for registered a user as Secondary Instructor,Student,Group_Admin
 * and Content Author
 * @param data RunData instance
 * @param context Context instance
 *
 */
	private String LangFile=null;
	
	public void doRegister(RunData data, Context context) throws Exception
	{
		try{
		MultilingualUtil m_u= new MultilingualUtil();
		ParameterParser pp=data.getParameters();
		//String serverName=data.getServerName();
                //int srvrPort=data.getServerPort();
                //String serverPort=Integer.toString(srvrPort);
                String roleName=pp.getString("role","");
		/**
                 * Getting the value of file from temporary variable
                 * According to selection of Language.
                 * and replacing the String from property file.
                 * @see MultilingualUtil in utils
                 */   

		LangFile=(String)data.getUser().getTemp("LangFile");
		
		/**
                 * Retreiving details entered by the user
                 */
                String rollno = pp.getString("rollno","").trim();
		/**
                 * check if rollno have any special character then return it
                 */
		if(StringUtil.checkString(rollno) != -1)
                {
                	data.addMessage(MultilingualUtil.ConvertedString("c_msg3",LangFile));
                        return;
                }
                String program = pp.getString("prg","");
		String gname=new String();
		gname=pp.getString("group","");
		if(gname.equals(""))
		{
			gname=new String();	
			gname=pp.getString("group_author");
		}
		if(roleName.equals(""))
		{
			roleName=new String();	
			roleName=pp.getString("role_author");
		}
                String passwd=pp.getString("PASSWD");
                String fname=pp.getString("FNAME");
                String lname=pp.getString("LNAME");
                String email=pp.getString("EMAIL");
		email=UserManagement.ChkMailId(email);
		/**
		*Set the password as the value of 0th position of mailId
		*/
                if(passwd.equals("")){
			String []starr=email.split("@");
                	passwd =starr[0];
		}
		/**
                 * Passing the value of file from temporary variable
                 * According to selection of Language.
		 * Adds the new user in the database.
		 * @see UserManagement in utils
		 */
		//String msg=UserManagement.CreateUserProfile(email,passwd,fname,lname,"",email,gname,roleName,serverName,serverPort,LangFile,rollno,program,"");   //modified by Shikha. Last parameter added by Priyanka.
		String msg=UserManagement.CreateUserProfile(email,passwd,fname,lname,"",email,gname,roleName,LangFile,rollno,program,"");
		data.setMessage(msg);
		}
		catch(Exception ex){
		data.setMessage("The Error in AddUser Action");
		}
	}
	/**
 	*This method return the courses of selected institute in which admin register the user
 	*/ 
	public void doSearch(RunData data, Context context) throws Exception
        {
		String LangFile=(String)data.getUser().getTemp("LangFile");
                ParameterParser pp=data.getParameters();
                String instName=pp.getString("institute");
                String instituteId = Integer.toString(InstituteIdUtil.getIst_Id(instName));
                //Get mapped department list
                List DeptList=ListManagement.getMapDeptList(instituteId);
                context.put("deptlist",DeptList);
		Vector CourseList=InstituteDetailsManagement.InstwiseCourse(instName);
                if(CourseList.size()!=0){
			context.put("courseList",CourseList);
		}
		else{
			data.setMessage("The courses are not registered in this Institute");
		}
	}//method
	/**
	 * This is the default method called when the button is not found
	 * @param data RunData
	 * @param context Context
	 */
	public void doPerform(RunData data,Context context) throws Exception
	{
		String action=data.getParameters().getString("actionName","");
               /**  
		 * Passing the value of file from temporary variable
                 * According to selection of Language.
		 */
		MultilingualUtil m_u= new MultilingualUtil();
		LangFile=(String)data.getUser().getTemp("LangFile");
		if(action.equals("eventSubmit_doRegister"))
			doRegister(data,context);
		else if(action.equals("eventSubmit_doSearch"))
                        doSearch(data,context);
		else
		{       String str=m_u.ConvertedString("c_msg",LangFile);
                        data.setMessage(str);
		}
	}
}

