package xor.lib;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SimpleXorStreamTest {

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test
	public void Simple() throws IOException {
		String original = "test";
		InputStream input = new ByteArrayInputStream(original.getBytes());
		InputStream pad1 = new ByteArrayInputStream("AAAA".getBytes());

		SimpleXorStream encryptingSimpleXorStream = new SimpleXorStream(input, pad1);

		byte[] encrypted = new byte[4];
		encryptingSimpleXorStream.read(encrypted);
		String encryptedMessage = new String(encrypted);
		assertNotEquals(original, encryptedMessage);


		InputStream encryptedInput = new ByteArrayInputStream(encrypted);
		InputStream pad2 = new ByteArrayInputStream("AAAA".getBytes());
		SimpleXorStream decryptingSimpleXorStream = new SimpleXorStream(encryptedInput, pad2);

		byte[] decrypted = new byte[4];
		decryptingSimpleXorStream.read(decrypted);
		String decryptedMessage = new String(decrypted);

		assertEquals(original, decryptedMessage);
	}

}
