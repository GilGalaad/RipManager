package ripmanager.engine.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Track {

    private int index;
    private TrackType type;
    private String label;

    public Track(int index, TrackType type, String label) {
        this.index = index;
        this.type = type;
        this.label = label;
    }
}
