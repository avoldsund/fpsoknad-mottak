package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class PDFElementRendererTest {

    @Test
    public void softLineBreakOnSpace() {
        List<String> lines = splitLine("test1 test2", 8);
        assertEquals("test1", lines.get(0));
        assertEquals("test2", lines.get(1));
    }

    @Test
    public void hardLineBreak() {
        List<String> linesA = splitLine("test1 test2test3", 10);
        assertEquals("test1", linesA.get(0));
        assertEquals("test2test3", linesA.get(1));

        List<String> linesB = splitLine("test1 test2test3test4", 17);
        assertEquals("test1 test2test3t-", linesB.get(0));
        assertEquals("est4", linesB.get(1));
    }

    private List<String> splitLine(String str, int maxLength) {
        PDFElementRenderer renderer = new PDFElementRenderer();
        return renderer.splitLineIfNecessary(str, maxLength);
    }

}
