package uk.gov.legislation.converters;

import uk.gov.legislation.util.Extent;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public class ExtentConverter {

    public static final String SAME_AS_AFFECTED = "S+A+M+E+A+S+A+F+F+E+C+T+E+D";

    public static EnumSet<Extent> convert(String clml) {
        if (clml == null)
            return EnumSet.noneOf(Extent.class);
        if (clml.isBlank())
            return EnumSet.noneOf(Extent.class);
        return Arrays.stream(clml.split("\\+"))
            .map(extent -> extent.replace(".", ""))
            .map(Extent::valueOf)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(Extent.class)));
    }

}
