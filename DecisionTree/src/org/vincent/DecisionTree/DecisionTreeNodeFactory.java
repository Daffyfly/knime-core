package org.vincent.DecisionTree;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "DecisionTree" Node.
 * 
 *
 * @author 
 */
public class DecisionTreeNodeFactory 
        extends NodeFactory<DecisionTreeNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DecisionTreeNodeModel createNodeModel() {
        return new DecisionTreeNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<DecisionTreeNodeModel> createNodeView(final int viewIndex,
            final DecisionTreeNodeModel nodeModel) {
        return new DecisionTreeNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new DecisionTreeNodeDialog();
    }

}

