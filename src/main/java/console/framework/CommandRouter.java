package console.framework;

import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

public class CommandRouter {

    private final List<Command<?>> commands;
    private final CommandHandler commandHandler;

    public CommandRouter(final List<Command<?>> commands, final CommandHandler commandHandler) {
        checkInvitesUnique(requireNonNull(commands));

        this.commands = unmodifiableList(commands);
        this.commandHandler = requireNonNull(commandHandler);
    }

    private void checkInvitesUnique(final List<Command<?>> commands) {
        final long count = requireNonNull(commands).stream().map(Command::getInvite).distinct().count();
        if (count != commands.size()) {
            throw new IllegalArgumentException("there are non-unique invites");
        }
    }

    public void handleCommands(final ConsoleReader reader, final ConsoleWriter writer) {
        requireNonNull(reader);
        requireNonNull(writer);
        printInvites(writer);
        while (true) {
            int commandNumber = readCommandNumber(reader, writer);
            if (isCommandNumberValid(commandNumber)) {
                try {
                    commandHandler.handleCommand(reader, writer, commands.get(commandNumber));
                } catch (Exception e) {
                    writer.writeLine("failed: " + e.getMessage());
                }
                return;
            } else {
                printInvalidCommandNumber(writer);
            }
        }
    }

    private int readCommandNumber(final ConsoleReader reader, final ConsoleWriter writer) {
        while (true) {
            try {
                return parseInt(reader.readLine());
            } catch (final Exception e) {
                printCanNotReadCommandNumber(writer);
            }
        }
    }

    private boolean isCommandNumberValid(final int number) {
        return number >= 0 && number < commands.size();
    }

    private void printCanNotReadCommandNumber(final ConsoleWriter writer) {
        writer.writeLine("can't read command number");
    }

    private void printInvalidCommandNumber(final ConsoleWriter writer) {
        writer.writeLine("wrong command number");
    }

    private void printInvites(final ConsoleWriter writer) {
        for (int i = 0; i < commands.size(); i++) {
            writer.writeLine("enter " + i + " to " + commands.get(i).getInvite());
        }
    }
}
