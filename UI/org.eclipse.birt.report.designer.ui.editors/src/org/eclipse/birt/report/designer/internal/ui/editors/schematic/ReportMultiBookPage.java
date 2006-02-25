/*************************************************************************************
 * Copyright (c) 2004 Actuate Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Actuate Corporation - Initial implementation.
 ************************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.editors.schematic;

import java.util.ArrayList;

import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class ReportMultiBookPage extends Page implements
		IContentOutlinePage,
		ISelectionProvider,
		ISelectionChangedListener
{

	private PageBook pagebook;
	private ISelection selection;
	private ArrayList listeners = new ArrayList( );
	private IPageBookViewPage currentPage;
	private IPageBookViewPage emptyPage;
	private IActionBars actionBars;

	public ReportMultiBookPage( )
	{

	}

	public void addFocusListener( FocusListener listener )
	{
	}

	public void addSelectionChangedListener( ISelectionChangedListener listener )
	{
		listeners.add( listener );
	}

	public void createControl( Composite parent )
	{
		pagebook = new PageBook( parent, SWT.NONE );
	}

	public void dispose( )
	{
		if ( pagebook != null && !pagebook.isDisposed( ) )
			pagebook.dispose( );
		if ( emptyPage != null )
		{
			emptyPage.dispose( );
			emptyPage = null;
		}
		pagebook = null;
		listeners = null;
	}

	public boolean isDisposed( )
	{
		return listeners == null;
	}

	public Control getControl( )
	{
		return pagebook;
	}

	public PageBook getPagebook( )
	{
		return pagebook;
	}

	public ISelection getSelection( )
	{
		return selection;
	}

	public void makeContributions( IMenuManager menuManager,
			IToolBarManager toolBarManager, IStatusLineManager statusLineManager )
	{
	}

	public void removeFocusListener( FocusListener listener )
	{
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener )
	{
		listeners.remove( listener );
	}

	public void selectionChanged( SelectionChangedEvent event )
	{
		setSelection( event.getSelection( ) );
		StructuredSelection selection = (StructuredSelection) event.getSelection( );
		Object obj = selection.getFirstElement( );
		if ( obj instanceof IFormPage )
		{
			Object palette = ( (IFormPage) obj ).getAdapter( PalettePage.class );
			setActivePage( (IPageBookViewPage) palette );
		}
	}

	public void setActionBars( IActionBars actionBars )
	{
		// this.actionBars = actionBars;
		// registerToolbarActions(actionBars);
		if ( currentPage != null )
			setActivePage( currentPage );

	}

	public IActionBars getActionBars( )
	{
		return actionBars;
	}

	public void setFocus( )
	{
		if ( currentPage != null )
			currentPage.setFocus( );
	}

	private IPageBookViewPage getEmptyPage( )
	{
		if ( emptyPage == null )
			emptyPage = new EmptyPage( );
		return emptyPage;
	}

	public void setActivePage( IPageBookViewPage page )
	{
		if ( page == null )
		{
			page = getEmptyPage( );
		}
		else if ( currentPage != null
				&& currentPage != getEmptyPage( )
				&& !( currentPage instanceof PalettePage ) && page!=currentPage )
		{
			currentPage.dispose( );
		}
		this.currentPage = page;
		if ( pagebook == null )
		{
			// still not being made
			return;
		}
		Control control = null;
		try
		{
			control = page.getControl( );
		}
		catch ( Exception e )
		{

		}
		if ( control == null || control.isDisposed( ) )
		{
			if ( page.getSite( ) == null )
			{
				try
				{
					page.init( getSite( ) );
				}
				catch ( PartInitException e )
				{
					page = getEmptyPage( );
				}
			}
			// first time
			page.createControl( pagebook );
			// page.setActionBars(getActionBars());
			control = page.getControl( );
		}
		pagebook.showPage( control );
		this.currentPage = page;
	}

	/**
	 * Set the selection.
	 */
	public void setSelection( ISelection selection )
	{
		this.selection = selection;
		if ( listeners == null )
			return;
		SelectionChangedEvent e = new SelectionChangedEvent( this, selection );
		for ( int i = 0; i < listeners.size( ); i++ )
		{
			( (ISelectionChangedListener) listeners.get( i ) ).selectionChanged( e );
		}
	}

	protected class EmptyPage implements IPageBookViewPage
	{

		private Composite control;
		private IPageSite site;

		public IPageSite getSite( )
		{
			return site;
		}

		public void init( IPageSite site ) throws PartInitException
		{
			this.site = site;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl( Composite parent )
		{
			control = new Composite( parent, SWT.NULL );
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.part.IPage#dispose()
		 */
		public void dispose( )
		{
			control = null;
			site = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.part.IPage#getControl()
		 */
		public Control getControl( )
		{
			return control;
		}

		public void setActionBars( IActionBars actionBars )
		{
			// TODO Auto-generated method stub

		}

		public void setFocus( )
		{
			// TODO Auto-generated method stub

		}
	}
}
