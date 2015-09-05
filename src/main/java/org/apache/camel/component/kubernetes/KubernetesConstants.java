/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.kubernetes;

public interface KubernetesConstants {
    String KUBERNETES_OPERATION = "CamelKubernetesOperation";
    String KUBERNETES_NAMESPACE_NAME = "CamelKubernetesNamespaceName";
    String KUBERNETES_NAMESPACE_LABELS = "CamelKubernetesNamespaceLabels";
    String KUBERNETES_SERVICE_LABELS = "CamelKubernetesServiceLabels";
    String KUBERNETES_SERVICE_NAME = "CamelKubernetesServiceName";
    String KUBERNETES_SERVICE_SPEC = "CamelKubernetesServiceSpec";
    String KUBERNETES_REPLICATION_CONTROLLERS_LABELS = "CamelKubernetesReplicationControllersLabels";
    String KUBERNETES_REPLICATION_CONTROLLER_NAME = "CamelKubernetesReplicationControllerName";
    String KUBERNETES_REPLICATION_CONTROLLER_SPEC = "CamelKubernetesReplicationControllerSpec";
    String KUBERNETES_PODS_LABELS = "CamelKubernetesPodsLabels";
    String KUBERNETES_POD_NAME = "CamelKubernetesPodName";
    String KUBERNETES_POD_SPEC = "CamelKubernetesPodSpec";
    String KUBERNETES_PERSISTENT_VOLUMES_LABELS = "CamelKubernetesPersistentVolumesLabels";
    String KUBERNETES_PERSISTENT_VOLUME_NAME = "CamelKubernetesPersistentVolumeName";
    String KUBERNETES_PERSISTENT_VOLUMES_CLAIMS_LABELS = "CamelKubernetesPersistentVolumesClaimsLabels";
    String KUBERNETES_PERSISTENT_VOLUME_CLAIM_NAME = "CamelKubernetesPersistentVolumeClaimName";
    String KUBERNETES_PERSISTENT_VOLUME_CLAIM_SPEC = "CamelKubernetesPersistentVolumeClaimSpec";
}
