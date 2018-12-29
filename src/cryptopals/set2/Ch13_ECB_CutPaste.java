package cryptopals.set2;

import java.util.Random;

import javax.crypto.Cipher;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cryptopals.utils.ArrayUtils;
import cryptopals.utils.CipherUtils;

@JsonPropertyOrder({ "emailId", "uid", "role"})
class UserProfile {
	
	private String emailId, role;
	private int uid;
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public String toString() {
		return "email="+emailId+"&uid="+uid+"&role="+role;
	}
}

public class Ch13_ECB_CutPaste {
	
	public static byte[] randomKey = new byte[16];
	public static Cipher cipher = null;
	
	static {
		new Random().nextBytes(randomKey);
	}
	
	/*public static String getEncodedString(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.readTree(jsonString).
		return profile.toString();
	}*/
	
	public static byte[] encrypt(byte[] text) throws Exception {
		cipher = CipherUtils.getCipher("AES","AES/ECB/NoPadding",true,randomKey);
		return cipher.doFinal(Ch9_PKCS7_Padding.padString(text, cipher.getBlockSize()));
	}
	
	public static byte[] decrypt(byte[] text) throws Exception {
		cipher = CipherUtils.getCipher("AES","AES/ECB/NoPadding",false,randomKey);
		return cipher.doFinal(text);
	}
	
	public static String sanitizedText(String text) {
		text = text.replace("&","");
		return text.replace("=","");
	}
	
	public static String profile_for(/*UserProfile profile*/String emailId) throws JsonProcessingException {
		UserProfile profile = new UserProfile();
		profile.setEmailId(sanitizedText(emailId));
		profile.setUid(10);
		profile.setRole("user");
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(profile);
		return profile.toString();
	}
	
	public static void generateAdminProfile(byte[] inputEncyText) throws Exception {
		String roleAdmin = "role=admin";
		int blockSize = cipher.getBlockSize();
		byte[] roleAdminBytes = encrypt(roleAdmin.getBytes());
		/*String templateString = new String("email=<>&uid=10&");
		int templateStringLength = templateString.length() - 2;//exclude <> from the count
		int lengthOfEmailId = blockSize - (templateStringLength % blockSize);
		StringBuilder constructedEmailId = new StringBuilder();
		for(int i = 0; i < lengthOfEmailId; i++)
			constructedEmailId.append('a');
		templateString = templateString.replaceAll("<>", constructedEmailId.toString());*/
		String templateString = new String("foofoo1234@bar.com");
		byte[] encryptedBytes = encrypt(profile_for(templateString).getBytes());
		int untilBlock = ArrayUtils.countBlocks(encryptedBytes,blockSize) - 1;
		byte[] extractedBytes = ArrayUtils.extractBytes(encryptedBytes, 0,  untilBlock * blockSize);
		byte[] finalencrytedBytes = ArrayUtils.mergeBytes(extractedBytes, roleAdminBytes);
		byte[] decryptedBytes = decrypt(finalencrytedBytes);
		System.out.println(new String(Ch9_PKCS7_Padding.removePadding(decryptedBytes)));
	}
	
	public static void main(String[] args) {
		
		try {
			String jsonString = profile_for("foo@bar.com&role=admin");
			System.out.println(jsonString);
			try {
				byte[] encryptedText = encrypt(jsonString.getBytes());
				System.out.println(new String(encryptedText));
				byte[] decryptedText = decrypt(encryptedText);
				System.out.println(new String(decryptedText));
				
				generateAdminProfile(encryptedText);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
