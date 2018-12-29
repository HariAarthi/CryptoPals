package cryptopals.susbsystems.SRH;

import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import cryptopals.utils.ArrayUtils;

public class SHA256 {
	
	public String getSHA256Hash(String data) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return bytesToHex(ArrayUtils.extractBytes(hash, 0, 16)); // make it printable
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
	
	private String bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }

}
