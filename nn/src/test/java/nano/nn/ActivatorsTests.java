package nano.nn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ActivatorsTests {

    @Test
    public void testSigmoid() {
        var sigmoid = Activators.sigmoid();
        Assertions.assertEquals(1.0, sigmoid.apply(1000));
        Assertions.assertEquals(0.0, sigmoid.apply(-1000));
        Assertions.assertEquals(0.5, sigmoid.apply(0));
    }
}
