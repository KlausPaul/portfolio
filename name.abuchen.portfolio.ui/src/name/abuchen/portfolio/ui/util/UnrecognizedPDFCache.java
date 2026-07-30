package name.abuchen.portfolio.ui.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

@Creatable
@Singleton
public class UnrecognizedPDFCache
{
    public static final class CacheEntry
    {
        private final String name;
        private final String extractedText;
        private final String pdfBoxVersion;
        private final Instant capturedAt;

        private CacheEntry(String name, String extractedText, String pdfBoxVersion, Instant capturedAt)
        {
            this.name = name;
            this.extractedText = extractedText;
            this.pdfBoxVersion = pdfBoxVersion;
            this.capturedAt = capturedAt;
        }

        public String getName()
        {
            return name;
        }

        public String getExtractedText()
        {
            return extractedText;
        }

        public String getPdfBoxVersion()
        {
            return pdfBoxVersion;
        }

        public Instant getCapturedAt()
        {
            return capturedAt;
        }
    }

    private static final int MAXIMUM = 5;

    private record Key(String name, String extractedText)
    {
    }

    private final LinkedHashMap<Key, CacheEntry> entries = new LinkedHashMap<>(8, 0.75f, false)
    {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Key, CacheEntry> eldest)
        {
            return size() > MAXIMUM;
        }
    };

    public synchronized void add(String name, String extractedText, String pdfBoxVersion)
    {
        var k = new Key(name, extractedText);
        entries.remove(k);
        entries.put(k, new CacheEntry(name, extractedText, pdfBoxVersion, Instant.now()));
    }

    public synchronized List<CacheEntry> getEntries()
    {
        var list = new ArrayList<>(entries.values());
        Collections.reverse(list);
        return Collections.unmodifiableList(list);
    }

    public synchronized void clear()
    {
        entries.clear();
    }
}
