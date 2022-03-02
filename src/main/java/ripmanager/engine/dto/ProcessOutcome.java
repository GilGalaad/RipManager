package ripmanager.engine.dto;

import lombok.Data;

@Data
public class ProcessOutcome {

    private final int exitCode;
    private final String stdout;

}
