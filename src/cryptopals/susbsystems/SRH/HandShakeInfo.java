package cryptopals.susbsystems.SRH;

public class HandShakeInfo {
	private String emailId;
	private Integer publicKey;
	private Client client;
	
	public String getEmailId() {
		return emailId;
	}
	
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public Client getClient() {
		return this.client;
	}
	
	public Integer getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(Integer publicKey) {
		this.publicKey = publicKey;
	}
}
