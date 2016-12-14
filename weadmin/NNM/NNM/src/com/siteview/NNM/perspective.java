package com.siteview.NNM;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.siteview.NNM.Views.NNMTreeView;

/**
 * 
 * @author Administrator
 * NNM 主页面
 */
public class perspective implements IPerspectiveFactory{

	@Override
	public void createInitialLayout(IPageLayout layout) {
		// TODO Auto-generated method stub
		IFolderLayout navFold = layout.createFolder("Navigator",IPageLayout.LEFT, 0.18f, layout.getEditorArea());
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		navFold.addView(NNMTreeView.ID);
//		navFold.addView(BrowseView.ID);
//		layout.addStandaloneView(NNMTreeView.ID, true, IPageLayout.LEFT, 0.18f, layout.getEditorArea());
		
		
	}

}
