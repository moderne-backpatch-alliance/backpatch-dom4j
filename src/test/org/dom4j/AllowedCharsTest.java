/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.dom4j;

import junit.framework.TestCase;

public class AllowedCharsTest extends TestCase {
    public void testLocalName() {
        QName.get("element");
        QName.get(":element");
        QName.get("elem:ent");
    }

    public void testQname() {
        QName.get("element", "http://example.com/namespace");
        QName.get("ns:element", "http://example.com/namespace");
    }

    public void testLocalNameFail() {
        try {
            QName.get("!element");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testQnameFail1() {
        try {
            QName.get("ns:elem:ent", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testQnameFail2() {
        try {
            QName.get(":nselement", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testCreateElementLT() {
        try {
            DocumentHelper.createElement("element<name");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testCreateElementGT() {
        try {
            DocumentHelper.createElement("element>name");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testCreateElementAmpersand() {
        try {
            DocumentHelper.createElement("element&name");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddElement() {
        Element root = DocumentHelper.createElement("root");

        try {
            root.addElement("element>name");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddElementQualified() {
        Element root = DocumentHelper.createElement("root");

        try {
            root.addElement("element>name", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddElementQualifiedPrefix() {
        Element root = DocumentHelper.createElement("root");

        try {
            root.addElement("ns:element>name", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddElementPrefix() {
        Element root = DocumentHelper.createElement("root");

        try {
            root.addElement("ns>:element", "http://example.com/namespace");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    //TODO It is illegal to create element or attribute with namespace prefix and empty namespace IRI.
    //See https://www.w3.org/TR/2006/REC-xml-names11-20060816/#scoping
}

/*
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The name "DOM4J" must not be used to endorse or promote products derived
 * from this Software without prior written permission of MetaStuff, Ltd. For
 * written permission, please contact dom4j-info@metastuff.com.
 * 
 * 4. Products derived from this Software may not be called "DOM4J" nor may
 * "DOM4J" appear in their names without prior written permission of MetaStuff,
 * Ltd. DOM4J is a registered trademark of MetaStuff, Ltd.
 * 
 * 5. Due credit should be given to the DOM4J Project - http://www.dom4j.org
 * 
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL METASTUFF, LTD. OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 */
