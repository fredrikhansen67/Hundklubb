/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.tools.examples.controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Remove;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.tools.examples.data.DogListProducer;
import org.jboss.tools.examples.data.MemberRepository;
import org.jboss.tools.examples.model.DefinedDate;
import org.jboss.tools.examples.model.Member;
import org.jboss.tools.examples.service.MemberRegistration;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://www.cdi-spec.org/faq/#accordion6
@SuppressWarnings("serial")
@Named
@SessionScoped
public class MemberController implements Serializable {
	
	@Named
	private String pwdCheck="";

    public String getPwdCheck() {
		return pwdCheck;
	}


	public void setPwdCheck(String pwdCheck) {
		this.pwdCheck = pwdCheck;
	}

	@Inject
    private FacesContext facesContext;

    @Inject
    private MemberRegistration memberRegistration;

    @Produces
    @Named
    private Member newMember;

    @PostConstruct
    public void initNewMember() {
        newMember = new Member();
        
    }
    
    
    
    @Inject 
    MemberRepository memRep;
    
    public boolean validate(String user, String pass){
    	try{
    		newMember = memRep.findByEmail(user);
    	}catch(Exception e){System.out.println("No members found  "+e);}
    	if (newMember.getEmail()!=null){
    		System.out.println("NewMember found "+newMember.getEmail());
    		if(newMember.getPassWord().equals(pass)){
    			return true;
    		}
    		else
    			return false;    
    	}
    	return false;
    }
    

    	public void killSession() throws IOException {	
    		 ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    		 ec.invalidateSession();
    		 ec.redirect(ec.getRequestContextPath() + "/index.xhtml");
    		 initNewMember(); 
//    		 FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
  			
  		}
    
  		
    
   
    public boolean validateRetypedPassword(String pass, String passCheck){    		
       	return pass.equals(passCheck);
    }
    
    public String getMemberName() {
    	return newMember.getfName();
    }
    public String getMemberEmail() {
    	return newMember.getEmail();
    }
    

    
    public void register() throws Exception {
    	if (validateRetypedPassword(newMember.getPassWord(), this.pwdCheck)){
    		
    		newMember.setStartDate(new DefinedDate());
    	   try {
	            memberRegistration.register(newMember);
	            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
	            facesContext.addMessage(null, m);
	            initNewMember();
	        } catch (Exception e) {
	            String errorMessage = getRootErrorMessage(e);
	            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
	            facesContext.addMessage(null, m);
	        }
    	} else {
    		String errorMessage = "Passwords must match each other";
    		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
        	facesContext.addMessage(null, m);
    	}	
    	
    }

    private String getRootErrorMessage(Exception e) {
        // Default to general error message that registration failed.
        String errorMessage = "Registration failed. See server log for more information";
        if (e == null) {
            // This shouldn't happen, but return the default messages
            return errorMessage;
        }

        // Start with the exception and recurse to find the root cause
        Throwable t = e;
        while (t != null) {
            // Get the message from the Throwable class instance
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        // This is the root cause message
        return errorMessage;
    }

}
