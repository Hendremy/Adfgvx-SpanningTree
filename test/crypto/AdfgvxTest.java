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
	void invalidSubstitutionKey() {
		Exception illegalArgExc = assertThrows(IllegalArgumentException.class, ()->{
			new Adfgvx("abc","BRUTES");
		});
		
		assertNotNull(illegalArgExc);
	}
	
	@Test
	void tooShortTranspositionKey() {
		Exception illegalArgExc = assertThrows(IllegalArgumentException.class, ()->{
			new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","B");
		});
		
		assertNotNull(illegalArgExc);
	}
	
	@Test
	void repetitionTranspositionKey() {
		Exception illegalArgExc = assertThrows(IllegalArgumentException.class, ()->{
			new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","B");
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

	//TODO: + de tests
	
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
	void decryptShortMessage() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "VDFXG-D";
		String decrypted = "OUI";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(decrypted, cypher.decrypt(message).substring(0, decrypted.length()));
	}
	
	@Test
	void decryptLongMessage() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "DFDGD-DXAXD-DVVXG-XAVDD-GDXXD-DDXDX-XGFGG-XFADX-FFFFG-DXDFF-GGDFG-XVFVX-FFFXV-FFFGX-XDGFG-ADXFD-XVVGV-VXDDA";
		String decrypted = "ILNYAPASDEMAUVAISESOUBONNESITUATIONVOUSSAVEZ";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(decrypted, cypher.decrypt(message).substring(0, decrypted.length()));
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

}
