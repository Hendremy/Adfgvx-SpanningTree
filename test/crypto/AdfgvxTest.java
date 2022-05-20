package crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdfgvxTest {
	/*
	 * CONSTRUCTOR TESTS
	 */
	@Test
	void testAdfgvxExample() {
		assertNotNull(new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C", "BRUTES"));
	}
	
	@Test
	void nullSubstitutionKey() {
		Exception illegalArgExc = assertThrows(NullPointerException.class, ()->{
			new Adfgvx(null,"BRUTES");
		});
		
		assertNotNull(illegalArgExc);
	}
	
	@Test
	void nullTranspositionKey() {
		Exception illegalArgExc = assertThrows(NullPointerException.class, ()->{
			new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C",null);
		});
		
		assertNotNull(illegalArgExc);
	}
	
	@Test
	void tooShortSubstitutionKey() {
		Exception illegalArgExc = assertThrows(IllegalArgumentException.class, ()->{
			new Adfgvx("abc","BRUTES");
		});
		
		assertNotNull(illegalArgExc);
	}
	
	@Test
	void substitutionKeyNotContainingAllRequiredChars() {
		Exception illegalArgExc = assertThrows(IllegalArgumentException.class, ()->{
			new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2B","BRUTES");
		});
		
		assertNotNull(illegalArgExc);
	}
	
	@Test
	void tooShortTranspositionKey() {
		Exception illegalArgExc = assertThrows(IllegalArgumentException.class, ()->{
			new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","");
		});
		
		assertNotNull(illegalArgExc);
	}
	
	@Test
	void repetitionTranspositionKey() {
		Exception illegalArgExc = assertThrows(IllegalArgumentException.class, ()->{
			new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","BRUTEB");
		});
		
		assertNotNull(illegalArgExc);
	}
	
	@Test
	void lowerCaseTranspositionKey() {
		Exception illegalArgExc = assertThrows(IllegalArgumentException.class, ()->{
			new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","bruTES");
		});
		
		assertNotNull(illegalArgExc);
	}

	/*
	 * ENCRYPT TESTS
	 */
	@Test
	void testEncryptExample() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "DEMANDE RENFORTS D'URGENCE";
		String encrypted = "VDGXX-VVXFV-GVXXX-XDFGD-GDAXX-DGFFG-DXGDG-FXGGG-GXXGV-DGG";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(encrypted, cypher.encrypt(message));
	}

	@Test
	void encrypWithDiacritics() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "éèùîôöë+ÉÂÔÎÜÀÈÊ*ç'ûüùâéèôŸ";
		String encrypted ="GDGVD-XDGDV-DDGDG-FGXGF-FXGGG-FFGGG-GGGFG-XGGFF-GVGDG-DDV";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(encrypted, cypher.encrypt(message));
	}
	
	@Test
	void encrypWithNumbers() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "123456789";
		String encrypted ="VADFV-XGVDF-VAVVD-XFG";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(encrypted, cypher.encrypt(message));
	}
	
	@Test
	void encryptNullText() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertThrows(NullPointerException.class, () -> {
			cypher.encrypt(null);
		});
	}
	
	@Test
	void encryptEmptyText() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "";
		String encrypted ="";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(encrypted, cypher.encrypt(message));
	}
	
	/*
	 * DECRYPT TESTS
	 */
	@Test
	void testDecryptExample() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "VDGXX-VVXFV-GVXXX-XDFGD-GDAXX-DGFFG-DXGDG-FXGGG-GXXGV-DGG";
		String decrypted = "DEMANDERENFORTSDURGENCEC";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(decrypted, cypher.decrypt(message));
	}
	
	@Test
	void decryptNullText() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertThrows(NullPointerException.class, () -> {
			cypher.decrypt(null);
		});
	}
	
	@Test
	void decryptEmptyText() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "";
		String decrypted = "";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(decrypted, cypher.decrypt(message));
	}
	
	@Test
	void decryptShortMessage() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "VDFXG-D";
		String decrypted = "OUI";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(decrypted, cypher.decrypt(message));
	}
	
	@Test
	void decryptLongMessage() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "DFDGD-DXAXD-DVVXG-XAVDD-GDXXD-DDXDX-XGFGG-XFADX-FFFFG-DXDFF-GGDFG-XVFVX-FFFXV-FFFGX-XDGFG-ADXFD-XVVGV-VXDDA";
		String decrypted = "ILNYAPASDEMAUVAISESOUBONNESITUATIONVOUSSAVEZC";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(decrypted, cypher.decrypt(message));
	}
	
	@Test
	void decryptMessageLengthNotAMultipleOfTranspositionKeyLength() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "ADFGV-XADFG-VXADF";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertThrows(IllegalArgumentException.class, ()->{
			cypher.decrypt(message);
		});
	}
	
	@Test
	void decryptWithInvalidCharacters() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "ADFGV-X";
		String decrypted = "L2U";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(decrypted, cypher.decrypt(message));
	}
}
