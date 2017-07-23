package org.skywalking.apm.collector.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.skywalking.apm.collector.stream.impl.data.DataDefine;

/**
 * @author pengys5
 */
public abstract class WorkerContext implements Context {

    private Map<Integer, DataDefine> dataDefineMap;

    private Map<String, List<WorkerRef>> roleWorkers;

    public WorkerContext() {
        this.roleWorkers = new ConcurrentHashMap<>();
    }

    private Map<String, List<WorkerRef>> getRoleWorkers() {
        return this.roleWorkers;
    }

    public final DataDefine getDataDefine(int defineId) {
        return dataDefineMap.get(defineId);
    }

    @Override final public WorkerRefs lookup(Role role) throws WorkerNotFoundException {
        if (getRoleWorkers().containsKey(role.roleName())) {
            WorkerRefs refs = new WorkerRefs(getRoleWorkers().get(role.roleName()), role.workerSelector());
            return refs;
        } else {
            throw new WorkerNotFoundException("role=" + role.roleName() + ", no available worker.");
        }
    }

    @Override final public void put(WorkerRef workerRef) {
        if (!getRoleWorkers().containsKey(workerRef.getRole().roleName())) {
            getRoleWorkers().putIfAbsent(workerRef.getRole().roleName(), new ArrayList<WorkerRef>());
        }
        getRoleWorkers().get(workerRef.getRole().roleName()).add(workerRef);
    }

    @Override final public void remove(WorkerRef workerRef) {
        getRoleWorkers().remove(workerRef.getRole().roleName());
    }
}