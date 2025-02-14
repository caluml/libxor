package xor.lib;


import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class EndToEndTest {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Test
	public void End_to_end_test() throws Exception {
		// Given
		// Generate pad
		Random random = new Random(12345678); // Make it deterministic for the test
		byte[] pad = new byte[1024];
		random.nextBytes(pad);

		// Write two copies of pad
		File pad1 = temp.newFile("pad1");
		File pad2 = temp.newFile("pad2");
		FileUtils.writeByteArrayToFile(pad1, pad);
		FileUtils.writeByteArrayToFile(pad2, pad);

		// Create message payload
		byte[] payload = new byte[256];
		random.nextBytes(payload);

		// When
		// Encrypt it
		PadTruncatingXorInputStream padTruncatingXorInputStream = new PadTruncatingXorInputStream(new ByteArrayInputStream(payload), pad1, 0, false);
		byte[] encrypted = new byte[256];
		padTruncatingXorInputStream.read(encrypted);

		// Decrypt it
		ByteArrayOutputStream decrypted = new ByteArrayOutputStream();
		PadTruncatingXorOutputStream padTruncatingXorOutputStream = new PadTruncatingXorOutputStream(decrypted, pad2, 0, false);
		padTruncatingXorOutputStream.write(encrypted);

		// Then
		assertThat(decrypted.toByteArray()).containsExactly(payload);
		assertThat(pad1.length()).isEqualTo(pad.length - payload.length);
		assertThat(pad2.length()).isEqualTo(pad.length - payload.length);
	}

}
