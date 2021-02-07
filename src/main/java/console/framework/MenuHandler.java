package console.framework;

import java.util.ArrayList;
import java.util.List;

import static console.framework.MenuHandler.Query.EMPTY;
import static java.util.stream.Collectors.toList;

public class MenuHandler {

    final List<Option> options;

    public MenuHandler(final List<String> options) {
        this.options = prepareOptions(options);
    }

    public String handle(final ConsoleReader reader, final ConsoleWriter writer) {
        Query query = EMPTY;
        while (true) {
            final Option exactlyMatched = singleOrNull(query);
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
                            if (number >= 0 && number < options.size()) {
                                return options.get(number).value;
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

    private List<Option> prepareOptions(final List<String> options) {
        final List<Option> result = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            result.add(new Option(i, options.get(i)));
        }
        return result;
    }

    public Option singleOrNull(final Query query) {
        final List<Option> filteredOptions = options.stream()
                .filter(option -> option.valueLowerCase.equals(query.valueLowerCase))
                .limit(2)
                .collect(toList());
        if (filteredOptions.size() == 1) {
            return filteredOptions.get(0);
        }
        return null;
    }


    private List<Option> findAll(final Query query) {
        return options.stream().filter(option -> option.valueLowerCase.contains(query.valueLowerCase)).collect(toList());
    }

    static class Query {

        static Query EMPTY = new Query("");

        final String valueLowerCase;

        Query(String value) {
            this.valueLowerCase = value.toLowerCase();
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
