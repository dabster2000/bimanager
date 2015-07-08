package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.ReportDTO;
import dk.trustworks.bimanager.dto.Work;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;

/**
 * Created by hans on 21/05/15.
 */
public class ReportService extends DefaultLocalService {

    private final RestClient restClient;
    private final UserService userService;

    public ReportService() {
        restClient = new RestClient();
        userService = new UserService();
    }

    public ArrayList<ReportDTO> findByTaskUUIDAndUserUUID(Map<String, Deque<String>> queryParameters) {
        System.out.println("ReportService.findByTaskUUIDAndUserUUID");
        System.out.println("queryParameters = [" + queryParameters + "]");
        ArrayList<ReportDTO> reportDTOs = new ArrayList<>();

        for (Work work : restClient.getRegisteredWorkByMonth(Integer.parseInt(queryParameters.get("year").getFirst()), Integer.parseInt(queryParameters.get("month").getFirst()))) {

            System.out.println("work = " + work);
            ReportDTO reportDTO = null;
            for (ReportDTO newReportDTO : reportDTOs) {
                if(newReportDTO.getTaskUUID().equals(work.getTaskUUID()) && newReportDTO.getWorkerUUID().equals(work.getUserUUID())) {
                    reportDTO = newReportDTO;
                }
            }

            if(reportDTO == null) {
                reportDTO = new ReportDTO();
                if(work.getWorkDuration() > 0) reportDTOs.add(reportDTO);
                Map<String, Object> user = userService.getOneEntity(userService.getResourcePath(), work.getUserUUID());
                System.out.println("user = " + user);
                reportDTO.setWorkerName(user.get("firstname") + " " + user.get("lastname"));
                reportDTO.setWorkerUUID(work.getUserUUID());

                Map<String, String>  taskProjectClient = restClient.getTaskProjectClient(work.getTaskUUID());
                System.out.println("taskProjectClient = " + taskProjectClient);
                reportDTO.setClientName(taskProjectClient.get("clientname"));
                reportDTO.setProjectName(taskProjectClient.get("projectname"));
                reportDTO.setTaskName(taskProjectClient.get("taskname"));
                reportDTO.setTaskUUID(work.getTaskUUID());
            }

            reportDTO.setHours(reportDTO.getHours() + work.getWorkDuration());

            double workerRate = restClient.getTaskWorkerRate(work.getTaskUUID(), work.getUserUUID());
            System.out.println("workerRate = " + workerRate);
            reportDTO.setRate(workerRate);
            reportDTO.setSum(reportDTO.getHours() * reportDTO.getRate());
        }

        return reportDTOs;
    }

    @Override
    public GenericRepository getGenericRepository() {
        return null;
    }

    @Override
    public String getResourcePath() {
        return "reports";
    }

    @Override
    public void create(JsonNode clientJsonNode) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void update(JsonNode clientJsonNode, String uuid) throws SQLException {
        throw new RuntimeException("Not implemented");
    }
}
