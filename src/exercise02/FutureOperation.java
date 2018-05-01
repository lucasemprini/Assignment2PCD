package exercise02;

import java.util.Map;


public interface FutureOperation {
    void onCompleted(final Map<String, Long> map);
}
