package cryptopals.set3;

public class Ch23_CrackingMersenne {
	
	public static final int FINAL_FACTOR_1 = 0x9d2c5680;
	public static final int FINAL_FACTOR_2 = 0xefc60000;
	public static int max = 32;
	
	public static int reversingStep4_1(int transformedValue, int shiftBy) {
		int result = 0;
		int i = 0;
		int tempTransformedValue = transformedValue;
			
		while((shiftBy * i) < max) {
			int partMask = ((-1 << (max - shiftBy)) >>> (shiftBy * i));
			int part = tempTransformedValue & partMask;
			tempTransformedValue ^= (part >>> shiftBy);
			result |= part;
			i++;
		}
		return result;
	}
	
	public static int reversingStep2_3(int transformedValue, int shiftBy, int finalFactor) {
		int result = 0;
		int i = 0;
		int tempTransformedValue = transformedValue;
			
		while((shiftBy * i) < max) {
			int partMask = ((-1 >>> (max - shiftBy)) << (shiftBy * i));
			int part = tempTransformedValue & partMask;
			tempTransformedValue ^= (part << shiftBy) & finalFactor;
			result |= part;
			i++;
		}
		return result;
	}
	
	private static int getInternalStateFor(int value) {
		int internalState = value;
		internalState = reversingStep4_1(value,18);		
		internalState = reversingStep2_3(internalState,15,FINAL_FACTOR_2);
		internalState = reversingStep2_3(internalState,7,FINAL_FACTOR_1);
		internalState = reversingStep4_1(internalState,11);
		return internalState;
	}
	
	public static int[] calculateInternalStateFor(int[] values) {
		int[] mersenneInternalStates = new int[values.length];
		int i = 0;
		for(int value : values) {
			mersenneInternalStates[i++] = getInternalStateFor(value);
		}
		return mersenneInternalStates;
	}
	
	public static void crackMT() {
		Ch21_MersenneTwister_PRNG prng = new Ch21_MersenneTwister_PRNG(10);
		int[] fetchedValues = new int[624];
		for(int i = 0; i < 624; i++) {
			fetchedValues[i] = prng.getRandomInt();
		}
		int[] crackedMT = calculateInternalStateFor(fetchedValues);
		
		Ch21_MersenneTwister_PRNG prng_Cracked = new Ch21_MersenneTwister_PRNG(10);
		//prng_Cracked.setMT(prng.getMT());
		prng_Cracked.setMT(crackedMT);
		for(int i = 0; i < 624; i++) 
			prng_Cracked.getRandomInt();
		
		int originalNextInt = prng.getRandomInt();
		int crackedNextInt = prng_Cracked.getRandomInt();
		System.out.println("Both does match? " + ((originalNextInt == crackedNextInt) ? "Yes" : "No"));
		System.out.println("Original : " + originalNextInt);
		System.out.println("Cracked  : " + crackedNextInt);
	}
	
	public static void main(String[] args) {
		Ch23_CrackingMersenne.crackMT();
		//unBitshiftRightXor(1117407004,18);
		//unBitshiftRightXor(1015,5);
		/*int internalState = 1683130859;
		int lastButOne = 1117411258;
		int afterTransform = 1117407004;
		int tempLastButOne = reversingStep4_1(afterTransform, 18);
		System.out.println("original last but one : " + lastButOne);
		System.out.println("calculated last but one : " + tempLastButOne);*/
	}
}
