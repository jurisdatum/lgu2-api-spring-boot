package debug;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.TraceListener;
import net.sf.saxon.om.Item;
import net.sf.saxon.s9api.*;
import net.sf.saxon.trace.Traceable;
import uk.gov.legislation.transform.Helper;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Profiles XSLT template execution to identify which template rules consume the most time.
 * Compiles stylesheets with tracing enabled, so results will be slightly slower than production.
 */
public class Profiling {

    public static void main(String[] args) throws Exception {
        Profiling instance = new Profiling();
        String file = args.length > 0 ? args[0] : "/debug/ukpga-1996-18.xml";
        instance.profile(file);
    }

    private final XsltExecutable clml2akn;

    private Profiling() throws SaxonApiException {
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        compiler.setCompileWithTracing(true);
        String systemId;
        try {
            systemId = getClass().getResource("/transforms/clml2akn/clml2akn.xsl").toURI().toASCIIString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        clml2akn = compiler.compile(new StreamSource(systemId));
    }

    private void profile(String path) throws Exception {
        System.out.println("profiling " + path.substring(7));
        System.out.println();

        String clml = Files.readString(Path.of(Profiling.class.getResource(path).toURI()));
        XdmNode doc = Helper.parse(clml);

        TemplateProfiler profiler = new TemplateProfiler();

        XsltTransformer transformer = clml2akn.load();
        transformer.setTraceListener(profiler);
        transformer.setSource(doc.asSource());
        XdmDestination destination = new XdmDestination();
        transformer.setDestination(destination);
        transformer.transform();

        profiler.printReport();
    }

    static class TemplateProfiler implements TraceListener {

        private final Map<String, TemplateStats> stats = new LinkedHashMap<>();
        private final Deque<Long> timeStack = new ArrayDeque<>();

        @Override
        public void enter(Traceable instruction, Map<String, Object> properties, XPathContext context) {
            timeStack.push(System.nanoTime());
        }

        @Override
        public void leave(Traceable instruction) {
            long elapsed = System.nanoTime() - timeStack.pop();
            String key = describe(instruction);
            stats.computeIfAbsent(key, k -> new TemplateStats()).record(elapsed);
        }

        private static String describe(Traceable instruction) {
            String location = instruction.getLocation().getSystemId();
            if (location != null) {
                int slash = location.lastIndexOf('/');
                if (slash >= 0) location = location.substring(slash + 1);
            }
            int line = instruction.getLocation().getLineNumber();
            StringBuilder desc = new StringBuilder();
            if (instruction.getObjectName() != null) {
                desc.append(instruction.getObjectName().getLocalPart());
            }
            instruction.gatherProperties((key, value) -> {
                if ("match".equals(key) || "mode".equals(key) || "name".equals(key)) {
                    if (desc.length() > 0) desc.append(" ");
                    desc.append(key).append("=").append(value);
                }
            });
            if (desc.length() == 0) desc.append(instruction.getClass().getSimpleName());
            return String.format("%s:%d %s", location, line, desc);
        }

        void printReport() {
            List<Map.Entry<String, TemplateStats>> sorted = new ArrayList<>(stats.entrySet());
            sorted.sort((a, b) -> Long.compare(b.getValue().totalNanos, a.getValue().totalNanos));

            System.out.printf("%-70s %8s %8s %8s%n", "Template", "Calls", "Total ms", "Avg ms");
            System.out.println("-".repeat(100));

            int shown = 0;
            for (Map.Entry<String, TemplateStats> entry : sorted) {
                if (shown++ >= 30) break;
                TemplateStats s = entry.getValue();
                System.out.printf("%-70s %8d %8.1f %8.3f%n",
                    truncate(entry.getKey(), 70),
                    s.count,
                    s.totalNanos / 1_000_000.0,
                    s.totalNanos / 1_000_000.0 / s.count);
            }
        }

        private static String truncate(String s, int max) {
            return s.length() <= max ? s : s.substring(0, max - 3) + "...";
        }

        @Override public void open(net.sf.saxon.Controller controller) {}
        @Override public void close() {}
        @Override public void startCurrentItem(Item item) {}
        @Override public void endCurrentItem(Item item) {}
    }

    static class TemplateStats {
        long totalNanos;
        int count;

        void record(long nanos) {
            totalNanos += nanos;
            count++;
        }
    }

}
