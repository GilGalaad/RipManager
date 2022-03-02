package ripmanager.engine.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkerOutcome {

    public enum Status {
        OK,
        KO
    }

    private final Status status;
    private final String output;
    private final List<Track> tracks;

}
