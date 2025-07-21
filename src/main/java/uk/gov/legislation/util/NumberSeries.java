package uk.gov.legislation.util;


import java.util.Optional;

public record NumberSeries(Integer number, String series) {

    public static NumberSeries extractSeriesFromNumber(String numberValue) {
        if (numberValue == null || numberValue.isBlank()) {
            return null;
        }

        return parseSeries(numberValue)
            .map(NumberSeries::parseNumberSeries)
            .orElseThrow(() -> new NumberFormatException(
                "Series prefix is missing or invalid in input: " + numberValue));
    }

    private static Optional<Parsed> parseSeries(String numberValue) {
        if (numberValue.startsWith("ni") && numberValue.length() > 2) {
            return Optional.of(new Parsed("ni", numberValue.substring(2)));
        }
        if (numberValue.length() > 1) {
            char firstChar = numberValue.charAt(0);
            if ("wscl".indexOf(firstChar) != -1) {
                return Optional.of(new Parsed(String.valueOf(firstChar), numberValue.substring(1)));
            }
        }
        return Optional.empty();
    }

    private static NumberSeries parseNumberSeries(Parsed parsed) {
        String numStr = parsed.numberStr().trim();
        if (numStr.isEmpty()) {
            throw new NumberFormatException("Number part is empty after trimming");
        }
        try {
            Integer number = Integer.valueOf(numStr);
            return new NumberSeries(number, parsed.series());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The number part '" + parsed.numberStr() + "' is not a valid integer");
        }
    }

    private record Parsed(String series, String numberStr) {}
}



