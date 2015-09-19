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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ning.http.util.Base64;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Secret;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class KubernetesSecretsProducerTest extends CamelTestSupport {

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
        List<Secret> result = template.requestBody("direct:list", "",
                List.class);

        assertTrue(result.size() != 0);
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
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_SECRETS_LABELS, labels);
            }
        });

        List<Secret> result = ex.getOut().getBody(List.class);
    }

    @Test
    public void getSecretTest() throws Exception {
        if (username == null) {
            return;
        }
        Exchange ex = template.request("direct:get", new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_SECRET_NAME,
                        "builder-token-191oc");
            }
        });

        Secret result = ex.getOut().getBody(Secret.class);
    }

    @Test
    public void createAndDeleteSecret() throws Exception {
        if (username == null) {
            return;
        }
        Exchange ex = template.request("direct:create", new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_SECRET_NAME, "test");
                Map<String, String> labels = new HashMap<String, String>();
                labels.put("this", "rocks");
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_SECRETS_LABELS, labels);
                Secret s = new Secret();
                s.setKind("Secret");
                Map<String, String> mp = new HashMap<String, String>();
                mp.put("username", Base64.encode("pippo".getBytes()));
                mp.put("password", Base64.encode("password".getBytes()));
                s.setData(mp);

                ObjectMeta meta = new ObjectMeta();
                meta.setName("test");
                s.setMetadata(meta);
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_SECRET, s);
            }
        });

        Secret sec = ex.getOut().getBody(Secret.class);

        assertEquals(sec.getMetadata().getName(), "test");

        ex = template.request("direct:delete", new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_NAMESPACE_NAME,
                        "default");
                exchange.getIn().setHeader(
                        KubernetesConstants.KUBERNETES_SECRET_NAME, "test");
            }
        });

        boolean secDeleted = ex.getOut().getBody(Boolean.class);

        assertTrue(secDeleted);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:list")
                        .toF("kubernetes://%s?username=%s&password=%s&category=secrets&operation=listSecrets",
                                host, username, password);
                from("direct:listByLabels")
                        .toF("kubernetes://%s?username=%s&password=%s&category=secrets&operation=listSecretsByLabels",
                                host, username, password);
                from("direct:get")
                        .toF("kubernetes://%s?username=%s&password=%s&category=secrets&operation=getSecret",
                                host, username, password);
                from("direct:create")
                        .toF("kubernetes://%s?username=%s&password=%s&category=secrets&operation=createSecret",
                                host, username, password);
                from("direct:delete")
                        .toF("kubernetes://%s?username=%s&password=%s&category=secrets&operation=deleteSecret",
                                host, username, password);
            }
        };
    }
}
