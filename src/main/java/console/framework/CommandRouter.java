package console.framework;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

public class CommandRouter {

    private final List<Command> commands;
    private final CommandHandler commandHandler;

    public CommandRouter(final List<Command> commands, final CommandHandler commandHandler) {
        checkInvitesUnique(requireNonNull(commands));

        this.commands = unmodifiableList(commands);
        this.commandHandler = requireNonNull(commandHandler);
    }

    private void checkInvitesUnique(final List<Command> commands) {
        final long count = requireNonNull(commands).stream().map(Command::getInvite).distinct().count();
        if (count != commands.size()) {
            throw new IllegalArgumentException("there are non-unique invites");
        }
    }

    public void handleCommands(final Scanner scanner, final PrintStream out) {
        requireNonNull(scanner);
        requireNonNull(out);
        printInvites(out);
        while (true) {
            int commandNumber = readCommandNumber(scanner, out);
            if (isCommandNumberValid(commandNumber)) {
                commandHandler.handleCommand(scanner, out, commands.get(commandNumber));
                return;
            } else {
                printInvalidCommandNumber(out);
            }
        }
    }

    private int readCommandNumber(final Scanner scanner, final PrintStream out) {
        while (true) {
            try {
                return parseInt(scanner.nextLine());
            } catch (final Exception e) {
                printCanNotReadCommandNumber(out);
            }
        }
    }

    private boolean isCommandNumberValid(final int number) {
        return number >= 0 && number < commands.size();
    }

    private void printCanNotReadCommandNumber(final PrintStream out) {
        out.println("can't read command number");
    }

    private void printInvalidCommandNumber(final PrintStream out) {
        out.println("wrong command number");
    }

    private void printInvites(final PrintStream out) {
        for (int i = 0; i < commands.size(); i++) {
            out.println("enter " + i + " to " + commands.get(i).getInvite());
        }
    }
}
