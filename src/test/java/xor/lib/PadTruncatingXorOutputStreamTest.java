package xor.lib;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class PadTruncatingXorOutputStreamTest {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Test
	public void test() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[4];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		final PadTruncatingXorOutputStream stream = new PadTruncatingXorOutputStream(result, xorFile, 0, false);

		stream.write("test".getBytes());
		result.close();
		assertEquals(116, result.toByteArray()[0]);
		assertEquals(101, result.toByteArray()[1]);
		assertEquals(115, result.toByteArray()[2]);
		assertEquals(116, result.toByteArray()[3]);

		assertEquals(0, xorFile.length());

		stream.close();
	}

	@Test(expected = InsufficientPadDataRuntimeException.class)
	public void Too_short() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[2];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		final PadTruncatingXorOutputStream stream = new PadTruncatingXorOutputStream(result, xorFile, 0, false);

		stream.write("t".getBytes());
		assertEquals(116, result.toByteArray()[0]);
		stream.write("e".getBytes());
		assertEquals(101, result.toByteArray()[1]);
		assertEquals(0, xorFile.length());
		stream.write("s".getBytes());

		result.close();
		stream.close();
	}

	@Test(expected = InsufficientPadDataRuntimeException.class)
	public void Too_short_with_offset() throws Exception {
		final File xorFile = temp.newFile();
		final byte[] b = new byte[2];
		FileUtils.writeByteArrayToFile(xorFile, b);

		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		final PadTruncatingXorOutputStream stream = new PadTruncatingXorOutputStream(result, xorFile, 1, false);

		stream.write("t".getBytes());
		assertEquals(116, result.toByteArray()[0]);
		assertEquals(0, xorFile.length());
		stream.write("e".getBytes());

		result.close();
		stream.close();
	}

	@Test
	public void OutputStream_truncates_pad_from_the_end() throws Exception {
		// Given
		byte[] pad = "1234567890".getBytes();
		File padFile = temp.newFile();
		FileUtils.writeByteArrayToFile(padFile, pad);

		PadTruncatingXorOutputStream padTruncatingXorInputStream = new PadTruncatingXorOutputStream(new ByteArrayOutputStream(), padFile, 0, false);

		// When
		// Read 2 bytes, consuming 2 bytes of pad
		padTruncatingXorInputStream.write(1);
		padTruncatingXorInputStream.write(1);

		// Then
		assertThat(padFile).hasSize(pad.length - 2);
		assertThat(padFile).hasBinaryContent("12345678".getBytes());
	}
}
