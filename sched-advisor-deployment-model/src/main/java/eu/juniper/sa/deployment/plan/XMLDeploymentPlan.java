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
package eu.juniper.sa.deployment.plan;

import eu.juniper.sa.deployment.model.CloudNode;
import eu.juniper.sa.deployment.model.DataConnection;
import eu.juniper.sa.deployment.model.JuniperApplication;
import eu.juniper.sa.deployment.model.JuniperProgram;
import eu.juniper.sa.deployment.model.ModelEntity;
import eu.juniper.sa.deployment.model.MpiGroup;
import eu.juniper.sa.deployment.model.MpiGroupMember;
import eu.juniper.sa.deployment.model.ProgramInstance;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * The class for importing from and exporting to a deployment plan in XML file
 * to/from a Juniper application model object hierarchy.
 *
 * @author rychly
 */
public final class XMLDeploymentPlan {

    private static final String APPLICATION = "application";
    private static final String PROGRAMMODEL = "ProgramModel";
    private static final String GROUPMODEL = "GroupModel";
    private static final String COMMUNICATIONMODEL = "CommunicationModel";
    private static final String DEPLOYMENTMODEL = "DeploymentModel";
    private static final String PROGRAM = "program";
    private static final String MPIGROUP = "mpigroup";
    private static final String MEMBER = "member";
    private static final String DATACONNECTION = "dataconnection";
    private static final String CLOUDNODE = "cloudnode";
    private static final QName NAME = new QName("name");
    private static final QName JAVACLASS = new QName("javaclass");
    private static final QName MPIGLOBALRANK = new QName("mpiglobalrank");
    private static final QName MPILOCALRANK = new QName("mpilocalrank");
    private static final QName PROGRAMNAME = new QName("programName");
    private static final QName SENDINGGROUP = new QName("sendingGroup");
    private static final QName RECEIVERMPIGROUP = new QName("receiverMpiGroup");
    private static final QName TYPE = new QName("type");
    private static final QName HOSTIPADDR = new QName("hostipaddr");
    public static final QName SAOBJID = new QName("http://www.fit.vutbr.cz/homes/rychly/juniper/scheduling-advisor", "objId", "sa");

    /**
     * Create a Juniper application model object hierarchy from a XML deployment
     * plan in a given input stream.
     *
     * @param inputStream an input stream to import a XML deployment plan from
     * @return a Juniper application model object hierarchy
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * reading XML data from the stream
     * @throws eu.juniper.sa.deployment.plan.XMLDeploymentPlanException if there
     * is error when processing XML data to create Juniper application objects
     */
    public static JuniperApplication readJuniperApplication(InputStream inputStream) throws XMLStreamException, XMLDeploymentPlanException {
        // state variables
        JuniperApplication juniperApplication = null;
        MpiGroup mpiGroup = null;
        Map<Integer, CloudNode> programInstancesToCloudNodes = new HashMap<>();
        // XML parsing automata
        final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        final XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        while (eventReader.hasNext()) {
            final XMLEvent event = eventReader.nextEvent();
            if (event.isStartElement()) {
                final StartElement startElement = event.asStartElement();
                switch (startElement.getName().getLocalPart()) {
                    case APPLICATION: {
                        final String attrName = startElement.getAttributeByName(NAME).getValue();
                        if (attrName == null) {
                            throw new XMLDeploymentPlanException(APPLICATION + " element " + startElement + " has to have attribute " + NAME);
                        }
                        juniperApplication = new JuniperApplication(attrName);
                    }
                    break;
                    case PROGRAM: {
                        if (juniperApplication == null) {
                            throw new XMLDeploymentPlanException(PROGRAM + " element " + startElement + " has to be after " + APPLICATION + " element");
                        }
                        final String attrName = startElement.getAttributeByName(NAME).getValue();
                        final String attrJavaClass = startElement.getAttributeByName(JAVACLASS).getValue();
                        if ((attrName == null) || (attrJavaClass == null)) {
                            throw new XMLDeploymentPlanException(PROGRAM + " element " + startElement + " has to have attributes " + NAME + " and " + JAVACLASS);
                        }
                        JuniperProgram juniperProgram = juniperApplication.getProgramModel().addProgram(new JuniperProgram(attrName));
                        juniperProgram.setJavaClassCanonicalName(attrJavaClass);
                    }
                    break;
                    case MPIGROUP: {
                        if (juniperApplication == null) {
                            throw new XMLDeploymentPlanException(MPIGROUP + " element " + startElement + " has to be after " + APPLICATION + " element");
                        }
                        final String attrName = startElement.getAttributeByName(NAME).getValue();
                        if (attrName == null) {
                            throw new XMLDeploymentPlanException(MPIGROUP + " element " + startElement + " has to have attribute " + NAME);
                        }
                        mpiGroup = juniperApplication.getGroupModel().addMpiGroup(new MpiGroup(attrName));
                    }
                    break;
                    case MEMBER: {
                        if ((juniperApplication == null) || (mpiGroup == null)) {
                            throw new XMLDeploymentPlanException(MEMBER + " element " + startElement + " has to be after " + APPLICATION + " and " + MPIGROUP + " elements");
                        }
                        final String attrMpiGlobalRank = startElement.getAttributeByName(MPIGLOBALRANK).getValue();
                        final String attrMpiLocalRank = startElement.getAttributeByName(MPILOCALRANK).getValue();
                        final String attrProgramName = startElement.getAttributeByName(PROGRAMNAME).getValue();
                        if ((attrMpiGlobalRank == null) || (attrMpiLocalRank == null) || (attrProgramName == null)) {
                            throw new XMLDeploymentPlanException(MEMBER + " element " + startElement + " has to have attributes " + MPIGLOBALRANK + ", " + MPILOCALRANK + ", and " + PROGRAMNAME);
                        }
                        final MpiGroupMember mpiGroupMember = mpiGroup.addMember(new MpiGroupMember(Integer.parseInt(attrMpiLocalRank), mpiGroup));
                        final JuniperProgram juniperProgramForInstances = juniperApplication.getProgramModel().getProgramByName(attrProgramName);
                        if (juniperProgramForInstances == null) {
                            throw new XMLDeploymentPlanException(MEMBER + " element " + startElement + " has to be after a related " + PROGRAM + " element");
                        }
                        ProgramInstance programInstance = juniperApplication.getProgramModel().getProgramInstanceById(Integer.parseInt(attrMpiGlobalRank));
                        if (programInstance == null) {
                            programInstance = new ProgramInstance(Integer.parseInt(attrMpiGlobalRank));
                        }
                        juniperProgramForInstances.addInstance(programInstance);
                        programInstance.addMembership(mpiGroupMember);
                    }
                    break;
                    case DATACONNECTION: {
                        if (juniperApplication == null) {
                            throw new XMLDeploymentPlanException(DATACONNECTION + " element " + startElement + " has to be after " + APPLICATION + " element");
                        }
                        final String attrName = startElement.getAttributeByName(NAME).getValue();
                        final String attrSendingGroup = startElement.getAttributeByName(SENDINGGROUP).getValue();
                        final String attrReceiverGroup = startElement.getAttributeByName(RECEIVERMPIGROUP).getValue();
                        final String attrType = startElement.getAttributeByName(TYPE).getValue();
                        if ((attrName == null) || (attrSendingGroup == null) || (attrReceiverGroup == null) || (attrType == null)) {
                            throw new XMLDeploymentPlanException(DATACONNECTION + " element " + startElement + " has to have attributes " + NAME + ", " + SENDINGGROUP + ", " + RECEIVERMPIGROUP + ", and " + TYPE);
                        }
                        final DataConnection dataConnection = juniperApplication.getCommunicationModel().addConnection(new DataConnection(attrName));
                        final MpiGroup mpiGroupSending = juniperApplication.getGroupModel().getMpiGroupByName(attrSendingGroup);
                        final MpiGroup mpiGroupReceiving = juniperApplication.getGroupModel().getMpiGroupByName(attrReceiverGroup);
                        if ((mpiGroupSending == null) || (mpiGroupReceiving == null)) {
                            throw new XMLDeploymentPlanException(DATACONNECTION + " element " + startElement + " has to be after related " + MPIGROUP + " elements");
                        }
                        dataConnection.setSendingGroup(mpiGroupSending);
                        dataConnection.setReceivingGroup(mpiGroupReceiving);
                        if (!dataConnection.setType(attrType)) {
                            throw new XMLDeploymentPlanException(DATACONNECTION + " element " + startElement + " has an unknown type");
                        }
                    }
                    break;
                    case CLOUDNODE: {
                        if (juniperApplication == null) {
                            throw new XMLDeploymentPlanException(CLOUDNODE + " element " + startElement + " has to be after " + APPLICATION + " element");
                        }
                        final String attrHostIpAddr = startElement.getAttributeByName(HOSTIPADDR).getValue();
                        final String attrMpiGlobalRank = startElement.getAttributeByName(MPIGLOBALRANK).getValue();
                        if ((attrHostIpAddr == null) || (attrMpiGlobalRank == null)) {
                            throw new XMLDeploymentPlanException(CLOUDNODE + " element " + startElement + " has to have attributes " + HOSTIPADDR + " and " + MPIGLOBALRANK);
                        }
                        final CloudNode cloudNode = juniperApplication.getDeploymentModel().addCloudNode(new CloudNode(attrHostIpAddr));
                        final int attrMpiGlobalRankInt = Integer.parseInt(attrMpiGlobalRank);
                        final ProgramInstance programInstance = juniperApplication.getProgramModel().getProgramInstanceById(attrMpiGlobalRankInt);
                        if (programInstance == null) {
                            // assignment of the Juniper program instance to the cloud node will be postponed
                            //throw new XMLDeploymentPlanException(CLOUDNODE + " element " + startElement + " has to be after a related " + MEMBER + " element");
                            programInstancesToCloudNodes.put(attrMpiGlobalRankInt, cloudNode);
                        } else {
                            cloudNode.addProgramInstance(programInstance);
                        }
                    }
                    break;
                }
            }
            // do not put ifs together by else as an event can be startElement and also endElement in the case of <element ... />
            if (event.isEndElement()) {
                switch (event.asEndElement().getName().getLocalPart()) {
                    case MPIGROUP:
                        mpiGroup = null;
                        break;
                }
            }
        }
        if (juniperApplication != null) {
            // postponed assignment of Juniper program instances to the cloud nodes
            for (Entry<Integer, CloudNode> entry : programInstancesToCloudNodes.entrySet()) {
                final ProgramInstance programInstance = juniperApplication.getProgramModel().getProgramInstanceById(entry.getKey());
                final CloudNode cloudNode = entry.getValue();
                cloudNode.addProgramInstance(programInstance);
            }
        }
        return juniperApplication;
    }

    /**
     * Export a Juniper application model object hierarchy as a XML deployment
     * plan into a given output stream as a standalone XML document.
     *
     * @param juniperApplication a Juniper application model object hierarchy to
     * export
     * @param outputStream an output stream to export a XML deployment plan
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * writing XML data to the stream
     */
    public static void writeJuniperApplication(JuniperApplication juniperApplication, OutputStream outputStream) throws XMLStreamException {
        final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        final XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(outputStream);
        streamWriter.writeStartDocument();
        writeJuniperApplication(juniperApplication, streamWriter);
        streamWriter.writeEndDocument();
        streamWriter.close();
    }

    /**
     * Export a Juniper application model object hierarchy as a XML deployment
     * plan into a given output stream with or without the XML document header.
     *
     * @param juniperApplication a Juniper application model object hierarchy to
     * export
     * @param streamWriter an output XML stream writer to write a XML deployment
     * plan
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * writing XML data to the stream
     */
    public static void writeJuniperApplication(JuniperApplication juniperApplication, XMLStreamWriter streamWriter) throws XMLStreamException {
        // JuniperApplication - start
        streamWriter.writeStartElement(APPLICATION);
        streamWriter.writeAttribute(NAME.getLocalPart(), juniperApplication.getApplicationName());
        streamWriter.writeNamespace(SAOBJID.getPrefix(), SAOBJID.getNamespaceURI());
        xmlStreamWriteAttributeSaObjId(juniperApplication, streamWriter);
        // ProgramModel
        streamWriter.writeStartElement(PROGRAMMODEL);
        xmlStreamWriteAttributeSaObjId(juniperApplication.getProgramModel(), streamWriter);
        for (JuniperProgram juniperProgram : juniperApplication.getProgramModel().getPrograms()) {
            streamWriter.writeStartElement(PROGRAM);
            streamWriter.writeAttribute(NAME.getLocalPart(), juniperProgram.getProgramName());
            streamWriter.writeAttribute(JAVACLASS.getLocalPart(), juniperProgram.getJavaClassCanonicalName());
            xmlStreamWriteAttributeSaObjId(juniperProgram, streamWriter);
            streamWriter.writeEndElement();
        }
        streamWriter.writeEndElement();
        // GroupModel
        streamWriter.writeStartElement(GROUPMODEL);
        xmlStreamWriteAttributeSaObjId(juniperApplication.getGroupModel(), streamWriter);
        for (MpiGroup mpiGroup : juniperApplication.getGroupModel().getMpiGroups()) {
            streamWriter.writeStartElement(MPIGROUP);
            streamWriter.writeAttribute(NAME.getLocalPart(), mpiGroup.getGroupName());
            xmlStreamWriteAttributeSaObjId(mpiGroup, streamWriter);
            for (MpiGroupMember mpiGroupMember : mpiGroup.getMembers()) {
                streamWriter.writeStartElement(MEMBER);
                streamWriter.writeAttribute(MPIGLOBALRANK.getLocalPart(), String.valueOf(mpiGroupMember.getProgramInstance().getMpiGlobalRank()));
                streamWriter.writeAttribute(MPILOCALRANK.getLocalPart(), String.valueOf(mpiGroupMember.getMpiLocalRank()));
                streamWriter.writeAttribute(PROGRAMNAME.getLocalPart(), mpiGroupMember.getProgramInstance().getProgram().getProgramName());
                xmlStreamWriteAttributeSaObjId(mpiGroupMember, streamWriter);
                streamWriter.writeEndElement();
            }
            streamWriter.writeEndElement();
        }
        streamWriter.writeEndElement();
        // CommunicationModel
        streamWriter.writeStartElement(COMMUNICATIONMODEL);
        xmlStreamWriteAttributeSaObjId(juniperApplication.getCommunicationModel(), streamWriter);
        for (DataConnection dataConnection : juniperApplication.getCommunicationModel().getConnections()) {
            streamWriter.writeStartElement(DATACONNECTION);
            streamWriter.writeAttribute(NAME.getLocalPart(), dataConnection.getConnectionName());
            streamWriter.writeAttribute(SENDINGGROUP.getLocalPart(), dataConnection.getSendingGroup().getGroupName());
            streamWriter.writeAttribute(RECEIVERMPIGROUP.getLocalPart(), dataConnection.getReceivingGroup().getGroupName());
            streamWriter.writeAttribute(TYPE.getLocalPart(), dataConnection.getTypeAsString());
            xmlStreamWriteAttributeSaObjId(dataConnection, streamWriter);
            streamWriter.writeEndElement();
        }
        streamWriter.writeEndElement();
        // DeploymentModel
        streamWriter.writeStartElement(DEPLOYMENTMODEL);
        xmlStreamWriteAttributeSaObjId(juniperApplication.getDeploymentModel(), streamWriter);
        for (CloudNode cloudNode : juniperApplication.getDeploymentModel().getCloudNodes()) {
            for (ProgramInstance programInstance : cloudNode.getProgramInstances()) {
                streamWriter.writeStartElement(CLOUDNODE);
                streamWriter.writeAttribute(HOSTIPADDR.getLocalPart(), cloudNode.getHostIPAddr());
                streamWriter.writeAttribute(MPIGLOBALRANK.getLocalPart(), String.valueOf(programInstance.getMpiGlobalRank()));
                xmlStreamWriteAttributeSaObjId(programInstance, streamWriter);
                streamWriter.writeEndElement();
            }
        }
        streamWriter.writeEndElement();
        // JuniperApplication - end
        streamWriter.writeEndElement();
    }

    private static void xmlStreamWriteAttributeSaObjId(ModelEntity modelEntity, XMLStreamWriter streamWriter) throws XMLStreamException {
        if (modelEntity.hasUuid()) {
            streamWriter.writeAttribute(SAOBJID.getPrefix(), SAOBJID.getNamespaceURI(), SAOBJID.getLocalPart(), modelEntity.getUuidAsCName());
        }
    }

    /**
     * Create a Juniper application model object hierarchy from a XML deployment
     * plan in a given file.
     *
     * @param inputFile an input XML file to import a deployment plan from
     * @return a Juniper application model object hierarchy
     * @throws java.io.FileNotFoundException if an XML file cannot be found
     * @throws java.io.IOException if an XML file cannot be read
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * reading XML data from the stream
     * @throws eu.juniper.sa.deployment.plan.XMLDeploymentPlanException if there
     * is error when processing XML data to create Juniper application objects
     */
    public static JuniperApplication readJuniperApplication(String inputFile) throws FileNotFoundException, IOException, XMLStreamException, XMLDeploymentPlanException {
        try (final InputStream inputStream = new FileInputStream(inputFile)) {
            return readJuniperApplication(inputStream);
        }
    }

    /**
     * Export a Juniper application model object hierarchy as a XML deployment
     * plan into a given file.
     *
     * @param juniperApplication a Juniper application model object hierarchy to
     * export
     * @param outputFile an output file to export an XML deployment plan
     * @throws java.io.FileNotFoundException if an XML file cannot be created
     * @throws java.io.IOException if an XML file cannot be written
     * @throws javax.xml.stream.XMLStreamException if there is error when
     * writing XML data to the file
     */
    public static void writeJuniperApplication(JuniperApplication juniperApplication, String outputFile) throws FileNotFoundException, IOException, XMLStreamException {
        try (final OutputStream outputStream = new FileOutputStream(outputFile)) {
            writeJuniperApplication(juniperApplication, outputStream);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: " + XMLDeploymentPlan.class.getCanonicalName() + " <inputFile.xml> <outputFileXML>");
            System.err.println("Create JuniperApplication object from data in the input file and write the result as the output file.");
            System.exit(-1);
        }
        final String inputFile = args[0];
        final String outputFile = args[1];
        try {
            final JuniperApplication juniperApplication = XMLDeploymentPlan.readJuniperApplication(inputFile);
            XMLDeploymentPlan.writeJuniperApplication(juniperApplication, outputFile);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
