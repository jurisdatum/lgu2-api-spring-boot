package uk.gov.legislation.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Links {

    public static String extractFragmentIdentifierFromLink(String link) {
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
        Matcher matcher = Pattern.compile("^ukpga/\\d{4}/\\d+/?").matcher(link);
        if (matcher.find())
            link = link.substring(matcher.end());
        else
            return null;
        if (link.isEmpty())
            return null;
        String[] split = link.split("/");
        if (split[split.length - 1].equals("revision"))
            split = Arrays.copyOf(split, split.length - 1);
        if (split.length == 0)
            return null;
        if (Versions.isVersionLabel(split[split.length - 1]))
            split = Arrays.copyOf(split, split.length - 1);
        if (split.length == 0)
            return null;
        return String.join("/", split);
    }

}
