import javatube.Search;
import javatube.Youtube;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class TestSearch {

    @Test
    public void testResults() throws Exception {
        ArrayList<Youtube> ar = new Search("Alan Walker").results();
        Assertions.assertFalse(ar.isEmpty());
    }
}
