package xor.lib;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class XorTest {

	@Test
	public void test() throws Exception {
		byte[] plaintext = "plaintext".getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(plaintext);
		InputStream xorData = new TestingInputStream(new byte[]{(byte) 32});
		XoringInputStream stream = new XoringInputStream(inputStream, xorData, 0);

		String string = IOUtils.toString(stream, StandardCharsets.UTF_8);

		assertEquals(plaintext.length, string.length());
	}

}
