package cryptopals.set3;

import java.util.Date;

public class Ch22_FindSeed {
	
	public static int crackSeed(int randomNo, int newTimeStamp) {
		//guess seed
		Ch21_MersenneTwister_PRNG prng1 = null;
		int guessInt, i;
		//int newTimeStamp =  Math.abs((int) (new Date()).getTime());//(int)System.currentTimeMillis();
		System.out.println("to begin with : " + newTimeStamp);
		for(i = newTimeStamp; i > 0; i--) {
			prng1 = new Ch21_MersenneTwister_PRNG(i);
			guessInt = prng1.getRandomInt();
			if(guessInt == randomNo) {
				System.out.println("guessed random number : " + guessInt);
					break;
			}
		}
		return i;
	}
	
	public static void main(String[] args) {
		//int timeStamp = (int)System.currentTimeMillis();
		int timeStamp = Math.abs((int) (new Date()).getTime());
		System.out.println("original seed : " + timeStamp);
		Ch21_MersenneTwister_PRNG prng = new Ch21_MersenneTwister_PRNG(timeStamp);
		int randomInt = prng.getRandomInt();
		System.out.println("random number : " + randomInt);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int guessedSeed = Ch22_FindSeed.crackSeed(randomInt, timeStamp+10);
		System.out.println("guessed seed  : " + guessedSeed);		
	}

}
