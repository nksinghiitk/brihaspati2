package org.iitk.brihaspati.modules.utils;
/*
 * @(#)MarksView.java
 *
 *  Copyright (c) 2013 ETRG,IIT Kanpur. 
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
import java.util.Vector;
import java.util.List;
import org.apache.torque.util.Criteria;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.om.security.User;
import org.iitk.brihaspati.modules.screens.call.SecureScreen_Student;
import org.iitk.brihaspati.modules.utils.ErrorDumpUtil;
import org.iitk.brihaspati.om.StudentRollnoPeer;
import org.iitk.brihaspati.om.StudentRollno;
import org.iitk.brihaspati.om.ParentInfo;
import org.iitk.brihaspati.om.ParentInfoPeer;
import org.apache.commons.lang.StringUtils;
import org.iitk.brihaspati.modules.utils.UserUtil;
import org.iitk.brihaspati.modules.utils.MailNotificationThread;
import org.iitk.brihaspati.modules.utils.XMLWriter_Marks;
import org.iitk.brihaspati.modules.utils.MarksFileEntry;


public class MarksView{
	public static Vector getMarks(String semail,String dir) throws Exception{
		String rollno1=null,rollno2=null;
        	List v=null;
	        List pinfo=null;
                Vector markDetail=new Vector();
                Vector heading=new Vector();
                Vector Alias = new Vector();
                Vector Details = new Vector();

		 try
                {
	                Criteria crit=new Criteria();
                        crit.add(StudentRollnoPeer.EMAIL_ID,semail);
                        v=StudentRollnoPeer.doSelect(crit);
                        if(v.size()>0){
        	                StudentRollno element=(StudentRollno)v.get(0);
                                rollno1=element.getRollNo();
                                rollno2="";
                        	/**
	                         * Vector size greater than 1 shows that user have more 
        	                 * than 1 rollno then get another rollno 
                	         */
                        	 if(v.size()>1)
	                         {
        	          	       StudentRollno element1=(StudentRollno)v.get(1);
                	               rollno2=element1.getRollNo();
                        	}
	                }
              	}
                catch(Exception e)
                {
                	ErrorDumpUtil.ErrorLog("Error inside getting value in view marks"+e);
                }

                //Get Marks file name and alias information from xml file @Marks.xml
                String xmlPath=TurbineServlet.getRealPath("/Courses")+"/"+dir+"/Marks.xml";
                Vector marksfile = XMLWriter_Marks.ReadMarksDeatils(xmlPath);
                String tempfilePath=TurbineServlet.getRealPath("/Courses")+"/"+dir+"/Marks";
                //ErrorDumpUtil.ErrorLog("tempfilePath ==== "+ tempfilePath);
                File MarksDir=new File(tempfilePath);
                String[] listOfFiles = MarksDir.list();

                        // this block of code is responsible to read all files 
                        // from Marks directory and checks if user's 
                        // roll number exists or not ,if exists then show their 
                        // results according to alias name
                        if(v.size()>0){
                                for (int i=0; i<marksfile.size(); i++){
                                        MarksFileEntry MarksfileEntry=(MarksFileEntry)marksfile.get(i);
                                        String alias=MarksfileEntry.getAlias().toString();
                                        String tempfilename=MarksfileEntry.getFileName().toString();
                                        String filename=alias+"-"+tempfilename;
                                        String filePath=TurbineServlet.getRealPath("/Courses")+"/"+dir+"/Marks/"+filename;
                        		//ErrorDumpUtil.ErrorLog("tempfilePath inside vector check==== "+ filePath);

                                        FileReader fr=new FileReader(filePath);
                                        BufferedReader br=new BufferedReader(fr);
                                        String line;
                                        StringTokenizer sTokenizer;
                                        while((line=br.readLine())!=null)
                                        {
                                                sTokenizer=new StringTokenizer(line,",");
                                                try{
                                                        String userName=sTokenizer.nextToken().trim();
                                                        if(rollno1.equals(userName)||rollno2.equals(userName)){
                                                        //      String str = StringUtils.substringBeforeLast(filename,"-");
                                                                //String str[] = filename.split("-");
                                                                Alias.add(alias);

                                                                FileReader freader1 = null;
                                                                LineNumberReader lnreader1 = null;
                                                                File   file1 = new File(filePath);
                                                                freader1 = new FileReader(file1);
                                                                lnreader1 = new LineNumberReader(freader1);
                                                                String line1 = "";
                                                                while ((line1 = lnreader1.readLine()) != null){
                                                                        StringTokenizer Tokenizer=new StringTokenizer(line1,",");
                                                                        Vector heading1=new Vector();
                                                                        Tokenizer.nextToken();
                                                                        while(Tokenizer.hasMoreTokens())
                                                                        {
                                                                                heading1.addElement(Tokenizer.nextToken());
                                                                        }
                                                                        heading.add(heading1);

                                                                        break;
                                                                }
                                                                Vector markDetail1=new Vector();
                                                                while(sTokenizer.hasMoreTokens())
                                                                {
                                                                        markDetail1.addElement(sTokenizer.nextToken());
                                                                }
                                                                br.close();
                                                                markDetail.add(markDetail1);
                                                                break;
                                                        }
                                                }
                                                catch(Exception e){
                                                        ErrorDumpUtil.ErrorLog("The Error in View marks Part "+e);
                                                }
                                        }
                                }
                                ErrorDumpUtil.ErrorLog("The value of marks detail vector is "+ markDetail.toString());
                        }// end of if
	Details.add(Alias);
	Details.add(heading);
	Details.add(markDetail);
	
	return Details;
        }
}


	/*public static String getStudent(String pemail) throws Exception{
		String semail=null;
		List pinfo = null;
		Criteria mark=new Criteria();
                	mark.add(ParentInfoPeer.PARENT_ID,pemail);
                        pinfo = ParentInfoPeer.doSelect(mark);
                        for(int i=0;i<pinfo.size();i++){
                        	ParentInfo ele=(ParentInfo)pinfo.get(i);
                                semail = ele.getStudentId();
                        }
		return semail;
	}*/


