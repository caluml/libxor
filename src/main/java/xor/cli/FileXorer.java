package xor.cli;

import xor.lib.PadTruncatingXorOutputStream;
import xor.lib.XoringOutputStream;

import java.io.*;

public class FileXorer {

	/**
	 * A simple file XORer.
	 * NOTE: If the 2nd argument is true, this uses the PadTruncatingXorOutputStream, which deletes the pad after it's
	 * used. This means that you won't be able to decrypt data encrypted with this unless you make a copy of the pad
	 * first.
	 * <p>
	 * Pad, truncate pad?, Source file, Output file,
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 5) {
			System.err.println("Usage: FileXorer <pad> <truncate pad?> <input file> <output file>");
			System.exit(1);
		}
		File pad = new File(args[0]);
		if (!pad.canRead()) {
			throw new RuntimeException("Can't read from " + pad.getAbsolutePath());
		}

		File input = new File(args[2]);
		if (!input.canRead()) {
			throw new RuntimeException("Can't read from " + input.getAbsolutePath());
		}

		File output = new File(args[3]);
		if (output.exists()) {
			throw new RuntimeException(output.getAbsolutePath() + " already exists");
		}
		if (!output.createNewFile() || !output.canWrite()) {
			throw new RuntimeException("Can't write to " + output.getAbsolutePath());
		}

		FileOutputStream fileOutputStream = new FileOutputStream(output);
		OutputStream xoringOutputStream;
		if (Boolean.parseBoolean(args[1])) {
			xoringOutputStream = new PadTruncatingXorOutputStream(fileOutputStream, pad, 0, true);
			System.out.println("Using the PadTruncatingXorOutputStream - this will be slow.");
		} else {
			xoringOutputStream = new XoringOutputStream(fileOutputStream, pad, 0);
		}

		FileInputStream fileInputStream = new FileInputStream(input);

		int read;
		while ((read = fileInputStream.read()) != -1) {
			xoringOutputStream.write(read);
		}

		xoringOutputStream.close();
	}

}
