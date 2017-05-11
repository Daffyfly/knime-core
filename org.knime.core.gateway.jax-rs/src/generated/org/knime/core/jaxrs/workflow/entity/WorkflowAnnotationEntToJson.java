/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 */
package org.knime.core.jaxrs.workflow.entity;

import java.util.List;
import org.knime.core.gateway.v0.workflow.entity.BoundsEnt;
import org.knime.core.gateway.v0.workflow.entity.StyleRangeEnt;
import org.knime.core.gateway.v0.workflow.entity.WorkflowAnnotationEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.knime.core.gateway.v0.workflow.entity.builder.GatewayEntityBuilder;

import java.util.stream.Collectors;
import java.util.HashMap;


/**
 * Wrapper class for the {@link WorkflowAnnotationEnt} interface with Json-annotated getter-methods.
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public class WorkflowAnnotationEntToJson  implements WorkflowAnnotationEnt {

	private final WorkflowAnnotationEnt m_e;
	
	public WorkflowAnnotationEntToJson(final WorkflowAnnotationEnt e) {
		m_e = e;
	}

	@JsonProperty("Text")
    public String getText() {
    	return m_e.getText();
    }
    
	@JsonProperty("Bounds")
    public BoundsEnt getBounds() {
        return BoundsEntToJson.wrap(m_e.getBounds());
    }
    
	@JsonProperty("BgColor")
    public int getBgColor() {
    	return m_e.getBgColor();
    }
    
	@JsonProperty("BorderSize")
    public int getBorderSize() {
    	return m_e.getBorderSize();
    }
    
	@JsonProperty("BorderColor")
    public int getBorderColor() {
    	return m_e.getBorderColor();
    }
    
	@JsonProperty("FontSize")
    public int getFontSize() {
    	return m_e.getFontSize();
    }
    
	@JsonProperty("Alignment")
    public String getAlignment() {
    	return m_e.getAlignment();
    }
    
	@JsonProperty("StyleRanges")
    public List<StyleRangeEnt> getStyleRanges() {
    	return m_e.getStyleRanges().stream().map(l -> StyleRangeEntToJson.wrap(l)).collect(Collectors.toList());
    }
    


	@Override
	public String toString() {
	    return m_e.toString();
	}
	
	@JsonProperty("EntityType")
	public String getEntityType() {
		return "WorkflowAnnotationEnt";
	}
	
	public static WorkflowAnnotationEnt wrap(WorkflowAnnotationEnt e) {
	    return new WorkflowAnnotationEntToJson(e);
	}

}