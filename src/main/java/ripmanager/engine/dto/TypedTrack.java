package ripmanager.engine.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TypedTrack<T1, T2 extends DemuxOptions> extends Track {

    private T1 properties;
    private T2 demuxOptions;

    public TypedTrack(int index, TrackType type) {
        super(index, type);
    }

}
