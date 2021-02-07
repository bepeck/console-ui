package console.framework;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static console.framework.MenuHandler.Query.EMPTY;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class MenuHandler {

    private final String invite;
    private final Map<Integer, Option> options;

    public MenuHandler(final String invite, final List<String> options) {
        this.invite = requireNonNull(invite);
        this.options = prepareOptions(options);
    }

    public String handle(final ConsoleReader reader, final ConsoleWriter writer) {
        Query query = EMPTY;
        while (true) {
            final Option exactlyMatched = findSingleExactlyMatched(query);
            if (exactlyMatched == null) {
                final List<Option> filteredOptions = findAll(query);
                if (filteredOptions.isEmpty()) {
                    writer.writeLine("no options for this query");
                    query = EMPTY;
                } else {
                    printOptions(writer, filteredOptions);
                    final QueryOrOption queryOrValue = queryOrChoose(reader, writer);
                    if (queryOrValue.isOption()) {
                        return queryOrValue.option.value;
                    } else {
                        query = queryOrValue.query;
                    }
                }
            } else {
                writer.writeLine("confirm option '" + exactlyMatched + "' (yes)");
                if (reader.readLine().equalsIgnoreCase("yes")) {
                    return exactlyMatched.value;
                } else {
                    query = EMPTY;
                }
            }
        }
    }

    private void printOptions(final ConsoleWriter writer, final List<Option> filteredOptions) {
        writer.writeLine(invite);
        filteredOptions.forEach(option -> writer.writeLine(option.toString()));
    }

    private QueryOrOption queryOrChoose(final ConsoleReader reader, final ConsoleWriter writer) {
        while (true) {
            writer.writeLine("enter option number to choose or ?query to filter options");
            final String line = reader.readLine();
            if (line.startsWith("?")) {
                return new QueryOrOption(new Query(line.substring(1)));
            } else {
                try {
                    final int number = Integer.parseInt(line);
                    final Option option = options.get(number);
                    if (option != null) {
                        return new QueryOrOption(option);
                    }
                } catch (final NumberFormatException e) {
                    //ignore
                }
            }
            writer.writeLine("invalid option number");
        }
    }

    private Map<Integer, Option> prepareOptions(final List<String> options) {
        if (new HashSet<>(options).size() < options.size()) {
            throw new IllegalArgumentException("duplicated options");
        }
        final Map<Integer, Option> result = new LinkedHashMap<>();
        for (int i = 0; i < options.size(); i++) {
            result.put(i, new Option(i, options.get(i)));
        }
        return result;
    }

    private Option findSingleExactlyMatched(final Query query) {
        return options.values().stream()
                .filter(query::isExactlyMatched)
                .findAny()
                .orElse(null);
    }


    private List<Option> findAll(final Query query) {
        return options.values()
                .stream()
                .filter(query::isMatched)
                .collect(toList());
    }

    static class QueryOrOption {
        final Query query;
        final Option option;

        public QueryOrOption(final Query query) {
            this.query = requireNonNull(query);
            this.option = null;
        }

        public QueryOrOption(final Option option) {
            this.option = requireNonNull(option);
            this.query = null;
        }

        public boolean isOption() {
            return option != null;
        }
    }

    static class Query {
        static Query EMPTY = new Query("");

        final String valueLowerCase;

        Query(final String value) {
            this.valueLowerCase = requireNonNull(value).toLowerCase();
        }

        boolean isMatched(final Option option) {
            return requireNonNull(option).valueLowerCase.contains(valueLowerCase);
        }

        boolean isExactlyMatched(final Option option) {
            return requireNonNull(option).valueLowerCase.equalsIgnoreCase(valueLowerCase);
        }
    }

    static class Option {
        final int num;
        final String value;
        final String valueLowerCase;

        public Option(final int num, final String value) {
            this.num = num;
            this.value = requireNonNull(value);
            this.valueLowerCase = value.toLowerCase();
        }

        @Override
        public String toString() {
            return "[" + num + "] - '" + value + "'";
        }
    }
}
