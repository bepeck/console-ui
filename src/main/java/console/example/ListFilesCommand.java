package console.example;

import console.framework.Argument;
import console.framework.ArgumentAccessor;
import console.framework.ArgumentCaptureException;
import console.framework.ArgumentCollector;
import console.framework.Command;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ListFilesCommand implements Command {

    private static final Argument<Path> ARG_PATH = new Argument<>() {

        private static final String PATH = "path";

        @Override
        public String getInvite() {
            return "path to folder";
        }

        @Override
        public void convert(final Scanner in, final ArgumentCollector argumentCollector) throws ArgumentCaptureException {
            final String pathString = in.nextLine();
            final Path path;
            try {
                path = Paths.get(pathString);
            } catch (final Exception e) {
                throw new ArgumentCaptureException("can't resolve path: " + e.getMessage());
            }
            final boolean directory = Files.isDirectory(path);
            if (directory) {
                argumentCollector.add(PATH, path);
            } else {
                throw new ArgumentCaptureException("not a directory");
            }
        }

        @Override
        public Path resolve(final ArgumentAccessor argumentAccessor) {
            return (Path) argumentAccessor.get(PATH);
        }
    };

    private static final Argument<Boolean> ARG_SKIP_ZERO = new Argument<>() {

        private static final String SKIP_ZERO = "skipZero";

        @Override
        public String getInvite() {
            return "skip empty files (yes/no)";
        }

        @Override
        public void convert(final Scanner in, final ArgumentCollector argumentCollector) throws ArgumentCaptureException {
            switch (in.nextLine().toLowerCase()) {
                case "yes" -> argumentCollector.add(SKIP_ZERO, true);
                case "no" -> argumentCollector.add(SKIP_ZERO, false);
                default -> throw new ArgumentCaptureException("please answer 'yes' or 'no'");
            }
        }

        @Override
        public Boolean resolve(final ArgumentAccessor argumentAccessor) {
            return (Boolean) argumentAccessor.get(SKIP_ZERO);
        }
    };

    @Override
    public String getInvite() {
        return "list files";
    }

    @Override
    public List<Argument<?>> getArguments() {
        return List.of(ARG_PATH, ARG_SKIP_ZERO);
    }

    @Override
    public void run(final PrintStream out, final ArgumentAccessor argumentAccessor) {
        final Path path = ARG_PATH.resolve(argumentAccessor);
        final Boolean skipZero = ARG_SKIP_ZERO.resolve(argumentAccessor);

        System.out.printf("list files in %s, skip zero: %s\n", path, skipZero);
    }
}
