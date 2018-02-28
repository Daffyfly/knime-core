/*
 * ------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   14.02.2008 (cebron): created
 */
package org.vincent.DecisionTree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.knime.base.data.util.DataCellStringMapper;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.util.NonClosableInputStream;
import org.knime.core.data.util.NonClosableOutputStream;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContent;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.config.Config;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortTypeRegistry;

import dt.Attribute;
import dt.DecisionTree;
import dt.Examples;

import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;
import org.knime.core.node.port.PortType;


/**
 * A special ModelPort holding a weka classifier and additional meta information
 * (e.g. class column).
 *
 * @author cebron, University of Konstanz
 */
public class DecisionTreePortModel implements PortObject {

    /**
     * The Port Type.
     */
    public static final PortType TYPE =
        PortTypeRegistry.getInstance().getPortType(DecisionTreePortModel.class);

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(DecisionTreePortModel.class);

    /**
     * Serializer for {@link DecisionTreePortModel}s.
     */
    public static final class Serializer extends PortObjectSerializer<DecisionTreePortModel> {
        /** {@inheritDoc} */
        @Override
        public void savePortObject(
                final DecisionTreePortModel portObject,
                final PortObjectZipOutputStream out,
                final ExecutionMonitor exec) throws IOException,
                CanceledExecutionException {
            portObject.save(out);
        }

        /** {@inheritDoc} */
        @Override
        public DecisionTreePortModel loadPortObject(
                final PortObjectZipInputStream in,
                final PortObjectSpec spec, final ExecutionMonitor exec)
                throws IOException, CanceledExecutionException {
            return load(in, (DecisionTreePortModelSpec)spec);
        }
    }

    private void save(final PortObjectZipOutputStream out) {
        // save weka classifier
        ObjectOutputStream oo = null;
        try {
            out.putNextEntry(new ZipEntry("classifier.attributes"));
            oo = new ObjectOutputStream(new NonClosableOutputStream.Zip(out));
            oo.writeObject(m_tree.getAttributes());
        } catch (IOException ioe) {
            LOGGER.error("Internal error: Could not save settings", ioe);
        } finally {
            if (oo != null) {
                try {
                    oo.close();
                } catch (Exception e) {
                    LOGGER.debug("Could not close stream", e);
                }
            }
        }
        try {
            out.putNextEntry(new ZipEntry("classifier.examples"));
            oo = new ObjectOutputStream(new NonClosableOutputStream.Zip(out));
            oo.writeObject(m_tree.getExamples());
        } catch (IOException ioe) {
            LOGGER.error("Internal error: Could not save settings", ioe);
        } finally {
            if (oo != null) {
                try {
                    oo.close();
                } catch (Exception e) {
                    LOGGER.debug("Could not close stream", e);
                }
            }
        }
        
        /*try {
            out.putNextEntry(new ZipEntry("classifier"));
            oo = new ObjectOutputStream(new NonClosableOutputStream.Zip(out));
            oo.writeObject(m_tree);
        } catch (IOException ioe) {
            LOGGER.error("Internal error: Could not save settings", ioe);
        } finally {
            if (oo != null) {
                try {
                    oo.close();
                } catch (Exception e) {
                    LOGGER.debug("Could not close stream", e);
                }
            }
        }*/
    }

    private static DecisionTreePortModel load(
            final PortObjectZipInputStream in,
            final DecisionTreePortModelSpec spec) {
        ObjectInputStream oi = null;
        DecisionTree tree = new DecisionTree();

        LinkedHashSet<String> attributes = null;
        Map<String,Set<String>> decisions = null;
        Examples examples = null;
        
       try {
            // load attributes
            ZipEntry zentry = in.getNextEntry();
            assert zentry.getName().equals("classifier.attributes");
            oi = new ObjectInputStream(new NonClosableInputStream.Zip(in));
            attributes = (LinkedHashSet<String>)oi.readObject();
        } catch (IOException ioe) {
            LOGGER.error("Internal error: Could not load settings", ioe);
        } catch (ClassNotFoundException cnf) {
            LOGGER.error("Internal error: Could not load settings", cnf);
        } finally {
            if (oi != null) {
                try {
                    oi.close();
                } catch (Exception e) {
                    LOGGER.debug("Could not close stream", e);
                }
            }
        }
        
        try {
            // load attributes
            ZipEntry zentry = in.getNextEntry();
            assert zentry.getName().equals("classifier.examples");
            oi = new ObjectInputStream(new NonClosableInputStream.Zip(in));
            examples = (Examples)oi.readObject();
        } catch (IOException ioe) {
            LOGGER.error("Internal error: Could not load settings", ioe);
        } catch (ClassNotFoundException cnf) {
            LOGGER.error("Internal error: Could not load settings", cnf);
        } finally {
            if (oi != null) {
                try {
                    oi.close();
                } catch (Exception e) {
                    LOGGER.debug("Could not close stream", e);
                }
            }
        }
        
        System.out.println("attributes");
        for(String s : attributes)
        	System.out.println(s);
        
        tree.setAttributes(attributes.toArray(new String[0]));
        

        tree.setExamples(examples);
        tree.compile();
        
        assert (attributes != null);
        assert (examples != null);
        /*try {
            // load attributes
            ZipEntry zentry = in.getNextEntry();
            assert zentry.getName().equals("classifier");
            oi = new ObjectInputStream(new NonClosableInputStream.Zip(in));
            tree = (DecisionTree)oi.readObject();
        } catch (IOException ioe) {
            LOGGER.error("Internal error: Could not load settings", ioe);
        } catch (ClassNotFoundException cnf) {
            LOGGER.error("Internal error: Could not load settings", cnf);
        } finally {
            if (oi != null) {
                try {
                    oi.close();
                } catch (Exception e) {
                    LOGGER.debug("Could not close stream", e);
                }
            }
        }*/
        
        return new DecisionTreePortModel(tree, spec);
    }

    private DecisionTree m_tree;

    private DecisionTreePortModelSpec m_modelspec;

    /**
     * The WekaClassifierPortObject holds information about the used classifier,
     * training instances, columns and class column.
     *
     * @param classifier Classifier from weka.
     * @param traininginstances training instances used.
     * @param mapper mapping DataCells to Strings.
     * @param spec the {@link WekaClassifierModelPortObjectSpec}.
     */
    public DecisionTreePortModel(final DecisionTree tree,
            final DecisionTreePortModelSpec spec) {
        m_tree = tree;
        m_modelspec = spec;
    }

    /**
     * @return the classifier
     */
    public DecisionTree getClassifier() {
        return m_tree;
    }

    /**
     * @return the training {@link DataTableSpec}.
     */
    @Override
    public DecisionTreePortModelSpec getSpec() {
        return m_modelspec;
    }

    /** {@inheritDoc} */
    @Override
    public String getSummary() {
        return m_tree.getClass().getSimpleName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent[] getViews() {
        JPanel classifierInfoPanel = new JPanel();
        JTextArea field = new JTextArea();
        String text = m_tree.getClass().getSimpleName() + "\n";
        text += m_tree.toString();
        field.setText(text);
        classifierInfoPanel.add(field);
        JComponent comp = new JScrollPane(classifierInfoPanel);
        comp.setName("Weka Outport");
        return new JComponent[]{comp};
    }
}
