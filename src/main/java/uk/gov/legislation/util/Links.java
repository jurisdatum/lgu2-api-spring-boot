package uk.gov.legislation.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Links {

    public record Components(
        String type,
        String year,
        long number,
        Optional<String> fragment,  // with slashes, e.g., section/1
        Optional<String> version,
        Optional<String> language  // can be empty, 'english' or 'welsh'
    ) { }

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
        if (!matcher.find()) {
            matcher = Pattern.compile("^([a-z]{3,5})/([A-Za-z0-9]+/[0-9-]+)/(\\d+)/?").matcher(link);
            if (!matcher.find())
                return null;
        }
        final String type = matcher.group(1);
        final String year = matcher.group(2);
        final long number = Long.parseLong(matcher.group(3));
        link = link.substring(matcher.end());

        if (link.isEmpty())
            return new Components(type, year, number, Optional.empty(), Optional.empty(), Optional.empty());

        String[] split = link.split("/");
        if (split.length == 0)
            throw new IllegalStateException(link);

        if (split[split.length - 1].equals("revision"))  // this might no longer be necessary?
            split = Arrays.copyOf(split, split.length - 1);
        if (split.length == 0)
            return new Components(type, year, number, Optional.empty(), Optional.empty(), Optional.empty());

        final Optional<String> language;
        if ("english".equalsIgnoreCase(split[split.length - 1]) || "welsh".equalsIgnoreCase(split[split.length - 1])) {
            language = Optional.of(split[split.length - 1]);
            split = Arrays.copyOf(split, split.length - 1);
        } else {
            language = Optional.empty();
        }
        if (split.length == 0)
            return new Components(type, year, number, Optional.empty(), Optional.empty(), language);

        final Optional<String> version;
        if (Versions.isVersionLabel(split[split.length - 1])) {
            version = Optional.of(split[split.length - 1]);
            split = Arrays.copyOf(split, split.length - 1);
        } else {
            version = Optional.empty();
        }
        if (split.length == 0)
            return new Components(type, year, number, Optional.empty(), version, language);

        if ("contents".equals(split[split.length - 1]))  // FixMe find a way to capture 'contents'
            split = Arrays.copyOf(split, split.length - 1);
        if (split.length == 0)
            return new Components(type, year, number, Optional.empty(), version, language);

        final Optional<String> fragment = Optional.of(String.join("/", split));
        return new Components(type, year, number, fragment, version, language);
    }

    public static String shorten(String uri) {
        Components comp = parse(uri);
        if (comp == null)
            return uri;
        StringBuilder builder = new StringBuilder();
        builder.append(comp.type);
        builder.append("/");
        builder.append(comp.year);
        builder.append("/");
        builder.append(comp.number);
        if (comp.fragment.isPresent()) {
            builder.append("/");
            builder.append(comp.fragment.get());
        }
        if (comp.version.isPresent()) {
            builder.append("/");
            builder.append(comp.version.get());
        }
        if (comp.language.isPresent()) {
            builder.append("/");
            builder.append(comp.language.get());
        }
        return builder.toString();
    }

    // FixMe should return Optional<String>
    public static String extractFragmentIdentifierFromLink(String link) {
        Components components = parse(link);
        if (components == null)
            return null;
        return components.fragment().orElse(null);
    }

}
