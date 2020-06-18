package omnis.config.core.lb;

import java.util.List;

public interface LoadBalabce<T> {

    T chooseServer(List<T> list);

}
