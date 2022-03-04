package ripmanager.engine.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class TypedTrack<T1, T2 extends DemuxOptions> extends Track {

    private T1 properties;
    private T2 demuxOptions;

    public TypedTrack(int index, TrackType type, String label) {
        super(index, type, label);
    }
}
