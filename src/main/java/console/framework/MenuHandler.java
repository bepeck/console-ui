package console.framework;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static console.framework.MenuHandler.Query.EMPTY;
import static java.util.stream.Collectors.toList;

public class MenuHandler {

    private final Map<Integer, Option> options;

    private Query query = EMPTY;

    public MenuHandler(final List<String> options) {
        this.options = prepareOptions(options);
    }

    public String handle(final ConsoleReader reader, final ConsoleWriter writer) {
        while (true) {
            final Option exactlyMatched = findSingleExactlyMatched(query);
            if (exactlyMatched == null) {
                final List<Option> filteredOptions = findAll(query);
                if (filteredOptions.isEmpty()) {
                    writer.writeLine("no options for this query");
                    query = EMPTY;
                    continue;
                }
                writer.writeLine("options:");
                filteredOptions.forEach(option -> writer.writeLine(option.toString()));
                while (true) {
                    writer.writeLine("enter option number to choose or ?query to filter options");
                    final String line = reader.readLine();
                    if (line.startsWith("?")) {
                        query = new Query(line.substring(1));
                        break;
                    } else {
                        try {
                            final int number = Integer.parseInt(line);
                            final Option option = options.get(number);
                            if (option != null) {
                                return option.value;
                            }
                        } catch (final NumberFormatException e) {
                            //ignore
                        }
                        writer.writeLine("invalid option number");
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

    private Map<Integer, Option> prepareOptions(final List<String> options) {
        final Map<Integer, Option> result = new LinkedHashMap<>();
        for (int i = 0; i < options.size(); i++) {
            result.put(i, new Option(i, options.get(i)));
        }
        if (result.keySet().size() < options.size()) {
            throw new IllegalArgumentException("duplicated options");
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

    static class Query {

        static Query EMPTY = new Query("");

        final String valueLowerCase;

        Query(final String value) {
            this.valueLowerCase = value.toLowerCase();
        }

        boolean isMatched(final Option option) {
            return option.valueLowerCase.contains(valueLowerCase);
        }

        boolean isExactlyMatched(final Option option) {
            return option.valueLowerCase.equalsIgnoreCase(valueLowerCase);
        }
    }

    static class Option {
        final int num;
        final String value;
        final String valueLowerCase;

        public Option(final int num, final String value) {
            this.num = num;
            this.value = value;
            this.valueLowerCase = value.toLowerCase();
        }

        @Override
        public String toString() {
            return "[" + num + "]' - " + value;
        }
    }
}
