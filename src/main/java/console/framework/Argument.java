package console.framework;

import java.util.Scanner;

public interface Argument<T> {
    String getInvite();

    void convert(Scanner in, ArgumentCollector argumentCollector) throws ArgumentCaptureException;

    T resolve(ArgumentAccessor arguments);
}
