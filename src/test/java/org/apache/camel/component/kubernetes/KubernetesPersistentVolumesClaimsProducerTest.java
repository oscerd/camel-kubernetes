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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class KubernetesPersistentVolumesClaimsProducerTest extends
        CamelTestSupport {

    private String username;
    private String password;
    private String host;

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
    public void listTest() throws Exception {
        if (username == null) {
            return;
        }
        List<PersistentVolumeClaim> result = template.requestBody(
                "direct:list", "", List.class);

        assertTrue(result.size() == 0);
    }

    @Test
    public void listByLabelsTest() throws Exception {
        if (username == null) {
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
                exchange.getIn()
                        .setHeader(
                                KubernetesConstants.KUBERNETES_PERSISTENT_VOLUMES_CLAIMS_LABELS,
                                labels);
            }
        });

        List<PersistentVolume> result = ex.getOut().getBody(List.class);
    }

    @Test
    public void createListAndDeletePersistentVolumeClaim() throws Exception {
        if (username == null) {
            return;
        }
        Exchange ex = template.request("direct:create", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                exchange.getIn()
                        .setHeader(
                                KubernetesConstants.KUBERNETES_PERSISTENT_VOLUME_CLAIM_NAME,
                                "test");
                Map<String, String> labels = new HashMap<String, String>();
                labels.put("this", "rocks");
                exchange.getIn()
                        .setHeader(
                                KubernetesConstants.KUBERNETES_PERSISTENT_VOLUMES_CLAIMS_LABELS,
                                labels);
                PersistentVolumeClaimSpec pvcSpec = new PersistentVolumeClaimSpec();
                ResourceRequirements rr = new ResourceRequirements();
                Map<String, Quantity> mp = new HashMap<String, Quantity>();
                mp.put("storage", new Quantity("100"));
                rr.setLimits(mp);
                Map<String, Quantity> req = new HashMap<String, Quantity>();
                req.put("storage", new Quantity("100"));
                rr.setRequests(req);
                pvcSpec.setResources(rr);
                pvcSpec.setVolumeName("vol001");
                List<String> access = new ArrayList<String>();
                access.add("ReadWriteOnce");
                pvcSpec.setAccessModes(access);
                exchange.getIn()
                        .setHeader(
                                KubernetesConstants.KUBERNETES_PERSISTENT_VOLUME_CLAIM_SPEC,
                                pvcSpec);
            }
        });

        PersistentVolumeClaim pvc = ex.getOut().getBody(
                PersistentVolumeClaim.class);

        assertEquals(pvc.getMetadata().getName(), "test");

        ex = template.request("direct:listByLabels", new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                Map<String, String> labels = new HashMap<String, String>();
                labels.put("this", "rocks");
                exchange.getIn()
                        .setHeader(
                                KubernetesConstants.KUBERNETES_PERSISTENT_VOLUMES_CLAIMS_LABELS,
                                labels);
            }
        });

        List<PersistentVolumeClaim> result = ex.getOut().getBody(List.class);

        boolean pvcExists = false;
        Iterator<PersistentVolumeClaim> it = result.iterator();
        while (it.hasNext()) {
            PersistentVolumeClaim pvcLocal = (PersistentVolumeClaim) it.next();
            if ("test".equalsIgnoreCase(pvcLocal.getMetadata().getName())) {
                pvcExists = true;
            }
        }

        assertTrue(pvcExists);

        ex = template.request("direct:delete", new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                exchange.getIn()
                        .setHeader(
                                KubernetesConstants.KUBERNETES_PERSISTENT_VOLUME_CLAIM_NAME,
                                "test");
            }
        });

        boolean pvcDeleted = ex.getOut().getBody(Boolean.class);

        assertTrue(pvcDeleted);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:list")
                        .toF("kubernetes://%s?username=%s&password=%s&category=persistentVolumesClaims&operation=listPersistentVolumesClaims",
                                host, username, password);
                from("direct:listByLabels")
                        .toF("kubernetes://%s?username=%s&password=%s&category=persistentVolumesClaims&operation=listPersistentVolumesClaimsByLabels",
                                host, username, password);
                from("direct:create")
                        .toF("kubernetes://%s?username=%s&password=%s&category=persistentVolumesClaims&operation=createPersistentVolumeClaim",
                                host, username, password);
                from("direct:delete")
                        .toF("kubernetes://%s?username=%s&password=%s&category=persistentVolumesClaims&operation=deletePersistentVolumeClaim",
                                host, username, password);
            }
        };
    }
}
