package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANId;
import org.ab1rw.candora.core.payloads.CAN2Message;
import org.ab1rw.candora.core.payloads.CANFDMessage;

import java.util.Arrays;
/**
 * Copyright Header
 * Package-scoped to discourage exposure of this class beyond the implementation of an Adapter.
 */
class NativeCANFrame {

    /*
    struct canfd_frame {
            canid_t can_id;  // 32 bit CAN_ID + EFF/RTR/ERR flags
            __u8    len;     // frame payload length in byte (0 .. 64)
            __u8    flags;   // additional flags for CAN FD
            __u8    __res0;  // reserved / padding
            __u8    __res1;  // reserved / padding
            __u8    data[64] __attribute__((aligned(8)));
    };
    */

    NativeCANFrame() {}
    /**
     * ctor: build a raw frame for transmission from a CAN 2.0 compliant message payload
     * @param m value object
     */
    NativeCANFrame(CAN2Message m) {
    }

    /**
     * ctor build a raw frame for transmission from a CAN FD compliant message payload
     * @param m value object
     */
    NativeCANFrame(CANFDMessage m) {
        can_id = m.getId().getBits() & 0x80000000;
        can_dlc = (byte) m.getPayloadLength();
        can_data = m.getPayload();
        can_fd_flags = 0;
    }

    /*
     * Note that all attribute names, and types, are coupled to C code in the nativeapi maven module of
     * this project. If you change signature here, you must reflect the same changes in the C code, and
     * vice versa.
     */
    protected int  can_id;
    protected byte can_dlc;
    protected byte can_fd_flags;
    protected byte reserved0, reserved1;
    protected byte[] can_data;

    /** If true, Extended Frame */
    protected boolean effFlag;
    protected boolean errFlag;
    protected int timestamp;


}
