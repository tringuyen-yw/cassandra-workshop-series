# Resources config fixes for microk8s single-node cluster

Tested on 
```
$ snap list microk8s
Name      Version  Rev   Tracking     Publisher   Notes
microk8s  v1.18.6  1574  latest/edge  canonical✓  classic
```

## 1. Cassandra Operator

The Cassandra operator will fail to start its pod because of a timing sync issue between loading apparmor profile / microk8s startup.
[OCI runtime exec failed: apparmor failed to apply profile: write /proc/self/attr/exec](https://github.com/ubuntu/microk8s/issues/1454). There are two possible workarounds:

### 1.1 Comment out "allowPrivilegeEscalation: false"

This workaround is more reliable. This consists of commenting out the line "allowPrivilegeEscalation: false" (around line 6380) in [11-install-cass-operator-v1.3.yaml](.11-install-cass-operator-v1.3.yaml). See DataStax Community forum [cass-operator-v1.3 failed to start on microk8s](https://community.datastax.com/questions/7144/cass-operator-v13-failed-to-start-on-microk8s.html)

This workaround has been applied. You should install the Cassandra operator by

```bash
$ kubectl -n cass-operator apply -f ./cassandra/microk8s/11-install-cass-operator-v1.3.yaml
```

### 1.2 Reload manually apparmor profile

```bash
$ sudo apparmor_parser -R /var/lib/snapd/apparmor/profiles/snap.microk8s.daemon-containerd
$ sudo apparmor_parser -a /var/lib/snapd/apparmor/profiles/snap.microk8s.daemon-containerd
```

This workaround does not require to modify the yaml. However, it is still possible that the Cassandra operator could not be initialized properly. The `cass-operator-xxx` pod can still fail Liveness probe check. Due to the same error mentioned above.

If you choose this approach, you can use the same resource definition supplied by the course

```bash
$ kubectl -n cass-operator apply -f ./cassandra/11-install-cass-operator-v1.3.yaml
```


## 2. CassandraDatacenter resource

When creating a multi-nodes Cassandra cluster on a single-node microk8s cluster. As soon as you scale up the CassandraDatacenter resource to more than 1 node, the additional pods will fail to start due to pod affinity validation failure. To see the error

```bash
$ kubectl -n cass-operator describe pod id-of-newpod
```

Console output
```
... etc ...

Events:
  Type     Reason            Age                 From               Message
  ----     ------            ----                ----               -------
  Warning  FailedScheduling  52s (x20 over 25m)  default-scheduler  0/1 nodes are available: 1 node(s) didn't match pod affinity/anti-affinity, 1 node(s) didn't satisfy existing pods anti-affinity rules.
```


The solution is to add `allowMultipleNodesPerWorker: true` in the CassandraDatacenter resource config. Use the yaml provided in this `microk8s` directory to deploy your CassandraDatacenter. For example, to start with a single-node Cassandra cluster:

```bash
$ kubectl -n cass-operator apply -f ./cassandra/microk8s/12-cassandra-cluster-1nodes.yaml
```

Then scale up to a Cassandra 3-nodes cluster:

```bash
$ kubectl -n cass-operator apply -f ./cassandra/microk8s/13-cassandra-cluster-3nodes.yaml
```

Then scale up to a Cassandra 3-nodes cluster:

```bash
$ kubectl -n cass-operator apply -f ./cassandra/microk8s/13-cassandra-cluster-3nodes.yaml
```

Change the config `commitlog_sync_period_in_ms: 11000` of the Cassandra 3-nodes cluster:

```bash
$ kubectl -n cass-operator apply -f ./cassandra/microk8s/14-cassandra-cluster-3nodes-newconfig.yaml
```

Stop and Delete the Cassandra cluster:

```bash
$ kubectl -n cass-operator apply  -f ./cassandra/microk8s/15-cassandra-cluster-stop.yaml
$ kubectl -n cass-operator delete -f ./cassandra/microk8s/15-cassandra-cluster-stop.yaml
```


**(end)**
