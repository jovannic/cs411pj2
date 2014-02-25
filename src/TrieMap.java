import java.util.Arrays;

/**
 * Trie symbol map, supports full symbol, plus iterative searching 
 * @author Jovanni Cutigni
 */
public class TrieMap<T> implements SymbolMap<T> {
    private static final char DELIMITER = '\0';
    private static final String ALPHABET = "ABCDEFGHIJKLMONPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_$";

    private int[] start = new int[128]; // aka switch: char -> index in symbol

    private int size = 0;
    private char[] symbol = new char[1024];
    private int[] next = new int[1024]; // index in symbol -> index in symbol
    private Object[] data = new Object[1024];

    public TrieMap() {
        Arrays.fill(start, -1);
    }

    public class TrieSearch {
        private int index;
        private boolean match = false;
        private boolean ended;

        private TrieSearch(int index) {
            this.index = index;
            ended = index == -1;
        }

        public TrieSearch append(char c) {
            if (ended)
                return this;

            if (symbol[index] == c) {
                index++; // Continue along while symbol matches...
            } else {
                // If no match, try the next chain...
                // While there's a next and we haven't found the next symbol...
                while (next[index] != 0 && symbol[index] != c)
                    index = next[index];

                if (symbol[index] == c)
                    index++; // Found match on next chain, continue...
                else {
                    // Exhausted the next chain and still no complete match
                    ended = true; // appending after this leads to an invalid state
                }
            }

            return this;
        }

        public TrieSearch end() {
            if (ended)
                return this;

            // Until symbol hasEnded or the next list is exhausted...
            while (next[index] != 0 && !isDelim(symbol[index]))
                index = next[index];

            char c = symbol[index];
            if (isDelim(c)) {
                // Symbol hasEnded, match found
                match = true;
            } else {
                // Identifier is a substring of another, add a new shorter symbol
                match = false;
            }
            ended = true;

            return this;
        }

        public boolean hasNext(char c) {
            if (ended)
                return false;

            int i = index;
            while (next[i] != 0 && symbol[i] != c)
                i = next[i];

            return symbol[i] == c;
        }

        public boolean hasNextEnd() {
            if (ended)
                return false;

            int i = index;
            while (next[i] != 0 && isDelim(symbol[i]))
                i = next[i];

            return isDelim(symbol[i]);
        }

        public boolean hasEnded() {
            return ended;
        }

        public boolean isMatch() {
            return ended && match;
        }

        public T getData() {
            // data only valid when hasEnded, on match
            return match ? (T) data[index] : null;
        }
    }

    // iterative find
    public TrieSearch find(char first) {
        return new TrieSearch(start[first]);
    }

    private FindResult find(String identifier) {
        char c = identifier.charAt(0);
        // Start index
        int index = start[c];

        // If undefined start, definitely new identifier
        if (index == -1) {
            // TODO: Fix bug where this should not be called unless create
            start[c] = size; // adding symbol at the end
            return new FindResult(identifier.substring(1), size, false);
        }

        // For each remaining character in the identifier...
        int n = identifier.length();
        for (int i = 1; i < n; i++) {
            c = identifier.charAt(i);

            if (symbol[index] == c) {
                index++; // Continue along while symbol matches...
            } else {
                // If no match, try the next chain...
                // While there's a next and we haven't found the next symbol...
                while (next[index] != 0 && symbol[index] != c)
                    index = next[index];

                if (symbol[index] == c)
                    index++; // Found match on next chain, continue...
                else {
                    // Exhausted the next chain and still no complete match
                    return new FindResult(identifier.substring(i), index, false);
                }
            }
        }
        // Identifier ended

        // Until symbol ended or the next list is exhausted...
        while (next[index] != 0 && !isDelim(symbol[index]))
            index = next[index];

        c = symbol[index];
        if (isDelim(c)) {
            // Symbol ended, match found
            return new FindResult("", index, true);
        } else {
            // Identifier is a substring of another, add a new shorter symbol
            return new FindResult("", index, false);
        }
    }

    // Finish adding a new identifier from the give index in symbol
    private void create(String remaining, int fromIndex, T value) {
        // Assume calling lookup exhausted the next list

        // Need to mark next if not starting at the end (new first char)
        if (fromIndex != size)
            next[fromIndex] = size;

        ensureCapacity(size + (remaining.length() + 1));

        // Copy the rest of the identifier to the symbol table
        int n = remaining.length();
        for (int i = 0; i < n; i++)
            symbol[size++] = remaining.charAt(i);

        symbol[size] = DELIMITER;
        data[size] = value;
        ++size;
    }

    private void ensureCapacity(int minSize) {
        int oldSize = symbol.length;
        if (oldSize > minSize)
            return;

        int n = oldSize * 2;
        while (n < minSize)
            n *= 2;

        symbol = Arrays.copyOf(symbol, n);
        next = Arrays.copyOf(next, n);
        data = Arrays.copyOf(data, n);
    }

    public void printSymbolTable(int width) {
        width -= 8; // labels
        int perLine = width / 4;

        for (int lineStart = 0; lineStart < size; lineStart += perLine) {
            int lineEnd = lineStart + perLine;
            if (lineEnd > size)
                lineEnd = size;

            System.out.print("        ");
            for (int j = lineStart; j < lineEnd; j++)
                System.out.format("%4d", j);
            System.out.print("\nsymbol: ");
            for (int j = lineStart; j < lineEnd; j++)
                System.out.format("%4s", !isDelim(symbol[j]) ? symbol[j] : "END");
//            System.out.print("\ndata  : ");
//            for (int j = lineStart; j < lineEnd; j++)
//                System.out.format("%4s", data[j]);
            System.out.print("\nnext:   ");
            for (int j = lineStart; j < lineEnd; j++)
                if (next[j] != 0)
                    System.out.format("%4d", next[j]);
                else
                    System.out.print("    ");
            System.out.println("\n");
        }
    }

    public void printSwitchTable(int width) {
        width -= 8; // labels
        int perLine = width / 4;

        int n = ALPHABET.length();
        for (int lineStart = 0; lineStart < n; lineStart += perLine) {
            int lineEnd = lineStart + perLine;
            if (lineEnd > n)
                lineEnd = n;

            System.out.print("        ");
            for (int j = lineStart; j < lineEnd; j++)
                System.out.format("%4c", ALPHABET.charAt(j));
            System.out.print("\nswitch: ");
            for (int j = lineStart; j < lineEnd; j++)
                System.out.format("%4d", start[ALPHABET.charAt(j)]);
            System.out.println("\n");
        }
    }

    private static boolean isDelim(char c) {
        return c == DELIMITER;
    }

    @Override
    public T get(String symbol) {
        FindResult f = find(symbol);
        return f.match ? (T) data[f.index] : null;
    }

    @Override
    public void add(String symbol, T value) {
        FindResult f = find(symbol);
        if (!f.match)
            create(f.remaining, f.index, value);
        else
            data[f.index] = value;
    }

    @Override
    public T getOrAdd(String symbol, T value) {
        FindResult f = find(symbol);
        if (f.match)
            return (T) data[f.index];
        create(f.remaining, f.index, value);
        return value;
    }

    @Override
    public boolean contains(String symbol) {
        return find(symbol).match;
    }

    private class FindResult {
        public final String remaining;
        public final int index;
        public final boolean match;
        public FindResult(String remaining, int index, boolean match) {
            this.remaining = remaining;
            this.index = index;
            this.match = match;
        }
    }
}
