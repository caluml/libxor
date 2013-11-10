package xor.lib;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import org.junit.Test;

public class XoringInputStreamTest {

	private final Random random = new Random();

	@SuppressWarnings("resource")
	@Test
	public void InputStreams_encrypt_and_decrypt() throws Exception {
		byte[] original = "Фото".getBytes("UTF-8");
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
		byte[] original = "Фото".getBytes("UTF-8");
		byte[] xorData1 = getXorData(original.length);
		byte[] xorData2 = getXorData(original.length);

		@SuppressWarnings("resource")
		XoringInputStream xoringInputStream = new XoringInputStream(new ByteArrayInputStream(original),
				new ByteArrayInputStream(xorData1), 0);

		byte[] cipher = copyArray(original);
		xoringInputStream.read(cipher);
		// It must be different here
		assertNotEquals("Ciphertext should be different from original", new String(original), new String(cipher));

		@SuppressWarnings("resource")
		XoringInputStream xoringInputStream2 = new XoringInputStream(new ByteArrayInputStream(original),
				new ByteArrayInputStream(xorData2), 0);

		byte[] result = copyArray(cipher);
		xoringInputStream2.read(result);
		// And back to the origin here.
		assertNotEquals("Ciphertext should still be different from original", new String(original), new String(result));
	}

	@SuppressWarnings("resource")
	@Test(expected = InsufficientXorDataRuntimeException.class)
	public void XorData_too_short_causes_exception() throws Exception {
		byte[] original = "Фото".getBytes("UTF-8");
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
		byte[] original = "test".getBytes("UTF-8");
		byte[] xorData = getXorData(original.length);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		@SuppressWarnings("resource")
		XoringOutputStream xoringOutputStream = new XoringOutputStream(byteArrayOutputStream, new ByteArrayInputStream(
				xorData), 0);

		xoringOutputStream.write(original);

		byte[] byteArray = byteArrayOutputStream.toByteArray();
		assertNotEquals("Ciphertext should be different from original", new String(original), new String(byteArray));

		@SuppressWarnings("resource")
		XoringInputStream xoringInputStream = new XoringInputStream(new ByteArrayInputStream(byteArray),
				new ByteArrayInputStream(xorData), 0);

		byte[] result = copyArray(byteArray);
		xoringInputStream.read(result);

		assertArrayEquals(original, result);
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
