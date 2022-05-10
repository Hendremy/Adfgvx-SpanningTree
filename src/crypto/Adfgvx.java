package crypto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
//import java.util.Random;

public class Adfgvx {
	
	private final char[] ADFGVX = {'A','D','F','G','V','X'};
	private final char[] KEY_CHARS = {'0','1','2','3','4','5','6','7','8','9',
			'A','B','C','D','E','F','G','H','I','J','K','L','M',
			'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private final Map<String, Character> decryptMap;
	private final Map<Character, String> encryptMap;
	private final char[] transpositionKey;
	private final char[] sortedTranspKey;
	//private final Random rand;


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
		if(!substitutionKeyIsValid(substitutionKey) || !transpositionKeyIsValid(transpositionKey)) {
			throw new IllegalArgumentException();
		}
		this.encryptMap = new HashMap<Character,String>();
		this.decryptMap = new HashMap<String,Character>();
		this.generateSubstTable(substitutionKey);
		this.transpositionKey = transpositionKey.toCharArray();
		this.sortedTranspKey = Arrays.copyOf(this.transpositionKey, this.transpositionKey.length);
		Arrays.sort(this.sortedTranspKey);
		//this.rand = new Random();
	}
	
	/**
	 * Lance une NullPointerException si le texte est null.
	 * @param text le texte
	 * @throws NullPointerException
	 */
	private void checkIsNull(String text) {
		if(text == null) throw new NullPointerException();
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
		checkIsNull(substitutionKey);
		return substitutionKey.length() == 36 && this.containsAllDigitsAndLetters(substitutionKey);
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
		checkIsNull(transpositionKey);
		return transpositionKey.length() > 2 
				&& containsOnlyUpperCaseLetters(transpositionKey)
				&& hasNoRepetition(transpositionKey);
	}
	
	/**
	 * Retourne vrai si la clé de transposition ne contient que des lettres majuscules.
	 * @param transpositionKey la clé de transposition
	 * @return vrai si la clé de transposition ne contient que des lettres majuscules, sinon faux
	 */
	private boolean containsOnlyUpperCaseLetters(String transpositionKey) {
		char[] tpKey = transpositionKey.toCharArray();
		for(int i=0; i < tpKey.length; ++i) {
			if(!charInRange(tpKey[i], 'A', 'Z')) return false;
		}
		return true;
	}
	
	/**
	 * Retourne vrai si la clé de transposition n'a pas de caractères répétés
	 * @param transpositionKey la clé de transposition
	 * @return vrai si la clé de transposition n'a pas de caractères répétés, sinon faux
	 */
	private boolean hasNoRepetition(String transpositionKey) {
		Set<Character> uniqueChars = new HashSet<Character>();
		for(char caracter : transpositionKey.toCharArray()) {
			if(!uniqueChars.add(caracter)) return false;
		}
		return true;
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
		checkIsNull(textToEncrypt);
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
		char[] textArray = text.toCharArray();
		//Utilisation d'un stringbuilder pour avoir une taille max
		StringBuilder sb = new StringBuilder(textArray.length);
		for(int i = 0; i < textArray.length; ++i) {
			char carac = textArray[i];
			if(charInRange(carac,'A','Z') || charInRange(carac,'0','9')) {
				sb.append(carac);
			}else {
				handleAccents(carac, sb);
			}
		}
		return sb.toString().toCharArray();
	}
	
	/**
	 * Vérifie si un caractère est compris entre deux autres caractères.
	 * @param charToCheck le caractère à vérifier
	 * @param lowerBound borne inférieure de l'intervalle
	 * @param UpperBound borne supérieure de l'intervalle
	 * @return vrai si le caractère est compris dans l'intervalle de caractères, sinon faux
	 */
	private boolean charInRange(char charToCheck, char lowerBound, char upperBound) {
		return lowerBound <= charToCheck && charToCheck <= upperBound;
	}
	
	/**
	 * Ajoute au StringBuilder la lettre correspondant à la lettre accentuée
	 * donnée en argument.
	 * @param carac le caractère accentué
	 * @param sb le StringBuilder
	 */
	private void handleAccents(char carac, StringBuilder sb) {
		if(charInRange(carac, 'À','Å')) {
			sb.append('A');
		}else if(carac == 'Ç') {
			sb.append('C');
		}else if(charInRange(carac,'È','Ë')) {
			sb.append('E');
		}else if(charInRange(carac,'Ì','Ï')) {
			sb.append('I');
		}else if(charInRange(carac,'Ò','Ö')) {
			sb.append('O');
		}else if(charInRange(carac,'Ù','Ü')) {
			sb.append('U');
		}else if(carac == 'Ÿ') {
			sb.append('Y');
		}
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
		fillEncryptTpArray(tpKeyMap, text, rowCount, colCount);
		
		return readEncryptTpArray(tpKeyMap, rowCount, colCount);
	}
	
	/**
	 * Génère la map des colonnes du tableau de transposition.

	 * @param rowCount le nombre de rangées du tableau de transposition 
	 * @param transpoKey la clé de transposition à utiliser
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
	private void fillEncryptTpArray(Map<Character,char[]> tpMap, char[] text, int rowCount, int colCount) {
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
		/**int randIndex = this.rand.nextInt(ADFGVX.length);
		return ADFGVX[randIndex];*/
		return 'X';
	}
	
	/**
	 * Lit le tableau de transposition par colonne dans l'ordre alphabétique des lettres de la clé
	 * pour en obtenir le message transposé.
	 * 
	 * @param tpKeyMap le tableau de transposition
	 * @param rowCount le nombre de rangées du tableau
	 * @param colCount le nombre de colonnes du tableau
	 * @return le message transposé
	 */
	private String readEncryptTpArray(Map<Character, char[]> tpKeyMap, int rowCount, int colCount) {
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
		checkIsNull(textToDecrypt);
		char[] text = cleanTextToDecrypt(textToDecrypt);
		text = invertTranspose(text);
		return invertSubstitute(text);
	}
	
	/**
	 * Nettoie le texte à déchiffrer et vérifie qu'il est valide.
	 * @param textToDecrypt le texte à déchiffrer
	 * @throws IllegalArgumentException si le message chiffré n'est pas valide
	 * @return le texte nettoyé
	 */
	private char[] cleanTextToDecrypt(String textToDecrypt) {
		char[] cleanText = removeSeparators(textToDecrypt);
		if(!textToDecryptIsValid(cleanText)) {
			throw new IllegalArgumentException();
		}
		return cleanText;
	}
	
	/**
	 * Nettoie le texte à déchiffrer en retirant les tirets séparateurs.
	 * @param textToDecrypt le texte à déchiffré
	 * @return le texte nettoyé des tirets séparateurs
	 */
	private char[] removeSeparators(String textToDecrypt) {
		char[] textToClean = textToDecrypt.toCharArray();
		int cleanTextSize = textToClean.length - (textToClean.length/6);
		char[] cleanText = new char[cleanTextSize];
		int cleanTextPos = 0;
		for(int i = 0; i < textToClean.length; ++i) {
			char carac = textToClean[i];
			if(carac != '-') {
				cleanText[cleanTextPos] = carac;
				cleanTextPos++;
			}
		}
		return cleanText;
	}
	
	/**
	 * Vérifie si le texte à déchiffrer est valide.
	 * @param text le texte à déchiffrer
	 * @return vrai si le texte à déchiffrer est valide, sinon faux
	 */
	private boolean textToDecryptIsValid(char[] text) {
		return text.length % transpositionKey.length == 0;
	}
	
	/**
	 * Inverse la transposition du chiffrage en remettant en ordre les digrammes du message.
	 * @param text le texte à remettre en ordre
	 * @return le tableau des digrammes du texte en ordre
	 */
	private char[] invertTranspose(char[] text) {
		int colCount = this.sortedTranspKey.length;
		int rowCount = text.length / colCount;
		Map<Character, char[]> tpArray = new HashMap<Character, char[]>();
		fillDecryptTpArray(tpArray, text, rowCount);
		return readDecryptTpArray(tpArray, rowCount, colCount);
	}
	
	/**
	 * Remplis les colonnes du tableau de transposition à partir du message chiffré.
	 * @param tpArray le tableau de transposition à remplir
	 * @param text le message chiffré
	 * @param rowCount le nombre de rangées du tableau de transposition
	 */
	private void fillDecryptTpArray(Map<Character, char[]> tpArray, char[] text, int rowCount) {
		int textIndex = 0;
		for(char tpKeyChar : this.sortedTranspKey) {
			char[] column = Arrays.copyOfRange(text, textIndex, textIndex + rowCount);
			tpArray.put(tpKeyChar, column);
			textIndex += rowCount;
		}
	}
	
	/**
	 * Lis le tableau de transposition par rangée dans l'ordre de la clé de transposition non-triée.
	 * @param tpArray le tableau de transposition
	 * @param rowCount le nombre de rangées du tableau de transposition
	 * @param colCount le nombre de colonnes du tableau de transposition
	 * @return le message chiffré remis en ordre
	 */
	private char[] readDecryptTpArray(Map<Character, char[]> tpArray, int rowCount, int colCount) {
		char[] text = new char[rowCount * colCount];
		int colNum = 0;
		for(char tpKeyChar : this.transpositionKey) {
			char[] column = tpArray.get(tpKeyChar);
			for(int i = 0; i < column.length; ++i) {
				int posInString = i * colCount + colNum;
				text[posInString] = column[i];
			}
			colNum++;
		}
		return text;
	}
	
	/**
	 * Substitue les digrammes par leur caractère associés.
	 * @param text le texte à substituer
	 * @return le texte substitué
	 */
	private String invertSubstitute(char[] text) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < text.length; i += 2) {
			char[] nextTwoChar = new char[] {text[i], text[i+1]};
			String digram = new String(nextTwoChar);
			sb.append(this.decryptMap.get(digram));
		}
		return sb.toString();
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
