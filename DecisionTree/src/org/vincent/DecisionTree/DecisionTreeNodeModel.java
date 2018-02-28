package org.vincent.DecisionTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelFilterString;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import dt.DecisionTree;
import dt.Examples;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of DecisionTree.
 * 
 *
 * @author 
 */
public class DecisionTreeNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(DecisionTreeNodeModel.class);
    
    static final String CFGKEY_CLASSCOL = "Class";    
    static final String CFGKEY_INCLUDED = "Included";
    
    
    private final SettingsModelFilterString m_included =
            new SettingsModelFilterString(DecisionTreeNodeModel.CFGKEY_INCLUDED);
    
    private final SettingsModelString m_classcol =
    		new SettingsModelString(DecisionTreeNodeModel.CFGKEY_CLASSCOL,null);

    static ArrayList<String> m_excludedList;
    static DecisionTree m_tree;
    /**
     * Constructor for the node model.
     */
    public DecisionTreeNodeModel() {
        this(new DecisionTree());
    }
    
    public DecisionTreeNodeModel(final DecisionTree dt) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{DecisionTreePortModel.TYPE});
        m_tree = dt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {
    	
        BufferedDataTable table = (BufferedDataTable) inData[0];
        DataTableSpec tablespec = table.getDataTableSpec();
        
        DecisionTree dt = new DecisionTree();
        
        int indexClass= tablespec.findColumnIndex(m_classcol.getStringValue());
        
        ArrayList<Integer> indexes = new ArrayList<>();
        
        for(String s : m_included.getIncludeList()){
        	indexes.add(tablespec.findColumnIndex(s));
        }
        
        String[] attributes = m_included.getIncludeList().toArray(new String[0]);
        dt.setAttributes(attributes);
        
        
        CloseableRowIterator tableIterator = table.iterator();
        
        while(tableIterator.hasNext()){
        	DataRow row = tableIterator.next();
        	ArrayList<String> example = new ArrayList<>();
        	StringCell sample = new StringCell("sample");
        	for(int i : indexes){
        		if(row.getCell(i).getType().getName() == sample.getType().getName()){
            		example.add(((StringCell)row.getCell(i)).getStringValue());
        		}
        	}
        	
        	String[] exampleList = example.toArray(new String[0]);
        	dt.addExample(exampleList, ((BooleanCell)row.getCell(indexClass)).getBooleanValue());
        }
        
        dt.compile();
        DecisionTreePortModelSpec spec = new DecisionTreePortModelSpec(m_classcol.getStringValue(), tablespec);
        DecisionTreePortModel out = new DecisionTreePortModel(dt, spec);
        return new DecisionTreePortModel[]{out};
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
    	DataTableSpec inspec = (DataTableSpec) inSpecs[0];
    	
    	/*m_excludedList = new ArrayList<>();
    	
    	for(String s : inspec.getColumnNames()){
    		if(inspec.getColumnSpec(s).getType().isAdaptable(StringValue.class)){
    			m_excludedList.add(s);
    		}
    	}
    	
    	m_included.setExcludeList(m_excludedList);*/
    	
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DecisionTreePortModelSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        // TODO save user settings to the config object.
        
        m_included.saveSettingsTo(settings);
        m_classcol.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.
        
    	m_included.loadSettingsFrom(settings);
    	m_classcol.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.
    	m_included.validateSettings(settings);
    	m_classcol.validateSettings(settings);

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    	
    	 File f = new File(internDir, "decisionTree");
         ObjectInputStream in = null;
         try {
             in = new ObjectInputStream(new FileInputStream(f));
             
             LinkedHashSet<String> attributes = (LinkedHashSet<String>)in.readObject();
             Examples examples = (Examples)in.readObject();
             
             m_tree.setAttributes(attributes.toArray(new String[0]));
             
             
             m_tree.setExamples(examples);
             
             m_tree.compile();
             //m_tree = (DecisionTree)in.readObject();
         } catch (ClassNotFoundException e) {
             logger.error("Could not read weka classifier", e);
             IOException ioe = new IOException();
             ioe.initCause(e);
             throw ioe;
         } finally {
             if (in != null) {
                 try {
                     in.close();
                 } catch (Exception e) {
                	 logger.debug("Could not close stream", e);
                 }
             }
             exec.setProgress(1.0);
         }
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
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
       
    	 File f = new File(internDir, "decisionTree");
         ObjectOutputStream out = null;
         try {
             out = new ObjectOutputStream(new FileOutputStream(f));
             out.writeObject(m_tree.getAttributes());
             out.writeObject(m_tree.getExamples());
        	 //out.writeObject(m_tree);
             exec.setProgress(.5);
             exec.checkCanceled();
         } catch (IOException ioe) {
             throw ioe;
         } finally {
             if (out != null) {
                 out.close();
             }
             exec.setProgress(1.0);
         }
    }

}

