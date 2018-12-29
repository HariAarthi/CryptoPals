package cryptopals.set1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Ch4_DetectSingleCharXOR {

	public static int highScore = 0;
	public static String candidateString = null;
	public static int lineNumber = 0;
	
	public static String detectSingleCharXOR(String inputStr) {
		String potentialDecText = null;
		try {			
			potentialDecText =  Ch3_SingleByteXOR.findCipher(inputStr);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return potentialDecText;
	}
	
	public static void readFileAndDetect(String fileName) {
		try {
			BufferedReader fis = new BufferedReader(new FileReader(fileName));
			String temp;
			int score = 0;int i=0;
			String potentialDecText = null;
			System.out.println("Processing...");
			while((temp = fis.readLine()) != null) {
				calculateScore(Ch1_HexToBase64.hexDecoding(temp), i);
				i++;
			}			
			System.out.println("Line number : " + lineNumber);
			System.out.println(candidateString);
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void calculateScore(String temp, int i) {
		int score;
		String potentialDecText;
		potentialDecText = detectSingleCharXOR(temp);
		score = Ch3_SingleByteXOR.calculateScore(potentialDecText.getBytes());
		if(highScore < score) {
			highScore = score;
			candidateString = new String(potentialDecText);
			lineNumber = i;
		}
	}
	
	public static void main(String[] args) {
		String fileName = "4.txt";
		readFileAndDetect(fileName);
	}
}
