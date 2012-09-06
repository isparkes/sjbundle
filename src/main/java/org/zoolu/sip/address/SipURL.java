/*
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
 *
 *  This file is part of MjSip (http://www.mjsip.org)
 *
 *  MjSip is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  MjSip is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MjSip; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Author(s):
 *  Luca Veltri (luca.veltri@unipr.it)
 *
 *  Modified:
 *  Benhur Langoni (bhlangonijr@gmail.com)
 *  Thiago Camargo (barata7@gmail.com)
 */

package org.zoolu.sip.address;


import org.zoolu.sip.provider.SipParser;
import org.zoolu.tools.Parser;

import java.util.List;


/**
 * <P> Class <i>SipURL</i> implements SIP URLs.
 * <P> A SIP URL is a string of the form of:
 * <BR><BLOCKQUOTE><PRE>&nbsp&nbsp sip:[user@]hostname[:port][;parameters] </PRE></BLOCKQUOTE>
 * <P> If <i>port</i> number is ommitted, -1 is returned
 */
public class SipURL {
    protected String url;

    protected static final String transportParam = "transport";
    protected static final String maddrParam = "maddr";
    protected static final String ttlParam = "ttl";
    protected static final String lrParam = "lr";

    /**
     * Creates a new SipURL based on a hostname or on a sip url as sip:[user@]hostname[:port][;param1=value1]..
     *
     * @param sipurl
     */
    public SipURL(String sipurl) {
        sipurl = sipurl.toLowerCase();
        if (sipurl.startsWith("sip:")) {
            url = sipurl;
        } else {
            url = "sip:" + sipurl;
        }
    }

    /**
     * Creates a new SipURL
     *
     * @param username
     * @param hostname
     */
    public SipURL(String username, String hostname) {
        init(username, hostname, -1);
    }

    /**
     * Creates a new SipURL
     *
     * @param hostname
     * @param portnumber
     */
    public SipURL(String hostname, int portnumber) {
        init(null, hostname, portnumber);
    }

    /**
     * Creates a new SipURL
     *
     * @param username
     * @param hostname
     * @param portnumber
     */
    public SipURL(String username, String hostname, int portnumber) {
        init(username, hostname, portnumber);
    }

    /**
     * Inits the SipURL
     *
     * @param username
     * @param hostname
     * @param portnumber
     */
    private void init(String username, String hostname, int portnumber) {
        StringBuilder sb = new StringBuilder("sip:");
        if (username != null) {
            sb.append(username).append('@');
        }
        sb.append(hostname);
        if (portnumber > 0) {
            sb.append(":").append(portnumber);
        }
        url = sb.toString();
    }

    /**
     * Creates and returns a copy of the URL
     */
    public Object clone() {
        return new SipURL(url);
    }

    /**
     * Indicates whether some other Object is "equal to" this URL
     */
    public boolean equals(Object obj) {
        SipURL newurl = (SipURL) obj;
        return url.equals(newurl.toString());
    }

    /**
     * Gets user name of SipURL (Returns null if user name does not exist)
     *
     * @return
     */
    public String getUserName() {
        int begin = 4; // skip "sip:"
        int end = url.indexOf('@', begin);
        if (end < 0) {
            return null;
        } else {
            return url.substring(begin, end);
        }
    }

    /**
     * Gets host of SipURL
     *
     * @return
     */
    public String getHost() {
        char[] host_terminators = {':', ';', '?'};
        Parser par = new Parser(url);
        int begin = par.indexOf('@'); // skip "sip:user@"
        if (begin < 0) {
            begin = 4; // skip "sip:"
        } else {
            begin++; // skip "@"
        }
        par.setPos(begin);
        int end = par.indexOf(host_terminators);
        if (end < 0) {
            return url.substring(begin);
        } else {
            return url.substring(begin, end);
        }
    }

    /**
     * Gets port of SipURL; returns -1 if port is not specidfied
     *
     * @return
     */
    public int getPort() {
        char[] port_terminators = {';', '?'};
        Parser par = new Parser(url, 4); // skip "sip:"
        int begin = par.indexOf(':');
        if (begin < 0) {
            return -1;
        } else {
            begin++;
            par.setPos(begin);
            int end = par.indexOf(port_terminators);
            if (end < 0) {
                return Integer.parseInt(url.substring(begin));
            } else {
                return Integer.parseInt(url.substring(begin, end));
            }
        }
    }

    /**
     * Gets boolean value to indicate if SipURL has user name
     *
     * @return
     */
    public boolean hasUserName() {
        return getUserName() != null;
    }

    /**
     * Gets boolean value to indicate if SipURL has port
     *
     * @return
     */
    public boolean hasPort() {
        return getPort() >= 0;
    }

    /**
     * Whether two SipURLs are equals
     *
     * @param sip_url
     * @return
     */
    public boolean equals(SipURL sip_url) {
        return (url.equals(sip_url.url));
    }


    /**
     * Gets string representation of URL
     */
    public String toString() {
        return url;
    }

    /**
     * Gets the value of specified parameter.
     *
     * @param name
     * @return null if parameter does not exist.
     */
    public String getParameter(String name) {
        SipParser par = new SipParser(url);
        return ((SipParser) par.goTo(';').skipChar()).getParameter(name);
    }


    /**
     * Gets a String List of parameter names.
     *
     * @return null if no parameter is present
     */
    public List getParameters() {
        SipParser par = new SipParser(url);
        return ((SipParser) par.goTo(';').skipChar()).getParameters();
    }

    /**
     * Whether there is the specified parameter
     *
     * @param name
     * @return
     */
    public boolean hasParameter(String name) {
        SipParser par = new SipParser(url);
        return ((SipParser) par.goTo(';').skipChar()).hasParameter(name);
    }

    /**
     * Whether there are any parameters
     *
     * @return
     */
    public boolean hasParameters() {
        return url != null && url.indexOf(';') >= 0;
    }

    /**
     * Adds a new parameter without a value
     *
     * @param name
     */
    public void addParameter(String name) {
        url = url + ";" + name;
    }

    /**
     * Adds a new parameter with value
     *
     * @param name
     * @param value
     */
    public void addParameter(String name, String value) {
        if (value != null) {
            url = url + ";" + name + "=" + value;
        } else {
            url = url + ";" + name;
        }
    }

    /**
     * Removes all parameters (if any)
     */
    public void removeParameters() {
        int index = url.indexOf(';');
        if (index >= 0) {
            url = url.substring(0, index);
        }
    }

    /**
     * Removes specified parameter (if present)
     *
     * @param name
     */
    public void removeParameter(String name) {
        int index = url.indexOf(';');
        if (index < 0) {
            return;
        }
        Parser par = new Parser(url, index);
        while (par.hasMore()) {
            int beginParam = par.getPos();
            par.skipChar();
            if (par.getWord(SipParser.param_separators).equals(name)) {
                String top = url.substring(0, beginParam);
                par.goToSkippingQuoted(';');
                String bottom = "";
                if (par.hasMore()) {
                    bottom = url.substring(par.getPos());
                }
                url = top.concat(bottom);
                return;
            }
            par.goTo(';');
        }
    }


    /**
     * Gets the value of transport parameter.
     *
     * @return null if no transport parameter is present.
     */
    public String getTransport() {
        return getParameter(transportParam);
    }

    /**
     * Whether transport parameter is present
     *
     * @return
     */
    public boolean hasTransport() {
        return hasParameter(transportParam);
    }

    /**
     * Adds transport parameter
     *
     * @param proto
     */
    public void addTransport(String proto) {
        addParameter(transportParam, proto.toLowerCase());
    }


    /**
     * Gets the value of maddr parameter.
     *
     * @return null if no maddr parameter is present.
     */
    public String getMaddr() {
        return getParameter(maddrParam);
    }

    /**
     * Whether maddr parameter is present
     *
     * @return
     */
    public boolean hasMaddr() {
        return hasParameter(maddrParam);
    }

    /**
     * Adds maddr parameter
     *
     * @param maddr
     */
    public void addMaddr(String maddr) {
        addParameter(maddrParam, maddr);
    }


    /**
     * Gets the value of ttl parameter.
     *
     * @return 1 if no ttl parameter is present.
     */
    public int getTtl() {
        try {
            return Integer.parseInt(getParameter(ttlParam));
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Whether ttl parameter is present
     *
     * @return
     */
    public boolean hasTtl() {
        return hasParameter(ttlParam);
    }

    /**
     * Adds ttl parameter
     *
     * @param ttl
     */
    public void addTtl(int ttl) {
        addParameter(ttlParam, Integer.toString(ttl));
    }


    /**
     * Whether lr (loose-route) parameter is present
     *
     * @return
     */
    public boolean hasLr() {
        return hasParameter(lrParam);
    }

    /**
     * Adds lr parameter
     */
    public void addLr() {
        addParameter(lrParam);
    }
}


