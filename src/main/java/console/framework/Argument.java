package console.framework;

public interface Argument<ARGS> {
    String getInvite();

    void convert(ConsoleReader reader, ARGS args) throws ArgumentCaptureException;
}
