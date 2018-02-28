package org.vincent.DecisionTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import dt.BadDecisionException;
import dt.DecisionTree;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of DecisionTreePredictor.
 * 
 *
 * @author 
 */
public class DecisionTreePredictorNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(DecisionTreePredictorNodeModel.class);
        
       
    /**
     * Constructor for the node model.
     */
    protected DecisionTreePredictorNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(new PortType[]{BufferedDataTable.TYPE, DecisionTreePortModel.TYPE}, new PortType[]{BufferedDataTable.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {

    	BufferedDataTable table = (BufferedDataTable) inData[0];
    	DataTableSpec inSpec = table.getSpec();
    	
    	
    	System.out.println(inData[1]);
    	DecisionTree dt = ((DecisionTreePortModel) inData[1]).getClassifier();
        

        CloseableRowIterator tableIterator = table.iterator();
    	

    	ColumnRearranger rearranger = createColumnRearranger(inSpec, dt);
		
    	BufferedDataTable outTable = exec.createColumnRearrangeTable(
    			table, rearranger, exec);
    	return new BufferedDataTable[]{outTable};
    }
    
    private ColumnRearranger createColumnRearranger(
    		DataTableSpec spec, DecisionTree dt) throws InvalidSettingsException {
        	ColumnRearranger result = new ColumnRearranger(spec);
    	// the following code appends a single column
    	DataColumnSpecCreator appendSpecCreator = 
    		new DataColumnSpecCreator("prediction", BooleanCell.TYPE);
    	DataColumnSpec appendSpec = appendSpecCreator.createSpec();
        	result.append(new SingleCellFactory(appendSpec) {
    		public DataCell getCell(final DataRow row) {
    			// perform calculation based on input row
    			Map<String, String> case1 = new HashMap<String, String>();
    			for(String s : dt.getAttributes()){
    				case1.put(s, ((StringCell) row.getCell(spec.findColumnIndex(s))).getStringValue());
    			}

    			Boolean result = false;
    			try {
    				System.out.println(row.toString());
					result = dt.apply(case1);
					System.out.println(result);
				} catch (BadDecisionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			DataCell resultCell;
    			if(result)
    				resultCell = BooleanCell.TRUE;
    			else
    				resultCell = BooleanCell.FALSE;
    			return resultCell;
    		}
        	});
    	return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {


    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute tmethod, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}

