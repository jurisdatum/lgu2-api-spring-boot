package uk.gov.legislation.endpoints.search;

import uk.gov.legislation.data.marklogic.search.Parameters;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a legislative number that may include an optional series prefix.
 *
 * <p>Examples of valid formats:
 * <ul>
 *   <li>{@code "123"} - number only</li>
 *   <li>{@code "w123"} - Welsh series</li>
 *   <li>{@code "ni456"} - Northern Ireland series</li>
 *   <li>{@code "s789"} - Scottish series</li>
 * </ul>
 *
 * <p>Parsing is case-insensitive, so {@code "NI123"} and {@code "ni123"} are equivalent.
 *
 * @param number The numeric part of the legislative identifier
 * @param series The optional series identifier (W=Welsh, S=Scottish, C=Commencement,
 *               L=Legal, NI=Northern Ireland). This value is {@code null} if no
 *               series is specified.
 */
public record NumberAndSeries(int number, Parameters.Series series) {

    private static final Pattern pattern = Pattern.compile("^(?<series>ni|[wscl])?(?<number>\\d+)$", Pattern.CASE_INSENSITIVE);

    public static Optional<NumberAndSeries> parse(String param) {
        if (param == null || param.isBlank())
            return Optional.empty();
        Matcher m = pattern.matcher(param);
        if (!m.matches())
           throw new IllegalArgumentException("invalid number format");
        return Optional.of(
            new NumberAndSeries(m.group("number"), m.group("series"))
        );
    }

    private NumberAndSeries(String number, String series) {
        this(Integer.parseInt(number), series == null ? null : Parameters.Series.valueOf(series.toUpperCase()));
    }

}
