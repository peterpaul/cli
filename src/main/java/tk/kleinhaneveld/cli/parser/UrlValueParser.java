package tk.kleinhaneveld.cli.parser;

import tk.kleinhaneveld.cli.ValueParseException;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlValueParser implements ValueParser<URL> {
    public Class[] getSupportedClasses() {
        return new Class[]{URL.class};
    }

    public URL parse(String argument) throws ValueParseException {
        try {
            return new URL(argument);
        } catch (MalformedURLException e) {
            throw new ValueParseException(e);
        }
    }
}
