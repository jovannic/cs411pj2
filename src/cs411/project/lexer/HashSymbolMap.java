package cs411.project.lexer;

import java.util.HashMap;

/**
 * Created by Jovanni on 2/20/14.
 */
public class HashSymbolMap<T> implements SymbolMap<T> {
    HashMap<String, T> map= new HashMap<String, T>();

    @Override
    public T get(String symbol) {
        return map.get(symbol);
    }

    @Override
    public void add(String symbol, T value) {
        map.put(symbol, value);
    }

    @Override
    public T getOrAdd(String symbol, T value) {
        if (map.containsKey(symbol))
            return map.get(symbol);
        map.put(symbol, value);
        return value;
    }

    @Override
    public boolean contains(String symbol) {
        return map.containsKey(symbol);
    }
}
