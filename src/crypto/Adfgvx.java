package crypto;

import java.util.Map;

public class Adfgvx {
	
	private final char[] ADFGVX = {'A','D','F','G','V','X'};
	private Map encryptMap;
	private Map decryptMap;
	private String transpositionKey;

	/**
	 * Constructor
	 */
	public Adfgvx(String substitutionKey, String transpositionKey) {
		// TODO
		if(!substitutionKeyIsValid(substitutionKey) || !transpositionKeyIsValid(transpositionKey)) {
			throw new IllegalArgumentException();
		}
		String[] substKeyRows = substitutionKey.split("",6);
		
		
	}
	
	private boolean substitutionKeyIsValid(String substitutionKey) {
		boolean isValid = false;
		isValid = substitutionKey.length() == 36;
		return isValid;
	}
	
	private boolean transpositionKeyIsValid(String transpositionKey) {
		return transpositionKey.length() > 2 /*&& matches([a-zA-Z]+)*/;
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Encrypts a text into an ADFGVX cryptogram. The cryptogram is formatted with
	 * an hyphen '-' after each group of 5 characters. Ex: VXVGG-GFDXD-XFDDD
	 * 
	 * @param textToEncrypt A text to encrypt
	 * @return the ADFGVX cryptogram
	 */
	public String encrypt(String textToEncrypt) {
		String cleanText = textToEncrypt.replaceAll("[^a-zA-Z0-9]+", "");
		return null;
	}

	/**
	 * Decrypts an ADFGVX cryptogram.
	 * 
	 * @param textToDecrypt An ADFGVX cryptogram
	 * @return the decrypted text
	 */
	public String decrypt(String textToDecrypt) {
		// TODO
		return null;
	}

	/*
	 * MAIN - TESTS
	 */
	/**
	 * The main method illustrates the use of the ADFGVX class.
	 * 
	 * @param args None
	 */
	public static void main(String[] args) {
		System.out.println("ADFGVX - Exemple de l'énoncé");
		System.out.println();

		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "DEMANDE RENFORTS D'URGENCE";
		System.out.println("Message : " + message);
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		String encrypted = cypher.encrypt(message);
		System.out.println("Message chiffré : " + encrypted);
		String decrypted = cypher.decrypt(encrypted);
		System.out.println("Message déchiffré : " + decrypted);

		System.out.println();
		System.out.println("-------------------------------------");
		System.out.println();
		
		int repeatCount = 20000000;

		System.out.println(">>> PERFORMANCE - ENCRYPT x " + repeatCount);
		long time = System.currentTimeMillis();
		for (int i = 0; i < repeatCount; i++) {
			cypher.encrypt(message);
		}
		System.out.printf("Elapsed time = %.2f seconds\n", (System.currentTimeMillis() - time) / 1000.0);
		System.out.println();

		System.out.println(">>> PERFORMANCE - DECRYPT x " + repeatCount);
		time = System.currentTimeMillis();
		for (int i = 0; i < repeatCount; i++) {
			cypher.decrypt(encrypted);
		}
		System.out.printf("Elapsed time = %.2f seconds\n", (System.currentTimeMillis() - time) / 1000.0);
	}
}
