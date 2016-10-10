package com.github.peterpaul.cli;

import com.github.peterpaul.cli.parser.FileValueParser;
import com.github.peterpaul.cli.parser.StringValueParser;
import com.github.peterpaul.cli.parser.UrlValueParser;

import java.io.File;
import java.net.URL;
import java.util.List;

@Cli.Command(name = "test", description = "This command showcases all the options available for the @Cli annotation framework.")
public class TestCommand {

    @Cli.Argument(description = "Integer number argument, automatically converted.")
    private int number;

    @Cli.Argument(name = "another_number", description = "Another integer number argument, with custom name.")
    private int anotherNumber;

    @Cli.Argument(description = "File argument, with registered ValueParser.", parser = FileValueParser.class)
    private File file;

    @Cli.Argument(description = "List of optional arguments, must occur as last argument.", parser = StringValueParser.class)
    private List<String> args;

    @Cli.Option(shortName = 'v', description = "Option with name verbose, used as --verbose, or --verbose=true|false. Also accessible with -v os -v=true|false")
    private boolean verbose;

    @Cli.Option(name = "url", description = "Url option, with ValueParser.", parser = UrlValueParser.class)
    private URL url;

    /*
     * The Command framework will search for a method with the name run, and/or the annotation {@link Cli#Run}.
     * A Command must have no arguments, and be void. There can only be one such method per Command class.
     */
    @Cli.Run
    public void run() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "TestCommand{" +
                "number=" + number +
                ", anotherNumber=" + anotherNumber +
                ", file=" + file +
                ", args=" + args +
                ", verbose=" + verbose +
                ", url=" + url +
                '}';
    }
}
