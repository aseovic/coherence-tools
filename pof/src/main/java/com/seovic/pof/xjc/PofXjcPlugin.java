/*
 * Copyright 2009 Aleksandar Seovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seovic.pof.xjc;


import com.seovic.pof.internal.PofConfig;
import com.seovic.pof.internal.SerializerType;
import com.seovic.pof.internal.UserType;
import com.seovic.pof.internal.UserTypeList;
import com.seovic.core.io.JAXBMarshaller;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

import com.tangosol.io.pof.EnumPofSerializer;
import com.tangosol.io.pof.PofHelper;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.RawDate;
import com.tangosol.io.pof.RawDateTime;
import com.tangosol.io.pof.RawTime;
import com.tangosol.io.pof.annotation.Portable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import static com.sun.codemodel.JExpr.*;


/**
 * @author Aleksandar Seovic  2011.12.15
 */
public class PofXjcPlugin
        extends Plugin {
    private static final JType[] NONE = new JType[0];

    private long typeId = 1000;

    public PofXjcPlugin() {
    }

    public String getOptionName() {
        return "Xpof";
    }

    public String getUsage() {
        return "  -Xpof              :  implement Portable Object Format (POF) serialization in generated classes\n"
             + "  -Xpof:typeId=<id>  :  start type identifier for the generated POF types";
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
        int ret = 0;
        if (args[i].startsWith("-Xpof")) {
            ret = 1;
            if (args[i].startsWith("-Xpof:typeId=")) {
                String sTypeId = args[i].substring("-Xpof:typeId=".length());
                try {
                    typeId = Long.parseLong(sTypeId);
                    if (typeId < 0) {
                        throw new BadCommandLineException("Initial POF type ID must be a positive integer");
                    }
                }
                catch (NumberFormatException e) {
                    throw new BadCommandLineException("Initial POF type ID must be a positive integer");
                }
            }
        }
        return ret;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean run(Outline outline, Options options, ErrorHandler handler)
            throws SAXException {

        PofConfig    cfg       = new PofConfig();
        UserTypeList userTypes = new UserTypeList();
        cfg.setUserTypeList(userTypes);

        for(ClassOutline co : outline.getClasses()) {
            JDefinedClass c = co.implClass;
            c._implements(PortableObject.class);
            c.annotate(Portable.class);

            new ReadExternalCreator(co, "readExternal", PofReader.class, "reader").createMethod();
            new WriteExternalCreator(co, "writeExternal", PofWriter.class, "writer").createMethod();

            userTypes.getUserTypeOrInclude().add(new UserType(BigInteger.valueOf(typeId++), c.fullName(), null));
        }

        SerializerType enumSerializer = new SerializerType().withClassName(EnumPofSerializer.class.getName());
        for (EnumOutline eo : outline.getEnums()) {
            JDefinedClass c = eo.clazz;
            userTypes.getUserTypeOrInclude().add(new UserType(BigInteger.valueOf(typeId++), c.fullName(), enumSerializer));
        }

        try {
            File targetDir = new File(options.targetDir, options.defaultPackage.replace('.', File.separatorChar));
            targetDir.mkdirs();
            File pofConfig = new File(targetDir, "pof-config.xml");
            JAXBMarshaller marshaller = new JAXBMarshaller(PofConfig.class, true);
            marshaller.marshal(cfg, new FileOutputStream(pofConfig));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    abstract class MethodCreator {
        protected ClassOutline classOutline;
        protected String name;
        protected Class argType;
        protected String argName;
        protected JMethod method;
        protected JVar arg;

        protected MethodCreator(ClassOutline classOutline, String name, Class argType, String argName) {
            this.classOutline = classOutline;
            this.name = name;
            this.argType = argType;
            this.argName = argName;

            JDefinedClass cls = classOutline.implClass;
            this.method = cls.method(JMod.PUBLIC, cls.owner().VOID, name)
                            ._throws(IOException.class);
            this.arg = this.method.param(argType, argName);
        }

        /**
        * Return all direct and inherited fields of the given class.
        * @return list of all fields
        */
        protected List<FieldOutline> getAllDeclaredAndInheritedFields() {
            List<FieldOutline> fields = new LinkedList<FieldOutline>();
            ClassOutline currentClassOutline = classOutline;
            while (currentClassOutline != null) {
                fields.addAll(Arrays.asList(currentClassOutline.getDeclaredFields()));
                currentClassOutline = currentClassOutline.getSuperClass();
            }
            return fields;
        }

        /**
        * Returns the getter method for a given field, taking care of <em>get</em> vs. <em>is</em>
        * prefixes.
        *
        * @param fieldOutline  field outline
        * @return getter for the specified field
        */
        protected JMethod getter(FieldOutline fieldOutline) {
            JDefinedClass theClass = fieldOutline.parent().implClass;
            String publicName = fieldOutline.getPropertyInfo().getName(true);
            JMethod getter = theClass.getMethod("get" + publicName, NONE);
            if (getter == null) {
                getter = theClass.getMethod("is" + publicName, NONE);
            }
            return getter;
        }

        /**
        * Returns the setter method for a given field.
        *
        * @param fieldOutline  field outline
        * @return setter for the specified field
        */
        protected JMethod setter(FieldOutline fieldOutline) {
            JDefinedClass theClass = fieldOutline.parent().implClass;
            String publicName = fieldOutline.getPropertyInfo().getName(true);
            return theClass.getMethod("set" + publicName, new JType[] {fieldOutline.getRawType()});
        }
    }

    class ReadExternalCreator extends MethodCreator {
        ReadExternalCreator(ClassOutline classOutline, String name, Class argType, String argName) {
            super(classOutline, name, argType, argName);
        }

        public void createMethod() {
            JBlock body = method.body();
            int    n    = 0;

            List<FieldOutline> fields = getAllDeclaredAndInheritedFields();
            for (FieldOutline fo : fields) {
                JMethod setter  = setter(fo);
                JType fieldType = fo.getRawType();
                String t = fieldType.name();
                JInvocation invocation;

                if (setter != null) {
                    String methodName = getReadMethod(fo);
                    invocation = arg.invoke(methodName).arg(lit(n++));

                    // XML date types require special handling
                    if (t.equals("XMLGregorianCalendar")) {
                        JClass pofHelper    = classOutline.implClass.owner().ref(PofHelper.class);
                        JClass TRawDate     = classOutline.implClass.owner().ref(RawDate.class);
                        JClass TRawDateTime = classOutline.implClass.owner().ref(RawDateTime.class);
                        JClass TRawTime     = classOutline.implClass.owner().ref(RawTime.class);

                        QName qName = fo.getPropertyInfo().getSchemaType();
                        String st = qName == null ? "dateTime" : qName.getLocalPart();
                        if (st.equals("date")) {
                            invocation = pofHelper.staticInvoke("fromRawDate")
                                                  .arg(cast(TRawDate, invocation));
                        }
                        if (st.equals("dateTime")) {
                            invocation = pofHelper.staticInvoke("fromRawDateTime")
                                                  .arg(cast(TRawDateTime, invocation));
                        }
                        if (st.equals("time")) {
                            invocation = pofHelper.staticInvoke("fromRawTime")
                                                  .arg(cast(TRawTime, invocation));
                        }
                    }

                    body.invoke(setter)
                            .arg("readObject".equals(methodName)
                                 ? cast(fo.getRawType(), invocation)
                                 : invocation);
                }
                else if (fo.getPropertyInfo().isCollection()) {
                    JInvocation readMethod = arg.invoke("readCollection")
                            .arg(lit(n++))
                            .arg(invoke(getter(fo)));
                    body.add(readMethod);
                }
            }
        }

        private String getReadMethod(FieldOutline field) {
            JType fieldType = field.getRawType();
            String t = fieldType.name();

            // primitive types
            if (t.equals("boolean")) return "readBoolean";
            if (t.equals("byte"))    return "readByte";
            if (t.equals("double"))  return "readDouble";
            if (t.equals("float"))   return "readFloat";
            if (t.equals("int"))     return "readInt";
            if (t.equals("long"))    return "readLong";
            if (t.equals("short"))   return "readShort";

            // natively supported reference types
            if (t.equals("BigDecimal"))  return "readBigDecimal";
            if (t.equals("BigInteger"))  return "readBigInteger";
            if (t.equals("String"))      return "readString";

            // XML date types
            if (t.equals("XMLGregorianCalendar")) {
                QName qName = field.getPropertyInfo().getSchemaType();
                String st = qName == null ? "readRawDateTime" : qName.getLocalPart();
                if (st.equals("date"))     return "readRawDate";
                if (st.equals("dateTime")) return "readRawDateTime";
                if (st.equals("time"))     return "readRawTime";
            }

            return "readObject";
        }
    }

    class WriteExternalCreator extends MethodCreator {
        WriteExternalCreator(ClassOutline classOutline, String name, Class argType, String argName) {
            super(classOutline, name, argType, argName);
        }

        public void createMethod() {
            JBlock body = method.body();
            int    n    = 0;

            List<FieldOutline> fields = getAllDeclaredAndInheritedFields();
            for (FieldOutline fo : fields) {
                JType fieldType = fo.getRawType();
                String t = fieldType.name();
                JInvocation invocation = null;

                // XML date types require special handling
                if (t.equals("XMLGregorianCalendar")) {
                    JClass pofHelper = classOutline.implClass.owner().ref(PofHelper.class);
                    QName qName = fo.getPropertyInfo().getSchemaType();
                    String st = qName == null ? "dateTime" : qName.getLocalPart();
                    if (st.equals("date")) {
                        invocation = arg.invoke("writeRawDate")
                                .arg(lit(n++))
                                .arg(pofHelper.staticInvoke("toRawDate")
                                        .arg(invoke(getter(fo))));
                    }
                    if (st.equals("dateTime")) {
                        invocation = arg.invoke("writeRawDateTime")
                                .arg(lit(n++))
                                .arg(pofHelper.staticInvoke("toRawDateTime")
                                        .arg(invoke(getter(fo))));
                    }
                    if (st.equals("time")) {
                        invocation = arg.invoke("writeRawTime")
                                .arg(lit(n++))
                                .arg(pofHelper.staticInvoke("toRawTime")
                                        .arg(invoke(getter(fo))));
                    }
                }
                else {
                    invocation = arg.invoke(getWriteMethod(fo))
                            .arg(lit(n++))
                            .arg(invoke(getter(fo)));
                }
                body.add(invocation);
            }
        }

        private String getWriteMethod(FieldOutline field) {
            JType fieldType = field.getRawType();
            String t = fieldType.name();

            // primitive types
            if (t.equals("boolean")) return "writeBoolean";
            if (t.equals("byte"))    return "writeByte";
            if (t.equals("double"))  return "writeDouble";
            if (t.equals("float"))   return "writeFloat";
            if (t.equals("int"))     return "writeInt";
            if (t.equals("long"))    return "writeLong";
            if (t.equals("short"))   return "writeShort";

            // natively supported reference types
            if (t.equals("BigDecimal"))  return "writeBigDecimal";
            if (t.equals("BigInteger"))  return "writeBigInteger";
            if (t.equals("String"))      return "writeString";

            // check for collections
            return field.getPropertyInfo().isCollection()
                   ? "writeCollection"
                   : "writeObject";
        }
    }
}
