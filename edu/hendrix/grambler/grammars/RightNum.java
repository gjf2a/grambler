package edu.hendrix.grambler.grammars;

public class RightNum extends edu.hendrix.grambler.Grammar {
    public RightNum() {
        super();
        addProduction("num", new String[]{"\"[0-9]\"", "num"}, new String[]{"'.'"});
    }
}

