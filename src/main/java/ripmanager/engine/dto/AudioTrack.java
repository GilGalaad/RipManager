package ripmanager.engine.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AudioTrack extends TypedTrack<AudioProperties, AudioDemuxOptions> {

    public AudioTrack(int index) {
        super(index, TrackType.AUDIO);
    }

}
