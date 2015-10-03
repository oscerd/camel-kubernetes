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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kubernetes.KubernetesConstants;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class KubernetesPodsProducerTest extends CamelTestSupport {

    private String authToken;
    private String host;

    // The Camel-Kubernetes tests are based on vagrant fabric8-image
    // https://github.com/fabric8io/fabric8-installer/tree/master/vagrant/openshift
    // by running the vagrant image you'll have an environment with
    // Openshift/Kubernetes installed

    @Override
    public void setUp() throws Exception {
        // INSERT credentials and host here
        authToken = "Pg4zPRjTG8fukBGcJpDfqP-1IF9Y2yp0aKp8zgCb6eo";
        host = "https://172.28.128.4:8443";
        super.setUp();
    }

    @Test
    public void listTest() throws Exception {
        if (authToken == null) {
            return;
        }
        List<Pod> result = template.requestBody("direct:list", "", List.class);

        boolean defaultExists = false;

        Iterator<Pod> it = result.iterator();
        while (it.hasNext()) {
            Pod pod = (Pod) it.next();
            if ((pod.getMetadata().getName()).contains("fabric8")) {
                defaultExists = true;
            }
        }

        assertTrue(defaultExists);
    }

    @Test
    public void listByLabelsTest() throws Exception {
        if (authToken == null) {
            return;
        }
        Exchange ex = template.request("direct:listByLabels", new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                Map<String, String> labels = new HashMap<String, String>();
                labels.put("component", "elasticsearch");
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_PODS_LABELS, labels);
            }
        });

        List<Pod> result = ex.getOut().getBody(List.class);

        boolean podExists = false;
        Iterator<Pod> it = result.iterator();
        while (it.hasNext()) {
            Pod pod = (Pod) it.next();
            if (pod.getMetadata().getLabels().containsValue("elasticsearch")) {
                podExists = true;
            }
        }

        assertFalse(podExists);
    }

    @Test
    public void getPodTest() throws Exception {
        if (authToken == null) {
            return;
        }
        Exchange ex = template.request("direct:getPod", new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_POD_NAME,
                        "elasticsearch-7015o");
            }
        });

        Pod result = ex.getOut().getBody(Pod.class);

        assertNull(result);
    }

    @Test
    public void createAndDeletePod() throws Exception {
        if (authToken == null) {
            return;
        }
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
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:list")
                        .toF("kubernetes://%s?oauthToken=%s&category=pods&operation=listPods",
                                host, authToken);
                from("direct:listByLabels")
                        .toF("kubernetes://%s?oauthToken=%s&category=pods&operation=listPodsByLabels",
                                host, authToken);
                from("direct:getPod")
                        .toF("kubernetes://%s?oauthToken=%s&category=pods&operation=getPod",
                                host, authToken);
                from("direct:createPod")
                        .toF("kubernetes://%s?oauthToken=%s&category=pods&operation=createPod",
                                host, authToken);
                from("direct:deletePod")
                        .toF("kubernetes://%s?oauthToken=%s&category=pods&operation=deletePod",
                                host, authToken);
            }
        };
    }
}
