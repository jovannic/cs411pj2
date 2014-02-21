import java.util.Arrays;

public class TrieMap<T> implements SymbolMap<T> {
    public static final char DELIMITER = '\0';
    public static final String ALPHABET = "ABCDEFGHIJKLMONPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_$";

    private int[] start = new int[128]; // aka switch: char -> index in symbol

    private int size = 0;
    private char[] symbol = new char[1024];
    private int[] next = new int[1024]; // index in symbol -> index in symbol
    private Object[] data = new Object[1024];

    public TrieMap() {
        Arrays.fill(start, -1);
    }

    private FindResult find(String identifier) {
        char c = identifier.charAt(0);
        // Start index
        int ptr = start[c];

        // If undefined start, definitely new identifier
        if (ptr == -1) {
            start[c] = size; // adding symbol at the end
            return new FindResult(identifier.substring(1), size, false);
        }

        // For each remaining character in the identifier...
        int n = identifier.length();
        for (int i = 1; i < n; i++) {
            c = identifier.charAt(i);

            if (symbol[ptr] == c) {
                ptr++; // Continue alone while symbol matches...
            } else {
                // If no match, try the next chain...
                // While there's a next and we haven't found the next symbol...
                while (next[ptr] != 0 && symbol[ptr] != c)
                    ptr = next[ptr];

                if (symbol[ptr] == c)
                    ptr++; // Found match on next chain, continue...
                else {
                    // Exhausted the next chain and still no complete match
                    return new FindResult(identifier.substring(i), ptr, false);
                }
            }
        }
        // Identifier ended

        // Until symbol ended or the next list is exhausted...
        while (next[ptr] != 0 && !isDelim(symbol[ptr]))
            ptr = next[ptr];

        c = symbol[ptr];
        if (isDelim(c)) {
            // Symbol ended, match found
            return new FindResult("", ptr, true);
        } else {
            // Identifier is a substring of another, add a new shorter symbol
            return new FindResult("", ptr, false);
        }
    }

    // Finish adding a new identifier from the give index in symbol
    private void create(String remaining, int fromSymbolIndex, T value) {
        // Assume calling lookup exhausted the next list

        // Need to mark next if not starting at the end (new first char)
        if (fromSymbolIndex != size)
            next[fromSymbolIndex] = size;

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
        return f.match ? (T) data[f.symbolIndex] : null;
    }

    @Override
    public void add(String symbol, T value) {
        FindResult f = find(symbol);
        if (!f.match)
            create(f.remaining, f.symbolIndex, value);
    }

    @Override
    public T getOrAdd(String symbol, T value) {
        FindResult f = find(symbol);
        if (f.match)
            return (T) data[f.symbolIndex];
        create(f.remaining, f.symbolIndex, value);
        return value;
    }

    @Override
    public boolean contains(String symbol) {
        return find(symbol).match;
    }

    private class FindResult {
        public final String remaining;
        public final int symbolIndex;
        public final boolean match;
        public FindResult(String remaining, int symbolIndex, boolean match) {
            this.remaining = remaining;
            this.symbolIndex = symbolIndex;
            this.match = match;
        }
    }
}
