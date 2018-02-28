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
import java.util.zip.ZipEntry;

import javax.swing.JComponent;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContent;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.config.Config;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectSpecZipInputStream;
import org.knime.core.node.port.PortObjectSpecZipOutputStream;

/**
 *
 * @author cebron, University of Konstanz
 */
public class DecisionTreePortModelSpec implements PortObjectSpec {

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(DecisionTreePortModelSpec.class);

    /*
     * Model info identifier.
     */
    private static final String MODEL_INFO = "model_info";

    /*
     * Key to store the class column.
     */
    private static final String CLASSCOL_KEY = "ClassColumn";

    /**
     * Key to store the DataTableSpec.
     */
    private static final String SPEC_KEY = "tablespec";

    /**
     * Serializer for {@link DecisionTreePortModelSpec}s.
     */
    public static final class Serializer extends PortObjectSpecSerializer<DecisionTreePortModelSpec> {
        /** {@inheritDoc} */
        @Override
        public void savePortObjectSpec(
                final DecisionTreePortModelSpec portObject,
                final PortObjectSpecZipOutputStream out) throws IOException {
            portObject.save(out);

        }

        /** {@inheritDoc} */
        @Override
        public DecisionTreePortModelSpec loadPortObjectSpec(
                final PortObjectSpecZipInputStream in) throws IOException {
            return load(in);
        }
    }

    private void save(final PortObjectSpecZipOutputStream out) {
        ModelContent model = new ModelContent(MODEL_INFO);
        model.addString(CLASSCOL_KEY, m_classCol);
        Config specconf = model.addConfig(SPEC_KEY);
        m_spec.save(specconf);
        try {
            out.putNextEntry(new ZipEntry("mapper.xmlout"));
            model.saveToXML(out);
        } catch (IOException ioe) {
            LOGGER.error("Internal error: Could not save settings", ioe);
        }
    }

    private static DecisionTreePortModelSpec load(
            final PortObjectSpecZipInputStream in) {
        ModelContentRO model = null;
        try {
            ZipEntry zentry = in.getNextEntry();
            assert zentry.getName().equals("mapper.xmlout");
            model = ModelContent.loadFromXML(in);
        } catch (IOException ioe) {
            LOGGER.error("Internal error: Could not load settings", ioe);
        }
        String classcol = null;
        DataTableSpec spec = null;
       
        return new DecisionTreePortModelSpec(classcol, spec);
    }

    private String m_classCol;

    private DataTableSpec m_spec;

    /**
     * The {@link DecisionTreePortModelSpec} holds the columns of the
     * training data and the class column.
     *
     * @param classCol class column used.
     * @param spec {@link DataTableSpec} of training data.
     */
    public DecisionTreePortModelSpec(final String classCol,
            final DataTableSpec spec) {
        m_classCol = classCol;
        m_spec = spec;
    }

    /**
     * @return the class column
     */
    public String getClassCol() {
        return m_classCol;
    }

    /**
     * @return the training {@link DataTableSpec}, can be <code>null</code> if trained on a vector cell
     */
    public DataTableSpec getSpec() {
        return m_spec;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent[] getViews() {
        return new JComponent[]{};
    }
}
