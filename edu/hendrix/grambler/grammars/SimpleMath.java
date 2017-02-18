package edu.hendrix.grambler.grammars;

public class SimpleMath extends edu.hendrix.grambler.Grammar {
    public SimpleMath() {
        super();
        addProduction("sum", new String[]{"sum", "sp", "op", "sp", "number"}, new String[]{"number"});
        addProduction("sp", new String[]{"\"\\s*\""});
        addProduction("op", new String[]{"'+'"}, new String[]{"'-'"});
        addProduction("number", new String[]{"\"\\d+\""});
    }
}

