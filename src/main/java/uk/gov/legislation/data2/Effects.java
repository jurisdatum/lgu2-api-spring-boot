package uk.gov.legislation.data2;

import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.changes.Changes;
import uk.gov.legislation.data.marklogic.changes.Parameters;
import uk.gov.legislation.transform.simple.effects.Effect;
import uk.gov.legislation.transform.simple.effects.EffectsSimplifier;

import java.io.IOException;
import java.util.List;

@Service
public class Effects {

    private final Changes marklogic;
    private final EffectsSimplifier simplifier;

    public Effects(Changes marklogic, EffectsSimplifier simplifier) {
        this.marklogic = marklogic;
        this.simplifier = simplifier;
    }

    public List<Effect> getComingIntoForce(String type, int year, int number) throws IOException, InterruptedException, SaxonApiException {
        Parameters params = Parameters.comingIntoForce(type, year, number);
        String atom = marklogic.fetch(params);
        return simplifier.parse(atom).entries.stream()
            .map(e -> e.content.effect).toList();
    }

}
