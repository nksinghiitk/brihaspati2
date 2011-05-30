package org.iitk.brihaspati.modules.screens.call.OLES;

/*
 * @(#)AttemptPractice_Quiz.java	
 *
 *  Copyright (c) 2010-2011 MHRD, DEI Agra. 
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
 *  Contributors: Members of MHRD, DEI Agra.
 * 
 */

import java.util.Calendar;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.iitk.brihaspati.modules.screens.call.SecureScreen;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.turbine.om.security.User;
import org.iitk.brihaspati.modules.utils.TopicMetaDataXmlReader;
import org.iitk.brihaspati.modules.utils.ErrorDumpUtil;
import org.iitk.brihaspati.modules.utils.QuizFileEntry;
import org.iitk.brihaspati.modules.utils.QuizUtil;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.io.File;
import java.util.StringTokenizer;
import org.iitk.brihaspati.modules.utils.MultilingualUtil;
import org.iitk.brihaspati.modules.utils.GroupUtil;
import org.iitk.brihaspati.modules.utils.ExpiryUtil;
import org.iitk.brihaspati.modules.utils.ListManagement;
import org.iitk.brihaspati.modules.utils.AdminProperties;
import org.iitk.brihaspati.modules.screens.call.SecureScreen;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.iitk.brihaspati.modules.utils.QuizMetaDataXmlReader;
import org.iitk.brihaspati.modules.utils.QuizMetaDataXmlWriter;
import org.iitk.brihaspati.modules.utils.XmlWriter;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.iitk.brihaspati.modules.utils.UserGroupRoleUtil;
import org.iitk.brihaspati.modules.utils.GroupUtil;
import org.iitk.brihaspati.modules.utils.UserUtil;
import org.iitk.brihaspati.modules.utils.DbDetail;
import org.iitk.brihaspati.modules.utils.ListManagement;
import org.iitk.brihaspati.om.QuizPeer;
import org.iitk.brihaspati.om.Quiz;

/**
 *   This class contains code for attempt quiz part of student
 *   @author  <a href="noopur.here@gmail.com">Nupur Dixit</a>
 */
public class AttemptPractice_Quiz extends SecureScreen
{
	static int msg = 0;
	public void doBuildTemplate( RunData data, Context context )
	{
		ParameterParser pp=data.getParameters();
		try{
			 		    
			User user=data.getUser();
			String uname=user.getName();
			Vector<QuizFileEntry> questionVector = (Vector)user.getTemp("questionvector"); 
			/*freshquiz variable is to check-if student is attempting the fresh quiz(to delete the 
			content of answer xml file to store the fresh answers )			
			*/
			String freshQuiz = (String)user.getTemp("freshQuiz","yes"); 
			ErrorDumpUtil.ErrorLog("value of fresh quiz ::"+user.getTemp("freshQuiz"));
			String LangFile=user.getTemp("LangFile").toString();
			String loginname=user.getName();           
			String uid=Integer.toString(UserUtil.getUID(uname));
			String cid=(String)user.getTemp("course_id");
			String Role=(String)user.getTemp("role");
			context.put("user_role",Role);
			
//			String maxMarks = pp.getString("maxMarks","");			
//			String maxQuestion = pp.getString("maxQuestion","");
//			ErrorDumpUtil.ErrorLog("\ninside attemptpracticequiz.java max marks and maxquestion"+maxMarks +" "+maxQuestion);
//			if(maxMarks.equals("") || maxQuestion.equals("")){				
//			}
//			else{
//				context.put("maxMarks",maxMarks);
//				context.put("maxQuestion",maxQuestion);
//			}
			
			
			
			String quizName = pp.getString("quizName","");
			context.put("quizName",quizName);
			String quizID="";
			String maxTime="";
			int totalMarks =0;		
			String count = pp.getString("count","");
			context.put("tdcolor",count);			
			String quizIDTime = pp.getString("quizIDTime","");
			if(!quizIDTime.isEmpty()){
				String quizIDTimeArray[] = quizIDTime.split(",");				                               	
				quizID = quizIDTimeArray[0];        		
				maxTime = quizIDTimeArray[1];        						
			}
			else{
				quizID = pp.getString("quizID","");
				maxTime = pp.getString("maxTime","");
			}
			context.put("quizID",quizID);
			context.put("maxTime",maxTime);
			String quesID,fileName,quesType,markPerQues;
			quesID=fileName=quesType=markPerQues="";
			quesType = pp.getString("quesType","");
			String quesDetail = pp.getString("quesDetail","");
			ErrorDumpUtil.ErrorLog("getting parameter of quesDetail"+quesDetail);
			
			if(quesDetail.isEmpty()){ 
				markPerQues = pp.getString("markPerQues","");            
				quesID = pp.getString("quesID","");
				fileName = pp.getString("fileName","");
			}
			else{   
				String queDetail[] = quesDetail.split(",");				                               	
				quesID = queDetail[0];        		
				fileName = queDetail[1];        		
				quesType = queDetail[2];
				markPerQues = queDetail[3];
			}            
			context.put("quesID",quesID); 
			context.put("fileName",fileName); 
			context.put("quesType",quesType);
			context.put("markPerQues",markPerQues);
			String question = pp.getString("question","");
			context.put("question",question);
			String option1 = pp.getString("option1","");
			context.put("option1",option1);
			String option2 = pp.getString("option2","");
			context.put("option2",option2);
			String option3 = pp.getString("option3","");
			context.put("option3",option3);
			String option4 = pp.getString("option4","");
			context.put("option4",option4);
			String finalAnswer = pp.getString("finalAnswer","");
			context.put("finalAnswer",finalAnswer);

			ErrorDumpUtil.ErrorLog("\n final answer is "+finalAnswer);
			
			String answerFilePath=TurbineServlet.getRealPath("/Courses"+"/"+cid+"/Exam/"+quizID+"/");
			String answerPath=uid+".xml";
			Vector quizList=new Vector();
			Vector answerList=new Vector();
			QuizMetaDataXmlReader quizmetadata=null;
			/*
			 * coding to get maxquestion and maxmarks
			 */
			
//			String filePath=data.getServletContext().getRealPath("/Courses"+"/"+courseid+"/Exam/"+quizID+"/");
			String questionSettingPath=quizID+"_QuestionSetting.xml";
//			QuizMetaDataXmlReader quizmetadata=null;
//			Vector allQuizSetting=new Vector();
			File f=new File(answerFilePath+"/"+questionSettingPath);									
				if(!f.exists()){
					data.setMessage("No questions are stored to attempt");	
					return;
				}
//            	else{            				
            		quizmetadata=new QuizMetaDataXmlReader(answerFilePath+"/"+questionSettingPath);	
            		HashMap hm = new HashMap();
            		hm = quizmetadata.getQuizQuestionNoMarks(quizmetadata,quizID);
            		int mark =((Integer)hm.get("marks"));
            		int enteredQuestions = ((Integer)hm.get("noQuestion"));             		
            		context.put("maxQuestion",enteredQuestions);	
					context.put("maxMarks",mark);
					
			/*
			 * back side timer implementation
			 */
			int maxtime; 							
			if(maxTime.indexOf(":")==-1){
				ErrorDumpUtil.ErrorLog("\n inside -1");
				 maxtime=Integer.parseInt(maxTime);
			}
			else{
				String maxtimeArray[] = maxTime.split(":");
				ErrorDumpUtil.ErrorLog("\n inside -1 else after split");
				maxtime=Integer.parseInt(maxtimeArray[0]);
			}
			Timer timer = new Timer();
		    timer.schedule(new TimerTask(){
		    	public void run(){
		    		Attempt_Quiz.msg = 1;
		    		ErrorDumpUtil.ErrorLog("msg value inside run:"+Attempt_Quiz.msg);
		    	}
		    }, (maxtime*60) * 1000);
		    
		    if(msg==1){		    	
		    	data.setMessage("Oops! Your Quiz Time is over");
		    	data.setScreenTemplate("call,OLES,Student_Quiz.vm");
		    }	
			/*
			 * this block is used to set timer value in session so that if student refresh the
			 * page timer is not restarted from the starting point
			 */
			String timerValue = pp.getString("timerValue","");
			String timerValueSession = (String)user.getTemp("timerValue"); 
			ErrorDumpUtil.ErrorLog("\n timer value and timersession"+timerValue+":"+timerValueSession);
			if(timerValue==null || timerValue.equalsIgnoreCase("")){
				if(timerValueSession==null || timerValueSession.equalsIgnoreCase("")){
					context.put("timerValue",maxTime);						
				}
				else{						
					context.put("timerValue",timerValueSession);
				}

			}
			else{
				context.put("timerValue",timerValue);
				user.setTemp("timerValue",timerValue);					
			}
			//this part is used to show shuffed list of questions
			if(questionVector!=null && questionVector.size()!=0){
				context.put("quizQuestionList",questionVector); 
			}
			//this part is used to show already inserted answers
			if(freshQuiz.equalsIgnoreCase("yes")){
				File answerFile=new File(answerFilePath+"/"+answerPath);
				if(!answerFile.exists()){}
				else{
					answerFile.delete();
				}
				user.setTemp("freshQuiz","no");
			}
			else{
				File answerFile=new File(answerFilePath+"/"+answerPath);
				if(!answerFile.exists()){			
				}
				else{
					quizmetadata=new QuizMetaDataXmlReader(answerFilePath+"/"+answerPath);
					answerList=quizmetadata.getFinalAnswer();
					if(answerList!=null && answerList.size()!=0){				
						context.put("answerList",answerList); 							
					}	
				}
			}
		}
		catch(Exception e)
		{
			ErrorDumpUtil.ErrorLog("The exception in attempt_quiz ::"+e);
			data.setMessage("See ExceptionLog !! ");
		}
	}
}

