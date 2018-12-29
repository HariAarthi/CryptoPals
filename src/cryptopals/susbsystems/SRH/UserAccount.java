package cryptopals.susbsystems.SRH;

public class UserAccount {
	private String emailId;
	private int v;
	
	public UserAccount(String emailId) {
		this.emailId = emailId;
	}
	
	public String getPassword() {
		return "password";
	}
	
	public void setV(int incomingV) {
		this.v = incomingV;
	}
	
	public static UserAccount fetchUserAccount(String emailId) {
		UserAccount account =  new UserAccount(emailId);
		return account;
	}
}
