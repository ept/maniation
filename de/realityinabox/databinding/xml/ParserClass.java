package de.realityinabox.databinding.xml;

import de.realityinabox.databinding.sourcemodel.AccessLevel;
import de.realityinabox.databinding.sourcemodel.Class;
import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.Constructor;
import de.realityinabox.databinding.sourcemodel.Field;
import de.realityinabox.databinding.sourcemodel.Method;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.databinding.sourcemodel.Variable;
import de.realityinabox.databinding.types.TypeFactory;

public class ParserClass extends Class {

    private XMLPackage pack;
    private ClassType rootType;

    public ParserClass(XMLPackage pack, ClassType rootType) {
        super(pack, null, "XMLParser");
        this.pack = pack;
        this.rootType = rootType;
        setAccessLevel(AccessLevel.PUBLIC);
        Field f = SourceFactory.newField("factory", TypeFactory.newReferenceType(pack,
                "javax.xml.parsers.SAXParserFactory", "SAXParserFactory"));
        f.setAccessLevel(AccessLevel.PRIVATE);
        addField(f);
        addConstructor();
        addParseFileMethod();
        addParseInputSourceMethod();
        addParseInputStreamMethod1();
        addParseInputStreamMethod2();
        addParseURIMethod();
    }
    
    private void addConstructor() {
        Constructor c = SourceFactory.newConstructor(this);
        c.setAccessLevel(AccessLevel.PUBLIC);
        c.addArgument(SourceFactory.newVariable("parserFactory", TypeFactory.newReferenceType(pack,
                "javax.xml.parsers.SAXParserFactory", "SAXParserFactory")));
        c.addSourceLine("this.factory = parserFactory;");
        c.addSourceLine("factory.setNamespaceAware(true);");
    }
    
    private Method getParseMethod(Variable argument, String parseArguments) {
        Method m = SourceFactory.newMethod("parse", TypeFactory.newReferenceType(rootType));
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addArgument(argument);
        m.addException(TypeFactory.newReferenceType(pack,
                "org.xml.sax.SAXException", "SAXException"));
        m.addException(TypeFactory.newReferenceType(pack,
                "javax.xml.parsers.ParserConfigurationException", "ParserConfigurationException"));
        m.addException(TypeFactory.newReferenceType(pack,
                "java.io.IOException", "IOException"));
        m.addSourceLine("XMLDocument document = new XMLDocument();");
        m.addSourceLine("factory.newSAXParser().parse(" + parseArguments + ");");
        m.addSourceLine("de.realityinabox.databinding.libs.XMLElement root = document.getRoot();");
        m.addSourceLine("if (root instanceof " + rootType.getFullName() + ") return (" +
                rootType.getFullName() + ") root;");
        m.addSourceLine("return null;");
        return m;
    }
    
    private void addParseFileMethod() {
        addMethod(getParseMethod(SourceFactory.newVariable("input",
                TypeFactory.newReferenceType(pack, "java.io.File", "File")),
                "input, document.getParseStack().getHandler()"));
    }

    private void addParseInputSourceMethod() {
        addMethod(getParseMethod(SourceFactory.newVariable("input",
                TypeFactory.newReferenceType(pack,
                "org.xml.sax.InputSource", "InputSource")),
                "input, document.getParseStack().getHandler()"));
    }

    private void addParseInputStreamMethod1() {
        addMethod(getParseMethod(SourceFactory.newVariable("input",
                TypeFactory.newReferenceType(pack,
                "java.io.InputStream", "InputStream")),
                "input, document.getParseStack().getHandler()"));
    }

    private void addParseInputStreamMethod2() {
        Method m = getParseMethod(SourceFactory.newVariable("input",
                TypeFactory.newReferenceType(pack,
                "java.io.InputStream", "InputStream")),
                "input, document.getParseStack().getHandler(), systemId");
        m.addArgument(SourceFactory.newVariable("systemId",
                TypeFactory.newReferenceType(pack, "java.lang.String", "String")));
        addMethod(m);
    }

    private void addParseURIMethod() {
        addMethod(getParseMethod(SourceFactory.newVariable("uri",
                TypeFactory.newReferenceType(pack, "java.lang.String", "String")),
                "uri, document.getParseStack().getHandler()"));
    }
}
