package cryptopals.susbsystems.SRH;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.RandomUtils;

import cryptopals.utils.ArrayUtils;
import cryptopals.validator.HMAC_SHA1_Validator;

public class Server {
	
	private byte[] salt = RandomUtils.nextBytes(16);//new ArrayUtils().createInitializedArray(16, (byte)1);
	private int g = 2, k = 3;
	private Integer NIST_PRIME = 13;
	private Integer B;
	private int b = 16;
	private BigDecimal S;
	private String K;
	
	public void initiateConnection(HandShakeInfo handShakeInfo) {
		SHA256 sha256 = new SHA256();
		UserAccount account = getUserAccountFor(handShakeInfo.getEmailId());
		String xH = sha256.getSHA256Hash(new String(salt)+account.getPassword());
		Integer x = xH.hashCode(); //Integer.parseInt(xH);
		int v = (g ^ x.intValue()) % NIST_PRIME;
		v = v % NIST_PRIME;
		account.setV(v);
		//B=kv + g**b % N
		this.B =(((g ^ b) % NIST_PRIME) + ( k * v )) % NIST_PRIME;
		
		String uH = sha256.getSHA256Hash(handShakeInfo.getPublicKey()+""+B);
		Integer u = uH.hashCode();//Integer.parseInt(uH);

		S = BigDecimal.valueOf(((((v ^ u) % NIST_PRIME) * handShakeInfo.getPublicKey()) ^  b) % NIST_PRIME);
		//S = (handShakeInfo.getPublicKey() * v ^ u) ^ b % NIST_PRIME;
		K = sha256.getSHA256Hash(S.toEngineeringString() /*Integer.toHexString(S)*/);
		
		AckInfo ackInfo = new AckInfo(salt,B);
		handShakeInfo.getClient().ack(ackInfo);
	}
	
	public boolean finalizeConnection(ClientInfo clientInfo) {
		byte[] hmac = HMAC_SHA1_Validator.calculateSignature(K + "" + new String(salt));
		return Arrays.equals(clientInfo.hmac, hmac);
	}

	private UserAccount getUserAccountFor(String emailId) {
		return UserAccount.fetchUserAccount(emailId);
	}

}
