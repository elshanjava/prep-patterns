package coding.structural.adapter;

import java.util.logging.Level;

public class ThirdPartyLogAdapter implements Logger {
    private final ThirdPartyLog thirdPartyLog;

    public ThirdPartyLogAdapter(ThirdPartyLog thirdPartyLog) {
        this.thirdPartyLog = thirdPartyLog;
    }

    @Override
    public void log(Level level, String message) {
        thirdPartyLog.writeLog(message, level.intValue());
    }
}
