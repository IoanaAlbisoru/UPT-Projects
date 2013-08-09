package user;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoginPage {

	//ArrayList<UserDetails> pDetails = new ArrayList<UserDetails>();
	 UserDetails pers = new UserDetails("anca","abc");
	String username = "";
	String password = "";
	String filename = "person.txt";

	
	ArrayList<UserDetails> pDetails = null;
	FileInputStream fis = null;
	ObjectInputStream in = null;
	
	 public String getUsername() {
	        return username;
	    }
	    public void setUsername(String username) {
	        this.username = username.trim();
	    }
	    
	    public String getPassword() {
	        return password;
	    }
	    public void setPassword(String password) {
	        this.password = password.trim();
	    }
	
	 public static final Integer ERR_USER_PASS = new Integer(1);
	 public static final Integer ERR_NO_DATA = new Integer(2);
	 public static final Integer ERR_SUCCES = new Integer(2);
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
    
    public boolean isValid2() {
        // Clear all errors
        errorCodes.clear();
    
		
        if(username.length() == 0){
        	errorCodes.put("username", ERR_NO_DATA);
        }
       /* if(username.equals(pers.getUsername())){
        	errorCodes.put("username", ERR_SUCCES);
        	*/
       
        
        return errorCodes.size() == 0;
    }
    
  
	public boolean process2() {
        if (!isValid2()) {
            return false;
        }
        errorCodes.clear();
        return true;
    }
}
