package uk.co.tfd.kindle.signalk;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ieb on 20/06/2020.
 */
public class StatusUpdates {

    public interface StatusUpdateListener {
        void onStatusChange(String text);
    }
    private StatusUpdateListener[] listeners = new StatusUpdateListener[0];
    private Set<StatusUpdateListener> listenerSet = new HashSet<StatusUpdateListener>();

    public void addStatusUpdateListener(StatusUpdateListener l) {
        listenerSet.add(l);
        listeners = listenerSet.toArray(new StatusUpdateListener[listenerSet.size()]);
    }
    public void removeListener(StatusUpdateListener l) {
        listenerSet.remove(l);
        listeners = listenerSet.toArray(new StatusUpdateListener[listenerSet.size()]);
    }
    protected void updateStatus(String text) {


            for (StatusUpdateListener listener: listeners) {
                listener.onStatusChange(text);
            }
    }

}
