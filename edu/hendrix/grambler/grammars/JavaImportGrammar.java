package edu.hendrix.grambler.grammars;

public class JavaImportGrammar extends edu.hendrix.grambler.Grammar {
    public JavaImportGrammar() {
        super();
        addProduction("top", new String[]{"package", "class"}, new String[]{"class"});
        addProduction("package", new String[]{"optSpace", "'package'", "space", "packageName", "';'", "optSpace"});
        addProduction("packageName", new String[]{"packageName", "'.'", "name"}, new String[]{"name"});
        addProduction("name", new String[]{"\"\\w+\""});
        addProduction("class", new String[]{"'public class'", "space", "name", "space", "'extends '", "grammarClass", "' {'", "optSpace", "constructor", "optSpace", "'}'", "space"});
        addProduction("grammarClass", new String[]{"'Grammar'"}, new String[]{"'edu.hendrix.grambler.Grammar'"});
        addProduction("constructor", new String[]{"'public'", "space", "name", "optSpace", "'() {'", "optSpace", "'super();'", "optSpace", "productions", "optSpace", "'}'"});
        addProduction("productions", new String[]{"productions", "space", "production"}, new String[]{"production"});
        addProduction("production", new String[]{"'addProduction('", "string", "optSpace", "rhs", "');'"});
        addProduction("rhs", new String[]{"rhs", "alternative"}, new String[]{"alternative"});
        addProduction("alternative", new String[]{"','", "optSpace", "'new String[]{'", "stringList", "'}'", "optSpace"});
        addProduction("stringList", new String[]{"stringList", "','", "optSpace", "string"}, new String[]{"string"});
        addProduction("string", new String[]{"\"\\\".*?[^\\\\]\\\"\""});
        addProduction("space", new String[]{"\"\\s+\""});
        addProduction("optSpace", new String[]{"\"\\s*\""});
    }
}

