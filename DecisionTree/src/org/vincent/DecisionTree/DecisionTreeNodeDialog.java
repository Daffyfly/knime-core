package org.vincent.DecisionTree;

import java.util.List;

import org.knime.core.data.BooleanValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelFilterString;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.ColumnFilter;
import org.knime.core.node.util.DataValueColumnFilter;

/**
 * <code>NodeDialog</code> for the "DecisionTree" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class DecisionTreeNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring DecisionTree node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected DecisionTreeNodeDialog() {
        super();
        
        DataValueColumnFilter filterClass = new DataValueColumnFilter(BooleanValue.class);
        
        addDialogComponent(new DialogComponentColumnNameSelection(
                new SettingsModelString(
                    DecisionTreeNodeModel.CFGKEY_CLASSCOL,
                    null),
                    "Class:", 0, filterClass));
        
        DataValueColumnFilter filterColumns = new DataValueColumnFilter(StringValue.class);
        
        addDialogComponent(new DialogComponentColumnFilter(
                new SettingsModelFilterString(
                		DecisionTreeNodeModel.CFGKEY_INCLUDED),
                    0, true, filterColumns));
                    
    }
}

