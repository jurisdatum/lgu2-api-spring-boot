package uk.gov.legislation.data.marklogic.custom;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecentPublishedDates {

    private final Custom custom;

    public RecentPublishedDates(Custom custom) {
        this.custom = custom;
    }

    private static final String QUERY = """
        xquery version "1.0-ml";
        declare namespace dct = "http://purl.org/dc/terms/";
        let $recent as xs:dateTime? := cts:element-values(xs:QName("dct:available"), (), "descending", cts:collection-query("best"))[1]
        let $q := cts:and-query((
          cts:element-range-query( xs:QName('dct:available'), '>=', $recent - xs:dayTimeDuration('P21D') ),
          cts:element-range-query( xs:QName('dct:available'), '<', $recent + xs:dayTimeDuration('P1D') )
        ))
        let $dates as xs:dateTime* := cts:element-values(xs:QName("dct:available"), (), "descending", cts:and-query((cts:collection-query("best"), $q)))
        return distinct-values(for $date in $dates return xs:date($date))[1 to 10]
        """;

    public List<String> fetch() {
        return custom.query(QUERY);
    }

}
