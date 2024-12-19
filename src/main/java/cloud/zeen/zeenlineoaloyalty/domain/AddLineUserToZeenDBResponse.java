package cloud.zeen.zeenlineoaloyalty.domain;

import java.util.List;

public class AddLineUserToZeenDBResponse {
    private String lineUserId;
    private String zeenUserId;
    private List<LIDToZIDMapping> currentLIDToZIDMappings;

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

    public List<LIDToZIDMapping> getCurrentLIDToZIDMappings() {
        return currentLIDToZIDMappings;
    }

    public void setCurrentLIDToZIDMappings(List<LIDToZIDMapping> currentLIDToZIDMappings) {
        this.currentLIDToZIDMappings = currentLIDToZIDMappings;
    }
}
