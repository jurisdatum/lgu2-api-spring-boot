package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Session;
import uk.gov.legislation.data.virtuoso.jsonld.SessionLD;
import static uk.gov.legislation.converters.ld.LDConverter.*;

public class SessionConverter {
    public static Session convert(SessionLD ld) {
        if(ld == null)
            return null;
        Session session = new Session();
        session.uri = ld.id;
        session.label = ld.label;
        session.description = ld.comment;
        session.legislature = extractLastComponentOfUri(ld.sessionOf);
        session.startDate = extractDateAtEndOfUri(ld.startDate);
        session.endDate = extractDateAtEndOfUri(ld.endDate);
        session.startRegnalYear = extractIntegerAtEndOfUri(ld.startRegnalYear);
        session.endRegnalYear = extractIntegerAtEndOfUri(ld.endRegnalYear);
        return session;
    }

}

