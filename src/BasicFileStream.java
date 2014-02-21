import java.io.*;

/**
 * Reading past the end of the stream is an exception
 */
public class BasicFileStream implements CharStream {
    boolean have = false;
    int current = '\0';
    private BufferedReader reader = null;

    public BasicFileStream(String filename) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(filename));
    }

    @Override
    public boolean hasNext() throws IOException {
        readOptional();
        return have;
    }

    @Override
    public char next() throws IOException {
        readMandatory();
        have = false;
        return (char) current;
    }

    @Override
    public char peek() throws IOException {
        readMandatory();
        return (char) current;
    }

    @Override
    public void pop() throws IOException {
        if (have)
            have = false;
        else
           readOptional();
    }

    // throws on eof
    private void readMandatory() throws IOException {
        if (!have) {
            current = reader.read();
            if (current != -1)
                have = true;
            else
                throw new EOFException();
        }
    }

    // doesn't throw on eof
    private void readOptional() throws IOException {
        if (!have) {
            current = reader.read();
            if (current != -1)
                have = true;
        }
    }

    public void close() throws IOException {
        reader.close();
    }
}
