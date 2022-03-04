package ripmanager.engine.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AudioTrack extends TypedTrack<AudioProperties, AudioDemuxOptions> {

    public AudioTrack(int index, String label) {
        super(index, TrackType.AUDIO, label);
    }

}
