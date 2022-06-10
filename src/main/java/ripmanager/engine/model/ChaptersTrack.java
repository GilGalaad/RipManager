package ripmanager.engine.model;

import lombok.Data;
import lombok.ToString;
import ripmanager.engine.enums.TrackType;

@Data
@ToString(callSuper = true)
public class ChaptersTrack extends TypedTrack<ChaptersProperties, DemuxOptions> {

    public ChaptersTrack(int index, String label) {
        super(index, TrackType.CHAPTERS, label);
    }

}
