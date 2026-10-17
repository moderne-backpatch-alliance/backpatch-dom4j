/*
 * Copyright 2001 (C) MetaStuff, Ltd. All Rights Reserved.
 * 
 * This software is open source. 
 * See the bottom of this file for the licence.
 * 
 * $Id$
 */

package org.dom4j;

import junit.framework.*;
import junit.textui.TestRunner;

/** Element, attribute and namespace names are checked against the XML
  * Name, NCName and QName productions (CVE-2018-1000632). Ported from
  * upstream's AllowedCharsTest (dom4j 2.1.1).
  *
  * @version $Revision$
  */
public class TestAllowedChars extends TestCase {

    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestAllowedChars.class );
    }

    public TestAllowedChars(String name) {
        super(name);
    }

    // Test case(s)
    //-------------------------------------------------------------------------
    public void testLocalName() {
        QName.get("element");
        QName.get(":element");
        QName.get("elem:ent");
    }

    public void testLocalNameFail() {
        try {
            QName.get("!element");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testQName() {
        QName.get("element", "http://example.com/namespace");
        QName.get("ns:element", "http://example.com/namespace");
    }

    public void testQNameFail1() {
        try {
            QName.get("ns:elem:ent", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testQNameFail2() {
        try {
            QName.get(":nselement", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testCreateElementLT() {
        try {
            DocumentHelper.createElement("element<name");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testCreateElementGT() {
        try {
            DocumentHelper.createElement("element>name");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testCreateElementAmpersand() {
        try {
            DocumentHelper.createElement("element&name");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testAddElement() {
        Element root = DocumentHelper.createElement("root");
        try {
            root.addElement("element>name");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testAddElementQualified() {
        Element root = DocumentHelper.createElement("root");
        try {
            root.addElement("element>name", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testAddElementQualifiedPrefix() {
        Element root = DocumentHelper.createElement("root");
        try {
            root.addElement("ns:element>name", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testAddElementPrefix() {
        Element root = DocumentHelper.createElement("root");
        try {
            root.addElement("ns>:element", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testAddAttribute() {
        Element root = DocumentHelper.createElement("root");
        root.addAttribute("valid-name", "value");
        try {
            root.addAttribute("a=\"b\" c", "value");
            fail("IllegalArgumentException expected");
        }
        catch (IllegalArgumentException e) {
        }
    }
}




/*
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "DOM4J" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of MetaStuff, Ltd.  For written permission,
 *    please contact dom4j-info@metastuff.com.
 *
 * 4. Products derived from this Software may not be called "DOM4J"
 *    nor may "DOM4J" appear in their names without prior written
 *    permission of MetaStuff, Ltd. DOM4J is a registered
 *    trademark of MetaStuff, Ltd.
 *
 * 5. Due credit should be given to the DOM4J Project
 *    (http://dom4j.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * METASTUFF, LTD. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2001 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * $Id$
 */
