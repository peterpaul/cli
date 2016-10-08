package tk.kleinhaneveld.cli.parser;

import java.io.File;

public class FileValueParser implements ValueParser<File> {
    public Class<File> getSupportedClass() {
        return File.class;
    }

    public File parse(String argument) {
        return new File(argument);
    }
}
