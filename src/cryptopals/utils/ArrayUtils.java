package cryptopals.utils;

import java.util.Arrays;

public class ArrayUtils {
	
	public static byte[] extractBytes(byte[] raw, int from, int noOfBytes) {
		return Arrays.copyOfRange(raw, from, (from+noOfBytes));
	}
	
	public static byte[] intToBytes(int x) {
	    byte[] bytes = new byte[4];

	    for (int i = 0; x != 0; i++, x >>>= 8) {
	      bytes[4 - i - 1] = (byte) (x & 0xFF);
	    }

	    return bytes;
	  }
	
	public static byte[] bitewiseToBytes(int[] ints) {
	    byte[] result = new byte[0];
	    for (int i = 0; i < ints.length; i++) {
	      result = mergeBytes(result, intToBytes(ints[i]));
	    }
	    return result;
	  }
	
	public static byte[] xorBlocks(byte[] block1, byte[] block2) {
		int noOfBytes = Math.min(block1.length,block2.length);
		byte[] combinedBytes = new byte[noOfBytes];
		for(int i = 0; i < noOfBytes; i++) {
			combinedBytes[i] = (byte)(block1[i] ^ block2[i]);
		}
		return combinedBytes;
	}
	
	public static byte[] mergeBytes(byte[] b1, byte[] b2) {
		byte[] temp = new byte[b1.length+b2.length];
		System.arraycopy(b1, 0, temp, 0, b1.length);
		System.arraycopy(b2, 0, temp, b1.length, b2.length);
		return temp;
	}
	
	public static int countBlocks(byte[] raw, int blockSize) {
		int extraBlock = (raw.length % blockSize > 0) ? 1 : 0;
		return (raw.length / blockSize) + extraBlock;
	}
	
	public static byte[] replaceByteAtPosition(byte[] raw, int pos, byte b) {
		byte[] temp = new byte[raw.length];
		//temp[0] = b;
		int i = 0;
		for(; i < pos; i++)
			temp[i] = raw[i];
		temp[i] = b;
		for(i = pos + 1; i < raw.length; i++)
			temp[i] = raw[i];
		/*System.arraycopy(b, 0, temp, 0, 1);
		System.arraycopy(raw, 1, temp, 1, raw.length - 1);*/
		return temp;
	}
	
	public static byte[] imposeInReverseAgainstFirstBlock(byte[] raw, byte[] toImpose) {
		int rawPosition = (raw.length / 2) - 1;
		for(int i = toImpose.length - 1; i >= 0; i--) {
			raw[rawPosition--] = toImpose[i];
		}
		return raw;
	}
	
	public static int[] bitewiseToIntegers(byte[] bytes) {
	    int length = bytes.length / 4 + (bytes.length % 4 == 0 ? 0 : 1);
	    int[] result = new int[length];
	    for (int i = 0; i < bytes.length; i++) {
	      int indx = i / 4;
	      result[indx] = result[indx] << 8;
	      result[indx] += bytes[i] & 0xff;
	    }
	    //    if (bytes.length % 4!=0) for (int i = bytes.length; i<bytes.length/1 +1; i++ ) {
	    //      int indx=i/4;
	    //      result[indx]=result[indx] << 8;
	    //    }
	    return result;
	}
	
	public static byte[] createInitializedArray(int length, byte content) {
	    byte[] result = new byte[length];
	    Arrays.fill(result, content);
	    return result;
	}
	
	public static int[] join(int[] first, int[]... rest) {
	    int[] result = first;
	    for (int[] next : rest) {
	      result = joinTwo(result, next);
	    }
	    return result;
	  }
	
	private static int[] joinTwo(int[] first, int[] second) {
	    int[] result = new int[first.length + second.length];
	    System.arraycopy(first, 0, result, 0, first.length);
	    System.arraycopy(second, 0, result, first.length, second.length);
	    return result;
	  }
	
	public static byte[] join(byte[] first, byte[]... rest) {
	    byte[] result = first;
	    for (byte[] next : rest) {
	      result = joinTwo(result, next);
	    }
	    return result;
	  }

	  private static byte[] joinTwo(byte[] first, byte[] second) {
	    byte[] result = new byte[first.length + second.length];
	    System.arraycopy(first, 0, result, 0, first.length);
	    System.arraycopy(second, 0, result, first.length, second.length);
	    return result;
	  }

}
