package org.ab1rw.candora.core.adapter;

/**
 *
 * Never expose this class public ; it is only here to make the C++ code abit simpler.
 * This class lives as an Array of objects access by JNI Code.
 */
class NativeCANFilter {

    /*
     * struct can_filter {
     *       canid_t can_id;
     *       canid_t can_mask;
     * };
     */

    protected int can_id;
    protected int can_mask;

}
