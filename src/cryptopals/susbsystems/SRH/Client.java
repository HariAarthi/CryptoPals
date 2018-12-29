package cryptopals.susbsystems.SRH;

import java.math.BigDecimal;

import cryptopals.validator.HMAC_SHA1_Validator;

public class Client {
	
	private String password ="password";
	private BigDecimal S;
	private String K;
	private Integer NIST_PRIME = 13;
	private int g = 2, k = 3;
	private Integer B;
	private Integer a = 14;
	private Integer A;
	private Server server;
	public byte[] salt;
	
	public void initiateConnectionWithServer() {
		A = (g ^ a) % NIST_PRIME;
		
		server = new Server();
		HandShakeInfo handShakeInfo = new HandShakeInfo();
		handShakeInfo.setEmailId("hari@cryptopals.com");
		handShakeInfo.setClient(this);
		handShakeInfo.setPublicKey(A);
		server.initiateConnection(handShakeInfo);
	}
	
	public void ack(AckInfo ackInfo) {
		SHA256 sha256 = new SHA256();
		salt = ackInfo.salt;
		String xH = sha256.getSHA256Hash(new String(salt)+password);
		Integer x = xH.hashCode(); //Integer.parseInt(xH);
		
		
		String uH = sha256.getSHA256Hash(A+""+ackInfo.publicKey);
		Integer u = uH.hashCode();//Integer.parseInt(uH);
		
		S = BigDecimal.valueOf(((((ackInfo.publicKey - (k * ((g ^ x) % NIST_PRIME))) ^ (a + (u * x)))) % NIST_PRIME) % NIST_PRIME);
		//S = (B - k * g**x)**(a + u * x) % N
		//S = (ackInfo.publicKey - k * g ^ x) ^ (a + u * x) % NIST_PRIME;
		K = sha256.getSHA256Hash(S.toEngineeringString() /*Integer.toHexString(S)*/);
		
		finalizeConnection();
	}
	
	public void finalizeConnection() {
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.hmac = HMAC_SHA1_Validator.calculateSignature(K + "" + new String(salt));
		boolean isFinalized = server.finalizeConnection(clientInfo);
		System.out.println("isFinalized : " + isFinalized);
	}
}
