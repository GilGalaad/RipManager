package ripmanager.engine.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChaptersTrack extends Track<Void, DemuxOptions> {

    public ChaptersTrack(int index) {
        super(index, TrackType.CHAPTERS);
    }

}
