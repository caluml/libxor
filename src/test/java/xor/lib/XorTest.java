package xor.lib;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class XorTest {

	@SuppressWarnings("resource")
	@Test
	public void test() throws Exception {
		File file = new File("/etc/group");
		InputStream inputStream = new FileInputStream(file);
		InputStream xorData = new TestingInputStream(new byte[] { (byte) 32 });
		XoringInputStream stream = new XoringInputStream(inputStream, xorData, 0);

		String string = IOUtils.toString(stream);
		System.out.println(string);

		assertEquals(file.length(), string.length());
	}

}
