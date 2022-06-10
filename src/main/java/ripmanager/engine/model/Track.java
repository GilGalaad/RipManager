package ripmanager.engine.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ripmanager.engine.enums.TrackType;

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
