package xor.lib;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class XorTest {

	@SuppressWarnings("resource")
	@Test
	public void test() throws Exception {
		byte[] plaintext = "plaintext".getBytes("UTF-8");
		InputStream inputStream = new ByteArrayInputStream(plaintext);
		InputStream xorData = new TestingInputStream(new byte[] { (byte) 32 });
		XoringInputStream stream = new XoringInputStream(inputStream, xorData, 0);

		String string = IOUtils.toString(stream);

		assertEquals(plaintext.length, string.length());
	}

}
