package be.kuleuven.cs.flexsim.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.naming.directory.NoSuchAttributeException;

import org.junit.Before;
import org.junit.Test;

public class GrapherTest {

    // mock for nonnull check.
    protected GrapherTester g = mock(GrapherTester.class);

    @Before
    public void setUp() throws Exception {
        g = new GrapherTester();

    }

    @Test
    public void testInitial() {
        assertTrue(g.getTitlemap().isEmpty());
        assertTrue(g.getSeries().isEmpty());
    }

    @Test
    public void insertOneTest() {
        String s = "T1";
        g.insert(s, 1);
        hasOneTest(s, 1);
    }

    private void hasOneTest(String s, int i) {
        assertFalse(g.getTitlemap().isEmpty());
        assertFalse(g.getSeries().isEmpty());
        assertEquals(i, g.getTitlemap().size());
        assertTrue(g.getTitlemap().get(s) == i - 1);
        assertNotNull(g.getSeries().get(i - 1));
    }

    @Test
    public void insertTwoTest() {
        String s = "T1";
        g.insert("T0", 0);
        g.insert(s, 1);
        hasOneTest(s, 2);
    }

    @Test
    public void insertMultipleTwoTest() {
        String s = "T1";
        g.insert("T0", 0);
        g.insert(s, 1);
        g.insert(s, 2);
        g.insert(s, 3);
        assertEquals(3, g.getSeries().get(1).getItemCount());
        hasOneTest(s, 2);
    }

    public static class GrapherTester extends Grapher {
        public GrapherTester() {
            super("tester");
        }

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void insert(String s, int v) {
            addRecord(s, v, v);
        }

        @Override
        protected void record(be.kuleuven.cs.flexsim.event.Event e)
                throws NoSuchAttributeException {
            Long t = e.getAttribute("time", Long.class);
            addRecord("Test", 1, 2);

        }
    }
}
