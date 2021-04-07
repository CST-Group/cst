/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util.viewer;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.util.ChartViewerUtil;
import br.unicamp.cst.util.Refresher;
import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author rgudwin
 */
public class AnalysisPanel extends javax.swing.JPanel {
    
    private ChartPanel codeletsChart;
    private Thread threadMindEntities;
    private Refresher refresher = null;
    private boolean bStopRefresh = false;
    private long instant = 0;
    private List<Codelet> analyzedCodelets;


    /**
     * Creates new form AnalysisPanel
     */
    public AnalysisPanel(List<Codelet> ac) {
        initComponents();
        setAnalyzedCodelets(ac);
        startMindEntitiesThread();
    }
    
   public ChartPanel getCodeletsChart() {
        return codeletsChart;
    }

    public void setCodeletsChart(ChartPanel codeletsChart) {
        this.codeletsChart = codeletsChart;
    }
    
    /**
     * @return the threadMindEntities
     */
    public Thread getThreadMindEntities() {
        return threadMindEntities;
    }

    /**
     * @param threadMindEntities the threadMindEntities to set
     */
    public void setThreadMindEntities(Thread threadMindEntities) {
        this.threadMindEntities = threadMindEntities;
    }
    
    /**
     * @return the bStopRefresh
     */
    public boolean isbStopRefresh() {
        return bStopRefresh;
    }
    
     public long getInstant() {
        return instant;
    }

    public void setInstant(long instant) {
        this.instant = instant;
    }
    
    public List<Codelet> getAnalyzedCodelets() {
        return analyzedCodelets;
    }

    public void setAnalyzedCodelets(List<Codelet> analyzedCodelets) {
        this.analyzedCodelets = analyzedCodelets;
    }

    
    private void startMindEntitiesThread() {

        long initialTime = Calendar.getInstance().getTimeInMillis();

        setThreadMindEntities(new Thread() {
            @Override
            public void run() {

                XYSeriesCollection dataset = new XYSeriesCollection();

                for (Codelet co : getAnalyzedCodelets()) {
                    dataset.addSeries(new XYSeries(co.getName()));
                }

                synchronized (pnCodelets) {
                    pnCodelets.setLayout(new BorderLayout());
                    setCodeletsChart(ChartViewerUtil.createLineXYChart(dataset, "CodeRack Inspection", "Codelets", "Activation", 100));
                    pnCodelets.add(getCodeletsChart(), BorderLayout.CENTER);
                    pnCodelets.validate();
                }

                while (!isbStopRefresh()) {
                    if (cbRefreshChart.isSelected()) {
                        if (refresher != null) setInstant(refresher.refresh());
                        else setInstant(Calendar.getInstance().getTimeInMillis() - initialTime);
                        ChartViewerUtil.updateValuesInXYLineChart(dataset, getAnalyzedCodelets(), getInstant());
                    }
                    try {
                        //int refreshTime = txtRefreshTime.getText().trim().equals("") ? 100 : Integer.parseInt(txtRefreshTime.getText());
                        int refreshTime = 500;
                        Thread.currentThread().sleep(refreshTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        getThreadMindEntities().start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        cbRefreshChart = new javax.swing.JCheckBox();
        pnCodelets = new javax.swing.JPanel();
        sdChart = new javax.swing.JSlider();

        setLayout(new java.awt.GridBagLayout());

        cbRefreshChart.setSelected(true);
        cbRefreshChart.setText("Auto Refresh");
        cbRefreshChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRefreshChartActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        add(cbRefreshChart, gridBagConstraints);

        pnCodelets.setBackground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout pnCodeletsLayout = new javax.swing.GroupLayout(pnCodelets);
        pnCodelets.setLayout(pnCodeletsLayout);
        pnCodeletsLayout.setHorizontalGroup(
            pnCodeletsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        pnCodeletsLayout.setVerticalGroup(
            pnCodeletsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 10.0;
        add(pnCodelets, gridBagConstraints);

        sdChart.setValue(100);
        sdChart.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sdChartStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(sdChart, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cbRefreshChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRefreshChartActionPerformed
        sdChart.setValue(100);
        if (cbRefreshChart.isSelected()) {
            synchronized (getThreadMindEntities()) {
                if (getThreadMindEntities() != null) {
                    getThreadMindEntities().notify();
                }
            }
        }
    }//GEN-LAST:event_cbRefreshChartActionPerformed

    private void sdChartStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sdChartStateChanged
        if (sdChart.getValue() == 100) {
            getCodeletsChart().restoreAutoDomainBounds();
        } else {
            XYPlot plot = (XYPlot) getCodeletsChart().getChart().getPlot();
            double newUpper = sdChart.getValue() * getInstant() / sdChart.getMaximum();
            plot.getDomainAxis().setRange(newUpper - 10000, newUpper);
        }
    }//GEN-LAST:event_sdChartStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbRefreshChart;
    private javax.swing.JPanel pnCodelets;
    private javax.swing.JSlider sdChart;
    // End of variables declaration//GEN-END:variables
}