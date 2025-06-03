package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.DataSet;
import uk.gov.legislation.data.virtuoso.jsonld.DatasetLD;

import java.net.URI;
import java.time.ZonedDateTime;

public class DataSetConverter {

    public static DataSet convert(DatasetLD ld) {
        DataSet ds = new DataSet();
        ds.uri = ld.id;
        ds.type = ld.types.stream()
            .map(URI::toASCIIString)
            .filter(uri -> uri.startsWith("http://www.legislation.gov.uk/def/legislation/"))
            .map(uri -> uri.substring(46))
            .findFirst()
            .orElse(null);
        ds.created = ld.created == null ? null : ZonedDateTime.parse(ld.created.value);
        ds.modified = ld.modified == null ? null : ZonedDateTime.parse(ld.modified.value);
        ds.title = ld.title;
        return ds;
    }

}
