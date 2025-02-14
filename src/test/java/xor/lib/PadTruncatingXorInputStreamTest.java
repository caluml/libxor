package xor.lib;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class PadTruncatingXorInputStreamTest {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Test
	public void test() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[4];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final InputStream inputStream = new ByteArrayInputStream("test".getBytes());
		final PadTruncatingXorInputStream stream = new PadTruncatingXorInputStream(inputStream, xorFile, 0, false);

		assertEquals(116, stream.read());
		assertEquals(101, stream.read());
		assertEquals(115, stream.read());
		assertEquals(116, stream.read());

		assertEquals(0, xorFile.length());

		stream.close();
	}

	@Test(expected = InsufficientPadDataRuntimeException.class)
	public void Too_short() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[2];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final InputStream inputStream = new ByteArrayInputStream("test".getBytes());
		final PadTruncatingXorInputStream stream = new PadTruncatingXorInputStream(inputStream, xorFile, 0, false);

		assertEquals(116, stream.read());
		assertEquals(101, stream.read());
		assertEquals(0, xorFile.length());
		stream.read();
	}

	@Test(expected = InsufficientPadDataRuntimeException.class)
	public void Too_short_with_offset() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[2];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final InputStream inputStream = new ByteArrayInputStream("test".getBytes());
		final PadTruncatingXorInputStream stream = new PadTruncatingXorInputStream(inputStream, xorFile, 1, false);

		assertEquals(116, stream.read());
		assertEquals(0, xorFile.length());
		stream.read();
	}

	@Test
	public void InputStream_truncates_pad_from_the_end() throws Exception {
		// Given
		byte[] pad = "1234567890".getBytes();
		File padFile = temp.newFile();
		FileUtils.writeByteArrayToFile(padFile, pad);

		PadTruncatingXorInputStream padTruncatingXorInputStream = new PadTruncatingXorInputStream(new ByteArrayInputStream("xx".getBytes()), padFile, 0, false);

		// When
		// Read 2 bytes, consuming 2 bytes of pad
		padTruncatingXorInputStream.read();
		padTruncatingXorInputStream.read();

		// Then
		assertThat(padFile).hasSize(pad.length - 2);
		assertThat(padFile).hasBinaryContent("12345678".getBytes());
	}
}
