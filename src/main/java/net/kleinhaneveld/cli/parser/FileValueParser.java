package net.kleinhaneveld.cli.parser;

import java.io.File;

public class FileValueParser implements ValueParser<File> {
    public Class[] getSupportedClasses() {
        return new Class[]{File.class};
    }

    public File parse(String argument) {
        return new File(argument);
    }
}
