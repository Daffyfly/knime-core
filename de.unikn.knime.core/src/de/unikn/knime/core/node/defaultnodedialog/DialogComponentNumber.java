/* @(#)$RCSfile$ 
 * $Revision$ $Date$ $Author$
 * 
 * -------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 * 
 * Copyright, 2003 - 2004
 * Universitaet Konstanz, Germany.
 * Lehrstuhl fuer Angewandte Informatik
 * Prof. Dr. Michael R. Berthold
 * 
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner.
 * -------------------------------------------------------------------
 * 
 * History
 *   18.09.2005 (mb): created
 */
package de.unikn.knime.core.node.defaultnodedialog;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.unikn.knime.core.data.DataTableSpec;
import de.unikn.knime.core.node.InvalidSettingsException;
import de.unikn.knime.core.node.NodeSettings;

/**
 * Provide a standard component for a dialog that allows to edit an integer
 * value. Provides label and spinner that checks ranges as well as functionality
 * to load/store into config object.
 * 
 * @author M. Berthold, University of Konstanz
 */
public class DialogComponentNumber extends DialogComponent {
    private JSpinner m_spinner;

    private String m_configName;
    
    /* final field to store the type, either int or double */
    private final Type m_type;
    
    /** possible types. */
    private enum Type {
        /** Int type. */
        INT,
        /** Double type. */
        DOUBLE
    }
    

    /**
     * Constructor put label and spinner into panel (int type).
     * 
     * @param configName name used in configuration file
     * @param label label for dialog in front of spinner
     * @param minValue min value, and
     * @param maxValue max value for spinner
     * @param defaultValue initial value if no value is stored in the config
     */
    public DialogComponentNumber(final String configName, final String label,
            final int minValue, final int maxValue, final int defaultValue) {
        this.add(new JLabel(label));
        SpinnerNumberModel model = new SpinnerNumberModel(defaultValue,
                minValue, maxValue, 1);
        m_spinner = new JSpinner(model);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)m_spinner
                .getEditor();
        editor.getTextField().setColumns(
                Integer.toString(maxValue).length() + 1);
        this.add(m_spinner);
        m_configName = configName;
        m_type = Type.INT;
    }
    
    
    
    /**
     * Constructor put label and spinner into panel (double type).
     * 
     * @param configName name used in configuration file
     * @param label label for dialog in front of spinner
     * @param minValue min value, and
     * @param maxValue max value for spinner
     * @param defaultValue initial value if no value is stored in the config
     * @param stepSize the step size of the spinner 
     */
    public DialogComponentNumber(final String configName, final String label,
            final double minValue, final double maxValue, 
            final double defaultValue, final double stepSize) {
        this.add(new JLabel(label));
        SpinnerNumberModel model = new SpinnerNumberModel(defaultValue,
                minValue, maxValue, stepSize);
        m_spinner = new JSpinner(model);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)m_spinner
                .getEditor();
        editor.getTextField().setColumns(
                Double.toString(maxValue).length() + 1);
        this.add(m_spinner);
        m_configName = configName;
        m_type = Type.DOUBLE;
    }

    /**
     * Read value for this dialog component from configuration object.
     * 
     * @param settings The <code>NodeSettings</code> to read from.
     * @param specs The input specs.
     * @throws InvalidSettingsException if load fails.
     */
    public void loadSettingsFrom(final NodeSettings settings,
            final DataTableSpec[] specs) throws InvalidSettingsException {
        assert (settings != null);
        try {
            if (m_type.equals(Type.INT)) {
                int newInt = settings.getInt(m_configName);
                m_spinner.setValue(newInt);
            } else if (m_type.equals(Type.DOUBLE)) {
                double newDouble = settings.getDouble(m_configName);
                m_spinner.setValue(newDouble);
            }
        } catch (InvalidSettingsException ise) {
            // do nothing
        }
    }

    /**
     * write settings of this dialog component into the configuration object.
     * 
     * @param settings The <code>NodeSettings</code> to write into.
     */
    public void saveSettingsTo(final NodeSettings settings) {
        if (m_type.equals(Type.INT)) {
            settings.addInt(m_configName, ((Integer)m_spinner.getValue()));
        } else if (m_type.equals(Type.DOUBLE)) {
            settings.addDouble(m_configName, ((Double)m_spinner.getValue()));
        }
    }

    /**
     * @see de.unikn.knime.core.node.defaultnodedialog.DialogComponent
     *      #setEnabledComponents(boolean)
     */
    @Override
    protected void setEnabledComponents(final boolean enabled) {
        m_spinner.setEnabled(enabled);
    }
}
