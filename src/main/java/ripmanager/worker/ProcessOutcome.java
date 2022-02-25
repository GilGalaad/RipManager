package ripmanager.worker;

import lombok.Data;
import ripmanager.engine.dto.Track;

import java.util.List;

@Data
public class ProcessOutcome {

    private final int exitCode;
    private final String output;
    private final List<Track> tracks;

    public ProcessOutcome(int exitCode, String output) {
        this.exitCode = exitCode;
        this.output = output;
        this.tracks = null;
    }

    public ProcessOutcome(int exitCode, String output, List<Track> tracks) {
        this.exitCode = exitCode;
        this.output = output;
        this.tracks = tracks;
    }

}
