libxor
======

libxor is a Java library to help with XORing functionality.

It consists of XoringInputStream and XoringOutputStream.
These take an XOR data file, and can wrap other Streams (FileInputStream, network socket streams, etc).
When data is written/read from the stream, the content is XORed against the data in the XOR data file.

If you run out of pad data, an InsufficientXorDataRuntimeException is thrown.

This can be used to implement a <a href="https://en.wikipedia.org/wiki/One-time_pad">One-time Pad</a>.
Remember, if you use a one time pad, they are only secure if you never reuse a pad, if the pad data is perfectly random, and if the pad is securely destroyed after the pad is used.

To encourage the non-reuse of One Time Pads, there are the PadTruncatingXorInputStream and PadTruncatingXorInputStream.
Using these classes will cause the pad file to be overwritten with random data, and truncated as it is used. This is a lot slower, but means the pad can't be reused.
If your filesystem uses caching, journalling, or wear-levelling though, the original pad <a href="https://en.wikipedia.org/wiki/Data_erasure#Limitations">may still be recoverable</a>.

Usage
=====

<pre>
FileInputStream source = new FileInputStream("/etc/passwd");
InputStream xorData = new FileInputStream("/your/pad/file");

XoringInputStream xoringInputStream = new XoringInputStream(source, xorData, 0);

FileUtils.copyInputStreamToFile(xoringInputStream, new File("/tmp/output"));

// Remember to delete /your/pad/file now.
</pre>

Example CLI tools for sending files over a network
<pre>
mvn clean package
pad generator: java -cp target/classes/ xor.cli.PadGenerator /tmp/pad 1024000
simple xorer : java -cp target/classes/ xor.cli.Xorer false /tmp/pad /etc/passwd /tmp/xorpasswd

receiver     : java -cp target/classes/ xor.cli.Receiver /my/random-pad 0 /tmp/received 5000
sender       : java -cp target/classes/ xor.cli.Sender /my/random-pad 0 /file/to/send 127.0.0.1 5000
receiver     : shred -uvz /my/random-pad
sender       : shred -uvz /my/random-pad
</pre>