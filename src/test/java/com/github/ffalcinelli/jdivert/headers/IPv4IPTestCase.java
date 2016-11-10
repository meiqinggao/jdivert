/*
 * Copyright (c) Fabio Falcinelli 2016.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ffalcinelli.jdivert.headers;

import com.github.ffalcinelli.jdivert.Enums;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static com.github.ffalcinelli.jdivert.Enums.Protocol.ROUTING;
import static org.junit.Assert.*;

/**
 * Created by fabio on 26/10/2016.
 */
public abstract class IPv4IPTestCase extends IPTestCase {

    protected Ipv4 ipv4Hdr;
    protected int ipCksum;
    protected int ident;
    protected byte[] options;
    protected int ttl;

    @Before
    public void setUp() {
        super.setUp();
        ipv4Hdr = new Ipv4(ByteBuffer.wrap(rawData));
        ipHdr = ipv4Hdr;
        localhost = "127.0.0.1";
        ipVersion = 4;
        ttl = 64;
    }

    @Test
    public void nextHeaderProtocolBis() {
        assertEquals(ipHdr.getNextHeaderProtocol(), ipv4Hdr.getProtocol());
    }

    @Test
    public void protocol() {
        assertEquals(protocol, ipv4Hdr.getProtocol());
        ipv4Hdr.setProtocol(ROUTING);
        assertEquals(ROUTING, ipv4Hdr.getProtocol());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalProtocol() {
        ipv4Hdr.setProtocol(Enums.Protocol.fromValue(11));
    }

    @Test
    public void headerLengthBis() {
        assertNull(ipv4Hdr.getOptions());
        assertEquals(ipHeaderLength, ipHdr.getRawHeaderBytes().length);
    }

    @Test
    public void internetHeaderLength() {
        assertEquals(ipHdr.getHeaderLength(), ipv4Hdr.getIHL() * 4);
        ipv4Hdr.setIHL(6);
        assertEquals(6, ipv4Hdr.getIHL());
        assertEquals(24, ipv4Hdr.getHeaderLength());
        assertEquals(4, ipv4Hdr.getVersion());
        assertEquals(4, ipv4Hdr.getOptions().length);
    }

    @Test
    public void totalLength() {
        assertEquals(rawData.length, ipv4Hdr.getTotalLength());
        ipv4Hdr.setTotalLength((short) 21);
        assertEquals(21, ipv4Hdr.getTotalLength());
    }

    @Test
    public void flags() {
        for (Ipv4.Flag flag : Ipv4.Flag.values()) {
            assertFalse(ipv4Hdr.is(flag));
            ipv4Hdr.set(flag, true);
            assertTrue(ipv4Hdr.is(flag));
        }
    }

    @Test
    public void checksum() {
        assertEquals(ipCksum, ipv4Hdr.getChecksum());
        ipv4Hdr.setChecksum((short) 21);
        assertEquals(21, ipv4Hdr.getChecksum());
    }

    @Test
    public void options() {
        assertEquals(null, ipv4Hdr.getOptions());
    }

    @Test(expected = IllegalStateException.class)
    public void optionsIllegalSize() {
        ipv4Hdr.setOptions(new byte[]{0x1, 0x2, 0x3, 0x4});
    }

    @Test
    public void adjustLengthAndAddOptions() {
        byte[] options = new byte[]{0x1, 0x2, 0x3, 0x4};
        ipv4Hdr.setIHL(6);
        ipv4Hdr.setOptions(options);
        assertArrayEquals(options, ipv4Hdr.getOptions());
    }

    @Test
    public void identification() {
        assertEquals(ident, ipv4Hdr.getID());
        ipv4Hdr.setID((short) 2);
        assertEquals(2, ipv4Hdr.getID());
    }

    @Test
    public void equalsAndHashCode() {
        Ipv4 ipHdr2 = new Ipv4(ByteBuffer.wrap(rawData));
        assertTrue(ipHdr.equals(ipHdr2));
        assertEquals(ipHdr.hashCode(), ipHdr2.hashCode());
    }

    @Test
    public void ttl() {
        assertEquals(ttl, ipv4Hdr.getTTL());
        ipv4Hdr.setTTL(64);
        assertEquals(64, ipv4Hdr.getTTL());
        assertTrue(ipv4Hdr.toString().contains("TTL=" + 64));
    }
}
