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

import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

import org.apache.camel.Exchange;
import org.apache.camel.component.kubernetes.KubernetesConstants;
import org.apache.camel.component.kubernetes.KubernetesEndpoint;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesNamespacesProducer extends DefaultProducer {

	private static final Logger LOG = LoggerFactory.getLogger(KubernetesNamespacesProducer.class);

    private final KubernetesEndpoint endpoint;

    public KubernetesNamespacesProducer(KubernetesEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }
    
    @Override
    public KubernetesEndpoint getEndpoint() {
        return (KubernetesEndpoint) super.getEndpoint();
    }
   
	@Override
	public void process(Exchange exchange) throws Exception {
        String operation;
        if (ObjectHelper.isEmpty(getEndpoint().getKubernetesConfiguration().getOperation())) {
            operation = exchange.getIn().getHeader(KubernetesConstants.KUBERNETES_OPERATION, String.class);
        } else {
            operation = getEndpoint().getKubernetesConfiguration().getOperation();
        }
        
        switch (operation) {
        
        case KubernetesOperations.LIST_OPERATION:
            doList(exchange, operation);
            break;
                
        default:
            throw new IllegalArgumentException("Local path must specified to execute " + operation);
        }
	}
	
    protected void doList(Exchange exchange, String operation) throws Exception {
        NamespaceList namespacesList = getEndpoint().getKubernetesClient().namespaces().list();
        exchange.getOut().setBody(SerializationUtils.getMapper().writeValueAsString(namespacesList));
    }

}
