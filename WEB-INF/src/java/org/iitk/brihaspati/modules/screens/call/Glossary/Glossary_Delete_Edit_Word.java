package org.iitk.brihaspati.modules.screens.call.Glossary;

/*
 *  @(#)Glossary_Delete_Edit_Word.java

 *  Copyright (c) 2005-2006,2009 ETRG,IIT Kanpur.
 *  All Rights Reserved.

 *  Redistribution and use in source and binary forms, with or
 *  without modification, are permitted provided that the following
 *  conditions are met:

 *  Redistributions of source code must retain the above copyright
 *  notice, this  list of conditions and the following disclaimer.

 *  Redistribution in binary form must reproducuce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.


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

 *  Contributors: Members of ETRG, I.I.T. Kanpur 

*/

import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import org.apache.turbine.util.RunData;
import org.apache.turbine.om.security.User;
import org.apache.torque.util.Criteria;
import org.apache.velocity.context.Context;

import org.iitk.brihaspati.om.GlossaryPeer;
import org.iitk.brihaspati.modules.utils.UserUtil;
import org.iitk.brihaspati.modules.utils.MultilingualUtil;
import org.iitk.brihaspati.modules.screens.call.SecureScreen;

public class Glossary_Delete_Edit_Word extends SecureScreen
{
 
 /* This class contain the code for getting word and their wordid according 
  * to particular alphabate.
  */
 /* @author <a href="mailto:nksngh_p@yahoo.co.in">Nagendra Kumar Singh</a>
  * @author <a href="mailto:sharad23nov@yahoo.com">Sharad Singh</a>
  * @author <a href="mailto:singh_jaivir@rediffmail.com">Jaivir Singh</a>
  *
  */
 
 public void doBuildTemplate(RunData data,Context context)
  {
   /**
    *  place all data object in the context
    *  for use in the template.
    *  @param data RunData.
    *  @param context Context.
    *  
    */  
  
   try
    {
	User user=data.getUser();
	String username=user.getName();
                if(username.equals("guest"))
                context.put("guest_login","true");
                else
                context.put("guest_login","false");	
        String cuid=String.valueOf(UserUtil.getUID(username));
	context.put("currentuid",cuid);
	context.put("tdcolor",data.getParameters().getString("count",""));
	String word = data.getParameters().getString("alpha","");
     	context.put("alpha",word);
	String LangFile=(String)data.getUser().getTemp("LangFile");
	word=MultilingualUtil.ConvertedAlphabates(word, LangFile);
     	String status = data.getParameters().getString("status");
     	context.put("stat",status);

     // Get the wordlist and their corresponding wordid according to a alphabate.

     	if(word !=""){
   		Criteria crit = new Criteria(); 
		crit.add(GlossaryPeer.WORD,(Object)(word+"%"), crit.LIKE);

		List u=GlossaryPeer.doSelect(crit);
	
		if(u.size()!=0)
			{context.put("v",u);}
		else if((status !="")&&(word !=null)) 
     		{
		        context.put("stat","blank");
                        String msg=MultilingualUtil.ConvertedString("GlossaryMsg9",LangFile);
                        data.setMessage(msg + word);
		       // data.setMessage("No Word Exist Regarding Alphabate:-" + word);
		}
	}
   }
   catch(Exception e){data.addMessage("Error in Delete Glossary screen !!"+e);}
  }
}
 
		                   
