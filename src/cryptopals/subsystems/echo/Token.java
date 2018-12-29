package cryptopals.subsystems.echo;

public class Token {
	
	private byte[] sessionKey;
	
	public void setSessionKey(byte[] sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	public byte[] getSessionKey() {
		return sessionKey;
	}

}
