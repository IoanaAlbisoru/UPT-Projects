package user;

import java.util.HashMap;
import java.util.Map;

public class Checker {
	String email = "";
	String nume = "";
    String prenume = "" ;
    String parola = "";
    String confirmareParola = "";
    String username = "";

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
    	//email = value;
        this.email = email.trim();
    }
    
    public String getNume() {
        return nume;
    }
    public void setNume(String nume) {
        this.nume = nume.trim();
    }
    
    
    public String getPrenume() {
        return prenume;
    }
    public void setPrenume(String prenume) {
        this.prenume = prenume.trim();
    }
    
    public String getParola() {
        return parola;
    }
    public void setParola(String parola) {
        this.parola = parola.trim();
    }
    
    public String getConfirmareParola() {
        return confirmareParola;
    }
    public void setConfirmareParola(String confirmareParola) {
        this.confirmareParola = confirmareParola.trim();
    }
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username.trim();
    }

    
    public static final Integer ERR_EMAIL_ENTER = new Integer(1);
    public static final Integer ERR_EMAIL_INVALID = new Integer(2);
    public static final Integer ERR_INVALID_NAME= new Integer(3);
    public static final Integer ERR_NO_NAME = new Integer(4);
    public static final Integer ERR_INVALID_SURNAME = new Integer(5);
    public static final Integer ERR_NO_SURNAME = new Integer(6);
    public static final Integer ERR_NO_USERNAME = new Integer(7);
    public static final Integer ERR_NO_PASS = new Integer(8);
    public static final Integer ERR_PASS_TOO_SHORT = new Integer(9);
    public static final Integer ERR_PASS_NOT_MATCH = new Integer(10);
    

    // Holds error messages
    Map errorCodes = new HashMap();

    // Maps error codes to text messages
        Map msgMap;
    public void setErrorMessages(Map msgMap) {
        this.msgMap = msgMap;
    }

    public String getErrorMessage(String propName) {
        Integer code = (Integer)(errorCodes.get(propName));
        if (code == null) {
            return "";
        } else if (msgMap != null) {
            String msg = (String)msgMap.get(code);
            if (msg != null) {
                return msg;
            }
        }
        return "Error";
    }

    /* Form validation and processing */
    public boolean isValid() {
        // Clear all errors
        errorCodes.clear();

       
        
        //Validate name
        if(nume.length() == 0){
        	errorCodes.put("nume", ERR_NO_NAME);
        } else if(!nume.matches("^[a-zA-Z]+$")){
        	errorCodes.put("nume", ERR_INVALID_NAME);
        }
        
        //Validate surname
        if(prenume.length() == 0){
        	errorCodes.put("prenume", ERR_NO_SURNAME);
        } else if(!prenume.matches("^[a-zA-Z]+$")){
        	errorCodes.put("nume", ERR_INVALID_SURNAME);
        }
        
        //Validate password regarding length>6
        if(parola.length() == 0){
        	errorCodes.put("parola", ERR_NO_PASS);
        } 
        if(parola.length() < 6){
        	errorCodes.put("parola", ERR_PASS_TOO_SHORT);
        }
        
        //Validate password match
        if(parola.length()!=confirmareParola.length()){
        	if(!parola.equals(confirmareParola))
        	{
        		errorCodes.put("confirmareParola", ERR_PASS_NOT_MATCH);
        	}
        }
        
        // Validate email
        if (email.length() == 0) {
            errorCodes.put("email", ERR_EMAIL_ENTER);
        } else if (!email.matches(".+@.+\\..+")) {
            errorCodes.put("email", ERR_EMAIL_INVALID);
        }

        // If no errors, form is valid
        return errorCodes.size() == 0;
    }

    public boolean process() {
        if (!isValid()) {
            return false;
        }

        // Process form...

        // Clear the form
      // email = "";
     //   nume = "" ;
      //  prenume = "" ;
       // parola = "";
       // confirmareParola ="";
      //  username = "";
      //  
        errorCodes.clear();
        return true;
    }
}

