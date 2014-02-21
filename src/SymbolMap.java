
public interface SymbolMap<T> {
    T get(String symbol);
    void add(String symbol, T value);
    T getOrAdd(String symbol, T value);
    boolean contains(String symbol);
}
