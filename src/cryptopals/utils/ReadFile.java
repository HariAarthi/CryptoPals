package cryptopals.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ReadFile {
	
	public static byte[] readFile(String fileName) {
		byte[] decodedBytes = null;
		try {
			DataInputStream ffs = new DataInputStream(new FileInputStream(new File(fileName/*, "UTF-8"*/)));
			byte[] fileBytes = new byte[ffs.available()];
			ffs.read(fileBytes, 0, ffs.available());
			decodedBytes = org.apache.shiro.codec.Base64.decode(fileBytes); //Base64.getDecoder().decode(new String(fileBytes)); Shiro decode works but not Java 8?!
			ffs.close();
			
		}catch(Exception e) {}
		return decodedBytes;
	}
	
	public static List<String> getFile(String fileName) {
		List<String> stringList = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(new File(fileName));
			BufferedReader bufReader = new BufferedReader(fileReader);
			Stream<String> stream = bufReader.lines();
			//lines = new byte[(int) stream.count()][];
			Iterator<String> iter = stream.iterator();
			int count = 0;
			while(iter.hasNext()) {
				stringList.add(iter.next());
				//System.out.println(iter.next());
			}
			bufReader.close();
		}catch(Exception e) {}
		return stringList;
	}

}
