package ripmanager.worker;

import lombok.Data;
import ripmanager.engine.dto.Track;

import java.util.List;

@Data
public class WorkerOutcome {

    public enum Status {
        OK,
        KO
    }

    private final Status status;
    private final String output;
    private final List<Track> tasks;

}
