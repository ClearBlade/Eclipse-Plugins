package com.clearblade.platform.eclipse.popup.actions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.Bundle;

public class AddPlatform implements IObjectActionDelegate {

	private Shell shell;
	private IProject activeProject;
	
	/**
	 * Constructor for Action1.
	 */
	public AddPlatform() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		//shell = targetPart.getSite().getShell();
		//activeProject = (IProject)targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		if (isWorklightProject()){
			if (!hasJavascriptPlatform()){
				boolean success = addJSProject();
				if (success){
					MessageDialog.openInformation(
							shell,
							"Eclipse",
							"ClearBlade javascript Client succesfully added to Worklight project.");

				}else {
					MessageDialog.openInformation(
							shell,
							"Eclipse",
							"ClearBlade javascript Client failed to add");

				}
							}else {
				MessageDialog.openInformation(
						shell,
						"Eclipse",
						"Platform already found");

			}
		} else if (isAndroidProject()){
			if (!hasAndroidPlatform()) {
				boolean success = addAndroidProject();
				if (success){
					MessageDialog.openInformation(
							shell,
							"Eclipse",
							"ClearBlade android client libraries successfully added to Android project.");
				}else {
					MessageDialog.openInformation(
							shell,
							"Eclipse",
							"ClearBlade android client libraries failed to add.");
				}
				
			}else {
				MessageDialog.openInformation(
						shell,
						"Eclipse",
						"Platform already found");
			}
		}
	}

	private boolean addAndroidProject() {
		Bundle bundle = Platform.getBundle( "com.clearblade.platform.eclipse" );
		URL url = bundle.getResource("resources/ClearBladeAndroidAPI.jar");//IPath("path.in.plugin");
		InputStream stream;
		try {
			url = FileLocator.toFileURL(url);
			stream = url.openStream();// bundle, url, false );
			IFile file = activeProject.getFile( "libs/ClearBladeAndroidAPI.jar" );
			file.create( stream, true, null );
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean addJSProject() {
		Bundle bundle = Platform.getBundle( "com.clearblade.platform.eclipse" );
		URL url = bundle.getResource("resources/ClearBlade.js");
		InputStream stream;
		try {
			url = FileLocator.toFileURL(url);
			stream = url.openStream();
			IFolder appsFolder = activeProject.getFolder("apps");
			IResource[] children= appsFolder.members();
			for (int i=0; i<children.length; i++){
				if (children[i] instanceof IFolder){
					IFolder folder = (IFolder)children[i];
					IFile file = activeProject.getFile( "apps/"+folder.getName()+"/common/js/ClearBlade.js" );
					file.create( stream, true, null );
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean isAndroidProject() {
		String[] natures;
		try {
			natures = (activeProject).getDescription().getNatureIds();
			for (int i = 0; i < natures.length; i++) {
				if (natures[i].equals("com.android.ide.eclipse.adt.AndroidNature")){
					return true;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean hasAndroidPlatform(){
		IFile file = activeProject.getFile( "libs/ClearBladeAndroidAPI.jar" );
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
	private boolean hasJavascriptPlatform(){
		return false;
	}

	private boolean isWorklightProject() {
		String[] natures;
		try {
			natures = activeProject.getDescription().getNatureIds();
			for (int i = 0; i < natures.length; i++) {
				if (natures[i].equals("com.worklight.studio.plugin.WorklightNature")){
					return true;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		//activeProject = (IProject)selection;
		if (selection instanceof TreeSelection){
			Object elem = ((TreeSelection)selection).getFirstElement();
			if (elem instanceof IProject){
				activeProject = (IProject)elem;
			}
		}
	}

}
