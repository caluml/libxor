package xor.lib;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class XoringInputStreamTest {

	private final Random random = new Random();

	@Test
	public void InputStreams_encrypt_and_decrypt() throws Exception {
		byte[] original = "Фото".getBytes(StandardCharsets.UTF_8);
		byte[] xorData = getXorData(original.length);

		XoringInputStream xoringInputStream = new XoringInputStream(new ByteArrayInputStream(original),
				new ByteArrayInputStream(xorData), 0);

		byte[] cipher = copyArray(original);
		xoringInputStream.read(cipher);
		// It must be different here
		assertNotEquals("Ciphertext should be different from original", new String(original), new String(cipher));

		XoringInputStream xoringInputStream2 = new XoringInputStream(new ByteArrayInputStream(cipher),
				new ByteArrayInputStream(xorData), 0);

		byte[] result = copyArray(cipher);
		xoringInputStream2.read(result);
		// And back to the origin here.
		assertArrayEquals("The resulting bytes should be the same as the original now.", original, result);
	}

	@Test
	public void Differing_XOR_data_causes_difference() throws Exception {
		byte[] original = "Фото".getBytes(StandardCharsets.UTF_8);
		byte[] xorData1 = getXorData(original.length);
		byte[] xorData2 = getXorData(original.length);

		XoringInputStream xoringInputStream = new XoringInputStream(new ByteArrayInputStream(original),
				new ByteArrayInputStream(xorData1), 0);

		byte[] cipher = copyArray(original);
		xoringInputStream.read(cipher);
		// It must be different here
		assertNotEquals("Ciphertext should be different from original", new String(original), new String(cipher));

		XoringInputStream xoringInputStream2 = new XoringInputStream(new ByteArrayInputStream(original),
				new ByteArrayInputStream(xorData2), 0);

		byte[] result = copyArray(cipher);
		xoringInputStream2.read(result);
		// And back to the origin here.
		assertNotEquals("Ciphertext should still be different from original", new String(original), new String(result));
	}

	@Test(expected = InsufficientPadDataRuntimeException.class)
	public void XorData_too_short_causes_exception() throws Exception {
		byte[] original = "Фото".getBytes(StandardCharsets.UTF_8);
		// XOR data is too short.
		byte[] xorData = new byte[1];
		random.nextBytes(xorData);

		XoringInputStream xoringInputStream = new XoringInputStream(new ByteArrayInputStream(original),
				new ByteArrayInputStream(xorData), 0);

		byte[] cipher = copyArray(original);
		xoringInputStream.read(cipher);
	}

	@Test
	public void OutputStream_xors_data_and_InputStream_reverses_it() throws Exception {
		byte[] original = "test".getBytes(StandardCharsets.UTF_8);
		byte[] xorData = getXorData(original.length);
		ByteArrayInputStream xorIs1 = new ByteArrayInputStream(xorData);
		ByteArrayInputStream xorIs2 = new ByteArrayInputStream(xorData);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XoringOutputStream xoringOutputStream = new XoringOutputStream(byteArrayOutputStream, xorIs1, 0);

		IOUtils.write(original, xoringOutputStream);

		byte[] byteArray = byteArrayOutputStream.toByteArray();
		assertNotEquals("Ciphertext should be different from original", new String(original), new String(byteArray));

		XoringInputStream xoringInputStream = new XoringInputStream(new ByteArrayInputStream(byteArray), xorIs2, 0);

		byte[] result = IOUtils.toByteArray(xoringInputStream);

		assertArrayEquals(original, result);
	}

	@Test
	public void InputStream_returns_minusone_when_the_source_InputStream_is_empty() throws Exception {
		byte[] original = "test".getBytes(StandardCharsets.UTF_8);
		byte[] xorData = getXorData(original.length * 2);

		XoringInputStream xoringInputStream = new XoringInputStream(new ByteArrayInputStream(original),
				new ByteArrayInputStream(xorData), 0);

		for (@SuppressWarnings("unused") byte element : original) {
			xoringInputStream.read();
		}

		int read = xoringInputStream.read();
		assertEquals(-1, read);
	}

	@Test
	public void XoringInputStream_behaves_like_a_stream() throws Exception {
		byte[] original = "AA".getBytes(StandardCharsets.UTF_8);
		byte[] xorData = "A ".getBytes(StandardCharsets.UTF_8);

		XoringInputStream xoringInputStream = new XoringInputStream(new ByteArrayInputStream(original),
				new ByteArrayInputStream(xorData), 0);

		assertTrue(xoringInputStream.available() > 0);
		assertEquals(0, xoringInputStream.read());
		assertTrue(xoringInputStream.available() > 0);
		assertEquals(97, xoringInputStream.read());
		assertTrue(xoringInputStream.available() == 0);
		assertEquals(-1, xoringInputStream.read());
	}

	@Test(timeout = 200)
	public void IOUtils_can_copy_from_XoringInputStream() throws Exception {
		byte[] original = "test".getBytes(StandardCharsets.UTF_8);
		byte[] xorData = getXorData(original.length);

		XoringInputStream xoringInputStream = new XoringInputStream(
				new ByteArrayInputStream(original),
				new ByteArrayInputStream(xorData), 0);

		ByteArrayOutputStream output = new ByteArrayOutputStream(4);
		IOUtils.copy(xoringInputStream, output);

		byte[] outputBytes = output.toByteArray();
		assertTrue("Should be some data", outputBytes.length > 0);
		assertEquals(original.length, outputBytes.length);
	}

	private byte[] copyArray(byte[] bytes) {
		byte[] result = new byte[bytes.length];
		System.arraycopy(bytes, 0, result, 0, bytes.length);
		return result;
	}

	private byte[] getXorData(int length) {
		byte[] xorData = new byte[length];
		random.nextBytes(xorData);
		return xorData;
	}

}
