/*
 * Copyright 2001 (C) MetaStuff, Ltd. All Rights Reserved.
 * 
 * This software is open source. 
 * See the bottom of this file for the licence.
 * 
 */

package org.dom4j.io;

import java.io.File;
import java.lang.reflect.Method;

import junit.framework.*;
import junit.textui.TestRunner;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

/** External DTDs and external entities are not read where dom4j now
  * disables them by default (CVE-2020-10683): an external DTD for any
  * <code>SAXReader</code> using the default parser, and external DTDs and
  * external entities for <code>DocumentHelper.parseText()</code> and
  * <code>SAXReader.createDefault()</code>. Only local files are used.
  * Xerces is selected through JAXP because the features are Xerces' and
  * SAX's; the default parser on this test class path is Crimson.
  */
public class TestExternalResources extends TestCase {

    protected static final String SAX_PARSER_FACTORY = "javax.xml.parsers.SAXParserFactory";

    protected static final String XERCES_SAX_PARSER_FACTORY = "org.apache.xerces.jaxp.SAXParserFactoryImpl";

    protected static final String ENTITY_TEXT = "external entity text";

    private String oldFactory;

    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestExternalResources.class );
    }

    public TestExternalResources(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        oldFactory = System.getProperty( SAX_PARSER_FACTORY );
        System.getProperties().put( SAX_PARSER_FACTORY, XERCES_SAX_PARSER_FACTORY );
    }

    protected void tearDown() throws Exception {
        if ( oldFactory == null ) {
            System.getProperties().remove( SAX_PARSER_FACTORY );
        }
        else {
            System.getProperties().put( SAX_PARSER_FACTORY, oldFactory );
        }
    }

    // Test case(s)
    //-------------------------------------------------------------------------
    public void testParseTextDoesNotReadExternalEntity() throws Exception {
        Document document = DocumentHelper.parseText( externalEntityDocument() );
        assertEquals( "", document.getRootElement().getText() );
    }

    public void testCreateDefaultDoesNotReadExternalEntity() throws Exception {
        Method createDefault = SAXReader.class.getMethod( "createDefault", new Class[0] );
        SAXReader reader = (SAXReader) createDefault.invoke( null, new Object[0] );
        Document document = reader.read( new java.io.StringReader( externalEntityDocument() ) );
        assertEquals( "", document.getRootElement().getText() );
    }

    public void testSAXReaderDoesNotLoadExternalDTD() throws Exception {
        Document document = new SAXReader().read( new File( "xml/dtd/defaults.xml" ).getAbsoluteFile().toURL() );
        assertNull( document.getRootElement().attributeValue( "source" ) );
    }

    public void testExternalDTDCanBeEnabled() throws Exception {
        SAXReader reader = new SAXReader();
        reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", true );
        Document document = reader.read( new File( "xml/dtd/defaults.xml" ).getAbsoluteFile().toURL() );
        assertEquals( "external-dtd", document.getRootElement().attributeValue( "source" ) );
    }

    public void testSAXReaderKeepsParserDefaultForEntities() throws Exception {
        Document document = new SAXReader().read( new java.io.StringReader( externalEntityDocument() ) );
        assertEquals( ENTITY_TEXT, document.getRootElement().getText().trim() );
    }

    protected String externalEntityDocument() throws Exception {
        String url = new File( "xml/dtd/entity.txt" ).getAbsoluteFile().toURL().toString();
        return "<?xml version='1.0'?>\n"
            + "<!DOCTYPE greeting [\n"
            + "  <!ENTITY ext SYSTEM '" + url + "'>\n"
            + "]>\n"
            + "<greeting>&ext;</greeting>";
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
