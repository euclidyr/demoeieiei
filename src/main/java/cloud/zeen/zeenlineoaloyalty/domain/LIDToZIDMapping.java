package cloud.zeen.zeenlineoaloyalty.domain;

public class LIDToZIDMapping {
    private String lineUserId;
    private String zeenUserId;

    public LIDToZIDMapping(String lineUserId, String zeenUserId) {
        this.lineUserId = lineUserId;
        this.zeenUserId = zeenUserId;
    }

    public String getLineUserId() {
        return lineUserId;
    }

    public void setLineUserId(String lineUserId) {
        this.lineUserId = lineUserId;
    }

    public String getZeenUserId() {
        return zeenUserId;
    }

    public void setZeenUserId(String zeenUserId) {
        this.zeenUserId = zeenUserId;
    }
}
