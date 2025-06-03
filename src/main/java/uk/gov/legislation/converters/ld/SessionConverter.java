package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Session;
import uk.gov.legislation.data.virtuoso.jsonld.SessionLD;

public class SessionConverter {
    public static Session convert(SessionLD ld) {
        if (ld == null) return null;

        Session session = new Session();
        session.uri = ld.id;
        session.type = ld.type;
        session.label = ld.label;
        session.description = ld.comment;
        session.sessionOf = ld.sessionOf;

        return session;
    }
}

