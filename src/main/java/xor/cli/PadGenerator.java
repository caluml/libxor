package xor.cli;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class PadGenerator {

	private static final long INTERVAL_BYTES = (1024 * 1024) / 4;

	/**
	 * Generates a one-time pad.
	 * Pad, length.
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: PadGenerator <output file> <size>");
			System.exit(1);
		}
		File pad = new File(args[0]);
		if (pad.exists()) {
			throw new RuntimeException(pad.getAbsolutePath() + " already exists");
		}
		if (!pad.createNewFile() || !pad.canWrite()) {
			throw new RuntimeException("Can't write to " + pad.getAbsolutePath());
		}

		long length = Long.parseLong(args[1]);

		SecureRandom secureRandom = new SecureRandom();
		FileOutputStream fileOutputStream = new FileOutputStream(pad);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

		int bytes = 0;
		System.out.println("Starting writing. Large amounts of random data may take a while.");
		long start = System.currentTimeMillis();
		while (bytes < length) {
			bufferedOutputStream.write(secureRandom.nextInt());
			bytes++;
			if (bytes % INTERVAL_BYTES == 0) {
				System.out.println((100f / length) * bytes + "%");
			}
		}
		System.out.println("Wrote " + bytes + " to " + pad + " in " + (System.currentTimeMillis() - start + " ms"));
		bufferedOutputStream.flush();
		bufferedOutputStream.close();
	}
}
