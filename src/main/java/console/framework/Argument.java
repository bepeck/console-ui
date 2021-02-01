package console.framework;

public interface Argument<T> {
    String getInvite();

    void convert(ConsoleReader reader, ArgumentCollector argumentCollector) throws ArgumentCaptureException;

    T resolve(ArgumentAccessor arguments);
}
