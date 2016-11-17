package net.kleinhaneveld.cli;

import net.kleinhaneveld.cli.parser.IntValueParser;
import net.kleinhaneveld.fn.Container;
import net.kleinhaneveld.fn.Functions;
import net.kleinhaneveld.fn.Option;
import net.kleinhaneveld.fn.Reductions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static net.kleinhaneveld.fn.Stream.stream;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Cli.Command(name = "HelloWorldTest", description = "some command")
public class HelloWorldTest {
    @Cli.Option(description = "some option", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "some argument")
    private String who;

    @Cli.Argument(description = "some list", parser = IntValueParser.class)
    private List<Integer> numbers;

    private Container<String> result;

    @Cli.Run
    void perform() {
        String value = "Hello " + who + stream(numbers).map(Functions.TO_STRING).reduce("", Reductions.join(","));
        if (uppercase) {
            value = value.toUpperCase();
        }
        result.set(value);
    }

    @Before
    public void setup() {
        this.result = new Container<>();
    }

    @Test
    public void test() {
        ProgramRunner.run(this, new String[]{"World"});
        assertThat(result.get(), is(equalTo(Option.some("Hello World"))));
    }

    @Test
    public void testUppercase() {
        ProgramRunner.run(this, new String[]{"World", "--uppercase"});
        assertThat(result.get(), is(equalTo(Option.some("HELLO WORLD"))));
    }


    @Test
    public void testNumbers() {
        ProgramRunner.run(this, new String[]{"World", "--uppercase=false", "1", "2", "3"});
        assertThat(result.get(), is(equalTo(Option.some("Hello World,1,2,3"))));
    }

    @Test
    public void testHelp() {
        String actual = HelpGenerator.generateHelp(this);
        System.out.println(actual);
    }

    @Test
    public void testNoArgs() {
        ProgramRunner.run(this, new String[]{});
    }
}
