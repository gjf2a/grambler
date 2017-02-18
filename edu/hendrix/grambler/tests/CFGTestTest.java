package edu.hendrix.grambler.tests;

public class CFGTestTest extends edu.hendrix.grambler.Grammar {
    public CFGTestTest() {
        super();
        addProduction("g", new String[]{"g", "sp", "line"}, new String[]{"line"});
        addProduction("line", new String[]{"nonterm", "sp", "':'", "sp", "rhs", "sp", "';'", "sp"});
        addProduction("rhs", new String[]{"rhs", "sp", "'|'", "sp", "elements"}, new String[]{"elements"});
        addProduction("sp", new String[]{"\"\\s*\""});
        addProduction("spacing", new String[]{"\"\\s+\""});
        addProduction("elements", new String[]{"elements", "spacing", "element"}, new String[]{"element"});
        addProduction("element", new String[]{"term"}, new String[]{"nonterm"}, new String[]{"regex"});
        addProduction("term", new String[]{"\"'.*?[^\\\\]'\""});
        addProduction("nonterm", new String[]{"\"\\w+\""});
        addProduction("regex", new String[]{"\"\\\".*?[^\\\\]\\\"\""});
    }
}

