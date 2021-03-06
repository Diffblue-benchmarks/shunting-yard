/**
 * Copyright (C) 2016-2018 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.shunting.yard.replicator.yaml;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.Transient;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;

public class AdvancedPropertyUtilsTest {

  static class TestBean {
    private String longPropertyName;
    public transient int transientField;
    private char transientProperty;

    public String getLongPropertyName() {
      return longPropertyName;
    }

    public void setLongPropertyName(String longPropertyName) {
      this.longPropertyName = longPropertyName;
    }

    @Transient
    public char getTransientProperty() {
      return transientProperty;
    }

    @Transient
    public void setTransientProperty(char transientProperty) {
      this.transientProperty = transientProperty;
    }
  }

  private final AdvancedPropertyUtils properyUtils = new AdvancedPropertyUtils();

  @Test
  public void regularPropertyName() throws Exception {
    Property property = properyUtils.getProperty(TestBean.class, "longPropertyName");
    assertThat(property).isNotNull();
    assertThat(property.getName()).isEqualTo("longPropertyName");
  }

  @Test
  public void lowerHyphenPropertyName() throws Exception {
    Property property = properyUtils.getProperty(TestBean.class, "long-property-name");
    assertThat(property).isNotNull();
    assertThat(property.getName()).isEqualTo("longPropertyName");
  }

  @Test(expected = YAMLException.class)
  public void illegalPropertyName() throws Exception {
    properyUtils.getProperty(TestBean.class, "unknown");
  }

  @Test
  public void createPropertySetWithDefaultBeanAccess() throws Exception {
    Set<Property> properties = properyUtils.createPropertySet(TestBean.class, BeanAccess.DEFAULT);
    assertThat(properties.size()).isEqualTo(1);
    assertThat(properties.iterator().next().getName()).isEqualTo("longPropertyName");
  }

  @Test
  public void createPropertySetWithFieldBeanAccess() throws Exception {
    Set<Property> properties = properyUtils.createPropertySet(TestBean.class, BeanAccess.FIELD);
    assertThat(properties.size()).isEqualTo(2);
    Iterator<Property> iterator = properties.iterator();
    assertThat(iterator.next().getName()).isEqualTo("longPropertyName");
    assertThat(iterator.next().getName()).isEqualTo("transientProperty");
  }

  @Test
  public void createPropertySetWithPropertyBeanAccess() throws Exception {
    Set<Property> properties = properyUtils.createPropertySet(TestBean.class, BeanAccess.PROPERTY);
    assertThat(properties.size()).isEqualTo(1);
    assertThat(properties.iterator().next().getName()).isEqualTo("longPropertyName");
  }

}
