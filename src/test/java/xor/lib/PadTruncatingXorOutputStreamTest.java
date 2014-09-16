package xor.lib;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PadTruncatingXorOutputStreamTest {

	@Rule
	public TemporaryFolder	temp	= new TemporaryFolder();

	@Test
	public void test() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[4];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		final PadTruncatingXorOutputStream stream = new PadTruncatingXorOutputStream(result, xorFile, 0);

		stream.write("test".getBytes());
		result.close();
		assertEquals(116, result.toByteArray()[0]);
		assertEquals(101, result.toByteArray()[1]);
		assertEquals(115, result.toByteArray()[2]);
		assertEquals(116, result.toByteArray()[3]);

		assertEquals(0, xorFile.length());

		stream.close();
	}

	@Test(expected = InsufficientXorDataRuntimeException.class)
	public void Too_short() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[2];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		final PadTruncatingXorOutputStream stream = new PadTruncatingXorOutputStream(result, xorFile, 0);

		stream.write("t".getBytes());
		assertEquals(116, result.toByteArray()[0]);
		stream.write("e".getBytes());
		assertEquals(101, result.toByteArray()[1]);
		assertEquals(0, xorFile.length());
		stream.write("s".getBytes());

		result.close();
		stream.close();
	}

	@Test(expected = InsufficientXorDataRuntimeException.class)
	public void Too_short_with_offset() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[2];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		final PadTruncatingXorOutputStream stream = new PadTruncatingXorOutputStream(result, xorFile, 1);

		stream.write("t".getBytes());
		assertEquals(116, result.toByteArray()[0]);
		assertEquals(0, xorFile.length());
		stream.write("e".getBytes());

		result.close();
		stream.close();
	}
}
