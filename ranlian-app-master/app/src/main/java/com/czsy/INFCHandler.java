package com.czsy;

import java.io.Serializable;

public interface INFCHandler {
    public static class NFCInfo implements Serializable {
        final public String xpbm;
        final public String chip_sn;//芯片号<D47178CC>

        public NFCInfo(String xpbm, String chip_sn) {
            this.xpbm = xpbm;
            this.chip_sn = chip_sn;
        }
    }

    void onNFCIntent(NFCInfo i);
}
