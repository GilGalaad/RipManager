package ripmanager.engine.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AudioTrack extends Track<AudioProperties, AudioDemuxOptions> {

    public AudioTrack(int index) {
        super(index, TrackType.AUDIO);
    }

}
