package dk.trustworks.bimanager.caches;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dk.trustworks.bimanager.caches.items.WorkItem;
import dk.trustworks.bimanager.dto.ProjectYearEconomy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 29/09/15.
 */
public class CacheHandler {

    public static final Multimap<Integer, WorkItem> workItems = HashMultimap.create();

    public static final Map<String, Map<String, ProjectYearEconomy>> userWorkByMonthProject = new HashMap<>();

}
