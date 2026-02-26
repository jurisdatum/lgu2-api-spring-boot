package uk.gov.legislation.converters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.legislation.data.marklogic.changes.Changes;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.effects.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnappliedEffectsFetcherTest {

    @Mock
    private Changes changes;

    @Mock
    private EffectsSimplifier simplifier;

    @InjectMocks
    private UnappliedEffectsFetcher fetcher;

    private static Metadata createFinalMetadata() {
        Metadata m = new Metadata();
        m.status = "final";
        m.longType = "UnitedKingdomPublicGeneralAct";
        m.year = 2024;
        m.number = 10;
        m.setVersions(List.of("enacted"));
        return m;
    }

    private static Effect createEffect(String id) {
        Effect e = new Effect();
        e.id = id;
        return e;
    }

    private static Page createPage(int totalPages, Effect... effects) {
        Page page = new Page();
        page.totalPages = totalPages;
        page.entries = new java.util.ArrayList<>();
        for (Effect effect : effects) {
            Entry entry = new Entry();
            entry.content = new Entry.Content();
            entry.content.effect = effect;
            page.entries.add(entry);
        }
        return page;
    }

    @Test
    void skipsRevisedStatus() {
        Metadata m = new Metadata();
        m.status = "revised";
        m.longType = "UnitedKingdomPublicGeneralAct";
        m.year = 2024;
        m.number = 10;

        fetcher.fetchIfNeeded(m);

        assertFalse(m.finalEffectsEnriched);
        assertEquals(Collections.emptyList(), m.rawEffects);
        verifyNoInteractions(changes);
    }

    @Test
    void successSetsEffectsAndFlag() throws Exception {
        Metadata m = createFinalMetadata();
        Effect effect = createEffect("e1");
        Page page = createPage(1, effect);

        when(changes.fetch(any())).thenReturn("<atom/>");
        when(simplifier.parse("<atom/>")).thenReturn(page);

        fetcher.fetchIfNeeded(m);

        assertTrue(m.finalEffectsEnriched);
        assertEquals(1, m.rawEffects.size());
        assertEquals("e1", m.rawEffects.get(0).id);
    }

    @Test
    void failureLeavesEffectsUnchangedAndFlagFalse() throws Exception {
        Metadata m = createFinalMetadata();

        when(changes.fetch(any())).thenThrow(new java.io.IOException("connection refused"));

        fetcher.fetchIfNeeded(m);

        assertFalse(m.finalEffectsEnriched);
        assertEquals(Collections.emptyList(), m.rawEffects);
    }

    @Test
    void paginatesMultiplePages() throws Exception {
        Metadata m = createFinalMetadata();
        Effect e1 = createEffect("e1");
        Effect e2 = createEffect("e2");
        Page page1 = createPage(2, e1);
        Page page2 = createPage(2, e2);

        when(changes.fetch(any()))
            .thenReturn("<atom-page-1/>")
            .thenReturn("<atom-page-2/>");
        when(simplifier.parse("<atom-page-1/>")).thenReturn(page1);
        when(simplifier.parse("<atom-page-2/>")).thenReturn(page2);

        fetcher.fetchIfNeeded(m);

        assertTrue(m.finalEffectsEnriched);
        assertEquals(2, m.rawEffects.size());
        assertEquals("e1", m.rawEffects.get(0).id);
        assertEquals("e2", m.rawEffects.get(1).id);
    }

}
