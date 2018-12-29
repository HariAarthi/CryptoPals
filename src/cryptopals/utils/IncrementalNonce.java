package cryptopals.utils;

public class IncrementalNonce {

	private byte[] nonceBytes;
	private int prefixLength;
	
	public IncrementalNonce(int prefixLength, int length) {
		this.prefixLength = prefixLength;
		nonceBytes = new byte[prefixLength+length];
		nonceBytes[0] = 0;
	}
	
	public IncrementalNonce increment() throws Exception {
		for(int i = prefixLength; i < nonceBytes.length; i++) {
			if(nonceBytes[i] != Byte.MAX_VALUE) {
				nonceBytes[i]++;
				return this;
			}
			nonceBytes[i]=0;
		}
		throw new Exception("Nounce is full!!");
	}
	
	public byte[] getNonceBytes() {
		return nonceBytes;
	}
}
