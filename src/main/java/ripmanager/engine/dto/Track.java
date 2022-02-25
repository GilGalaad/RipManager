package ripmanager.engine.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Track {

    private int index;
    private TrackType type;

    public Track(int index, TrackType type) {
        this.index = index;
        this.type = type;
    }

}
