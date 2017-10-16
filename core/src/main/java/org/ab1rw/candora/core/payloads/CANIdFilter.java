package org.ab1rw.candora.core.payloads;

/**
 * A filter rule passed to the Linux SocketCAN Layer to define which can IDs
 * we might receive traffic on.  A filter matches, when received_can_id &amp; mask == can_id &amp; mask
 */
public class CANIdFilter {

    private final int mask;
    private int id;

    public static final int CAN_INV_FILTER = 0x20000000;

    public CANIdFilter(CANId _id, int _mask) {
        if (_mask == 0) throw new IllegalArgumentException("Filter mask must be non-zero, given "+_mask);
        id=_id.getBits(); mask=_mask;
    }
    public CANIdFilter(int _id, int _mask) {

        id=_id; mask=_mask;
    }

    public int getMask() {
        return mask;
    }

    public int getId() {
        return id;
    }

    /**
     * Test to see if a given CAN ID might match this filter rule.
     * A filter matches, when received_can_id &amp; mask == can_id &amp; mask
     * @param test proposed CAN Id
     * @return true if the proposed CAN Id matches this filter rule.
     */
    public final boolean matches(CANId test) {
            return (test.getBits() & mask) == (id & mask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CANIdFilter that = (CANIdFilter) o;

        if (mask != that.mask) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        int result = mask;
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "CANIdFilter{" +
                "id=" + String.format("%08x",id) +
                ", mask=" + String.format("%08x",mask) +
                '}';
    }


}
