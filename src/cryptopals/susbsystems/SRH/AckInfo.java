package cryptopals.susbsystems.SRH;

public class AckInfo {
	public AckInfo(byte[] salt2, Integer b) {
		this.salt = salt2;
		this.publicKey = b;
	}

	public byte[] salt;
	public Integer publicKey;
}
