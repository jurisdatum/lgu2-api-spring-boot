package uk.gov.legislation.util;


import java.util.Optional;

public record NumberSeries(Integer number, String series) {

    public static NumberSeries extractSeriesFromNumber(String numberValue) {
        if (numberValue == null || numberValue.isBlank()) {
            return null;
        }
        Optional<Parsed> parsed = parseSeries(numberValue);
        if (parsed.isPresent()) {
            return parseNumberSeries(parsed.get());
        }

        if (Character.isLetter(numberValue.charAt(0))) {
            throw new IllegalArgumentException("Invalid series prefix: " + numberValue.charAt(0));
        }

        String trimmed = numberValue.trim();
        try {
            Integer number = Integer.valueOf(trimmed);
            return new NumberSeries(number, null);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + numberValue, e);
        }
    }

    private static Optional<Parsed> parseSeries(String numberValue) {
        if (numberValue.startsWith("ni")) {
            String rest = numberValue.substring(2);
            return Optional.of(new Parsed("ni", rest));
        }
        if (!numberValue.isEmpty()) {
            char firstChar = numberValue.charAt(0);
            if ("wscl".indexOf(firstChar) != -1) {
                String rest = numberValue.length() > 1 ? numberValue.substring(1) : "";
                return Optional.of(new Parsed(String.valueOf(firstChar), rest));
            }
        }
        return Optional.empty();
    }

    private static NumberSeries parseNumberSeries(Parsed parsed) {
        String numStr = parsed.numberStr().trim();
        if (numStr.isEmpty()) {
            throw new IllegalArgumentException("Missing number after series: " + parsed.series());
        }
        try {
            Integer number = Integer.valueOf(numStr);
            return new NumberSeries(number, parsed.series());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in series '" + parsed.series() + "': " + numStr, e);
        }
    }

    private record Parsed(String series, String numberStr) {}
}




