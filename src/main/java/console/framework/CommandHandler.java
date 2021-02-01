package console.framework;

interface CommandHandler {
    void handleCommand(ConsoleReader reader, ConsoleWriter writer, Command command);
}
