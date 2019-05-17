package se.havochvatten.unionvms;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVRecord;

public class AISEncoder {

	public static Map<String, Character> binToAsciiMap;
	
	public AISEncoder() {
		buildBinToAsciiMap();
	}
	
	// [# Timestamp, Type of mobile, MMSI, Latitude, Longitude, Navigational status, ROT, SOG, COG, Heading, IMO, Callsign, Name, Ship type, Cargo type, Width, Length, Type of position fixing device, Draught, Destination, ETA, Data source type, A, B, C, D]
	// 13/05/2019 00:00:00,Class A,219000028,55.858673,12.835107,Under way using engine,0.0,0.0,221.7,74,Unknown,,,Undefined,,,,Undefined,,,,AIS,,,,	
	public String encode(CSVRecord record) {
		return "!AIVDM,1,1,,B," + binaryStringToSymbol(encodePayload(record)) + ",0*5C";
	}
	
	private String encodePayload(CSVRecord record) {
		StringBuilder binaryString = new StringBuilder();
		// MessageType, length 6
		binaryString.append(prependZeros(Integer.toBinaryString(1), 6));
		// Repeat indicator, length 2
		binaryString.append("00");
		// MMSI, length 30
		binaryString.append(prependZeros(Integer.toBinaryString(Integer.parseInt(record.get("MMSI"))), 30));
		// Navigation Status, length 4
		binaryString.append(prependZeros("0", 4));
		// Rate of turn, length 8
		binaryString.append(prependZeros("0", 8));
		// Speed over ground, length 10
		String sogString = record.get("SOG");
		Double sog;
		if (!sogString.isEmpty()) {
			sog = Double.parseDouble(sogString);
		} else {
			sog = 102.3;
		}
		binaryString.append(prependZeros(Integer.toBinaryString((int)(sog * 10)), 10));
		// Position accuracy, length 1
		binaryString.append("0");
		// Longitude, length 28
		double lon = Double.parseDouble(record.get("Longitude"));
		binaryString.append(prependZeros(Integer.toBinaryString((int)(lon * 10000 * 60)), 28));
		// Latitude, length 27
		double lat = Double.parseDouble(record.get("Latitude"));
		binaryString.append(prependZeros(Integer.toBinaryString((int)(lat * 10000 * 60)), 27));
		// Course over ground, length 12
		String cogString = record.get("COG");
		double cog;
		if (!cogString.isEmpty()) {
			cog = Double.parseDouble(cogString);
		} else { 
			cog = 3600;
		}
		binaryString.append(prependZeros(Integer.toBinaryString((int)(cog * 10)), 12));
		// True Heading, length 9
		String headingString = record.get("Heading");
		int heading;
		if (!headingString.isEmpty()) {
			heading = Integer.parseInt(headingString);
		} else { 
			heading = 511;
		}
		binaryString.append(prependZeros(Integer.toBinaryString(heading),9));
		// Timestamp, length 6 (second)
		int second = Integer.parseInt(record.get("# Timestamp").split(":")[2]);
		binaryString.append(prependZeros(Integer.toBinaryString(second),6));
		// Maneuver Indicator, length 2
		binaryString.append(prependZeros("1", 2));
		// Spare, length 3
		binaryString.append(prependZeros("0", 3));
		// RAIM, length 1
		binaryString.append(prependZeros("0", 1));
		// Radio status, length 19
		binaryString.append(prependZeros("0", 19));
		return binaryString.toString();
	}
	
	private static String prependZeros(String value, int expectedLength) {
		if (value.length() < expectedLength) {
			return new String(new char[expectedLength - value.length()]).replace("\0", "0") + value;
		}
		return value;
	}

	private String binaryStringToSymbol(String binary) {
		StringBuilder symbolString = new StringBuilder();
		for (int i = 0; i < binary.length() - 6; i += 6) {
			symbolString.append(binToAsciiMap.get(binary.substring(i, i + 6)));
		}
		return symbolString.toString();
	}
	
	private void buildBinToAsciiMap() {
        // Table 3 sixbit ascii
        // http://catb.org/gpsd/AIVDM.html

        binToAsciiMap = new TreeMap<>();

        binToAsciiMap.put("000000",'0');
        binToAsciiMap.put("000001",'1');
        binToAsciiMap.put("000010",'2');
        binToAsciiMap.put("000011",'3');
        binToAsciiMap.put("000100",'4');
        binToAsciiMap.put("000101",'5');
        binToAsciiMap.put("000110",'6');
        binToAsciiMap.put("000111",'7');
        binToAsciiMap.put("001000",'8');
        binToAsciiMap.put("001001",'9');
        binToAsciiMap.put("001010",':');
        binToAsciiMap.put("001011",';');
        binToAsciiMap.put("001100",'<');
        binToAsciiMap.put("001101",'=');
        binToAsciiMap.put("001110",'>');
        binToAsciiMap.put("001111",'?');


        binToAsciiMap.put("010000",'@');
        binToAsciiMap.put("010001",'A');
        binToAsciiMap.put("010010",'B');
        binToAsciiMap.put("010011",'C');
        binToAsciiMap.put("010100",'D');
        binToAsciiMap.put("010101",'E');
        binToAsciiMap.put("010110",'F');
        binToAsciiMap.put("010111",'G');
        binToAsciiMap.put("011000",'H');
        binToAsciiMap.put("011001",'I');
        binToAsciiMap.put("011010",'J');
        binToAsciiMap.put("011011",'K');
        binToAsciiMap.put("011100",'L');
        binToAsciiMap.put("011101",'M');
        binToAsciiMap.put("011110",'N');
        binToAsciiMap.put("011111",'O');

        binToAsciiMap.put("100000",'P');
        binToAsciiMap.put("100001",'Q');
        binToAsciiMap.put("100010",'R');
        binToAsciiMap.put("100011",'S');
        binToAsciiMap.put("100100",'T');
        binToAsciiMap.put("100101",'U');
        binToAsciiMap.put("100110",'V');
        binToAsciiMap.put("100111",'W');
        binToAsciiMap.put("101000",'`');
        binToAsciiMap.put("101001",'a');
        binToAsciiMap.put("101010",'b');
        binToAsciiMap.put("101011",'c');
        binToAsciiMap.put("101100",'d');
        binToAsciiMap.put("101101",'e');
        binToAsciiMap.put("101110",'f');
        binToAsciiMap.put("101111",'g');

        binToAsciiMap.put("110000",'h');
        binToAsciiMap.put("110001",'i');
        binToAsciiMap.put("110010",'j');
        binToAsciiMap.put("110011",'k');
        binToAsciiMap.put("110100",'l');
        binToAsciiMap.put("110101",'m');
        binToAsciiMap.put("110110",'n');
        binToAsciiMap.put("110111",'o');
        binToAsciiMap.put("111000",'p');
        binToAsciiMap.put("111001",'q');
        binToAsciiMap.put("111010",'r');
        binToAsciiMap.put("111011",'s');
        binToAsciiMap.put("111100",'t');
        binToAsciiMap.put("111101",'u');
        binToAsciiMap.put("111110",'v');
        binToAsciiMap.put("111111",'w');

    }
}