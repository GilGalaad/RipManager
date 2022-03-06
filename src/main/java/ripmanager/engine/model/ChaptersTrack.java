package ripmanager.engine.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChaptersTrack extends TypedTrack<ChaptersProperties, DemuxOptions> {

    public ChaptersTrack(int index, String label) {
        super(index, TrackType.CHAPTERS, label);
    }

}