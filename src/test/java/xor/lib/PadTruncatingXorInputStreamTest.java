package xor.lib;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PadTruncatingXorInputStreamTest {

	@Rule
	public TemporaryFolder	temp	= new TemporaryFolder();

	@Test
	public void test() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[4];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final InputStream inputStream = new ByteArrayInputStream("test".getBytes());
		final PadTruncatingXorInputStream stream = new PadTruncatingXorInputStream(inputStream, xorFile, 0);

		assertEquals(116, stream.read());
		assertEquals(101, stream.read());
		assertEquals(115, stream.read());
		assertEquals(116, stream.read());

		assertEquals(0, xorFile.length());

		stream.close();
	}

	@Test(expected = InsufficientXorDataRuntimeException.class)
	public void Too_short() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[2];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final InputStream inputStream = new ByteArrayInputStream("test".getBytes());
		final PadTruncatingXorInputStream stream = new PadTruncatingXorInputStream(inputStream, xorFile, 0);

		assertEquals(116, stream.read());
		assertEquals(101, stream.read());
		assertEquals(0, xorFile.length());
		stream.read();
	}

	@Test(expected = InsufficientXorDataRuntimeException.class)
	public void Too_short_with_offset() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[2];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final InputStream inputStream = new ByteArrayInputStream("test".getBytes());
		final PadTruncatingXorInputStream stream = new PadTruncatingXorInputStream(inputStream, xorFile, 1);

		assertEquals(116, stream.read());
		assertEquals(0, xorFile.length());
		stream.read();
	}
}
