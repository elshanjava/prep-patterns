package coding.structural.adapter;


public class ThirdPartyLog {

    public void writeLog(String text, int severity) {
        System.out.println("[" + severity + "] " + text);
    }
}
