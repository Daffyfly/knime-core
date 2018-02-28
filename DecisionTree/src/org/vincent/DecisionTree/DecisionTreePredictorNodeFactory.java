package org.vincent.DecisionTree;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "DecisionTreePredictor" Node.
 * 
 *
 * @author 
 */
public class DecisionTreePredictorNodeFactory 
        extends NodeFactory<DecisionTreePredictorNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DecisionTreePredictorNodeModel createNodeModel() {
        return new DecisionTreePredictorNodeModel();
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
    public NodeView<DecisionTreePredictorNodeModel> createNodeView(final int viewIndex,
            final DecisionTreePredictorNodeModel nodeModel) {
        return new DecisionTreePredictorNodeView(nodeModel);
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
        return new DecisionTreePredictorNodeDialog();
    }

}

