package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANId;
import org.ab1rw.candora.core.payloads.CAN2Message;
import org.ab1rw.candora.core.payloads.CANFDMessage;

import java.util.Arrays;
/**
 * Mutable Value Object to shuttle canfd_frame data between the C++ Native code and the Java adapter class.
 * Package-scoped to discourage exposure of this class beyond the inner implementation of an Adapter.
 *
 * Never expose this class public ; it is only here to make the C++ code abit simpler.
 *
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

    /*
     * Note that all attribute names, and types, are coupled to C code in the nativeapi maven module of
     * this project. If you change signature here, you must reflect the same changes in the C code, and
     * vice versa.
     */
    protected String canInterface;
    protected int  can_id;
    protected byte can_dlc;
    protected byte can_fd_flags;
    protected byte reserved0, reserved1;
    protected byte[] can_data;


    protected boolean effFlag; // set by native adapter, if extended frame (29 bit address)
    protected boolean errFlag; // set by native adapter if payload contains error bits
    protected int timestamp;   // set by native adapter, SocketCAN implementation specific timestamp.


}
