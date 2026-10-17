/*
 * Copyright 2001 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 *
 * $Id$
 */

package org.dom4j;


import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.dom4j.tree.QNameCache;
import java.util.*;

/** <p><code>QName</code> represents a qualified name value of an XML element
  * or attribute. It consists of a local name and a {@link Namespace}
  * instance. This object is immutable.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision$
  */
public class QName implements Serializable {

    //protected transient static QNameCache cache = new QNameCache();
    protected transient static ThreadLocal cachePerThread = new ThreadLocal();

    /** The local name of the element or attribute */
    private String name;

    /** The qualified name of the element or attribute */
    private String qualifiedName;

    /** The Namespace of this element or attribute */
    private transient Namespace namespace;

    /** A cached version of the hashcode for efficiency */
    private int hashCode;

    /** The document factory used for this QName if specified or null */
    private DocumentFactory documentFactory;


    public static QName get(String name) {
        return getCache().get(name);
    }

    public static QName get(String name, Namespace namespace) {
        return getCache().get(name, namespace);
    }

    public static QName get(String name, String prefix, String uri) {
        return getCache().get(name, Namespace.get( prefix, uri ));
    }

    public static QName get(String qualifiedName, String uri) {
        return getCache().get(qualifiedName, uri);
    }

    public static QName get(String localName, Namespace namespace, String qualifiedName) {
        return getCache().get(localName, namespace, qualifiedName);
    }

    public QName(String name) {
        this( name, Namespace.NO_NAMESPACE );
    }

    public QName(String name, Namespace namespace) {
        this.name = (name == null) ? "" : name;
        this.namespace = (namespace == null) ? Namespace.NO_NAMESPACE : namespace;
        if ( this.namespace.equals( Namespace.NO_NAMESPACE ) ) {
            validateName( this.name );
        }
        else {
            validateNCName( this.name );
        }
    }

    public QName(String name, Namespace namespace, String qualifiedName) {
        this.name = (name == null) ? "" : name;
        this.qualifiedName = qualifiedName;
        this.namespace = (namespace == null) ? Namespace.NO_NAMESPACE : namespace;
        validateNCName( this.name );
        // a null qualified name is derived later from the validated prefix and name
        if ( this.qualifiedName != null ) {
            validateQName( this.qualifiedName );
        }
    }


    /** @return the local name
      */
    public String getName() {
        return name;
    }

    /** @return the qualified name in the format <code>prefix:localName</code>
      */
    public String getQualifiedName() {
        if ( qualifiedName == null ) {
            String prefix = getNamespacePrefix();
            if ( prefix != null && prefix.length() > 0 ) {
                qualifiedName = prefix + ":" + name;
            }
            else {
                qualifiedName = name;
            }
        }
        return qualifiedName;
    }

    /** @return the namespace of this QName
      */
    public Namespace getNamespace() {
        return namespace;
    }

    /** @return the namespace URI of this QName
      */
    public String getNamespacePrefix() {
        if ( namespace == null ) {
            return "";
        }
        return namespace.getPrefix();
    }

    /** @return the namespace URI of this QName
      */
    public String getNamespaceURI() {
        if ( namespace == null ) {
            return "";
        }
        return namespace.getURI();
    }


    /** @return the hash code based on the qualified name and the URI of the
      * namespace.
      */
    public int hashCode() {
        if ( hashCode == 0 ) {
            hashCode = getName().hashCode()
                ^ getNamespaceURI().hashCode();
            if ( hashCode == 0 ) {
                hashCode = 0xbabe;
            }
        }
        return hashCode;
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }
        else if ( object instanceof QName ) {
            QName that = (QName) object;
            // we cache hash codes so this should be quick
            if ( hashCode() == that.hashCode() ) {
                return getName().equals( that.getName() )
                    && getNamespaceURI().equals( that.getNamespaceURI());
            }
        }
        return false;
    }

    public String toString() {
        return super.toString() + " [name: " + getName()
            + " namespace: \"" + getNamespace() + "\"]";
    }

    /** @return the factory that should be used for Elements of this QName */
    public DocumentFactory getDocumentFactory() {
        return documentFactory;
    }

    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {

        // We use writeObject() and not writeUTF() to minimize space
        // This allows for writing pointers to already written strings
        out.writeObject(namespace.getPrefix());
        out.writeObject(namespace.getURI());

        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

        String prefix = (String) in.readObject();
        String uri = (String) in.readObject();

        in.defaultReadObject();

        namespace = Namespace.get( prefix, uri );
    }


    private static QNameCache getCache() {
      QNameCache cache = (QNameCache) cachePerThread.get();
      if (cache==null) {
        cache = new QNameCache();
        cachePerThread.set(cache);
      }
      return cache;
    }

    private static void validateName(String name) {
        if ( ! isName( name, true ) ) {
            throw new IllegalArgumentException( "Illegal character in name: '" + name + "'." );
        }
    }

    protected static void validateNCName(String ncname) {
        if ( ! isName( ncname, false ) ) {
            throw new IllegalArgumentException( "Illegal character in local name: '" + ncname + "'." );
        }
    }

    private static void validateQName(String qname) {
        int index = qname.indexOf( ':' );
        boolean valid = ( index < 0 )
            ? isName( qname, false )
            : isName( qname.substring( 0, index ), false ) && isName( qname.substring( index + 1 ), false );
        if ( ! valid ) {
            throw new IllegalArgumentException( "Illegal character in qualified name: '" + qname + "'." );
        }
    }

    /** XML 1.0 / 1.1 <code>Name</code> when <code>colon</code> is true,
      * otherwise <code>NCName</code>; characters outside the Basic
      * Multilingual Plane are rejected.
      */
    private static boolean isName(String name, boolean colon) {
        int length = name.length();
        if ( length == 0 ) {
            return false;
        }
        char c = name.charAt( 0 );
        if ( ! ( isNameStartChar( c ) || ( colon && c == ':' ) ) ) {
            return false;
        }
        for ( int i = 1; i < length; i++ ) {
            c = name.charAt( i );
            if ( ! ( isNameChar( c ) || ( colon && c == ':' ) ) ) {
                return false;
            }
        }
        return true;
    }

    /** <code>NameStartChar</code> without the colon */
    private static boolean isNameStartChar(char c) {
        return c == '_'
            || ( c >= 'A' && c <= 'Z' )
            || ( c >= 'a' && c <= 'z' )
            || ( c >= 'À' && c <= 'Ö' )
            || ( c >= 'Ø' && c <= 'ö' )
            || ( c >= 'ø' && c <= '˿' )
            || ( c >= 'Ͱ' && c <= 'ͽ' )
            || ( c >= 'Ϳ' && c <= '῿' )
            || ( c >= '‌' && c <= '‍' )
            || ( c >= '⁰' && c <= '↏' )
            || ( c >= 'Ⰰ' && c <= '⿯' )
            || ( c >= '、' && c <= '퟿' )
            || ( c >= '豈' && c <= '﷏' )
            || ( c >= 'ﷰ' && c <= '�' );
    }

    /** <code>NameChar</code> without the colon */
    private static boolean isNameChar(char c) {
        return isNameStartChar( c )
            || c == '-'
            || c == '.'
            || ( c >= '0' && c <= '9' )
            || c == '·'
            || ( c >= '̀' && c <= 'ͯ' )
            || ( c >= '‿' && c <= '⁀' );
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
