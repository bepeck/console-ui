package console.example;

import console.framework.CommandHandlerImpl;
import console.framework.CommandRouter;

import java.util.List;
import java.util.Scanner;

public class CommandRouterShowCase {
    public static void main(String[] args) {
        new CommandRouter(
                List.of(
                        new ListFilesCommand(),
                        new SayHelloCommand()
                ),
                new CommandHandlerImpl()
        ).handleCommands(new Scanner(System.in), System.out);
    }
}
