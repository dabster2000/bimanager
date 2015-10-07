package dk.trustworks.bimanager.caches.items;

import dk.trustworks.bimanager.dto.Project;
import dk.trustworks.bimanager.dto.Task;
import dk.trustworks.bimanager.dto.TaskWorkerConstraint;
import dk.trustworks.bimanager.dto.Work;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 25/09/15.
 */
public class WorkItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public String uuid;
    public String userUUID;
    public String projectUUID;
    public String taskUUID;
    public String taskWorkerConstraintUUID;
    public double rate;
    public int year;
    public int month;
    public int day;
    public Double hours;

    public WorkItem() {
    }

    public WorkItem(String userUUID, String projectUUID, String taskUUID, String taskWorkerConstraintUUID, double rate, int year, int month, int day, Double hours) {
        this.userUUID = userUUID;
        this.projectUUID = projectUUID;
        this.taskUUID = taskUUID;
        this.taskWorkerConstraintUUID = taskWorkerConstraintUUID;
        this.rate = rate;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = hours;
        createUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkItem workItem = (WorkItem) o;

        return uuid.equals(workItem.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public void createUUID() {
        uuid = DigestUtils.sha1Hex(this.taskUUID + this.userUUID + this.year + this.month + this.day);
    }

    public static List<WorkItem> createWorkItems(List<Work> registeredWork, List<Project> projects) {
        List<WorkItem> workItems = new ArrayList<>();
        for (Work work : registeredWork) {
            Project actualProject = null;
            Task actualTask = null;
            TaskWorkerConstraint actualTaskWorkerConstraint = null;
            for (Project project : projects) {
                for (Task task : project.getTasks()) {
                    if(task.getUUID().equals(work.getTaskUUID())) {
                        actualProject = project;
                        actualTask = task;
                        for (TaskWorkerConstraint taskWorkerConstraint : task.getTaskWorkerConstraints()) {
                            if(taskWorkerConstraint.getUserUUID().equals(work.getUserUUID())) {
                                actualTaskWorkerConstraint = taskWorkerConstraint;
                            }
                        }
                    }
                }
            }
            if(actualProject == null) continue;
            if(actualTask == null) continue;
            if(actualTaskWorkerConstraint == null) continue;

            WorkItem workItem = new WorkItem(work.getUserUUID(), actualProject.getUUID(), actualTask.getUUID(), actualTaskWorkerConstraint.getUUID(), actualTaskWorkerConstraint.getPrice(), work.getYear(), work.getMonth(), work.getDay(), work.getWorkDuration());
            workItems.add(workItem);
        }
        return workItems;
    }
}
