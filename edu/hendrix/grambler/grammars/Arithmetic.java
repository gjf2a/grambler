package edu.hendrix.grambler.grammars;

public class Arithmetic extends edu.hendrix.grambler.Grammar {
    public Arithmetic() {
        super();
        addProduction("expr", new String[]{"sum", "sp"});
        addProduction("sum", new String[]{"sum", "sp", "addOp", "sp", "product"}, new String[]{"product"});
        addProduction("product", new String[]{"product", "sp", "mulOp", "sp", "power"}, new String[]{"power"});
        addProduction("power", new String[]{"power", "sp", "expOp", "sp", "number"}, new String[]{"number"});
        addProduction("sp", new String[]{"\"\\s*\""});
        addProduction("expOp", new String[]{"'**'"});
        addProduction("mulOp", new String[]{"'*'"}, new String[]{"'/'"});
        addProduction("addOp", new String[]{"'+'"}, new String[]{"'-'"});
        addProduction("number", new String[]{"\"\\d+\""});
    }
}

