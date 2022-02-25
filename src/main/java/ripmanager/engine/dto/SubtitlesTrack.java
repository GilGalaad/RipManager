package ripmanager.engine.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SubtitlesTrack extends TypedTrack<SubtitlesProperties, DemuxOptions> {

    public SubtitlesTrack(int index, String label) {
        super(index, TrackType.SUBTITLES, label);
    }

}
