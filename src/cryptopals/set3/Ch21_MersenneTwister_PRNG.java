package cryptopals.set3;

public class Ch21_MersenneTwister_PRNG {
	
	private int w = 32;//word length
	private int MT_LENGTH = 624;
	private int[] MT = new int[MT_LENGTH];
	private int index = 0;
	
	public static final int LAST_31_BITS = 0x7fffffff;
	public static final int FIRST_BIT = 0x80000000;
	private static final int INIT_FACTOR = 1812433253;
	public static final int GENERATING_FACTOR = 0x9908b0df;
	public static final int FINAL_FACTOR_1 = 0x9d2c5680;
	public static final int FINAL_FACTOR_2 = 0xefc60000;
	  
	public Ch21_MersenneTwister_PRNG(int seed) {
		setSeed(seed);
	}
	
	private void setSeed(int seed) {
		MT[0] = seed;
		for(int i = 1; i < MT.length; i++) {
			MT[i] = INIT_FACTOR * (MT[i - 1] ^ (MT[i-1] >>> 30) ) + i;
		}
		twister();
	}
	
	private void twister() {
		int temp, nextI;
		for(int i = 0; i < MT.length; i++) {
			nextI = (i % MT.length);
			temp = (MT[i] & FIRST_BIT) + (MT[nextI] & LAST_31_BITS); 
			MT[i] = MT[(i + 397) % MT_LENGTH] ^ (temp >>> 1);
			if (isOdd(temp)) {
		        MT[i] = MT[i] ^ GENERATING_FACTOR;
		      }
		}
	}
	
	private boolean isOdd(int number) {
	    return (number << 31) !=0;
	}
	
	//Not really necessary. But here just for sake of Ch.22
	public void setMT(int[] MT) {
		this.MT = MT;
	}
	
	//Not really necessary. But here just for sake of Ch.22
	public int[] getMT() {
		return this.MT;
	}
	
	public int getRandomInt() {
	    int result = MT[index];
	    result ^= (result >>> 11);
	    result ^= (result << 7) & FINAL_FACTOR_1;
	    result ^= (result << 15) & FINAL_FACTOR_2;
	    result ^= (result >>> 18);

	    index = (index + 1) % MT_LENGTH;
	    if (index == 0) {
	    	twister(); 
	    }
	    return result;
	 }
	
	public static void main(String[] args) {
		Ch21_MersenneTwister_PRNG prng = new Ch21_MersenneTwister_PRNG(5);
		System.out.println(prng.getRandomInt());
	}

}
