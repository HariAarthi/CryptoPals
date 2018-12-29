package cryptopals.set4;

import java.util.Arrays;

import cryptopals.utils.ArrayUtils;

public class Ch28_SHA1 {
	
	public static final int DEFAULT_E = -1009589776;
	public static final int DEFAULT_D = 271733878;
	public static final int DEFAULT_C = -1732584194;
	public static final int DEFAULT_B = -271733879;
	public static final int DEFAULT_A = 1732584193;

	private byte[] key = ArrayUtils.createInitializedArray(16, (byte)1);
	  /*
	   * Bitwise rotate a 32-bit number to the left
	   */
	  private static int rol(int num, int cnt) {
	    return (num << cnt) | (num >>> (32 - cnt));
	  }

	  public byte[] encode(byte[] input) {
		byte[] content = ArrayUtils.mergeBytes(key, input);
	    int[] blks = toPaddedIntegerArray(content, 0);
	    int[] words = encodePaddedInts(blks, DEFAULT_A, DEFAULT_B, DEFAULT_C, DEFAULT_D, DEFAULT_E);
	    return ArrayUtils.bitewiseToBytes(words);
	  }

	  public byte[] encode(byte[] input, int a, int b, int c, int d, int e, int addToLength) {
	    int[] blks = toPaddedIntegerArray(input, addToLength);
	    int[] words = encodePaddedInts(blks, a, b, c, d, e);
	    return ArrayUtils.bitewiseToBytes(words);
	  }

	  // calculate 160 bit SHA1 hash of the sequence of blocks
	  public int[] encodePaddedInts(int[] blks, int a, int b, int c, int d, int e) {

	    for (int i = 0; i < blks.length; i += 16) {
	      int[] w = new int[80];
	      int olda = a;
	      int oldb = b;
	      int oldc = c;
	      int oldd = d;
	      int olde = e;

	      for (int j = 0; j < 80; j++) {
	        w[j] = (j < 16) ? blks[i + j] : (rol(w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16], 1));

	        int t = rol(a, 5) + e + w[j] + ((j < 20) ? 1518500249 + ((b & c) | ((~b) & d)) : (j < 40) ? 1859775393 + (b ^ c ^ d) : (j < 60) ? -1894007588 + ((b & c) | (b & d) | (c & d)) : -899497514 + (b ^ c ^ d));
	        e = d;
	        d = c;
	        c = rol(b, 30);
	        b = a;
	        a = t;
	      }

	      a = a + olda;
	      b = b + oldb;
	      c = c + oldc;
	      d = d + oldd;
	      e = e + olde;
	    }
	    int[] words = { a, b, c, d, e };
	    return words;
	  }

	  public int[] toPaddedIntegerArray(byte[] input, int addToLength) {
	    int[] ints = ArrayUtils.bitewiseToIntegers(input);	    
	    
	    // Convert an input to a sequence of 16-word blocks, stored as an array.
	    // Append padding bits and the length, as described in the SHA1 standard
	    int blksLength = (((input.length + 8) >> 6) + 1) * 16;
	    int[] blks = Arrays.copyOf(ints, blksLength);//new int[blksLength];
	    
	    int idx = input.length >> 2;
	    int currentPos=input.length; 
	    blks[idx] = ((blks[idx] << 8) | (0x80)) << ((3 - (currentPos & 3)) << 3);
	    blks[blks.length - 1] = (input.length * 8) + addToLength;
	    return blks;
	  }
	
	  public boolean validate(byte[] message, byte[] authentication) {
		  byte[] content = ArrayUtils.mergeBytes(key, message);
		  byte[] expected = encode(content);
		  return Arrays.equals(expected, authentication);
	  }
	  
	public static void main(String[] args) {
	    //full test
	    byte[] message = ArrayUtils.createInitializedArray(22, (byte)0);

		Ch28_SHA1 sha1 = new Ch28_SHA1();
	    byte[] tag = sha1.encode(message);
	    boolean validated = sha1.validate(message, tag);
	    System.out.println(validated);
	}

}
