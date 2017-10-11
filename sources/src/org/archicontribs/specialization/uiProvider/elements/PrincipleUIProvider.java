/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.elements;

import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.figure.CollaborationFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;


/**
 * Principle UI Provider
 * 
 * @author Herve Jouin
 */
public class PrincipleUIProvider extends com.archimatetool.editor.ui.factory.elements.PrincipleUIProvider {
    @Override
    public EditPart createEditPart() {
            // we override the standard method because we want our NodeFigure class to be called
        return new ArchimateElementEditPart(CollaborationFigure.class);
    }
    
    /**
     * Gets the icon image from the component's properties. If not found, return the default one.
     */
    @Override
    public Image getImage() {
        String iconName = null;
        
        if ( SpecializationPlugin.mustShowIcon(instance) )
            iconName = SpecializationPlugin.getIconName(instance, true);
        
        return iconName==null ? super.getImage() : getImageWithUserFillColor(iconName);
    }
}