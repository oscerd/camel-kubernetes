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
package org.apache.camel.component.kubernetes.consumer;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kubernetes.KubernetesConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class KubernetesPodsConsumerTest extends CamelTestSupport {

    private String username;
    private String password;
    private String host;
    
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint mockResultEndpoint;

    // The Camel-Kubernetes tests are based on vagrant fabric8-image
    // https://github.com/fabric8io/fabric8-installer/tree/master/vagrant/openshift
    // by running the vagrant image you'll have an environment with
    // Openshift/Kubernetes installed

    @Override
    public void setUp() throws Exception {
        // INSERT credentials and host here
        username = "admin";
        password = "admin";
        host = "https://172.28.128.4:8443";
        super.setUp();
    }

    @Test
    public void createAndDeletePod() throws Exception {
        if (username == null) {
            return;
        }
        
        mockResultEndpoint.expectedMessageCount(3);
        mockResultEndpoint.expectedHeaderValuesReceivedInAnyOrder(KubernetesConstants.KUBERNETES_EVENT_ACTION, "ADDED", "MODIFIED", "DELETED");
        Exchange ex = template.request("direct:createPod", new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_POD_NAME, "test");
                Map<String, String> labels = new HashMap<String, String>();
                labels.put("this", "rocks");
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_PODS_LABELS, labels);
                PodSpec podSpec = new PodSpec();
                podSpec.setHost("172.28.128.4");
                Container cont = new Container();
                cont.setImage("docker.io/jboss/wildfly:latest");
                cont.setName("pippo");

                List<ContainerPort> containerPort = new ArrayList<ContainerPort>();
                ContainerPort port = new ContainerPort();
                port.setHostIP("0.0.0.0");
                port.setHostPort(8080);
                port.setContainerPort(8080);

                containerPort.add(port);

                cont.setPorts(containerPort);

                List<Container> list = new ArrayList<Container>();
                list.add(cont);

                podSpec.setContainers(list);

                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_POD_SPEC, podSpec);
            }
        });

        Pod pod = ex.getOut().getBody(Pod.class);

        assertEquals(pod.getMetadata().getName(), "test");

        ex = template.request("direct:deletePod", new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_POD_NAME, "test");
            }
        });

        boolean podDeleted = ex.getOut().getBody(Boolean.class);

        assertTrue(podDeleted);
        
        Thread.sleep(1*1000);

        mockResultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:list")
                        .toF("kubernetes://%s?username=%s&password=%s&category=pods&operation=listPods",
                                host, username, password);
                from("direct:listByLabels")
                        .toF("kubernetes://%s?username=%s&password=%s&category=pods&operation=listPodsByLabels",
                                host, username, password);
                from("direct:getPod")
                        .toF("kubernetes://%s?username=%s&password=%s&category=pods&operation=getPod",
                                host, username, password);
                from("direct:createPod")
                        .toF("kubernetes://%s?username=%s&password=%s&category=pods&operation=createPod",
                                host, username, password);
                from("direct:deletePod")
                        .toF("kubernetes://%s?username=%s&password=%s&category=pods&operation=deletePod",
                                host, username, password);
                fromF("kubernetes://%s?username=%s&password=%s&category=pods", host, username, password)
                        .process(new KubernertesProcessor())
                        .to(mockResultEndpoint);
            }
        };
    }
    
    public class KubernertesProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Message in = exchange.getIn();
            log.info("Got event with body: " + in.getBody() + " and action " + in.getHeader(KubernetesConstants.KUBERNETES_EVENT_ACTION));
        }
    }
}
