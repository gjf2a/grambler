package edu.hendrix.grambler.grammars;

public class GoodNewline extends edu.hendrix.grambler.Grammar {
    public GoodNewline() {
        super();
        addProduction("lines", new String[]{"line", "lines"}, new String[]{"line"});
        addProduction("line", new String[]{"'thing'", "\"\\n\""});
    }
}

