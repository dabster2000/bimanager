package dk.trustworks.bimanager.handler;

import dk.trustworks.bimanager.service.ReportService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;

/**
 * Created by hans on 16/03/15.
 */
public class ReportHandler extends DefaultHandler {

    private final ReportService reportService;

    public ReportHandler() {
        super("report");
        this.reportService = new ReportService();
    }

    @Override
    protected DefaultLocalService getService() {
        return reportService;
    }
}
