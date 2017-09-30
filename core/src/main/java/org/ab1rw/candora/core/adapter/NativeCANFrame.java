package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANId;
import org.ab1rw.candora.core.payloads.CAN2Message;
import org.ab1rw.candora.core.payloads.CANFDMessage;

import java.util.Arrays;

/**
 * Package-scoped to discourage exposure of this class beyond the implementation of an Adapter.
 */
class NativeCANFrame {

    /*
    struct can_frame {
            canid_t can_id;  // 32 bit CAN_ID + EFF/RTR/ERR flags
            __u8    can_dlc;         // frame payload length in byte (0 .. 8)
            __u8    __pad;   // padding
            __u8    __res0;  //reserved / padding
            __u8    __res1;  //reserved / padding
            __u8    data[8] __attribute__((aligned(8)));
    };

    struct canfd_frame {
            canid_t can_id;  // 32 bit CAN_ID + EFF/RTR/ERR flags
            __u8    len;     // frame payload length in byte (0 .. 64)
            __u8    flags;   // additional flags for CAN FD
            __u8    __res0;  // reserved / padding
            __u8    __res1;  // reserved / padding
            __u8    data[64] __attribute__((aligned(8)));
    };
    */

    NativeCANFrame(CAN2Message m) {
    }
    NativeCANFrame(CANFDMessage m) {
        can_id = m.getId().getBits() & 0x80000000;
        can_dlc = (byte) m.getPayloadLength();
        can_data = m.getPayload();
        can_fd_flags = 0;
    }

    int  can_id;
    byte can_dlc;
    byte can_fd_flags;
    byte reserved0, reserved1;
    byte[] can_data;

    /** If true, Extended Frame */
    boolean effFlag;
    boolean errFlag;


}
