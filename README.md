libxor
======

libxor is a Java library to help with XORing functionality.

This can be used to implement a <a href="https://en.wikipedia.org/wiki/One-time_pad">One-time Pad</a>.
A one time pad is only secure if

* the pad data is perfectly random
* no-one obtains a copy of the pad
* you never reuse a pad
* the pad is securely destroyed after the pad is used.

It is recommended to use the pad truncating versions of the streams.

XoringInputStream and XoringOutputStream.
These take an pad file, and can wrap other Streams (FileInputStream, network socket streams, etc).
When data is written/read from the stream, the content is XORed against the data in the pad file.

To encourage the non-reuse of One Time Pads, there are the PadTruncatingXorInputStream and PadTruncatingXorInputStream.
Using these classes will cause the pad file to be optionally overwritten with random data, and truncated as it is used.
Ideally, this would mean the pad can't be reused or recovered.
However, if your filesystem uses caching, journalling, or wear-levelling though, (such as most solid state drives these
days) the original pad <a href="https://en.wikipedia.org/wiki/Data_erasure#Limitations">may still be recoverable</a>.

Enabling the optional overwriting with random data flag makes operations a lot slower.

If you run out of pad data, an InsufficientXorDataRuntimeException is thrown.

Usage
=====

<pre>
File sourceFile = new File("/etc/passwd");
FileInputStream source = new FileInputStream(sourceFile);

Random random = new Random();
byte[] pad = new byte[(int) sourceFile.length()];
random.nextBytes(pad);

// Write two copies of pad, as they get deleted after use
FileUtils.writeByteArrayToFile(new File("/tmp/pad1"), pad);
FileUtils.writeByteArrayToFile(new File("/tmp/pad2"), pad);

PadTruncatingXorInputStream padTruncatingXorInputStream = new PadTruncatingXorInputStream(source, new File("/tmp/pad1"), 0, true);
FileUtils.copyInputStreamToFile(padTruncatingXorInputStream, new File("/tmp/encrypted"));

PadTruncatingXorOutputStream padTruncatingXorOutputStream = new PadTruncatingXorOutputStream(new FileOutputStream("/tmp/decrypted"), new File("/tmp/pad2"), 0, true);
padTruncatingXorOutputStream.write(FileUtils.readFileToByteArray(new File("/tmp/encrypted")));
</pre>

Example CLI tools for sending files over a network
<pre>
cd cli
./PadGenerator.sh /tmp/pad 1024000
./FileReceiver.sh /tmp/pad 0 /tmp/received 5000
./FileSender.sh /tmp/pad 0 /file/to/send 127.0.0.1 5000
./FileXorer.sh /tmp/pad /input/file /output/file
</pre>
