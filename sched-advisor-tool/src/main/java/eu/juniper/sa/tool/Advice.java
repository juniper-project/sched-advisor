/*
 * Copyright (c) 2015, Brno University of Technology, Faculty of Information Technology
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of sched-advisor nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.juniper.sa.tool;

import eu.juniper.sa.deployment.model.CloudNode;
import eu.juniper.sa.deployment.model.JuniperApplication;
import eu.juniper.sa.deployment.model.ModelEntity;
import eu.juniper.sa.deployment.plan.XMLDeploymentPlan;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * The class for advice produced by the advisor.
 *
 * @author rychly
 */
public class Advice {

    final private String name;
    final private String problemDescriptionFormat;
    final private ModelEntity[] modelEntities;
    private String solutionDescription = null;
    private String noteDescription = null;

    public static final char problemDescriptionFormatEntityMark = '$';
    public final static String namespacePrefix = XMLDeploymentPlan.SAOBJID.getPrefix();
    public final static String namespaceURI = XMLDeploymentPlan.SAOBJID.getNamespaceURI();
    public final static String xMimeNamespacePrefix = "xmime";
    public final static String xMimeNamespaceURI = "http://www.w3.org/2005/05/xmlmime";
    public final static String xsiNamespacePrefix = "xsi";
    public final static String xsiNamespaceURI = "http://www.w3.org/2001/XMLSchema-instance";
    public final static String schemaLocation = "schemaLocation";
    public final static String schemaLocationVal = "http://www.fit.vutbr.cz/homes/rychly/juniper-sa/xsd/scheduling-advisor-v4.xsd";
    private static final String SCHEDULINGADVICE = "schedulingAdvice";
    private static final String ADVICE = "advice";
    private static final String CATEGORY = "category";
    private static final String CATEGORYVAL = "resource";
    private static final String SEVERITY = "severity";
    private static final String SEVERITYVAL = "warning";
    private static final String PROBLEM = "problem";
    private static final String SOLUTION = "solution";
    private static final String NOTE = "note";
    private static final String OBJECTREF = "objectRef";
    private static final String OBJID = "objId";
    private static final String SOURCES = "sources";
    private static final String ATTACHMENTREF = "attachmentRef";
    private static final String ATTID = "attId";
    private static final String ATTIDVAL = "deployment-plan";
    private static final String ATTACHMENT = "attachment";
    private static final String ANYXML = "anyXml";
    private static final String CONTENTTYPE = "contentType";
    private static final String CONTENTTYPEVAL = "application/xml";

    /**
     * Create an advice with a given name, a format string of problem
     * description, and a list of all model entities objects mentioned before.
     *
     * @param name a name of the advice
     * @param problemDescriptionFormat a format string of the advice problem
     * description where all occurences of model entities should be marked by
     * '$' character
     * @param modelEntities a list of all model entities objects that should be
     * mentioned in <code>problemDescriptionFormat</code>
     */
    public Advice(String name, String problemDescriptionFormat, ModelEntity... modelEntities) {
        this.name = name;
        this.problemDescriptionFormat = problemDescriptionFormat;
        this.modelEntities = modelEntities;
    }

    /**
     * Get a name of the advice.
     *
     * @return a name of the advice
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get a format string of problem description of the advice.
     *
     * @return a format string of problem description of the advice
     */
    public String getProblemDescriptionFormat() {
        return this.problemDescriptionFormat;
    }

    /**
     * Get a problem description of the advice as a plain-text.
     *
     * @return a problem description of the advice
     */
    public String getProblemDescriptionAsText() {
        return String.format(this.problemDescriptionFormat.replace(Character.toString(problemDescriptionFormatEntityMark), "%s"), (Object[]) this.modelEntities);
    }

    /**
     * Write a problem description of the advice into a XML stream.
     *
     * @param xmlStreamWriter a XML stream to write a problem description of the
     * advice into
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * writing XML data to the stream
     */
    public void writeProblemDescriptionIntoXMLStream(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        int idxPrevious = 0;
        int idxCurrent = this.problemDescriptionFormat.indexOf(problemDescriptionFormatEntityMark);
        int markNumber = 0;
        while (idxCurrent >= 0) {
            final ModelEntity modelEntity = this.modelEntities[markNumber];
            xmlStreamWriter.writeCharacters(this.problemDescriptionFormat.substring(idxPrevious, idxCurrent));
            if (modelEntity instanceof CloudNode) {
                xmlStreamWriter.writeCharacters(modelEntity.toString());
            } else {
                xmlStreamWriter.writeStartElement(namespacePrefix, OBJECTREF, namespaceURI);
                xmlStreamWriter.writeAttribute(ATTID, ATTIDVAL);
                xmlStreamWriter.writeAttribute(OBJID, modelEntity.getUuidAsCName());
                xmlStreamWriter.writeEndElement();
            }
            markNumber++;
            idxPrevious = idxCurrent + 1;
            idxCurrent = this.problemDescriptionFormat.indexOf(problemDescriptionFormatEntityMark, idxPrevious);
        }
        xmlStreamWriter.writeCharacters(this.problemDescriptionFormat.substring(idxPrevious));
    }

    /**
     * Get a solution description for a problem of the advice.
     *
     * @return a solution description for a problem of the advice
     */
    public String getSolutionDescription() {
        return solutionDescription;
    }

    /**
     * Set a solution description for a problem of the advice.
     *
     * @param solutionDescription a solution description for a problem of the
     * advice
     */
    public void setSolutionDescription(String solutionDescription) {
        this.solutionDescription = solutionDescription;
    }

    /**
     * Get a note description for a problem of the advice.
     *
     * @return a note description for a problem of the advice
     */
    public String getNoteDescription() {
        return noteDescription;
    }

    /**
     * Set a note description for a problem of the advice.
     *
     * @param noteDescription a note description for a problem of the advice
     */
    public void setNoteDescription(String noteDescription) {
        this.noteDescription = noteDescription;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + this.name
                + ": " + this.getProblemDescriptionAsText();
    }

    /**
     * Write an advice into a XML stream writer
     *
     * @param xmlStreamWriter a XML stream writer to write the advice into
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * writing XML data to the stream
     */
    public void writeToXmlStream(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        // advice - start
        xmlStreamWriter.writeStartElement(namespacePrefix, ADVICE, namespaceURI);
        xmlStreamWriter.writeNamespace(namespacePrefix, namespaceURI);
        xmlStreamWriter.writeAttribute(CATEGORY, CATEGORYVAL);
        xmlStreamWriter.writeAttribute(SEVERITY, SEVERITYVAL);
        // problem
        xmlStreamWriter.writeStartElement(namespacePrefix, PROBLEM, namespaceURI);
        this.writeProblemDescriptionIntoXMLStream(xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        // solution
        if (this.solutionDescription != null) {
            xmlStreamWriter.writeStartElement(namespacePrefix, SOLUTION, namespaceURI);
            xmlStreamWriter.writeCharacters(this.solutionDescription);
            xmlStreamWriter.writeEndElement();
        }
        // note
        if (this.noteDescription != null) {
            xmlStreamWriter.writeStartElement(namespacePrefix, NOTE, namespaceURI);
            xmlStreamWriter.writeCharacters(this.noteDescription);
            xmlStreamWriter.writeEndElement();
        }
        // sources/attachmentRef
        xmlStreamWriter.writeStartElement(namespacePrefix, SOURCES, namespaceURI);
        xmlStreamWriter.writeStartElement(namespacePrefix, ATTACHMENTREF, namespaceURI);
        xmlStreamWriter.writeAttribute(ATTID, ATTIDVAL);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();
        // advice - start
        xmlStreamWriter.writeEndElement();
    }

    /**
     * Write an array of the advice into an output XML file.
     *
     * @param adviceArray an array of the advice
     * @param outputFile an output XML file to write an array of the advice into
     * @param juniperApplication a Juniper application model object hierarchy to
     * export with the advice
     * @throws java.io.IOException if an XML file cannot be written
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * writing XML data to the stream
     */
    public static void writeAdviceArray(Advice[] adviceArray, String outputFile, JuniperApplication juniperApplication) throws IOException, XMLStreamException {
        try (final OutputStream outputStream = new FileOutputStream(outputFile)) {
            writeAdviceArray(adviceArray, outputStream, juniperApplication);
        }
    }

    /**
     * Write an array of the advice into an output stream.
     *
     * @param adviceArray an array of the advice
     * @param outputStream an output stream to write an array of the advice into
     * @param juniperApplication a Juniper application model object hierarchy to
     * export with the advice
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * writing XML data to the stream
     */
    public static void writeAdviceArray(Advice[] adviceArray, OutputStream outputStream, JuniperApplication juniperApplication) throws XMLStreamException {
        final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        final XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(outputStream);
        xmlStreamWriter.writeStartDocument();
        writeAdviceArray(adviceArray, xmlStreamWriter, juniperApplication);
        xmlStreamWriter.writeEndDocument();
        xmlStreamWriter.close();
    }

    /**
     * Write an array of the advice into an XML stream writer.
     *
     * @param adviceArray an array of the advice
     * @param xmlStreamWriter an XML stream writer to write an array of the advice
     * into an XML stream
     * @param juniperApplication a Juniper application model object hierarchy to
     * export with the advice
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * writing XML data to the stream
     */
    public static void writeAdviceArray(Advice[] adviceArray, XMLStreamWriter xmlStreamWriter, JuniperApplication juniperApplication) throws XMLStreamException {
        // schedulingAdvice - start
        xmlStreamWriter.writeStartElement(namespacePrefix, SCHEDULINGADVICE, namespaceURI);
        xmlStreamWriter.writeNamespace(namespacePrefix, namespaceURI);
        xmlStreamWriter.writeNamespace(xMimeNamespacePrefix, xMimeNamespaceURI);
        xmlStreamWriter.writeNamespace(xsiNamespacePrefix, xsiNamespaceURI);
        xmlStreamWriter.writeAttribute(xsiNamespacePrefix, xsiNamespaceURI, schemaLocation, schemaLocationVal);
        // advice
        for (Advice advice : adviceArray) {
            advice.writeToXmlStream(xmlStreamWriter);
        }
        // attachment/anyXml/application
        xmlStreamWriter.writeStartElement(namespacePrefix, ATTACHMENT, namespaceURI);
        xmlStreamWriter.writeAttribute(ATTID, ATTIDVAL);
        xmlStreamWriter.writeStartElement(namespacePrefix, ANYXML, namespaceURI);
        xmlStreamWriter.writeAttribute(xMimeNamespacePrefix, xMimeNamespaceURI, CONTENTTYPE, CONTENTTYPEVAL);
        XMLDeploymentPlan.writeJuniperApplication(juniperApplication, xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();
        // schedulingAdvice - start
        xmlStreamWriter.writeEndElement();
    }
}
