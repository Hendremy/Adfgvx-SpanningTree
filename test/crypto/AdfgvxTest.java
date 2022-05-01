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

}
