package cryptopals.set4;

import java.util.Arrays;
import java.util.Date;

import cryptopals.utils.WebServerSimulator;
import cryptopals.validator.HMAC_SHA1_Validator;

public class Ch31_HMAC_SHA1_TimeLeak {
	
	private String provideURL(String fileName) {
		StringBuilder url = new StringBuilder("http://localhost:9000/test?file=");
		url.append(fileName);
		url.append("&signature=");
		return url.toString();
	}
	
	public void crackHMACSHA1(String fileName) {
		//byte[] signature = HMAC_SHA1_Validator.calculateSignature(fileName);
		String url = provideURL(fileName);
		byte[] finalSignature = new byte[20];
		String url1 = null;
		long startTime = (new Date()).getTime(), endTime = (new Date()).getTime();
		String wildCards = "&=";
		long prgStartTime =System.currentTimeMillis();
		WebServerSimulator webServer = WebServerSimulator.getInstance();
		String tempBytes = null;
		long thresholdTimeMillis = 0;
		webServer.handleRequest(url+ new String(finalSignature));
		startTime = (new Date()).getTime();//System.currentTimeMillis();
		webServer.handleRequest(url+ new String(finalSignature));
		endTime = (new Date()).getTime();//System.currentTimeMillis();
		long previousInterval =(endTime - startTime), currentInterval;
		System.out.println("Working on...");
		for(int i =0; i < 20; i++) { //HMAC-SHA1 signature is 20 byte length
			//tempBytes = (i == 0) ? "" : new String(Arrays.copyOfRange(finalSignature, 0, i));
			System.out.println("Iteration : " + (i + 1) );
			thresholdTimeMillis = (i + 1) * 49;
			System.out.println("thresholdTimeMillis : " + thresholdTimeMillis );
			for(int possibleByte = Byte.MIN_VALUE; /*!shouldMove &&*/ (possibleByte <= Byte.MAX_VALUE); possibleByte++) {
				finalSignature[i] = (byte)possibleByte;
				url1 = url + new String(finalSignature/*new byte[] {(byte)possibleByte}*/);
				startTime = (new Date()).getTime();//System.currentTimeMillis();
				boolean isValue = webServer.handleRequest(url1);
				endTime = (new Date()).getTime();//System.currentTimeMillis();
				currentInterval = (endTime - startTime);
				if(i==3 && possibleByte == 16) System.out.println("Currently on 4th byte : ");
				//System.out.println("currentInterval : "+currentInterval);
				//System.out.println(Math.abs(currentInterval - previousInterval));
				if((currentInterval > thresholdTimeMillis) /*&& (Math.abs(currentInterval - previousInterval) > 45)*/) {
				//if((Math.abs(currentInterval - previousInterval) >= thresholdTimeMillis/*45*/) /*&& !wildCards.contains(new String(new byte[] {(byte)possibleByte}))*/) {
					System.out.println("Iteration complete : " + (i+1) + " previousInterval : " + previousInterval + " currentInterval : " + currentInterval);
					System.out.println("possibleByte : "+new String(new byte[] {(byte)possibleByte}));
					finalSignature[i] = (byte)possibleByte;
					//if(currentInterval < previousInterval) finalSignature[i] =0;
					previousInterval = Math.max(previousInterval, currentInterval);
					break;
				}
			}
			//previousInterval = thresholdTimeMillis;
		}
		System.out.println("Correct signature :" + new String(finalSignature));
		long prgEndTime =System.currentTimeMillis();
		System.out.println("Total time taken : " + (prgEndTime - prgStartTime));
	}
	
	public static void main(String[] args) {
		Ch31_HMAC_SHA1_TimeLeak timeLeak = new Ch31_HMAC_SHA1_TimeLeak();
		timeLeak.crackHMACSHA1("hari");
		/*for(int possibleByte = Byte.MIN_VALUE; (possibleByte <= Byte.MAX_VALUE); possibleByte++) {
			System.out.println(possibleByte + " :: " + new String(new byte[] {(byte) possibleByte}));
		}*/
		/*try {
			long startTime = System.currentTimeMillis();
			Thread.sleep(50);
			long endTime = System.currentTimeMillis();
			System.out.println((endTime-startTime));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


	}

}
