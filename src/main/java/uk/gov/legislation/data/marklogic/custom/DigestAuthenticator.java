package uk.gov.legislation.data.marklogic.custom;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Very small digest-auth wrapper for java.net.http.HttpClient (Java 21).
 *
 *   DigestAuthenticator auth = new DigestAuthenticator(client, "user", "pass");
 *   HttpResponse<String> r = auth.send(request, HttpResponse.BodyHandlers.ofString());
 */
public final class DigestAuthenticator {

    private final HttpClient client;
    private final String user;
    private final String pass;
    private final SecureRandom random = new SecureRandom();

    // persisted between calls for nonce-count & stale=false reuse
    private int nc = 1;
    private String lastNonce;
    private String lastOpaque;
    private String realm;
    private String algorithm = "MD5";     // default per RFC 7616
    private String qop = "auth";

    public DigestAuthenticator(HttpClient client, String user, String pass) {
        this.client = Objects.requireNonNull(client);
        this.user = Objects.requireNonNull(user);
        this.pass = Objects.requireNonNull(pass);
    }

    /* Public one-liner */
    public <T> HttpResponse<T> send(HttpRequest request,
                                    HttpResponse.BodyHandler<T> handler)
        throws IOException, InterruptedException {

        // -------- step 1: probe (unless we already have a nonce) --------
        if (lastNonce == null) {
            HttpRequest probe = cloneWithoutBody(request);
            HttpResponse<Void> r = client.send(probe, HttpResponse.BodyHandlers.discarding());

            if (r.statusCode() != 401)
                throw new IOException("Unexpected probe status " + r.statusCode());

            parseChallenge(r.headers().firstValue("WWW-Authenticate")
                .orElseThrow(() -> new IOException("No WWW-Authenticate header")));
        }

        // -------- step 2: compute & add Authorization --------
        String authHeader = buildAuthorizationValue(request);

        HttpRequest authorised = cloneWithExtraHeader(request, "Authorization", authHeader);

        // -------- step 3: real call --------
        HttpResponse<T> resp = client.send(authorised, handler);

        if (resp.statusCode() == 401) {          // stale nonce → retry once
            lastNonce = null;
            return send(request, handler);
        }
        return resp;
    }

    /* ------------------------------------------------------------------ */
    /* ------------------- internal helpers ----------------------------- */
    /* ------------------------------------------------------------------ */

    private void parseChallenge(String header) {
        if (!header.toLowerCase(Locale.ROOT).startsWith("digest "))
            throw new IllegalArgumentException("Not a Digest challenge: " + header);

        Map<String,String> params = new HashMap<>();
        for (String part : header.substring(7).split(",")) {
            int eq = part.indexOf('=');
            if (eq < 0) continue;
            String k = part.substring(0, eq).trim();
            String v = part.substring(eq + 1).trim().replaceAll("^\"|\"$", "");
            params.put(k, v);
        }
        realm     = require(params, "realm");
        lastNonce = require(params, "nonce");
        lastOpaque= params.get("opaque");
        algorithm = params.getOrDefault("algorithm", "MD5").toUpperCase(Locale.ROOT);
        qop       = params.getOrDefault("qop", "auth");
    }

    private String require(Map<String,String> map, String key) {
        String v = map.get(key);
        if (v == null) throw new IllegalArgumentException("Missing '" + key + "' in challenge");
        return v;
    }

    private String buildAuthorizationValue(HttpRequest req) {
        String uri   = req.uri().getPath() + optional(req.uri().getQuery(), "?");
        String cnonce= randomHex(16);
        String ncStr = String.format("%08x", nc++);

        // ---- digests ----
        String ha1   = hash(user + ":" + realm + ":" + pass);
        String ha2   = hash(req.method() + ":" + uri);
        String resp  = hash(ha1 + ":" + lastNonce + ":" + ncStr + ":" + cnonce + ":" + qop + ":" + ha2);

        // ---- header ----
        StringBuilder sb = new StringBuilder("Digest ");
        append(sb, "username", user)
            .append(", ")
            .append("realm=\"").append(realm).append('"')
            .append(", ")
            .append("nonce=\"").append(lastNonce).append('"')
            .append(", ")
            .append("uri=\"").append(uri).append('"')
            .append(", ")
            .append("response=\"").append(resp).append('"')
            .append(", ")
            .append("algorithm=").append(algorithm)
            .append(", ")
            .append("qop=").append(qop)
            .append(", ")
            .append("nc=").append(ncStr)
            .append(", ")
            .append("cnonce=\"").append(cnonce).append('"');
        if (lastOpaque != null)
            sb.append(", opaque=\"").append(lastOpaque).append('"');

        return sb.toString();
    }

    private StringBuilder append(StringBuilder sb, String k, String v) {
        return sb.append(k).append("=\"").append(v).append('"');
    }

    private String hash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] dig = md.digest(data.getBytes(StandardCharsets.ISO_8859_1));
            StringBuilder hex = new StringBuilder(dig.length * 2);
            for (byte b : dig) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String randomHex(int bytes) {
        byte[] buf = new byte[bytes];
        random.nextBytes(buf);
        StringBuilder sb = new StringBuilder(bytes * 2);
        for (byte b : buf) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private String optional(String q, String prefix) {
        return (q == null || q.isEmpty()) ? "" : prefix + q;
    }

    /* Re-builders that work even when the body has been consumed ---------------- */

    private HttpRequest cloneWithoutBody(HttpRequest src) {
        return cloneWithExtraHeader(src, null, null, true);
    }

    private HttpRequest cloneWithExtraHeader(HttpRequest src, String k, String v) {
        return cloneWithExtraHeader(src, k, v, false);
    }

    private HttpRequest cloneWithExtraHeader(HttpRequest src, String k, String v, boolean dropBody) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
            .uri(src.uri())
            .method(src.method(),
                dropBody
                    ? HttpRequest.BodyPublishers.noBody()
                    : src.bodyPublisher()
                    .orElse(HttpRequest.BodyPublishers.noBody()));
        src.headers().map().forEach((h, vals) ->
            vals.forEach(val -> b.header(h, val)));
        if (k != null) b.header(k, v);
        return b.build();
    }
}
