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
package org.apache.camel.component.kubernetes.producer;

public interface KubernetesOperations {

	// Namespaces
    String LIST_NAMESPACE_OPERATION = "listNamespaces";
    String LIST_NAMESPACE_BY_LABELS_OPERATION = "listNamespacesByLabels";
    String GET_NAMESPACE_OPERATION = "getNamespace";
    String CREATE_NAMESPACE_OPERATION = "createNamespace";
    String DELETE_NAMESPACE_OPERATION = "deleteNamespace";
    
    // Services 
    String LIST_SERVICES_OPERATION = "listServices";
    String LIST_SERVICES_BY_LABELS_OPERATION = "listServicesByLabels";
    String GET_SERVICE_OPERATION = "getService";
    String CREATE_SERVICE_OPERATION = "createService";
    String DELETE_SERVICE_OPERATION = "deleteService";
}
