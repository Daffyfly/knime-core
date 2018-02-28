package org.vincent.DecisionTree;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "DecisionTreePredictor" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class DecisionTreePredictorNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring DecisionTreePredictor node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected DecisionTreePredictorNodeDialog() {
        super();                    
    }
}

