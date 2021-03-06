package org.iitk.brihaspati.modules.screens;

/*
 * @(#)remPass.java	
 *
 *  Copyright (c) 2012 ETRG,IIT Kanpur. 
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

import org.apache.torque.util.Criteria;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.commons.lang.StringUtils;

import org.iitk.brihaspati.om.RemoteUsersPeer;
import org.iitk.brihaspati.om.RemoteUsers;

import org.iitk.brihaspati.modules.utils.ErrorDumpUtil;

import org.iitk.brihaspati.modules.utils.security.EncrptDecrpt;
import org.iitk.brihaspati.modules.utils.security.ReadNWriteInTxt;
import org.iitk.brihaspati.modules.utils.security.RemoteAuthProperties;

/**
 * @author <a href="mailto:nksinghiitk@gmail.com">Nagendra Kumar singh</a>
 */
public class remPass extends VelocityScreen
{
    /**
     * Place all the data object in the context
     * for use in the template.
     */
    public void doBuildTemplate( RunData data, Context context )
    {
	boolean flag = false;
	System.gc();
	List v=null;
        try{
		String lang=data.getParameters().getString("lang","english");
                        context.put("lang",lang);
		String email=data.getParameters().getString("email");
		context.put("email",email);
                String randomNo=data.getParameters().getString("sess") ;
                String hash=data.getParameters().getString("hash");
		String retUrl=null;
		String srcid=null;
		String remoteUrl=null;
		String hdir=System.getProperty("user.home");
		String path=hdir+"/remote_auth/brihaspati3-remote-server.properties";

// get url and source id form db
		Criteria crit=new Criteria();
		crit.add(RemoteUsersPeer.USERID,email);
		crit.add(RemoteUsersPeer.RANDOMKEY,randomNo);
		v=RemoteUsersPeer.doSelect(crit);
		ErrorDumpUtil.ErrorLog("The size of vector for that user "+v.size() + randomNo +email);
 		
		for(int i=0;i<v.size();i++){
                	RemoteUsers ur=(RemoteUsers)v.get(i);
                        retUrl=ur.getApplication();
                      	srcid=ur.getSourceid();
               }

                String skey="";
		String line=ReadNWriteInTxt.readLin(path,srcid);
                skey=StringUtils.substringBetween(line,";",";");
              //  url=StringUtils.substringAfterLast(line,";");

	//	String hkeyR="email="+email+";random="+randomNo+";secret="+skey+";";
                String hashcodeR=EncrptDecrpt.keyedHash(email,randomNo,skey);

		if (hash.equals(hashcodeR)){
			ErrorDumpUtil.ErrorLog("The value of ret url from db in rem pass  "+retUrl);
			if((v.size()>0) && (v.size()<2)){
				String url=retUrl+"?email="+email+"&sess="+randomNo;
				context.put("url",url);	
			ErrorDumpUtil.ErrorLog("The value of url in rem pass screen "+url);
			}	
			else{
				remoteUrl=retUrl+"?msg=You are not coming from authentic client";
	                        try{
        	                	data.getResponse().sendRedirect(remoteUrl);
                	        }
                        	catch (Exception ex){
					ErrorDumpUtil.ErrorLog("You are not coming from authentic client "+ex);
                        	}

			}
		}
		else{
                        ErrorDumpUtil.ErrorLog("The hash is not matched ");
			remoteUrl=retUrl+"?msg=You are not coming from authentic client";
                        try{
	                        data.getResponse().sendRedirect(remoteUrl);
                        }
                        catch (Exception ex){
        	                ErrorDumpUtil.ErrorLog("The hash is not matched "+ex);
                        }

		}
	}
        catch(Exception e)
	{
		data.setMessage("The Error in Global remote password Page !!"+e);
	}

    }
}
