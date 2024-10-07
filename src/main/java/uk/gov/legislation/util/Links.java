package uk.gov.legislation.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Links {

    public record Components(String type, int year, int number, Optional<String> fragment, Optional<String> version) { }

    public static Components parse(String link) {
        if (link == null)
            return null;
        if (link.isBlank())
            return null;
        if (link.startsWith("http://www.legislation.gov.uk/"))
            link = link.substring(30);
        else if (link.startsWith("https://www.legislation.gov.uk/"))
            link = link.substring(31);
        else
            return null;
        if (link.startsWith("id/"))
            link = link.substring(3);
        Matcher matcher = Pattern.compile("^([a-z]{3,5})/(\\d{4})/(\\d+)/?").matcher(link);
        if (!matcher.find())
            return null;
        final String type = matcher.group(1);
        final int year = Integer.parseInt(matcher.group(2));
        final int number = Integer.parseInt(matcher.group(3));
        link = link.substring(matcher.end());
        String[] split = link.split("/");
        if (split[split.length - 1].equals("revision"))
            split = Arrays.copyOf(split, split.length - 1);
        if (split.length == 0)
            return new Components(type, year, number, Optional.empty(), Optional.empty());
        final Optional<String> version;
        if (Versions.isVersionLabel(split[split.length - 1])) {
            version = Optional.of(split[split.length - 1]);
            split = Arrays.copyOf(split, split.length - 1);
        } else {
            version = Optional.empty();
        }
        if (split.length == 0)
            return new Components(type, year, number, Optional.empty(), version);
        if ("contents".equals(split[split.length - 1]))
            split = Arrays.copyOf(split, split.length - 1);
        if (split.length == 0)
            return new Components(type, year, number, Optional.empty(), version);
        final Optional<String> fragment = Optional.of(String.join("/", split));
        return new Components(type, year, number, fragment, version);
    }

    public static String extractFragmentIdentifierFromLink(String link) {
        Components components = parse(link);
        if (components == null)
            return null;
        return components.fragment().orElse(null);
    }

}
