/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import java.util.List;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.commands.SpecializationPropertyCommand;
import org.archicontribs.specialization.types.ElementSpecialization;
import org.archicontribs.specialization.types.ElementSpecializationMap;
import org.archicontribs.specialization.types.SpecializationProperty;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IProperty;

public class SpecializationElementSection extends org.archicontribs.specialization.propertysections.AbstractArchimatePropertySection {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationElementSection.class);

	ArchimateElementEditPart elementEditPart = null;

    private Composite compoElement;
	Combo comboElement;
	
    boolean mouseOverHelpButton = false;
	
	/**
	 * Filter to show or reject this section depending on input value
	 */
	public static class Filter extends ObjectFilter {
		@Override
		protected boolean isRequiredType(Object object) {
            if ( object != null && object instanceof ArchimateElementEditPart ) {
                logger.trace("Showing Element specialization tab.");
                return true;
            }
            return false;
		}

		@Override
		protected Class<?> getAdaptableType() {
			return ArchimateElementEditPart.class;
		}
	}
    
    @Override
    protected void setLayout(Composite parent) {
       parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, shouldUseExtraSpace()));
       
       parent.setLayout(new FormLayout());
    }

	/**
	 * Create the controls
	 */
	@Override
	protected void createControls(Composite parent) {
	    this.compoElement = new Composite(parent, SWT.NONE);
        this.compoElement.setForeground(parent.getForeground());
        this.compoElement.setBackground(parent.getBackground());
        this.compoElement.setLayout(new FormLayout());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        this.compoElement.setLayoutData(fd);

		Label label = new Label(this.compoElement, SWT.NONE);
		label.setText("Please select the element's specialization:");
		label.setForeground(this.compoElement.getForeground());
		label.setBackground(this.compoElement.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        label.setLayoutData(fd);
        
        this.comboElement = new Combo(this.compoElement, SWT.BORDER | SWT.READ_ONLY);
        this.comboElement.setFont(new Font(this.comboElement.getDisplay(), "consolas", this.compoElement.getFont().getFontData()[0].getHeight(), SWT.NONE));
        fd = new FormData();
        fd.top = new FormAttachment(label, 0, SWT.CENTER);
        fd.left = new FormAttachment(label, 20);
        fd.right = new FormAttachment(100, -20);
        this.comboElement.setLayoutData(fd);
        this.comboElement.addModifyListener(this.comboModifyListener);
        
        Label btnHelp = new Label(this.compoElement, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(label, 10);
        fd.bottom = new FormAttachment(label, 40, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(0, 40);
        btnHelp.setLayoutData(fd);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { SpecializationElementSection.this.mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { SpecializationElementSection.this.mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( SpecializationElementSection.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(SpecializationPlugin.HELP_ICON, 2, 2);
            }
        });
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceLabel.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceLabel.html"); } });
        
        Label helpLbl = new Label(this.compoElement, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setForeground(parent.getForeground());
        helpLbl.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp, 5);
        helpLbl.setLayoutData(fd);
	}
	
    /**
     * Called when the combo value is changed
     */
    ModifyListener comboModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent event) {
            IArchimateElement concept = SpecializationElementSection.this.elementEditPart.getModel().getArchimateConcept();
            String clazz = concept.getClass().getSimpleName();
        
        	String oldSpecializationName = null;
    		for ( IProperty property: concept.getProperties() ) {
    			if ( SpecializationPlugin.SPECIALIZATION_PROPERTY_KEY.equals(property.getKey()) ) {
    				oldSpecializationName = property.getValue();
    				break;
    			}
    		}
        	
            String newSpecializationName = ((Combo)event.widget).getText();
            
            if ( (oldSpecializationName == null) && newSpecializationName.isEmpty() )
            	return;		// nothing to do
            
            if ( newSpecializationName.equals(oldSpecializationName) )
            	return;		// nothing to do
            
            // if the new specialization differs from the old one, we set the SPECIALIZATION_PROPERTY_KEY property
            CompoundCommand commands = new CompoundCommand("Set specialization");
            commands.add(new SpecializationPropertyCommand(concept, SpecializationPlugin.SPECIALIZATION_PROPERTY_KEY, newSpecializationName, SpecializationElementSection.this.eAdapter));
            
            ElementSpecializationMap elementspecializationMap = ElementSpecializationMap.getFromArchimateModel(concept.getArchimateModel());
            ElementSpecialization oldElementSpecialization = elementspecializationMap.getElementSpecialization(clazz, oldSpecializationName);
            ElementSpecialization newElementSpecialization = elementspecializationMap.getElementSpecialization(clazz, newSpecializationName);
            
            // we remove the old concept properties
            if ( oldElementSpecialization != null ) {
	            for ( SpecializationProperty specializationProperty: oldElementSpecialization.getProperties() ) {
	            	boolean mustRemoveSpecializationProperty = false;
	            	for ( IProperty conceptProperty: concept.getProperties() ) {
	            		if ( specializationProperty.getName().equals(conceptProperty.getKey()) ) {
	            			if ( (conceptProperty.getValue() == null) || conceptProperty.getValue().equals(specializationProperty.getValue()) )
	            				mustRemoveSpecializationProperty = true;
	            			break;
	            		}
	            	}
	            	if ( mustRemoveSpecializationProperty ) {
	            		commands.add(new SpecializationPropertyCommand(concept, specializationProperty.getName(), null, SpecializationElementSection.this.eAdapter));
	            	}
	    		}
            }
            
            // we create the new concept properties
            if ( newElementSpecialization != null ) {
            	for ( SpecializationProperty specializationProperty: newElementSpecialization.getProperties() ) {
            		boolean mustCreateSpecializationProperty = true;
	            	for ( IProperty conceptProperty: concept.getProperties() ) {
	            		if ( specializationProperty.getName().equals(conceptProperty.getKey()) ) {
	            			mustCreateSpecializationProperty = false;
	            			break;
	            		}
	            	}
	            	if ( mustCreateSpecializationProperty ) {
	            		commands.add(new SpecializationPropertyCommand(concept, specializationProperty.getName(), specializationProperty.getValue(), SpecializationElementSection.this.eAdapter));
	            	}
	    		}
            }
            
            getCommandStack().execute(commands);
        }
    };
    
	@Override
	protected Adapter getECoreAdapter() {
		return this.eAdapter;
	}
	   
    /**
     * Adapter to listen to changes made elsewhere (including Undo/Redo commands)
     * This one is a EContentAdapter to listen to child IProperty changes
     */
    Adapter eAdapter = new EContentAdapter() {
        @Override
        public void notifyChanged(Notification msg) {
            if ( msg.getNotifier() instanceof IProperty ) {
                IProperty property = (IProperty)msg.getNotifier();
                if( SpecializationPlugin.SPECIALIZATION_PROPERTY_KEY.equals(property.getKey()) )
                    refreshControls();
            }
        }
        
        @Override
        public void setTarget(Notifier n) {
        	if ( n == null )
        		return;
        	
            if ( n instanceof IDiagramModelArchimateObject) {
                super.setTarget(((IDiagramModelArchimateObject)n).getArchimateConcept());
                refreshControls();
            }
            else
                super.setTarget(n);
        }
        
        @Override
        public void unsetTarget(Notifier n) {
        	if ( n == null )
        		return;
        	
            if ( n instanceof IDiagramModelArchimateObject)
                super.unsetTarget(((IDiagramModelArchimateObject)n).getArchimateConcept());
            else
                super.unsetTarget(n);
        }
    };

	@Override
	protected EObject getEObject() {
        if ( this.elementEditPart == null )
            return null;

        return this.elementEditPart.getModel();
	}

	@Override
    protected void setElement(Object element) {
        this.elementEditPart = (ArchimateElementEditPart)new Filter().adaptObject(element);

        logger.trace("Setting element to "+this.elementEditPart);

        refreshControls();
	}
	
	void refreshControls() {
		if ( this.comboElement == null || this.comboElement.isDisposed() )
			return;
		
        if ( this.elementEditPart == null ) {
            logger.trace("Not refreshing controls as elementEditPart is null");
            this.comboElement.removeModifyListener(this.comboModifyListener);
            this.comboElement.setText("");
            this.comboElement.addModifyListener(this.comboModifyListener);
            return;
        }

        logger.trace("Refreshing controls");
        IArchimateConcept concept = this.elementEditPart.getModel().getArchimateConcept();
        
        this.comboElement.removeModifyListener(this.comboModifyListener);
        this.comboElement.removeAll();
        this.comboElement.add("");

        ElementSpecializationMap elementSpecializationMap = ElementSpecializationMap.getFromArchimateModel(concept.getArchimateModel());
        List<ElementSpecialization> elementSpecializations = elementSpecializationMap.get(concept.getClass().getSimpleName());
        // TODO : remove unused properties from old specialization
        
        String actualSpecialization = null;
		for ( IProperty property:concept.getProperties() ) {
			if ( SpecializationPlugin.SPECIALIZATION_PROPERTY_KEY.equals(property.getKey()) ) {
				actualSpecialization = property.getValue();
				break;
			}
		}
        
        // then we fill in the combo with the existing specializations
        for ( ElementSpecialization elementSpecialization: elementSpecializations ) {
            this.comboElement.add(elementSpecialization.getSpecializationName());
            if ( elementSpecialization.getSpecializationName().equals(actualSpecialization) )
            	this.comboElement.setText(elementSpecialization.getSpecializationName());
        }
        
        // We ensure the modify listener is set (but only once ...)
        this.comboElement.removeModifyListener(this.comboModifyListener);
        this.comboElement.addModifyListener(this.comboModifyListener);
	}
}