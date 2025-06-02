package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.DatasetLD;

@Repository
public class DatasetQuery extends SingleParameterQuery<DatasetLD> {

    public DatasetQuery(Virtuoso virtuoso) {
        super(virtuoso, DatasetLD.class);
    }

    @Override
    public String makeUri(String param) {
        return String.format("http://www.legislation.gov.uk/id/dataset/%s", param);
    }

}
