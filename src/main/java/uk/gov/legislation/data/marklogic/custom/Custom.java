package uk.gov.legislation.data.marklogic.custom;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.StreamSupport;

@Repository
public class Custom {

    private final DatabaseClient client;

    public Custom(Environment env) {
        String host = env.getProperty("MARKLOGIC_HOST");
        int port = 8000;
        String database = "Legislation";
        String username = env.getProperty("MARKLOGIC_USERNAME");
        String password = env.getProperty("MARKLOGIC_PASSWORD");
        client = DatabaseClientFactory.newClient(host, port, database,
            new DatabaseClientFactory.DigestAuthContext(username, password));
    }

    public List<String> query(String xquery) {
        ServerEvaluationCall call = client.newServerEval().xquery(xquery);
        try (EvalResultIterator results = call.eval()) {
            return StreamSupport.stream(results.spliterator(), false)
                .map(EvalResult::getString)
                .toList();
        }
    }

}
