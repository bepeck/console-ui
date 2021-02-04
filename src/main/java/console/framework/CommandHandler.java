package console.framework;

interface CommandHandler {
    <ARGS> void handleCommand(ConsoleReader reader, ConsoleWriter writer, Command<ARGS> command);
}
