package xor.lib;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PadTruncatingTest {

	@Rule
	public TemporaryFolder	temp	= new TemporaryFolder();

	@Test
	public void Encrypt_and_decrypt() throws Exception {
		final byte[] originalData = "test".getBytes();
		final File inputXorFile = temp.newFile();
		final File outputXorFile = temp.newFile();
		final byte[] b = new byte[originalData.length];
		b[0] = (byte) 123;
		b[0] = (byte) 14;
		b[0] = (byte) 91;
		b[0] = (byte) 255;
		FileUtils.writeByteArrayToFile(inputXorFile, b);
		FileUtils.writeByteArrayToFile(outputXorFile, b);

		final PadTruncatingXorInputStream inputStream = new PadTruncatingXorInputStream(new ByteArrayInputStream(
		        originalData), inputXorFile, 0);

		final byte[] inputData = new byte[originalData.length];
		inputStream.read(inputData);
		inputStream.close();

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final PadTruncatingXorOutputStream outputStream = new PadTruncatingXorOutputStream(byteArrayOutputStream,
				outputXorFile, 0);

		outputStream.write(inputData);
		outputStream.close();

		Assert.assertArrayEquals(originalData, byteArrayOutputStream.toByteArray());
		assertEquals(0, inputXorFile.length());
		assertEquals(0, outputXorFile.length());

		byteArrayOutputStream.close();
	}
}
