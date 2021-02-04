package console.example;

import console.framework.Argument;
import console.framework.ArgumentCaptureException;
import console.framework.Command;
import console.framework.ConsoleReader;
import console.framework.ConsoleWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ListFilesCommand implements Command<ListFilesCommand.Args> {

    private static final Argument<ListFilesCommand.Args> ARG_PATH = new Argument<>() {

        @Override
        public String getInvite() {
            return "path to folder";
        }

        @Override
        public void convert(final ConsoleReader reader, final ListFilesCommand.Args args) throws ArgumentCaptureException {
            final String pathString = reader.readLine();
            final Path path;
            try {
                path = Paths.get(pathString);
            } catch (final Exception e) {
                throw new ArgumentCaptureException("can't resolve path: " + e.getMessage());
            }
            final boolean directory = Files.isDirectory(path);
            if (directory) {
                args.path = path;
            } else {
                throw new ArgumentCaptureException("not a directory");
            }
        }
    };

    private static final Argument<ListFilesCommand.Args> ARG_SKIP_ZERO = new Argument<>() {

        @Override
        public String getInvite() {
            return "skip empty files (yes/no)";
        }

        @Override
        public void convert(final ConsoleReader reader, final ListFilesCommand.Args args) throws ArgumentCaptureException {
            switch (reader.readLine().toLowerCase()) {
                case "yes" -> args.skipEmpty = true;
                case "no" -> args.skipEmpty = false;
                default -> throw new ArgumentCaptureException("please answer 'yes' or 'no'");
            }
        }
    };

    @Override
    public String getInvite() {
        return "list files";
    }

    @Override
    public List<Argument<Args>> getArguments() {
        return List.of(ARG_PATH, ARG_SKIP_ZERO);
    }

    @Override
    public void run(final ConsoleWriter writer, final Args args) {
        final Path path = args.path;
        final Boolean skipZero = args.skipEmpty;

        System.out.printf("list files in %s, skip empty: %s\n", path, skipZero);
    }

    @Override
    public Args newArgumentCollector() {
        return new Args();
    }

    static class Args {
        Path path;
        Boolean skipEmpty;
    }
}
