package org.iitk.brihaspati.modules.screens.call.UserMgmt_InstituteAdmin;

/*
 * @(#)InstituteUserForm.java	
 *
 *  Copyright (c) 2010,2011,2012 ETRG,IIT Kanpur. 
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
import java.util.List;
import java.util.ListIterator;
import com.workingdogs.village.Record;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.torque.util.Criteria;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.parser.ParameterParser;
import org.iitk.brihaspati.om.StudentRollno;
import org.iitk.brihaspati.om.InstituteProgramPeer;
import org.iitk.brihaspati.om.InstituteProgram;
import org.iitk.brihaspati.om.CourseProgram;
import org.iitk.brihaspati.modules.screens.call.SecureScreen_Institute_Admin;
import org.iitk.brihaspati.modules.utils.UserManagement;
import org.iitk.brihaspati.modules.utils.CourseProgramUtil;
import org.iitk.brihaspati.modules.utils.UserUtil;
import org.iitk.brihaspati.modules.utils.CourseUtil;
import org.iitk.brihaspati.modules.utils.GroupUtil;
import org.iitk.brihaspati.modules.utils.ErrorDumpUtil;
import org.iitk.brihaspati.modules.utils.CourseUserDetail;
import org.iitk.brihaspati.modules.utils.InstituteIdUtil;
import org.iitk.brihaspati.modules.utils.InstituteDetailsManagement;
import org.iitk.brihaspati.modules.utils.UserGroupRoleUtil;
/**
  * This class contains code for edit user profile according specific username
  * Grab all the records in a table using a Peer, and
  * place the List of data objects in the context
  * where they can be displayed by a #foreach loop.
  *
  * @author  <a href="singh_jaivir@rediffmail.com">Jaivir Singh</a>
  * @author  <a href="sharad23nov@yahoo.com">Sharad Singh</a>
  * @author  <a href="richa.tandon1@gmail.com">Richa Tandon</a>
  * @modified date:20-10-2010, 05-08-2011, 28-12-2011(Richa),30-10-2012(Richa)
  */
public class InstituteUserForm extends SecureScreen_Institute_Admin{
	 /**
	   * Place all the data object in the context for use in the template.
	   * @param data RunData instance
	   * @param context Context instance
	   * @exception Exception, a generic exception
	   */ 
	public void doBuildTemplate(RunData data, Context context){
		try{
			ParameterParser pp=data.getParameters();
			String userName=pp.getString("username");
			String modetype=pp.getString("Process");
			String mode=pp.getString("mode");
			String counter=pp.getString("count","");
			String InstId = (String)data.getUser().getTemp("Institute_id");
			context.put("tdcolor",counter);
			int uid=UserUtil.getUID(userName);
			List userList=UserManagement.getUserDetail(Integer.toString(uid));
			/**
 			 * Get detail of user rollno   
 			 */ 		
			List userRollNo=CourseProgramUtil.getUserInstituteRollnoList(userName,InstId);
			CourseUserDetail cDetails=new CourseUserDetail();
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
			Vector UsDetail = new Vector();
			ArrayList list = new ArrayList();
			Map map = new HashMap();
                	String rl="",CrsId="",CrsName="",CrsAlias="",CrsInstrName="",pgcode="",pgname="";
			/**
 			 * get user course program detail
 			 * @see CourseProgramUtil in utils  
 			 * then context put to display in vm 
 			 */ 		
			List Crslist = CourseProgramUtil.getCourseRollnoDetail(userName,Integer.parseInt(InstId));
			//ErrorDumpUtil.ErrorLog("CrsList in screen file for student==="+Crslist.size()+"Crslist>>>>>"+Crslist);
			if(Crslist.size()>0)
			{
	                	for(ListIterator k = Crslist.listIterator();k.hasNext();)
		                {
		 	                Record item = (Record)k.next();
	        	                rl = item.getValue ("ROLL_NO").asString();
	                	        //ErrorDumpUtil.ErrorLog("return value from execute query  :- "+rl);
		                        CrsId = item.getValue ("COURSE_ID").asString();
		                	//ErrorDumpUtil.ErrorLog("return value cid from execute query  :- "+CrsId);
	        	                String Insid = item.getValue ("INSTITUTE_ID").asString();
		                        //ErrorDumpUtil.ErrorLog("return value Insid from execute query  :- "+Insid);
        		                pgcode= item.getValue ("PROGRAM_CODE").asString();
	        	                //ErrorDumpUtil.ErrorLog("return value pgr from execute query  :- "+pgcode);
	                	        pgname = InstituteIdUtil.getPrgName(pgcode);
		                        //ErrorDumpUtil.ErrorLog("pgname from util :- "+pgname);
		                        //This chk is put to get institutewise course, program and rollno detail of user
		                        if(CrsId.endsWith(InstId)){
						CrsName = CourseUtil.getCourseName(CrsId);
						CrsAlias = CourseUtil.getCourseAlias(CrsId);
						CrsInstrName= CourseProgramUtil.getCourseInstructorName(CrsId);
						map = new HashMap();
						map.put("rlno",rl);
						map.put("grpname",CrsId);
						map.put("pgcode",pgcode);
						map.put("pgname",pgname);
						map.put("crsnm",CrsName);
						map.put("crsals",CrsAlias);
						map.put("crsInst",CrsInstrName);
						list.add(map);
					}
				}
			}	
		        context.put("UDetail",list);
			/**
 			 * getting list of registered course of user for that institute 
 			 */ 
			//Vector courseList=InstituteDetailsManagement.getInstituteCourseDetails(InstId);
			Vector courseList = UserGroupRoleUtil.getGID(uid,3);
			//ErrorDumpUtil.ErrorLog("courselist in screen file==="+courseList);
			Vector CourseDetail = new Vector();
			Vector CrsDescrp = new Vector();
			for(int k=0;k<courseList.size();k++)
			{
				String gid = (String)courseList.elementAt(k);
				String gname = GroupUtil.getGroupName(Integer.parseInt(gid));
				if(gname.endsWith(InstId))
				{
					CrsDescrp.add(gname);
				}
			}
			CourseDetail= InstituteDetailsManagement.getGroupCourseDetails(CrsDescrp);
			/**
 			 * getting institute id from temp & list of program for that institute 
 			 */ 
			Criteria crit=new Criteria();
	                crit.add(InstituteProgramPeer.INSTITUTE_ID,Integer.parseInt(InstId));
	                List Instplist= InstituteProgramPeer.doSelect(crit);
	                Vector PrgDetail = new Vector();
	                for(int i=0;i<Instplist.size();i++)
	                {
		                InstituteProgram element = (InstituteProgram)Instplist.get(i);
	                        String PrgCode = element.getProgramCode();
	                        String prgName = InstituteIdUtil.getPrgName(PrgCode);
	                        cDetails=new CourseUserDetail();
	                        cDetails.setPrgName(prgName);
	                        cDetails.setPrgCode(PrgCode);
				PrgDetail.add(cDetails);
			}
                        context.put("PrgDetail",PrgDetail);
			context.put("udetail",userList);
			context.put("urollno",userRollNo);
			context.put("Process",modetype);
			context.put("mode",mode);
			String from=pp.getString("from","");
			context.put("from",from);
			context.put("CourseList",CourseDetail);
		}
		catch (Exception e){
			data.setMessage("The error in user id :- "+e);
		}
	}
}

