package org.xmpp;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StunCheck extends TestCase {

    final static byte BINDING_REQUEST_ID = 0x0001;
    final static int MAPPED_ADDRESS = 0x0001;
    final static byte CHANGE_REQUEST_NO_CHANGE[] = {0, 3, 0, 4, 0, 0, 0, 0};
    final static Random r = new Random(System.nanoTime());

    private static byte[] getHeader(final int contentLenght, final int id) {
        final byte header[] = new byte[20];
        header[0] = 0;
        header[1] = BINDING_REQUEST_ID;
        header[2] = 0;
        header[3] = (byte) contentLenght;
        ByteBuffer idb = ByteBuffer.allocate(14);
        idb.putInt(id);
        System.arraycopy(idb.array(), 0, header, 4, 14);
        return header;
    }

    public static StunRequest createSTUNChangeRequest() {
        final int id = r.nextInt(2500);
        final byte header[] = getHeader(CHANGE_REQUEST_NO_CHANGE.length, id);
        final byte data[] = new byte[header.length + CHANGE_REQUEST_NO_CHANGE.length];
        System.arraycopy(header, 0, data, 0, header.length);
        System.arraycopy(CHANGE_REQUEST_NO_CHANGE, 0, data, header.length, CHANGE_REQUEST_NO_CHANGE.length);
        return new StunRequest(ByteBuffer.wrap(data), id);
    }

    public static StunResponse parseResponse(byte[] data) {
        byte[] lengthArray = new byte[2];
        System.arraycopy(data, 2, lengthArray, 0, 2);
        byte[] idb = new byte[14];
        System.arraycopy(data, 4, idb, 0, 14);
        int id = ByteBuffer.wrap(idb).getInt();
        int length = unsignedShortToInt(lengthArray);
        byte[] cuttedData;
        int offset = 20;

        while (length > 0) {
            cuttedData = new byte[length];
            System.arraycopy(data, offset, cuttedData, 0, length);
            Header h = parseHeader(cuttedData);

            if (h.getType() == MAPPED_ADDRESS) {
                final StunResponse sr = new StunResponse(id);
                sr.addHeader(h);
                return sr;
            }
            length -= h.getLength();
            offset += h.getLength();
        }
        return null;
    }

    private static Header parseHeader(byte[] data) {
        byte[] typeArray = new byte[2];
        System.arraycopy(data, 0, typeArray, 0, 2);
        int type = unsignedShortToInt(typeArray);
        byte[] lengthArray = new byte[2];
        System.arraycopy(data, 2, lengthArray, 0, 2);
        int lengthValue = unsignedShortToInt(lengthArray);
        byte[] valueArray = new byte[lengthValue];
        System.arraycopy(data, 4, valueArray, 0, lengthValue);
        if (data.length >= 8) {
            int family = unsignedByteToInt(valueArray[1]);
            if (family == 1) {
                byte[] portArray = new byte[2];
                System.arraycopy(valueArray, 2, portArray, 0, 2);
                int port = unsignedShortToInt(portArray);
                int firstOctet = unsignedByteToInt(valueArray[4]);
                int secondOctet = unsignedByteToInt(valueArray[5]);
                int thirdOctet = unsignedByteToInt(valueArray[6]);
                int fourthOctet = unsignedByteToInt(valueArray[7]);
                final StringBuilder ip = new StringBuilder().append(firstOctet).append(".").append(secondOctet).append(".").append(thirdOctet).append(".").append(fourthOctet);
                return new Header(new InetSocketAddress(ip.toString(), port), type, lengthValue + 4);
            }
        }
        return new Header(null, -1, lengthValue + 4);
    }

    public static int unsignedShortToInt(final byte[] b) {
        int a = b[0] & 0xFF;
        int aa = b[1] & 0xFF;
        return ((a << 8) + aa);
    }

    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    public static class Header {
        final InetSocketAddress address;
        final int type;
        final int length;

        public Header(final InetSocketAddress address, int type, int length) {
            this.address = address;
            this.type = type;
            this.length = length;
        }

        public int getType() {
            return type;
        }

        public InetSocketAddress getAddress() {
            return address;
        }

        public int getLength() {
            return length;
        }
    }

    public static class StunRequest {

        private ByteBuffer bytes;
        private int id;

        public StunRequest(ByteBuffer bytes, int id) {
            this.bytes = bytes;
            this.id = id;
        }

        public ByteBuffer getBytes() {
            return bytes;
        }

        public int getId() {
            return id;
        }
    }

    public static class StunResponse {

        private int id;
        private List<Header> headers = new ArrayList<Header>(1);

        public StunResponse(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void addHeader(final Header h) {
            headers.add(h);
        }

        public List<Header> getHeaders() {
            return headers;
        }
    }

    public void testStun() throws IOException, UnknownHostException {

        final DatagramSocket s = new DatagramSocket(4040);
        final StunRequest srq = createSTUNChangeRequest();
        final byte[] b = srq.getBytes().array();
        final DatagramPacket p = new DatagramPacket(b, b.length);

        p.setAddress(InetAddress.getByName("stun.ym.ms"));
        p.setPort(3478);

        s.send(p);

        final DatagramPacket r = new DatagramPacket(new byte[201], 200);

        s.receive(r);

        System.out.println(r.getData());

        final StunResponse sr = parseResponse(r.getData());

        assertEquals(srq.getId(), sr.getId());

        System.out.println(sr.getHeaders().get(0).getAddress() + " ID: " + sr.getId());


    }

}

