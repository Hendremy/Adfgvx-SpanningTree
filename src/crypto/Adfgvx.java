package crypto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Adfgvx {
	
	private final char[] ADFGVX = {'A','D','F','G','V','X'};
	private final char[] KEY_CHARS = {'0','1','2','3','4','5','6','7','8','9',
			'A','B','C','D','E','F','G','H','I','J','K','L','M',
			'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private final Map<String, Character> decryptMap;
	private final Map<Character, String> encryptMap;
	private final char[] transpositionKey;
	private final char[] sortedTranspKey; 


	/**
	 * Construit un objet Adfgvx qui permet de chiffrer ou déchiffrer un message selon
	 * la méthode ADFGVX à partir d'une clé de substitution et d'une clé de transposition.
	 * Si la clé de substitution ou la clé de transposition n'est pas valide,
	 *  une IllegalArgumentException est lancée.
	 * 
	 * La clé de substitution est valide si elle contient tous les chiffres et
	 * toutes les lettres majuscules sans répétition.
	 * 
	 * La clé de transposition est valide si elle ne contient que des lettres majuscules sans répétitions.
	 * 
	 * @param substitutionKey la clé de substitution
	 * @param transpositionKey la clé de transposition
	 */
	public Adfgvx(String substitutionKey, String transpositionKey) {
		// TODO
		if(!substitutionKeyIsValid(substitutionKey) || !transpositionKeyIsValid(transpositionKey)) {
			throw new IllegalArgumentException();
		}
		this.encryptMap = new HashMap<Character,String>();
		this.decryptMap = new HashMap<String,Character>();
		this.generateSubstTable(substitutionKey);
		this.transpositionKey = transpositionKey.toCharArray();
		this.sortedTranspKey = Arrays.copyOf(this.transpositionKey, this.transpositionKey.length);
		Arrays.sort(this.sortedTranspKey);
	}
	
	/**
	 * Retourne vrai si la clé de substitution est valide, sinon retourne faux.
	 * La clé de substitution est valide si elle contient tous les chiffres et
	 * toutes les lettres majuscules sans répétition.
	 * 
	 * @param substitutionKey la clé de substitution
	 * @return vrai si la clé de substitution est valide, sinon faux
	 */
	private boolean substitutionKeyIsValid(String substitutionKey) {
		return substitutionKey != null && substitutionKey.length() == 36 && this.containsAllDigitsAndLetters(substitutionKey);
	}
	
	/**
	 * Retourne vrai si la clé de substitution contient tous les chiffres et toutes les lettres. 
	 * 
	 * @param substitutionKey la clé de substitution
	 * @return vrai si la clé de substitution contient tous les chiffres et toutes les lettres, sinon faux
	 */
	private boolean containsAllDigitsAndLetters(String substitutionKey) {
		for(char keyChar : this.KEY_CHARS) {
			if(substitutionKey.indexOf(keyChar) == -1) return false;
		}
		return true;
	}
	
	/**
	 * Retourne vrai si la clé de transposition est valide, sinon retourne faux.
	 * La clé de transposition est valide si elle ne contient que des lettres majuscules sans répétitions.
	 * 
	 * @param transpositionKey
	 * @return vrai si la clé de transposition est valide, sinon faux
	 */
	private boolean transpositionKeyIsValid(String transpositionKey) {
		return transpositionKey != null && transpositionKey.length() > 2 && transpositionKey.matches("[A-Z]+");
	}
	
	/**
	 * Génère la table de substitution des caractères à partir de la clé de substitution.
	 * 
	 * @param substitutionKey la clé de substitution
	 */
	private void generateSubstTable(String substitutionKey) {
		int i = 0;
		for(char substRowChar : this.ADFGVX) {
			for(char substColChar : this.ADFGVX) {
				String bigram = new String(new char[]{substRowChar, substColChar});
				char associatedChar = substitutionKey.charAt(i);
				decryptMap.put(bigram, associatedChar);
				encryptMap.put(associatedChar, bigram);
				++i;
			}
		}
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
		char [] arrayToEncrypt = cleanTextToCipher(textToEncrypt);
		arrayToEncrypt = substituteText(arrayToEncrypt);
		return transposeText(arrayToEncrypt);
	}
	
	/**
	 * Nettoie le texte à chiffrer en le rendant tout en majuscule et 
	 * en ne concervant que les lettres majuscules et les chiffres
	 * 
	 * @param text le texte à nettoyer
	 * @return le text nettoyé
	 */
	private char[] cleanTextToCipher(String text) {
		text = text.toUpperCase();
		return text.replaceAll("[^A-Z0-9]+", "").toCharArray();
	}
	
	/**
	 * Substitue le texte à chiffrer en remplaçant les caractères par le digramme correspondant.
	 * 
	 * @param text le texte à substituer
	 * @return le texte substitué
	 */
	private char[] substituteText(char[] text) {
		int sbCapacity = text.length * 2;
		StringBuilder substText = new StringBuilder(sbCapacity);
		for(int i = 0; i < text.length; ++i) {
			String substBigram = encryptMap.get(text[i]);
			substText.append(substBigram);
		}
		return substText.toString().toCharArray();
	}
	
	/**
	 * Transpose le texte à chiffrer en utilisant la clé de transposition.
	 * 
	 * @param text le texte à chiffrer
	 * @return le texte transposé
	 */
	private String transposeText(char[] text) {
		int colCount = this.transpositionKey.length;
		int rowCount = (int) Math.ceil((double)text.length / colCount);
		
		Map<Character,char[]> tpKeyMap = generateTranspositionKeyMap(rowCount);
		fillTranspositionColumns(tpKeyMap, text, rowCount, colCount);
		
		return readTranspositionArray(tpKeyMap, rowCount, colCount);
	}
	
	/**
	 * Génère la map des colonnes du tableau de transposition.

	 * @param rowCount le nombre de rangées du tableau de transposition 
	 * @return la map des colonnes du tableau de transposition
	 */
	private Map<Character,char[]> generateTranspositionKeyMap(int rowCount){
		Map<Character,char[]> tpKeyMap = new HashMap<Character,char[]>();
		for(char tpKeyChar : this.transpositionKey) {
			tpKeyMap.put(tpKeyChar, new char[rowCount]);
		}
		return tpKeyMap;
	}
	
	/**
	 * Remplis colonne par colonne le tableau de transposition.
	 * 
	 * @param tpMap le tableau de transposition
	 * @param text le texte à chiffrer
	 * @param rowCount le nombre de rangées du tableau de transposition
	 * @param colCount le nombre de colonnes du tableau de transposition
	 */
	private void fillTranspositionColumns(Map<Character,char[]> tpMap, char[] text, int rowCount, int colCount) {
		//Pour chaque colonne
		for(int colNum = 0; colNum < colCount; ++colNum) {
			//Récupération de la colonne en fonction de la lettre
			char[] col = tpMap.get(this.transpositionKey[colNum]);
			//Remplissage de la colonne avec les caractères du texte substitué
			for(int i = 0; i < rowCount; ++i) {
				int nextCharIndex = colNum + i * colCount;
				if(nextCharIndex < text.length) {
					col[i] = text[nextCharIndex];
				}else{
					col[i] = getRandomFillerChar();
				}
			}
		}
	}
	
	/**
	 * Retourne un caractère de remplissage aléatoire parmi A, D, F, G, V et X
	 * 
	 * @return un caractère aléatoire parmi A, D, F, G, V, X
	 */
	private char getRandomFillerChar() {
		/*var rand = new Random();
		int randIndex = rand.nextInt(ADFGVX.length);
		return ADFGVX[randIndex];*/
		return 'X';
	}
	
	/**
	 * Lit le tableau de transposition dans l'ordre alphabétique des lettres de la clé
	 * pour en obtenir le message transposé.
	 * 
	 * @param tpKeyMap le tableau de transposition
	 * @param rowCount le nombre de rangées du tableau
	 * @param colCount le nombre de colonnes du tableau
	 * @return le message transposé
	 */
	private String readTranspositionArray(Map<Character, char[]> tpKeyMap, int rowCount, int colCount) {
		//Capacité du StringBuilder = nb de caractères dans le tableau + nb de tirets à rajouter entre groupe de 5 caractères
		int tpArraySize = rowCount * colCount;
		int separatorNb = tpArraySize/5; 
		int sbCapacity = tpArraySize + separatorNb;
		
		StringBuilder cypheredText = new StringBuilder(sbCapacity);
		for(char keyChar : this.sortedTranspKey) {
			char[] column = tpKeyMap.get(keyChar);
			String colToString = new String(column);
			cypheredText.append(colToString);
		}
		for(int i = separatorNb; i > 0; --i) {
			cypheredText.insert(5*i, '-');
		}
		return cypheredText.toString();
	}

	/**
	 * Decrypts an ADFGVX cryptogram.
	 * 
	 * @param textToDecrypt An ADFGVX cryptogram
	 * @return the decrypted text
	 */
	public String decrypt(String textToDecrypt) {
		// TODO
		return "";
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
