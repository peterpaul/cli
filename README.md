# cli

An annotation based CLI framework for Java.

    @Cli.Command(name = "HelloWorld", description = "some command")
    public class HelloWorld {
        @Cli.Option(description = "some option", shortName = 'U')
        private boolean uppercase;
    
        @Cli.Argument(description = "some argument")
        private String who;
    
        @Cli.Run
        void perform() {
            String value = "Hello " + who;
            if (uppercase) {
                value = value.toUpperCase();
            }
            System.out.println(value);
        }
        
        public static void main(String[] args) {
            ProgramRunner.run(new HelloWorld(), args);
        }
    }
