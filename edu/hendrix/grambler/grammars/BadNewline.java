package edu.hendrix.grambler.grammars;

public class BadNewline extends edu.hendrix.grambler.Grammar {
    public BadNewline() {
        super();
        addProduction("lines", new String[]{"lines", "'\\n'", "line"}, new String[]{"line"});
        addProduction("line", new String[]{"'thing'"});
    }
}

