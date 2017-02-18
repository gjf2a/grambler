package edu.hendrix.grambler.grammars;

public class SimpleMathSpaceBug extends edu.hendrix.grambler.Grammar {
    public SimpleMathSpaceBug() {
        super();
        addProduction("top", new String[]{"sp", "sum", "sp"});
        addProduction("sum", new String[]{"sum", "sp", "op", "sp", "number"}, new String[]{"number"});
        addProduction("sp", new String[]{"\"\\s*\""});
        addProduction("op", new String[]{"'+'"});
        addProduction("number", new String[]{"\"\\d+\""});
    }
}

