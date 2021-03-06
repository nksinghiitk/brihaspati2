package org.iitk.brihaspati.modules.screens.call.UserMgmt_InstituteAdmin;

/*
 * @(#) InstStudentCourseList.java	
 *
 *  Copyright (c) 2005-2006,2009,2010 ETRG,IIT Kanpur. 
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
 *  BUSINESS INTERRUPTIiON) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 *  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *  
 *  Contributors: Members of ETRG, I.I.T. Kanpur 
 * 
 */
/**
 * @author  <a href="awadhesh_trivedi@yahoo.co.in">Awadhesh Kumar Trivedi</a>
 * @author  <a href="singh_jaivir@rediffmail.com">Jaivir Singh</a>
 * @author  <a href="sharad23nov@yahoo.com">Sharad Singh</a>
 * @author  <a href="richa.tandon1@gmail.com">Richa Tandon</a>
 * @author  <a href="prajeev@iitk.ac.in">Rajeev Parashari</a>
 * @modified date:28-12-2011(Richa)
 * @modified date:18-07-2012(Rajeev),30-10-2012(Richa)
 */

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ListIterator;
import com.workingdogs.village.Record;
import org.apache.velocity.context.Context;
import org.apache.commons.lang.StringUtils;
import org.apache.turbine.util.RunData;
import org.apache.torque.util.Criteria;
import org.iitk.brihaspati.modules.utils.UserGroupRoleUtil;
import org.iitk.brihaspati.modules.utils.UserUtil;
import org.iitk.brihaspati.modules.utils.CourseUtil;
import org.iitk.brihaspati.modules.utils.GroupUtil;
import org.iitk.brihaspati.modules.utils.UserManagement;
import org.iitk.brihaspati.modules.utils.CourseManagement;
import org.iitk.brihaspati.modules.utils.InstituteDetailsManagement;
import org.iitk.brihaspati.modules.utils.InstituteIdUtil;
import org.iitk.brihaspati.modules.utils.CourseUserDetail;
import org.iitk.brihaspati.modules.utils.CourseProgramUtil;
import org.iitk.brihaspati.modules.utils.ErrorDumpUtil;
import org.iitk.brihaspati.om.InstituteProgramPeer;
import org.iitk.brihaspati.om.InstituteProgram;
import org.iitk.brihaspati.om.StudentRollno;
import org.iitk.brihaspati.modules.screens.call.SecureScreen_Institute_Admin;

/**
  * This class contains code for courses list where Students Registered
  * and registered in new course
  */

public class InstStudentCourseList extends SecureScreen_Institute_Admin{
	/**
	  * Place all the data object in the context for use in the template.
	  * @param data RunData instance
	  * @param context Context instance
	  * @exception Exception, a generic exception
	  * @see UserUtil in utils
	  * @see UserGroupRoleUtil in utils
	  * @see GroupUtil in utils
	  * @see UserManagement in utils
	  * @see ListManagement in utils
	  *
	  */
	public void doBuildTemplate(RunData data, Context context){
		try{
			Vector students=new Vector();
			Vector g=new Vector();
			Criteria crit=new Criteria();
			/**
			 * Get the user name from url and find the user id
			 * @see UserUtil in utils
			 */
			String mode=data.getParameters().getString("mode","");
			 context.put("mode",mode);
			String uname=data.getParameters().getString("username");
		        context.put("username",uname);
			String counter=data.getParameters().getString("count","");
			context.put("tdcolor",counter);
			int uid=UserUtil.getUID(uname);
			String instituteId=data.getUser().getTemp("Institute_id").toString();
	 		/**
			 * Find all groupId according userid and roleid
			 * @see UserGroupRoleUtil in utils
			 * @see UserManagement in utils
			 */
			 Vector gid=new Vector();
                        if((mode.equals("sclist"))){
                         gid=UserGroupRoleUtil.getGID(uid,3);}
                        if((mode.equals("instlist"))){
                         gid=UserGroupRoleUtil.getGID(uid,2);}
                        List student=UserManagement.getUserDetail(Integer.toString(uid));
			for(int j=0;j<gid.size();j++){
	 			/**
			 	  * Find all groupname according groupid
			 	  * @see GroupUtil in utils
			 	  */
				String group_id=(String)(gid.elementAt(j));
				String gname=GroupUtil.getGroupName(Integer.parseInt(group_id));
				String grpInsId = StringUtils.substringAfterLast(gname,"_");
				//Below check is put by Richa to get institute wise grpname
				if(instituteId.equals(grpInsId))
	              			g.add(gname);
             		}
	 		/**
			  * Find all groupname
			  * @see ListManagement in utils
			  */
			//List courseList=CourseManagement.getInstituteCourseNUserDetails("All",instituteId);
			Vector courseList=InstituteDetailsManagement.getInstituteCourseDetails(instituteId); 
			//ErrorDumpUtil.ErrorLog("The mode value in CourseList"+courseList); 
			if(g.size()!=0)
			{
				context.put("student",student);
				context.put("groupname",g);
				context.put("CourseList",courseList);
			}
			else
			{
				context.put("CourseList",courseList);
				setTemplate(data,"call,UserMgmt_InstituteAdmin,InstStudentCourseList.vm");
			}
			/**
                        * getting institute id from temp & list of program for that institute 
                       	*/
			crit=new Criteria();
                        crit.add(InstituteProgramPeer.INSTITUTE_ID,Integer.parseInt(instituteId));
                        List Instplist= InstituteProgramPeer.doSelect(crit);
                        Vector PrgDetail = new Vector();
                        CourseUserDetail cDetails=new CourseUserDetail();
                        for(int i=0;i<Instplist.size();i++)
                        {
                                InstituteProgram element = (InstituteProgram)Instplist.get(i);
                                String PrgCode = element.getProgramCode();
                                String prgName = InstituteIdUtil.getPrgName(PrgCode);
				cDetails = new CourseUserDetail();
                                cDetails.setPrgName(prgName);
                                cDetails.setPrgCode(PrgCode);
                                PrgDetail.add(cDetails);
                        }
                        context.put("PrgDetail",PrgDetail);
			/**
                         * getting user rollno and program list 
                         */
			List userRollNo=CourseProgramUtil.getUserInstituteRollnoList(uname,instituteId);
			Vector rollprglist = new Vector();
                        for(int j=0;j<userRollNo.size();j++)
                        {
                                StudentRollno element = (StudentRollno)userRollNo.get(j);
                                String rollno = element.getRollNo();
                                String Program = element.getProgram();
				String Pgname = InstituteIdUtil.getPrgName(Program);
                                cDetails = new CourseUserDetail();
				cDetails.setPrgName(Pgname);
                                cDetails.setPrgCode(Program);
                                cDetails.setRollNo(rollno);
                                rollprglist.add(cDetails);
                        }
                        context.put("rlprglist",rollprglist);
			/**
                        * getting registered course, program and rollno list of the user 
                        */
                        Vector UsDetail = new Vector();
			ArrayList list = new ArrayList();
                        Map map = new HashMap();
                        String rlprgcode="",rl="",pgname="", pgcode="",cId="",CrsName="",CrsAlias="",CrsInstrName="";
                        List Crslist = CourseProgramUtil.getCourseRollnoDetail(uname,Integer.parseInt(instituteId));
			//ErrorDumpUtil.ErrorLog("List from InstitueStudentList screen----"+Crslist+"-----Crslist size"+Crslist.size());
                        for(ListIterator k = Crslist.listIterator();k.hasNext();)
       	                {
	       	                Record item = (Record)k.next();
	                        rl = item.getValue ("ROLL_NO").asString();
	                        //ErrorDumpUtil.ErrorLog("return value from execute query  :- "+rl);
	                        cId = item.getValue ("COURSE_ID").asString();
	                        //ErrorDumpUtil.ErrorLog("return value cid from execute query  :- "+cId);
	                        pgcode= item.getValue ("PROGRAM_CODE").asString();
	                        //ErrorDumpUtil.ErrorLog("return value pgr from execute query  :- "+pgcode);
	                        pgname = InstituteIdUtil.getPrgName(pgcode);
	                        //ErrorDumpUtil.ErrorLog("pgname from util :- "+pgname);
	                        if(cId.endsWith(instituteId)){
					map = new HashMap();
					map.put("rlno",rl);
					map.put("grpname",cId);
					map.put("pgcode",pgcode);
					map.put("pgname",pgname);
	                               	list.add(map);
				}
                        }
       	                context.put("UDetail",list);
		}
		catch (Exception e)
		{
			data.setMessage("The error is :-"+e);
		}
	}
}

