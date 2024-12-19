package uk.gov.legislation.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Links {

    public record Components(String type, String year, int number, Optional<String> fragment, Optional<String> version) { }

    public static Components parse(String link) {
        if (link == null || link.isBlank()) {
            return null;
        }

        link = stripPrefix(link);
        if (link == null || link.isBlank()) {
            return null;
        }

        Matcher matcher = matchPattern(link);
        if (matcher == null) {
            return null;
        }

        String type = matcher.group(1);
        String year = matcher.group(2);
        int number = Integer.parseInt(matcher.group(3));
        link = link.substring(matcher.end());

        String[] split = cleanSplit(link);
        Optional<String> version = extractVersion(split);
        Optional<String> fragment = extractFragment(split);

        return new Components(type, year, number, fragment, version);
    }

    private static String stripPrefix(String link) {
        if (link.startsWith("http://www.legislation.gov.uk/")) {
            return link.substring(30);
        }
        if (link.startsWith("https://www.legislation.gov.uk/")) {
            return link.substring(31);
        }
        if (link.startsWith("id/")) {
            return link.substring(3);
        }
        return null;
    }

    private static Matcher matchPattern(String link) {
        Matcher matcher = Pattern.compile("^([a-z]{3,5})/(\\d{4})/(\\d+)/?").matcher(link);
        if (matcher.find()) {
            return matcher;
        }
        matcher = Pattern.compile("^([a-z]{3,5})/([A-Za-z0-9]+/[0-9-]+)/(\\d+)/?").matcher(link);
        return matcher.find() ? matcher : null;
    }

    private static String[] cleanSplit(String link) {
        String[] split = link.split("/");
        if (split.length > 0 && "revision".equals(split[split.length - 1])) {
            split = Arrays.copyOf(split, split.length - 1);
        }
        return split;
    }

    private static Optional<String> extractVersion(String[] split) {
        if (split.length > 0 && Versions.isVersionLabel(split[split.length - 1])) {
            String version = split[split.length - 1];
            return Optional.of(version);
        }
        return Optional.empty();
    }

    private static Optional<String> extractFragment(String[] split) {
        if (split.length > 0 && "contents".equals(split[split.length - 1])) {
            split = Arrays.copyOf(split, split.length - 1);
        }
        return split.length > 0 ? Optional.of(String.join("/", split)) : Optional.empty();
    }

    public static Optional<String> extractFragmentIdentifierFromLink(String link) {
        return Optional.ofNullable(parse(link))
                .flatMap(Components::fragment);
    }
}

