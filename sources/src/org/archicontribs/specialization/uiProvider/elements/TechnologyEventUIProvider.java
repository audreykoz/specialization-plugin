/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.elements;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;


/**
 * TechnologyEvent UI Provider
 * 
 * @author Herve Jouin
 */
public class TechnologyEventUIProvider extends com.archimatetool.editor.ui.factory.elements.TechnologyEventUIProvider {
    private static final SpecializationLogger logger = new SpecializationLogger(TechnologyEventUIProvider.class);
    
    @Override
    public EditPart createEditPart() {
            // we override the standard method because we want our EventFigure class to be called
        return new ArchimateElementEditPart(org.archicontribs.specialization.uiProvider.elements.figures.EventFigure.class);
    }
    
    /**
     * Gets the icon image from the component's properties. If not found, return the default one.
     */
    @Override
    public Image getImage() {
    	Image image = SpecializationPlugin.getIcon(this.instance);
        if ( image != null ) {
            if ( logger.isTraceEnabled() ) logger.trace(SpecializationPlugin.getFullName(this.instance)+": Displaying custom icon");
            return image;
        }
    	
        logger.trace(SpecializationPlugin.getFullName(this.instance)+": Displaying default icon");        	
    	return super.getImage();
    }
}