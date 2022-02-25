package ripmanager.engine.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SubtitlesTrack extends Track<SubtitlesProperties, DemuxOptions> {

    public SubtitlesTrack(int index) {
        super(index, TrackType.SUBTITLES);
    }

}
