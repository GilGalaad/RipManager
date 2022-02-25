package ripmanager.engine.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Track<T1, T2 extends DemuxOptions> {

    private int index;
    private TrackType type;
    private T1 properties;
    private T2 demuxOptions;

    public Track(int index, TrackType type) {
        this.index = index;
        this.type = type;
    }
}
