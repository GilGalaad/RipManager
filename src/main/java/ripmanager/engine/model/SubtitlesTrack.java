package ripmanager.engine.model;

import lombok.Data;
import lombok.ToString;
import ripmanager.engine.enums.TrackType;

@Data
@ToString(callSuper = true)
public class SubtitlesTrack extends TypedTrack<SubtitlesProperties, DemuxOptions> {

    public SubtitlesTrack(int index, String label) {
        super(index, TrackType.SUBTITLES, label);
    }

}
