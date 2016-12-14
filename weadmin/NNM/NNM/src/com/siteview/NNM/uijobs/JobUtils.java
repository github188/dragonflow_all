package com.siteview.NNM.uijobs;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.siteview.NNM.Editors.SubnetDataEditor;
import com.siteview.NNM.Editors.TopNetWorkEditor;
import com.siteview.NNM.dialogs.table.ImageKeys;

import Siteview.Api.BusinessObject;

/**
 * 批量操作监测器 Job
 * 
 */
public class JobUtils {
	private static JobUtils jobUtils = null;

	// ping规则
	private ISchedulingRule Ping_Schedule_RULE = new ISchedulingRule() {
		public boolean contains(ISchedulingRule rule) {
			return this.equals(rule);
		}

		public boolean isConflicting(ISchedulingRule rule) {
			return this.equals(rule);
		}
	};
	// refreshtop规则
		private ISchedulingRule refresh_Schedule_RULE = new ISchedulingRule() {
			public boolean contains(ISchedulingRule rule) {
				return this.equals(rule);
			}

			public boolean isConflicting(ISchedulingRule rule) {
				return this.equals(rule);
			}
		};

	public static JobUtils getJobUtils() {
		if (jobUtils == null) {
			jobUtils = new JobUtils();
		}
		return jobUtils;
	}

	// 监测器列表刷新监测器
	public void pingIp(final Display display,
			final List<CLabel> clabels,final List<String> ips,final DefaultCategoryDataset ds,
			final int[] ins,final SubnetDataEditor  sub) {
			Job refreshtree = new Job("ping ip ...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final Boolean[] f=new Boolean[]{false};
				for(int i=0;i<clabels.size();i++){
					if(f[0])
						return  Status.OK_STATUS;
					final CLabel cl=clabels.get(i);
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if(cl.isDisposed()){
								clabels.clear();
								sub.changeIpsection();
								f[0]=true;
								return;
							}
							sub.ipSection.setText(sub.getPartName()+"----后台正在ping:"+cl.getText());
							sub.ipSection.layout();
						}
					});
					String ip = ips.get(i);
					boolean flag =PingUtil.ping(ip, 100, 4, 1);
					if (flag){
						ins[0]++;
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								if(cl.isDisposed()){
									f[0]=true;
									clabels.clear();
									sub.changeIpsection();
									return;
								}
								cl.setImage(ImageKeys.images.get(ImageKeys.GOOD));
							}
						});
					}else{
						ins[1]++;
					}
				}
				return Status.OK_STATUS;
			}
		};
		refreshtree.setRule(Ping_Schedule_RULE);
		refreshtree.schedule(0);
		refreshtree.addJobChangeListener(new IJobChangeListener() {
			@Override
			public void sleeping(IJobChangeEvent event) {
			}

			@Override
			public void scheduled(IJobChangeEvent event) {
			}

			@Override
			public void running(IJobChangeEvent event) {
			}

			@Override
			public void done(IJobChangeEvent event) {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						sub.changeIpsection();
						if(clabels.size()<=0)
							return;
						 ds.addValue(ins[0], "ping通", "ping通");  
					     ds.addValue(ins[1], "ping不通", "ping不通");
					     sub.getchart();
					}
				});
			}

			@Override
			public void awake(IJobChangeEvent event) {
			}

			@Override
			public void aboutToRun(IJobChangeEvent event) {
			}
		});
	}
	
	// 监测器列表刷新监测器
	public void refreshtop(final TopNetWorkEditor top,final Display display) {
			Job refreshtree = new Job("刷新top流量排名 ...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				top.getData();
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						if(top.getIntable().isDisposed())
							return;
						top.createTableItem();
					}
				});
				return Status.OK_STATUS;
			}
		};
		refreshtree.setRule(refresh_Schedule_RULE);
		refreshtree.schedule(0);
		refreshtree.addJobChangeListener(new IJobChangeListener() {
			@Override
			public void sleeping(IJobChangeEvent event) {
			}
			@Override
			public void scheduled(IJobChangeEvent event) {
			}
			@Override
			public void running(IJobChangeEvent event) {
			}
			@Override
			public void done(IJobChangeEvent event) {
			}

			@Override
			public void awake(IJobChangeEvent event) {
			}
			@Override
			public void aboutToRun(IJobChangeEvent event) {
			}
		});
	}
}
